import java.util.ArrayList;
import java.util.List;

public class Parser {
    public static ParsedCommand parse(List<String> tokens) {
        List<String> args = new ArrayList<>();

        ParsedCommand cmd = new ParsedCommand(args);

        for (int i = 0; i < tokens.size(); i++) {

            String token = tokens.get(i);

            if (token.equals("&") && i == tokens.size() - 1) {
                cmd.background = true;
            }

            else if (token.equals(">") || token.equals("1>")) {
                cmd.stdoutFile = tokens.get(++i);
            }

            else if (token.equals(">>") || token.equals("1>>")) {
                cmd.stdoutFile = tokens.get(++i);
                cmd.appendStdout = true;
            }

            else if (token.equals("2>")) {
                cmd.stderrFile = tokens.get(++i);
            }

            else if (token.equals("2>>")) {
                cmd.stderrFile = tokens.get(++i);
                cmd.appendStderr = true;
            }

            else {
                args.add(token);
            }
        }

        if (args.contains("|")) {

            cmd.pipeline = new ArrayList<>();

            List<String> currentCommand = new ArrayList<>();

            for (String arg : args) {

                if (arg.equals("|")) {
                    cmd.pipeline.add(currentCommand);
                    currentCommand = new ArrayList<>();
                }
                else {
                    currentCommand.add(arg);
                }
            }

            cmd.pipeline.add(currentCommand);
        }

        return cmd;
    }
}
