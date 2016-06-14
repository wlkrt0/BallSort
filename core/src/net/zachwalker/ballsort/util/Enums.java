package net.zachwalker.ballsort.util;


public class Enums {

    public enum BallType {
        RED,
        YELLOW,
        BLUE
    }

    public enum BallState {
        CHUTE,
        RAMP,
        FALLING,
        CAUGHT,
        MISSED
    }

    public enum BallFellThru {
        NONE,
        LEFT_VALVE,
        RIGHT_VALVE,
        END
    }

    public enum ValveState {
        OPEN,
        CLOSED
    }

}
