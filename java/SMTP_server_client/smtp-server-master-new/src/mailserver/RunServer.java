package mailserver;

import java.net.InetSocketAddress;

public class RunServer {

    public static String EMAIL_LOG_DIRECTORY = "";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: mailserver <port>");
            return;
        }

        if (EMAIL_LOG_DIRECTORY.isEmpty()) {
            // Set Email log dir to working directory if it's empty
            EMAIL_LOG_DIRECTORY = System.getProperty("user.dir");
        }

        try {
            // start server
            new MailServer(new InetSocketAddress(Integer.valueOf(args[0])));
            System.out.println("Server on port " + args[0] + " is running now!\n");
        } catch (Throwable t) {
            System.out.println("Running Server failed");
            t.printStackTrace();
        }
    }

}
