import java.util.ArrayList;
import java.util.PriorityQueue;

public abstract class SchedulingAlgorithm {
    
    protected Scheduling scheduling;
    protected boolean step;
    protected boolean unitStep;

    protected int lastTime = -1;
    protected int currentTime = -1;
    protected PriorityQueue<Integer> nextTime = new PriorityQueue<Integer>();

    protected ArrayList<ProcessLog> processLog = new ArrayList<ProcessLog>();
    protected ArrayList<Process> completedProcesses = new ArrayList<Process>();
    protected ArrayList<Process> pendingProcesses = new ArrayList<Process>();
    protected PriorityQueue<Process> unArrivedProcesses = new PriorityQueue<Process>(new PriorityQueue<Process>(5,(a,b) -> a.arrivalTime - b.arrivalTime));

    public SchedulingAlgorithm(Scheduling scheduling){
        this.scheduling = scheduling;
        step = false;
        unitStep = false;
        initialize();
    }
    public SchedulingAlgorithm(Scheduling scheduling, boolean step, boolean unitStep){
        this(scheduling);
        this.step = step;
        this.unitStep = unitStep;
    }

    protected Integer nextTime(){
        return nextTime.peek();
    }

    private void initialize(){
        for (Process p : scheduling.processes){
            unArrivedProcesses.add(p);
            setNextTime(p.arrivalTime);
        }
    }

    public void simulate(){
        while (true){

            boolean complete = step();

            if (step){
                CLI.println("");
                CLI.pressEnter(true);
                CLI.in.nextLine();

                
            }

            if (complete){
                break;
            }
        }
        finishSimulation();
    }

    private void finishSimulation(){
        for (Process process : scheduling.processes){
            process.calcTTandWT();
        }
    }

    private boolean step(){
        lastTime = currentTime;
        if (unitStep){
            currentTime++;
            return preStepOperations(currentTime);
        }
        Integer next = nextTime.poll();
        CLI.log("Next Time: " + next);
        if (next == null){
            return true;
        }
        boolean complete = preStepOperations(next);
        currentTime = next;
        return complete;
    }
    
    private boolean preStepOperations(int time){
        addArrivingProcessesToPendingProcesses(time);

        return stepTo(time);
    }

    //Returns if scheduling is complete
    protected abstract boolean stepTo(int time);

    protected boolean queueEmpty(){
        return nextTime.size() == 0 && pendingProcesses.size() == 0;
    }

    protected void setNextTime(int t){
        boolean alreadyExist = timeExists(t);
        if (alreadyExist){
            //CLI.log("Same Time Already Exist!");
            return;
        }
        nextTime.add(t);
    }

    private void addArrivingProcessesToPendingProcesses(int currentTime){
        while (true){
            if (unArrivedProcesses.size() == 0){
                return;
            }
            Process process = unArrivedProcesses.peek();
            if (process.arrivalTime <= currentTime){
                addArrivedProcess(process);
                continue;
            }
            else{
                return;
            }
        }
    }

    protected void addArrivedProcess(Process process){
        unArrivedProcesses.poll();
        pendingProcesses.add(process);
    }

    protected void movePendingProcessToCompleted(Process process){
        if (pendingProcesses.contains(process)){
            completedProcesses.add(process);
        }
    }

    protected boolean timeExists(int t){
        for (Integer val : nextTime){
            if (val == t){
                return true;
            }
            if (val > t){
                break;
            }
        }
        return false;
    }

}

class ProcessLog{

    public Process process;
    public int startTime;
    public int endTime;
    public int remainingTime;

    public ProcessLog(Process process, int startTime, int endTime){
        this.process = process;
        this.startTime = startTime;
        this.endTime = endTime;
        remainingTime = process.remainingTime;
    }

    @Override
    public String toString(){
        return process.name + ": " + startTime + " -> " + endTime + "(R: " + remainingTime + ")";
    }
}
