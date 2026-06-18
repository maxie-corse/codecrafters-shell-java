public class JobManager {
    private static int nextJobNumber = 1;

    public static int allocateJobNumber() {
        return nextJobNumber++;
    }
}
