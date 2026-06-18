import java.util.Scanner;
import java.util.List;

import java.nio.file.Path;
import java.nio.file.Files;

import java.io.PrintStream;
import java.io.FileOutputStream;

public class Main {

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

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        Path currentDirectory = Path.of(System.getProperty("user.dir")).toAbsolutePath(), newDirectory;

        while (true) {
            System.out.print("$ ");

            String input = sc.nextLine();

            if (input.trim().isEmpty()) continue;

            List<String> tokens = Tokenizer.tokenize(input);

            if (tokens.isEmpty()) continue;

            ParsedCommand cmd = Parser.parse(tokens);

            tokens = cmd.args;

            String command = cmd.args.get(0);

            if (command.equals("exit")) {
                break;
            }
            else if (command.equals("echo")) {
                PrintStream out = getOutput(cmd);

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
                String target = tokens.size() > 1 ? tokens.get(1) : "";
                if (Builtins.isBuiltin(target)) {
                    PrintStream out = getOutput(cmd);

                    out.println(target + " is a shell builtin");

                    if (out != System.out) out.close();
                }
                else {
                    String executablePath = PathResolver.findExecutable(target);

                    if (!executablePath.isEmpty()) {
                        PrintStream out = getOutput(cmd);
                        System.out.println(target + " is " + executablePath);
                        if (out != System.out) out.close();
                    }
                    else {
                        PrintStream out = getOutput(cmd);
                        System.out.println(target + ": not found");
                        if (out != System.out) out.close();
                    }
                }
            }
            else if (command.equals("pwd")) {
                PrintStream out = getOutput(cmd);

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
                    PrintStream err = getError(cmd);

                    err.println("cd: " + tokens.get(1) + ": No such file or directory");

                    if (err != System.err) err.close();
                }
            }
            else {
                String executablePath = PathResolver.findExecutable(command);

                if (executablePath.isEmpty()) {
                    System.out.println(command + ": command not found");
                    continue;
                }
                
                Executor.executeExternal(cmd);
            }
        }

        sc.close();
    }
}
