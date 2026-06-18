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
            JobManager.addJob(process, String.join(" ", cmd.args) + " &");

            System.out.println("[" + (JobManager.getJobs().size()) + "] " + process.pid());
        }
        else {
            process.waitFor();
        }
    }

    public static void executePipeline(ParsedCommand cmd) throws Exception {

        ProcessBuilder leftPB =
            new ProcessBuilder(cmd.pipeline.get(0));

        ProcessBuilder rightPB =
            new ProcessBuilder(cmd.pipeline.get(1));

        leftPB.redirectError(ProcessBuilder.Redirect.INHERIT);
        rightPB.redirectError(ProcessBuilder.Redirect.INHERIT);

        List<Process> processes =
            ProcessBuilder.startPipeline(
                List.of(leftPB, rightPB)
            );

        processes.get(processes.size() - 1).waitFor();
    }
}
