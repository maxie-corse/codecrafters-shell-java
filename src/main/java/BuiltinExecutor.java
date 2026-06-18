import java.io.PrintStream;
import java.util.List;

public class BuiltinExecutor {

    public static void execute(
        List<String> args,
        PrintStream out
    ) {

        String command = args.get(0);

        if (command.equals("echo")) {

            for (int i = 1; i < args.size(); i++) {

                if (i > 1) {
                    out.print(" ");
                }

                out.print(args.get(i));
            }

            out.println();
        }

        else if (command.equals("type")) {

            String target =
                args.size() > 1 ? args.get(1) : "";

            if (Builtins.isBuiltin(target)) {

                out.println(
                    target +
                    " is a shell builtin"
                );
            }
            else {

                String executable =
                    PathResolver.findExecutable(target);

                if (!executable.isEmpty()) {

                    out.println(
                        target +
                        " is " +
                        executable
                    );
                }
                else {

                    out.println(
                        target +
                        ": not found"
                    );
                }
            }
        }
    }
}
