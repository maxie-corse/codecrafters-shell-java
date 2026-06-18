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

        if (cmd.pipeline.size() == 2) {

            List<String> left = cmd.pipeline.get(0);
            List<String> right = cmd.pipeline.get(1);

            boolean leftBuiltin =
                Builtins.isBuiltin(left.get(0));

            boolean rightBuiltin =
                Builtins.isBuiltin(right.get(0));

            if (leftBuiltin && !rightBuiltin) {

                java.io.ByteArrayOutputStream buffer =
                    new java.io.ByteArrayOutputStream();

                java.io.PrintStream pipeOut =
                    new java.io.PrintStream(buffer);

                BuiltinExecutor.execute(left, pipeOut);

                pipeOut.close();

                List<String> processCommand =
                    new ArrayList<>(right);

                String executable =
                    PathResolver.findExecutable(
                        processCommand.get(0)
                    );

                processCommand.set(0, executable);

                Process process =
                    new ProcessBuilder(processCommand)
                        .start();

                process.getOutputStream()
                    .write(buffer.toByteArray());

                process.getOutputStream().close();

                process.getInputStream()
                    .transferTo(System.out);

                process.waitFor();

                return;
            }

            if (!leftBuiltin && rightBuiltin) {

                BuiltinExecutor.execute(
                    right,
                    System.out
                );

                return;
            }
        }

        executeExternalPipeline(cmd);
    }

    private static void executeExternalPipeline(ParsedCommand cmd) throws Exception {
        List<ProcessBuilder> builders =
            new ArrayList<>();

        for (List<String> command : cmd.pipeline) {

            List<String> processCommand =
                new ArrayList<>(command);

            String executable =
                PathResolver.findExecutable(
                    processCommand.get(0)
                );

            if (!executable.isEmpty()) {
                processCommand.set(0, executable);
            }

            ProcessBuilder pb =
                new ProcessBuilder(processCommand);

            pb.redirectError(
                ProcessBuilder.Redirect.INHERIT
            );

            builders.add(pb);
        }

        builders.get(
            builders.size() - 1
        ).redirectOutput(
            ProcessBuilder.Redirect.INHERIT
        );

        List<Process> processes =
            ProcessBuilder.startPipeline(builders);

        processes.get(
            processes.size() - 1
        ).waitFor();
    }
}
