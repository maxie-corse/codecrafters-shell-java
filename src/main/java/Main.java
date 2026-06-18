import java.util.Scanner;
import java.util.List;

import java.nio.file.Path;
import java.nio.file.Files;

import java.io.PrintStream;

public class Main {

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        Path currentDirectory = Path.of(System.getProperty("user.dir")).toAbsolutePath(), newDirectory;

        while (true) {
            JobManager.reapCompletedJobs();

            System.out.print("$ ");

            String input = sc.nextLine();

            if (input.trim().isEmpty()) continue;

            List<String> tokens = Tokenizer.tokenize(input);

            ParsedCommand cmd = Parser.parse(tokens);

            tokens = cmd.args;

            if (tokens.isEmpty()) continue;

            String command = tokens.get(0);

            if (cmd.pipeline != null) {
                Executor.executePipeline(cmd);
                continue;
            }

            if (command.equals("exit")) {
                break;
            }
            else if (command.equals("echo")) {
                RedirectUtils.ensureErrorFileExists(cmd);
                PrintStream out = RedirectUtils.getOutput(cmd);

                for (int i = 1; i < tokens.size(); i++) {

                    if (i > 1) {
                        out.print(" ");
                    }

                    out.print(tokens.get(i));
                }

                out.println();

                if (out != System.out) {
                    out.close();
                }
            }
            else if (command.equals("type")) {
                RedirectUtils.ensureErrorFileExists(cmd);
                String target = tokens.size() > 1 ? tokens.get(1) : "";
                if (Builtins.isBuiltin(target)) {
                    PrintStream out = RedirectUtils.getOutput(cmd);

                    out.println(target + " is a shell builtin");

                    if (out != System.out) out.close();
                }
                else {
                    String executablePath = PathResolver.findExecutable(target);

                    if (!executablePath.isEmpty()) {
                        PrintStream out = RedirectUtils.getOutput(cmd);
                        out.println(target + " is " + executablePath);
                        if (out != System.out) out.close();
                    }
                    else {
                        PrintStream out = RedirectUtils.getOutput(cmd);
                        out.println(target + ": not found");
                        if (out != System.out) out.close();
                    }
                }
            }
            else if (command.equals("pwd")) {
                RedirectUtils.ensureErrorFileExists(cmd);
                PrintStream out = RedirectUtils.getOutput(cmd);

                out.println(currentDirectory);

                if (out != System.out) out.close();
            }
            else if (command.equals("cd")) {
                if (tokens.size() < 2) continue;

                if (tokens.get(1).equals("~")) {
                    newDirectory = Path.of(System.getenv("HOME"));
                }
                else if (Path.of(tokens.get(1)).isAbsolute()) {
                    newDirectory = Path.of(tokens.get(1));
                }
                else {
                    newDirectory = currentDirectory.resolve(tokens.get(1));
                }

                newDirectory = newDirectory.normalize();

                if (Files.exists(newDirectory) && Files.isDirectory(newDirectory)) {
                    currentDirectory = newDirectory.toAbsolutePath();
                }
                else {
                    PrintStream err = RedirectUtils.getError(cmd);

                    err.println("cd: " + tokens.get(1) + ": No such file or directory");

                    if (err != System.err) err.close();
                }
            }
            else if (command.equals("jobs")) {
                JobManager.printAndReapJobs();
            }
            else {
                String executablePath = PathResolver.findExecutable(command);

                if (executablePath.isEmpty()) {
                    PrintStream err = RedirectUtils.getError(cmd);
                    System.out.println(command + ": command not found");
                    if (err != System.err) {
                        err.close();
                    }
                    continue;
                }
                
                Executor.executeExternal(cmd);
            }
        }

        sc.close();
    }
}
