import java.util.Scanner;

public class Main {

    static boolean isBuiltin(String command) {
        return command.equals("exit") || command.equals("echo") || command.equals("type");
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("$ ");

            String input = sc.nextLine();
            int space_index = input.indexOf(' ');

            String command = (space_index == -1)? input : input.substring(0, space_index);
            String arguments = (space_index == -1)? "" : input.substring(space_index + 1);

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
                    System.out.println(arguments + ": not found");
                }
            }
            else {
                System.out.println(command + ": command not found");
            }
        }

        sc.close();
    }
}
