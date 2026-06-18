import java.util.List;

public class ParsedCommand {
    public List<String> args;

    public String stdoutFile;
    public String stderrFile;

    public boolean appendStdout;
    public boolean appendStderr;

    public ParsedCommand(List<String> args) {
        this.args = args;
    }
}
