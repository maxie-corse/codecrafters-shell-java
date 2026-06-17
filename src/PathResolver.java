public class PathResolver {
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
}
