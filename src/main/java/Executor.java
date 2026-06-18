import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class Executor {
    public static void executeExternal(ParsedCommand cmd) throws Exception {
        List<String> processArgs = new ArrayList<>(cmd.args);

        ProcessBuilder pb = new ProcessBuilder(processArgs);

        if (cmd.stdoutFile != null) {

            File outFile = new File(cmd.stdoutFile);

            if (cmd.appendStdout) {
                pb.redirectOutput(
                    ProcessBuilder.Redirect.appendTo(outFile)
                );
            }
            else {
                pb.redirectOutput(outFile);
            }
        }
        else {
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        }

        if (cmd.stderrFile != null) {

            File errFile = new File(cmd.stderrFile);

            if (cmd.appendStderr) {
                pb.redirectError(
                    ProcessBuilder.Redirect.appendTo(errFile)
                );
            }
            else {
                pb.redirectError(errFile);
            }
        }
        else {
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        }

        pb.redirectInput(ProcessBuilder.Redirect.INHERIT);

        Process process = pb.start();

        if (cmd.background) {
            int jobNumber = JobManager.allocateJobNumber();
            System.out.println("[" + jobNumber + "] " + process.pid());
        }
        else {
            process.waitFor();
        }
    }
}
