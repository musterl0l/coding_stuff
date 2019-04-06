package mailserver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum SmtpCommand {
    // All SMTP commands with corresponding regular expression
    HELO("^[Hh][Ee][Ll][Oo] +(.+) *\\r\\n$"),
    MAIL_FROM("^[Mm][Aa][Ii][Ll] [Ff][Rr][Oo][Mm]: *(.+) *\\r\\n$"),
    RCPT_TO("^[Rr][Cc][Pp][Tt] [Tt][Oo]: *(.+) *\\r\\n$"),
    DATA("^[Dd][Aa][Tt][Aa] *\\r\\n$"),
    HELP("^[Hh][Ee][Ll][Pp] *\\r\\n$"),
    QUIT("^[Qq][Uu][Ii][Tt] *\\r\\n$"),
    UNKNOWN;

    Pattern commandPattern;

    // Constructor for UNKNOWN command
    SmtpCommand() {
        this.commandPattern = null;
    }

    // Constructor for all valid commands
    SmtpCommand(String commandRegex) {
        // Compile the pattern
        this.commandPattern = Pattern.compile(commandRegex);
    }

    // Method to get SmtpCommand object to a commandline
    public static Tuple<SmtpCommand, String[]> getSmtpCommand(String commandLine) {
        // Iterate over all commands we know
        for (SmtpCommand command : SmtpCommand.values()) {
            if (command.commandPattern == null) {
                break;
            }

            Matcher m = command.commandPattern.matcher(commandLine);

            // Check if the commandline matches to the pattern of command
            if (m.find()) {
                // Get all arguments of the pattern
                String[] args = new String[m.groupCount()];

                for (int i = 1; i <= m.groupCount(); i++) {
                    args[i - 1] = m.group(i);
                }

                // Return the command with the arguments
                return new Tuple<>(command, args);
            }
        }

        // We iterated over all commands we know, but none of them matched!
        return new Tuple<>(SmtpCommand.UNKNOWN, new String[0]);
    }


}
