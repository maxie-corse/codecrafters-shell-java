import java.io.FileOutputStream;
import java.io.PrintStream;

public class RedirectUtils {
    static void ensureErrorFileExists(ParsedCommand cmd) throws Exception {

        if (cmd.stderrFile == null) {
            return;
        }

        PrintStream err = getError(cmd);

        if (err != System.err) {
            err.close();
        }
    }

    static PrintStream getOutput(ParsedCommand cmd) throws Exception {
        if (cmd.stdoutFile == null) {
            return System.out;
        }

        return new PrintStream(
            new FileOutputStream(
                cmd.stdoutFile,
                cmd.appendStdout
            )
        );
    }

    static PrintStream getError(ParsedCommand cmd) throws Exception {
        if (cmd.stderrFile == null) {
            return System.err;
        }

        return new PrintStream(
            new FileOutputStream(
                cmd.stderrFile,
                cmd.appendStderr
            )
        );
    }
}
