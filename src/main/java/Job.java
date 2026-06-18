public class Job {
    public int jobNumber;
    public Process process;
    public String command;
    public boolean doneDisplayed;

    public Job(int jobNumber, Process process, String command) {
        this.jobNumber = jobNumber;
        this.process = process;
        this.command = command;
        this.doneDisplayed = false;
    }
}

