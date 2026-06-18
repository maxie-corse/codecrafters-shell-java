import java.util.Set;

public class Builtins {
    private static final Set<String> BUILTINS = Set.of("exit", "echo", "type", "pwd", "cd", "jobs");

    public static boolean isBuiltin(String command) {
        return BUILTINS.contains(command);
    }
}
