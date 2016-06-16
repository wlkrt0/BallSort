package net.zachwalker.ballsort.util;


public class Enums {

    public enum GameState {
        GOTO_NEXT_LEVEL,
        STARTING_NEXT_LEVEL,
        PLAYING_NORMAL,
        GAME_OVER
    }

    public enum ValveState {
        OPEN,
        CLOSED
    }

    public enum BallType {
        RED,
        YELLOW,
        BLUE,
        POWERUP
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

}
