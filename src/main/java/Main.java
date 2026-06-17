import java.util.Scanner;

// locate executable files
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

// run external programs
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main {

    private static final Set<String> BUILTINS = Set.of("exit", "echo", "type", "pwd", "cd");

    static boolean isBuiltin(String command) {
        return BUILTINS.contains(command);
    }

    static String findExecutable(String command) {
        String pathEnv = System.getenv("PATH");

        if (pathEnv == null) return "";

        String[] directories = pathEnv.split(File.pathSeparator);

        for (String directory : directories) {
            Path fullPath = Path.of(directory, command);

            if (Files.exists(fullPath) && Files.isRegularFile(fullPath) && Files.isExecutable(fullPath)) {
                return fullPath.toString();
            }
        }

        return "";
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        Path currentDirectory = Path.of(System.getProperty("user.dir")).toAbsolutePath();

        while (true) {
            System.out.print("$ ");

            String input = sc.nextLine();

            if (input.trim().isEmpty()) continue;

            String[] tokens = input.trim().split("\\s+");

            String command = tokens[0];

            if (command.equals("exit")) {
                break;
            }
            else if (command.equals("echo")) {
                for (int i = 1; i < tokens.length; i++) {
                    if (i > 1) {
                        System.out.print(" ");
                    }
                    System.out.print(tokens[i]);
                }
                System.out.println();
            }
            else if (command.equals("type")) {
                String target = tokens.length > 1 ? tokens[1] : "";
                if (isBuiltin(target)) {
                    System.out.println(target + " is a shell builtin");
                }
                else {
                    String executablePath = findExecutable(target);

                    if (!executablePath.isEmpty()) {
                        System.out.println(target + " is " + executablePath);
                    }
                    else {
                        System.out.println(target + ": not found");
                    }
                }
            }
            else if (command.equals("pwd")) {
                System.out.println(currentDirectory);
            }
            else if (command.equals("cd")) {
                if (tokens.length < 2) continue;

                Path newDirectory = Path.of(tokens[1]);

                if (Files.exists(newDirectory) && Files.isDirectory(newDirectory)) {
                    currentDirectory = newDirectory.toAbsolutePath();
                }
                else {
                    System.out.println("cd: " + tokens[1] + " No such file or directory");
                }
            }
            else {
                String executablePath = findExecutable(command);

                if (executablePath.isEmpty()) {
                    System.out.println(command + ": command not found");
                    continue;
                }
                
                List<String> processArgs = new ArrayList<>(List.of(tokens));

                ProcessBuilder pb = new ProcessBuilder(processArgs);

                pb.inheritIO();

                Process process = pb.start();

                process.waitFor();
            }
        }

        sc.close();
    }
}
