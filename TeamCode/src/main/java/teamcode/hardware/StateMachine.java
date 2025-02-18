package teamcode.hardware;

import java.util.List;

public class StateMachine {
    private final List<State> sampleLine = List.of(State.BARRIER, State.HOVERG, State.GRABG, State.TRANSFER, State.SAMPLESCORE);
    private final List<State> specimenLine = List.of(State.HOVERW, State.GRABW, State.SPECIMENSCORE);
    private int currentIndex = -1;
    private State currentState = State.START;
    private List<State> currentLine = sampleLine;

    public void next() {
        int nextIndex = (currentIndex + 1) % currentLine.size();
        currentState = currentLine.get(nextIndex);
        currentIndex = nextIndex;
    }

    public void previous() {
        int nextIndex = (currentIndex - 1 + currentLine.size()) % currentLine.size();
        currentState = currentLine.get(nextIndex);
        currentIndex = nextIndex;
    }

    public void switchToSpecimen() {
        currentLine = specimenLine;

        currentState = currentLine.get(0);
        currentIndex = 0;

    }

    public void switchToSample() {
        currentLine = sampleLine;
        currentState = currentLine.get(0);
        currentIndex = 0;
    }

    public State getCurrentState() {
        return currentState;
    }

    public List<State> getCurrentLine() {
        return currentLine;
    }

    public void flush() {
        currentIndex = -1;
        currentState = State.START;
        currentLine = sampleLine;
    }

    public void startAuto() {
        currentLine = sampleLine;
        currentState = currentLine.get(3);
        currentIndex = 3;
    }

    public enum State {
        START, BARRIER, HOVERG, GRABG, TRANSFER, SAMPLESCORE, HOVERW, GRABW, SPECIMENSCORE
    }
}
