package cores;

public class TrafficLight {

    public enum State {
        RED, YELLOW, GREEN
    }

    private String id;
    private State state;

    // Duration for each state (in seconds)
    private int redDuration;
    private int greenDuration;
    private int yellowDuration;

    private double switchTimer; 					// time when the last switch occurred (in seconds)

    public TrafficLight(String id, int redDuration, int greenDuration, int yellowDuration) {
        this.id = id;
        this.state = State.RED; 					// default
        this.redDuration = redDuration;
        this.greenDuration = greenDuration;
        this.yellowDuration = yellowDuration;
        this.switchTimer = getCurrentTime();
    }

    private double getCurrentTime() {
        return System.currentTimeMillis() / 1000; 		// convert to seconds
    }

    public String getId() {
        return id;
    }

    public State getState() {
        return state;
    }

    public void setRedDuration(int sec) {
        this.redDuration = sec;
    }

    public void setGreenDuration(int sec) {
        this.greenDuration = sec;
    }

    public void setYellowDuration(int sec) {
        this.yellowDuration = sec;
    }

    public void toggleState() {
        switch (state) {
            case RED:
                state = State.GREEN;
                break;
            case GREEN:
                state = State.YELLOW;
                break;
            case YELLOW:
                state = State.RED;
                break;
        }
        switchTimer = getCurrentTime();
    }

    public void update() {
        double now = getCurrentTime();
        double elapsed = now - switchTimer;

        switch (state) {
            case RED:
                if (elapsed >= redDuration) {
                    state = State.GREEN;
                    switchTimer = now;
                }
                break;

            case GREEN:
                if (elapsed >= greenDuration) {
                    state = State.YELLOW;
                    switchTimer = now;
                }
                break;

            case YELLOW:
                if (elapsed >= yellowDuration) {
                    state = State.RED;
                    switchTimer = now;
                }
                break;
        }
    }
}