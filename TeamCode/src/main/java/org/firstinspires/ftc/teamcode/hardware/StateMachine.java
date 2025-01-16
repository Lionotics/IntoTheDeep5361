package org.firstinspires.ftc.teamcode.hardware;

import java.util.List;

public class StateMachine {
    private final List<State> sampleLine = List.of(State.BARRIER1, State.HOVERG, State.GRABG, State.BARRIER2, State.TRANSFER, State.SAMPLESCORE);
    private final List<State> specimenLine = List.of(State.DROP, State.HOVERW, State.GRABW, State.SPECIMENSCORE);
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
        if (currentState == State.TRANSFER || currentState == State.GRABG || currentState == State.BARRIER2) {
            currentState = currentLine.get(0);
            currentIndex = 0;
        } else {
            currentState = currentLine.get(1);
            currentIndex = 1;
        }
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

    public enum State {
        START, BARRIER1, HOVERG, GRABG, BARRIER2, TRANSFER, SAMPLESCORE, DROP, HOVERW, GRABW, SPECIMENSCORE
    }
}
