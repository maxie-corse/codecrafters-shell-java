import java.util.Scanner;

// locate executable files
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

// run external programs
import java.util.ArrayList;
import java.util.List;

public class Main {

    static boolean isBuiltin(String command) {
        return command.equals("exit") || command.equals("echo") || command.equals("type");
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

        while (true) {
            System.out.print("$ ");

            String input = sc.nextLine();

            if (input.trim().isEmpty()) continue;

            String[] tokens = input.trim().split("\\s+");

            String command = tokens[0];

            StringBuilder params = new StringBuilder();
            for (int i = 1; i < tokens.length; i++) {
                if (i > 1) params.append(" ");
                params.append(tokens[i]);
            }
            String arguments = params.toString();

            if (command.equals("exit")) {
                break;
            }
            else if (command.equals("echo")) {
                System.out.println(arguments);
            }
            else if (command.equals("type")) {
                if (isBuiltin(arguments)) {
                    System.out.println(arguments + " is a shell builtin");
                }
                else {
                    String executablePath = findExecutable(arguments);

                    if (!executablePath.isEmpty()) {
                        System.out.println(arguments + " is " + executablePath);
                    }
                    else {
                        System.out.println(arguments + ": not found");
                    }
                }
            }
            else {
                String executablePath = findExecutable(command);

                if (executablePath.isEmpty()) {
                    System.out.println(command + ": command not found");
                    continue;
                }
                
                List<String> processArgs = new ArrayList<>();

                for (String token : tokens) {
                    processArgs.add(token);
                }

                processArgs.set(0, executablePath);

                ProcessBuilder pb = new ProcessBuilder(processArgs);

                pb.inheritIO();

                Process process = pb.start();

                process.waitFor();
            }
        }

        sc.close();
    }
}
