import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        String input, command, arguments;

        while (true) {
            System.out.print("$ ");

            input = sc.nextLine();
            int space_index = input.indexOf(' ');

            if (space_index == -1) {
                command = input;
                arguments = "";
            }
            else {
                command = input.substring(0, space_index);
                arguments = input.substring(space_index + 1);
            }

            if (command.equals("exit")) {
                break;
            }
            else if (command.equals("echo")) {
                System.out.println(arguments);
            }
            else if (command.equals("type")) {
                if (arguments.equals("exit") || arguments.equals("echo") || arguments.equals("type")) {
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
