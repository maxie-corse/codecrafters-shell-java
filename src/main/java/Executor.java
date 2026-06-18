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
        List<String> left =
            cmd.pipeline.get(0);

        List<String> right =
            cmd.pipeline.get(1);

        boolean leftBuiltin =
            Builtins.isBuiltin(left.get(0));

        boolean rightBuiltin =
            Builtins.isBuiltin(right.get(0));

        if (leftBuiltin) {

            java.io.ByteArrayOutputStream buffer =
                new java.io.ByteArrayOutputStream();

            java.io.PrintStream pipeOut =
                new java.io.PrintStream(buffer);

            BuiltinExecutor.execute(
                left,
                pipeOut
            );

            pipeOut.close();

            List<String> rightCommand =
                new ArrayList<>(right);

            rightCommand.set(
                0,
                PathResolver.findExecutable(
                    rightCommand.get(0)
                )
            );

            ProcessBuilder pb =
                new ProcessBuilder(rightCommand);

            Process process = pb.start();

            process.getOutputStream().write(
                buffer.toByteArray()
            );

            process.getOutputStream().close();

            process.getInputStream()
                .transferTo(System.out);

            process.waitFor();

            return;
        }

        if (rightBuiltin) {

            List<String> leftCommand =
                new ArrayList<>(left);

            leftCommand.set(
                0,
                PathResolver.findExecutable(
                    leftCommand.get(0)
                )
            );

            ProcessBuilder pb =
                new ProcessBuilder(leftCommand);

            Process process = pb.start();

            process.waitFor();

            BuiltinExecutor.execute(
                right,
                System.out
            );

            return;
        }

        // existing external|external code

        List<String> leftCommand =
            new ArrayList<>(left);

        List<String> rightCommand =
            new ArrayList<>(right);

        leftCommand.set(
            0,
            PathResolver.findExecutable(
                leftCommand.get(0)
            )
        );

        rightCommand.set(
            0,
            PathResolver.findExecutable(
                rightCommand.get(0)
            )
        );

        ProcessBuilder leftPB =
            new ProcessBuilder(leftCommand);

        ProcessBuilder rightPB =
            new ProcessBuilder(rightCommand);

        leftPB.redirectError(
            ProcessBuilder.Redirect.INHERIT
        );

        rightPB.redirectError(
            ProcessBuilder.Redirect.INHERIT
        );

        rightPB.redirectOutput(
            ProcessBuilder.Redirect.INHERIT
        );

        List<Process> processes =
            ProcessBuilder.startPipeline(
                List.of(leftPB, rightPB)
            );

        processes.get(
            processes.size() - 1
        ).waitFor();
    }
}
