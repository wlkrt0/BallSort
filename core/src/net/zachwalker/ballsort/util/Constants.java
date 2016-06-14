package net.zachwalker.ballsort.util;

import com.badlogic.gdx.graphics.Color;


public final class Constants {
    public static final int WORLD_SIZE = 200;
    public static final Color BACKGROUND_COLOR = Color.BLACK;
    public static final float GRAVITY = 7.0f;

    public static final float BALL_SIZE = 5.0f;
    public static final float BALL_SPAWN_INTERVAL_MIN = 1;
    public static final float BALL_SPAWN_INTERVAL_MAX = 4;
    public static final float BALL_SPEED = 20.0f;

    public static final float CHUTE_WIDTH = BALL_SIZE * 2.0f;
    public static final float CHUTE_HEIGHT = 100.0f;
    public static final float CHUTE_MARGIN = 10.0f;

    public static final float RAMP_WIDTH = 50.0f;

    public static final float VALVE_WIDTH = BALL_SIZE * 2.0f;
    public static final float VALVE_HEIGHT = VALVE_WIDTH / 4.0f;
    public static final float VALVE_ROTATION = 270.0f;

    public static final float BUCKET_WIDTH = BALL_SIZE * 4.0f;
    public static final float BUCKET_HEIGHT = BALL_SIZE * 2.0f;

    public static final String SCORE_LABEL = "Score: ";
    public static final float SCORE_FONT_SCALE = 480.0f;
}
