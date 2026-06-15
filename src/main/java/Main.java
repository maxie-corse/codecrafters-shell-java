import java.util.Scanner;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

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
            int spaceIndex = input.indexOf(' ');

            String command = (spaceIndex == -1)? input : input.substring(0, spaceIndex);
            String arguments = (spaceIndex == -1)? "" : input.substring(spaceIndex + 1);

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
                System.out.println(command + ": command not found");
            }
        }

        sc.close();
    }
}
