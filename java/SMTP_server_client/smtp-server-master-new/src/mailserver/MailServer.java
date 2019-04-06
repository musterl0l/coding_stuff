package mailserver;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.net.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;

public class MailServer {

    ServerSocketChannel serverChannel;
    Selector selector;
    SelectionKey serverKey;


    //contructor for Mailserver that listens on a Socket address
    public MailServer(InetSocketAddress listenAdd) throws Throwable {

        //create & register ServerSocket
        serverChannel = ServerSocketChannel.open();

        serverChannel.configureBlocking(false);
        serverKey = serverChannel.register(selector = Selector.open(), SelectionKey.OP_ACCEPT);
        //bind server to listen on the address (ip + port number)
        serverChannel.bind(listenAdd);

        //Server is waiting for Notification
        new Thread(() -> {
            while (true) {
                try {
                    loop();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }).start();

        // For debugging:
        /*
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                loop();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }, 0, 500, TimeUnit.MILLISECONDS);

         */
    }

    //hash map to store all clients currently connected
    static HashMap<SelectionKey, ClientSession> clientMap = new HashMap<SelectionKey, ClientSession>();

    //loop method: looping through  the selected keys
    public void loop() throws Throwable {
        selector.selectNow();    //selectNow will not block -> if no keys ready: return instantly

        //iterating through the selected key set
        for (SelectionKey key : selector.selectedKeys()) {
            try {
                if (!key.isValid())
                    continue; //key is not valid -> skip it and go to the next one

                //or key == serverKey then connect to that channel -> connect client and open a socket channel
                if (key.isAcceptable()) {
                    //get the address of the client
                    SocketChannel acceptedChannel = serverChannel.accept();

                    if (acceptedChannel == null)
                        continue;
                    //configure channel as non-blocking
                    acceptedChannel.configureBlocking(false);

                    //register with Selector to read/write with that key
                    SelectionKey readKey = acceptedChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);


                    //start a new Client session with the accepted client and store it in client hash-map
                    clientMap.put(readKey, new ClientSession(readKey, acceptedChannel));

                    System.out.println("New client ip=" + acceptedChannel.getRemoteAddress() + ", total clients=" + MailServer.clientMap.size());


                }

                //read data from channel
                if (key.isReadable()) {
                    ClientSession sesh = clientMap.get(key);

                    if (sesh == null)
                        continue;

                    //call read method that handles reading within client session class
                    //System.out.println("server is reading...\n");
                    sesh.read();
                }

            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        //clear handled key set before the next loop
        selector.selectedKeys().clear();
    }

}
