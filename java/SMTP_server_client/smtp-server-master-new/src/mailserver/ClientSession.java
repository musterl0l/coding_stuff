package mailserver;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.*;
import java.util.Random;


public class ClientSession {

    //every client gets own selection key, socket channel and buffer
    SelectionKey selkey;
    SocketChannel chan;
    ByteBuffer buf;

    private static Charset messageCharset = null;
    private static CharsetDecoder decoder = null;

    private static byte[] crnlMsg = null;
    private static byte[] responseOpen = null;
    private static byte[] smaOkay = null;
    private static byte[] startMail = null;
    private static byte[] quit = null;
    private static byte[] help = null;
    private static byte[] error500 = null;
    private static byte[] error503 = null;

    CharBuffer cb = null;

    private SmtpCommand currentState = null; // Represents the state of the current session, last command that was successfully executed

    private String clientName;
    private String sender;
    private String receiver;
    private String content;

    ClientSession(SelectionKey selkey, SocketChannel chan) throws Throwable {
        this.selkey = selkey;
        this.chan = (SocketChannel) chan.configureBlocking(false); // asynchronous/non-blocking
        buf = ByteBuffer.allocateDirect(8192); // ByteBuffer with 8192 byte capacity

        //create a decoder for US-ASCII charset
        try {
            messageCharset = Charset.forName("US-ASCII");
        } catch (UnsupportedCharsetException uce) {
            this.log("Cannot create charset for this client-session. Exiting...");
            System.exit(1);
        }
        decoder = messageCharset.newDecoder();

        initResponseCodes();

        // We're done initializing the ClientSession, so send the client that we're ready
        this.sendOpen();
    }

    private void log(String msg) {
        try {
            System.out.println("[Client " + ((InetSocketAddress) chan.getRemoteAddress()).getHostString() + ":" + ((InetSocketAddress) chan.getRemoteAddress()).getPort() + "] >> " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Initialization of response codes the server sends back to the client
    void initResponseCodes() {
        crnlMsg = "\r\n".getBytes(messageCharset);

        responseOpen = "220 <SMTP-Server> Service ready".getBytes(messageCharset);
        smaOkay = "250 Requested mail action okay, completed".getBytes(messageCharset);
        startMail = "354 Start mail input".getBytes(messageCharset);
        quit = "221 <SMTP-Server> Service closing transmission channel".getBytes(messageCharset);
        help = "214 Help message".getBytes(messageCharset);
        error500 = "500 Syntax error, command unrecognized".getBytes(messageCharset);
        error503 = "503 Bad sequence of commands".getBytes(messageCharset);
    }

    //method for disconnecting client
    private void disconnect() {
        //remove client from client map
        MailServer.clientMap.remove(selkey);
        try {
            if (selkey != null)
                selkey.cancel();

            if (chan == null)
                return;

            this.log("bye bye");
            chan.close();
        } catch (Throwable t) { /** quietly ignore  */}
    }


    //method for reading
    void read() {
        try {
            //check if there is a statement ending with \r\n
            if (!readCommandLine(chan, buf))
                return;

            //convert buffer to string msg
            try {
                cb = decoder.decode(buf);
            } catch (CharacterCodingException e) {
                this.log("Cannot show buffer content. Character coding exception...");
                return;
            }

            // The last received command is data, so this should be the mail content
            if (SmtpCommand.DATA.equals(this.currentState)) {
                this.content = cb.toString();

                if (this.writeToFile()) {
                    this.log("Received mail content and saved it!");
                } else {
                    this.log("Received mail content but got error whilst saving it!");
                }

                // Reset all values to be able to receive more mails
                this.sender = null;
                this.receiver = null;
                this.content = null;
                this.currentState = null;

                this.sendMailActionOkay();
                return;
            }

            // Get command from commandLine
            Tuple<SmtpCommand, String[]> cmdTuple = SmtpCommand.getSmtpCommand(cb.toString());
            SmtpCommand command = cmdTuple.getX();
            String[] args = cmdTuple.getY();

            this.log("Received " + command.toString() + " from Client with arguments: " + String.join(",", args));

            // Handle command
            if (SmtpCommand.HELO.equals(command)) {
                // Received HELO command, check state
                if (this.currentState != null) {
                    this.sendError503(); // Wrong state, send 503 Bad sequence of commands
                    return;
                }

                // Set clientName and send 250 OK
                this.clientName = args[0];
                this.sendMailActionOkay();
                this.currentState = SmtpCommand.HELO;

            } else if (SmtpCommand.MAIL_FROM.equals(command)) {
                // Received MAIL_FROM command, check state
                if (!SmtpCommand.HELO.equals(this.currentState)) {
                    this.sendError503(); // Wrong state, send 503 Bad sequence of commands
                    return;
                }

                // Set sender email and send 250 OK
                this.sender = args[0];
                this.sendMailActionOkay();
                this.currentState = SmtpCommand.MAIL_FROM;

            } else if (SmtpCommand.RCPT_TO.equals(command)) {
                // Received RCPT_TO command, check state
                if (!SmtpCommand.MAIL_FROM.equals(this.currentState)) {
                    this.sendError503(); // Wrong state, send 503 Bad sequence of commands
                    return;
                }

                // Set receiver email and send 250 OK
                this.receiver = args[0];
                this.sendMailActionOkay();
                this.currentState = SmtpCommand.RCPT_TO;

            } else if (SmtpCommand.DATA.equals(command)) {
                // Received DATA command, check state
                if (!SmtpCommand.RCPT_TO.equals(this.currentState)) {
                    this.sendError503(); // Wrong state, send 503 Bad sequence of commands
                    return;
                }

                // Send client 354 intermediate reply
                this.sendStartMailInput();
                this.currentState = SmtpCommand.DATA;

            } else if (SmtpCommand.HELP.equals(command)) {
                // Received HELP command, can be answered at any state
                this.sendHelp();

            } else if (SmtpCommand.QUIT.equals(command)) {
                // Received QUIT command, can be done at any state
                this.sendQuit();

                // close channel
                this.disconnect();

            } else {
                // Command not found, send 500 syntax error
                this.log("Command not found: " + cb.toString());
                this.sendError500();
            }

        } catch (Throwable t) {
            //disconnect if something goes wrong
            disconnect();
            t.printStackTrace();
        }
    }

    private boolean writeToFile() {
        try {
            Path dir = Paths.get(RunServer.EMAIL_LOG_DIRECTORY + FileSystems.getDefault().getSeparator() + this.receiver);

            try {
                Files.createDirectory(dir);
            } catch (FileAlreadyExistsException ex) {
                // Ignore
            }

            buf.clear();
            buf.put(this.content.getBytes(messageCharset));
            buf.flip();

            Path filePath = Paths.get(dir.toAbsolutePath().toString(), this.sender + "_" + new Random().nextInt(10000) + ".txt");

            RandomAccessFile file = new RandomAccessFile(filePath.toAbsolutePath().toString(), "rw");
            FileChannel ch = file.getChannel();

            ch.write(buf);
            ch.close();

            buf.clear();

            return true;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    //checks for a valid SMTP input
    private static boolean readCommandLine(SocketChannel socketChannel, ByteBuffer buffer) throws IOException {

        boolean foundHyphen = false;
        int pos = buffer.position();

        socketChannel.read(buffer);

        for (int i = pos; i < buffer.position(); i++) {

            if (buffer.get(i) == '-' && (i == 3)) {
                foundHyphen = true;
            }

            if (buffer.get(i) == '\n') {
                if ((i - 1) >= 0 && buffer.get(i - 1) == '\r') {
                    if (foundHyphen) {
                        foundHyphen = false;
                    } else {
                        buffer.flip();
                        return true;
                    }
                }
            }
        }

        return false;
    }

    // Send a message eg a command followed by a \r\n to the client
    private void sendCommand(byte[] bytes) {
        buf.clear();
        // Put the message stored in bytes and a \r\n
        buf.put(bytes);
        buf.put(crnlMsg);
        buf.flip();

        try {
            // send it through the channel
            chan.write(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // clear the buffer
        buf.clear();
    }

    private void sendOpen() {
        // Send 220 service ready
        this.sendCommand(responseOpen);
    }

    private void sendMailActionOkay() {
        // Send 250 requested mail action okay
        this.sendCommand(smaOkay);
    }

    private void sendStartMailInput() {
        // Send 354 start mail input
        this.sendCommand(startMail);
    }

    private void sendHelp() {
        // Send 214 help message
        this.sendCommand(help);
    }

    private void sendQuit() {
        // Send 221 quit
        this.sendCommand(quit);
    }

    private void sendError500() {
        // Send 500 error
        this.sendCommand(error500);
    }

    private void sendError503() {
        // Send 500 error
        this.sendCommand(error503);
    }

}
