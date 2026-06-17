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

    enum State {
        NORMAL,
        SINGLE_QUOTE,
        DOUBLE_QUOTE
    }

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

    static List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();

        StringBuilder current = new StringBuilder();

        State state = State.NORMAL;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            switch (state) {
                case NORMAL:
                    if (Character.isWhitespace(c)) {
                        if (current.length() > 0) {
                            tokens.add(current.toString());
                            current.setLength(0);
                        }
                    }
                    else if (c == '\'') {
                        state = State.SINGLE_QUOTE;
                    }
                    else if (c == '"') {
                        state = State.DOUBLE_QUOTE;
                    }
                    else if (c == '\\') {
                        if (i + 1 < input.length()) {
                            current.append(input.charAt(++i));
                        }
                    }
                    else {
                        current.append(c);
                    }
                    break;
                case SINGLE_QUOTE:
                    if (c == '\'') {
                        state = State.NORMAL;
                    }
                    else {
                        current.append(c);
                    }
                    break;
                case DOUBLE_QUOTE:
                    if (c == '"') {
                        state = State.NORMAL;
                    }
                    else if (c == '\\') {

                        if (i + 1 < input.length()) {

                            char next = input.charAt(i + 1);

                            if (next == '"' || next == '\\') {
                                current.append(next);
                                i++;
                            }
                            else {
                                current.append('\\');
                            }
                        }
                        else {
                            current.append('\\');
                        }
                    }
                    else {
                        current.append(c);
                    }
                    break;
            }
        }

        if (current.length() > 0) {
            tokens.add(current.toString());
        }

        return tokens;
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        Path currentDirectory = Path.of(System.getProperty("user.dir")).toAbsolutePath(), newDirectory;

        while (true) {
            System.out.print("$ ");

            String input = sc.nextLine();

            if (input.trim().isEmpty()) continue;

            List<String> tokens = tokenize(input);

            if (tokens.isEmpty()) continue;

            String command = tokens.get(0);

            if (command.equals("exit")) {
                break;
            }
            else if (command.equals("echo")) {
                for (int i = 1; i < tokens.size(); i++) {
                    if (i > 1) {
                        System.out.print(" ");
                    }
                    System.out.print(tokens.get(i));
                }
                System.out.println();
            }
            else if (command.equals("type")) {
                String target = tokens.size() > 1 ? tokens.get(1) : "";
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
                    System.out.println("cd: " + tokens.get(1) + ": No such file or directory");
                }
            }
            else {
                String executablePath = findExecutable(command);

                if (executablePath.isEmpty()) {
                    System.out.println(command + ": command not found");
                    continue;
                }
                
                List<String> processArgs = new ArrayList<>(tokens);

                ProcessBuilder pb = new ProcessBuilder(processArgs);

                pb.inheritIO();

                Process process = pb.start();

                process.waitFor();
            }
        }

        sc.close();
    }
}
