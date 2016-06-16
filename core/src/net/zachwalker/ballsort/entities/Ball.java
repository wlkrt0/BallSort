package net.zachwalker.ballsort.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.zachwalker.ballsort.BallSortScreen;
import net.zachwalker.ballsort.util.Constants;
import net.zachwalker.ballsort.util.Enums;


public class Ball {

    private Enums.BallType ballType;
    public Enums.BallState ballState;
    public Enums.BallFellThru fellThru;
    private Color ballColor;
    private Vector2 position;
    private Vector2 velocity;
    private BallSortScreen ballSortScreen;

    //note that we are passing the BallSortScreen class to the ball constructor so that balls can get and set powerup status
    public Ball(BallSortScreen ballSortScreen) {
        this.ballSortScreen = ballSortScreen;
        //each new ball gets a random type. regular balls are equal probablity. powerup is rare.
        float randomNumber = MathUtils.random();
        if (randomNumber < 0.04f) {
            ballType = Enums.BallType.POWERUP;
        } else if (randomNumber >= 0.04f && randomNumber < 0.36f) {
            ballType = Enums.BallType.RED;
        } else if (randomNumber >= 0.36f && randomNumber < 0.68f) {
            ballType = Enums.BallType.YELLOW;
        } else {
            ballType = Enums.BallType.BLUE;
        }

        //set the ball's color based on its type
        switch(ballType) {
            case RED:
                ballColor = Color.RED;
                break;
            case YELLOW:
                ballColor = Color.YELLOW;
                break;
            case BLUE:
                ballColor = Color.BLUE;
                break;
            case POWERUP:
                ballColor = Color.WHITE;
                break;
        }

        //each new ball gets the same starting position with zero velocity and ballState = CHUTE
        position = new Vector2(Constants.CHUTE_MARGIN + Constants.BALL_SIZE, Constants.BALL_SIZE);
        velocity = new Vector2();
        ballState = Enums.BallState.CHUTE;
        fellThru = Enums.BallFellThru.NONE;
    }

    public void update(float delta, Array<Valve> valves) {
        if (ballType == Enums.BallType.POWERUP) {
            ballColor.set(MathUtils.random(), MathUtils.random(), MathUtils.random(), MathUtils.random());
        }
        switch (ballState) {
            case CHUTE:
                moveInChute(delta);
                break;
            case RAMP:
                moveOnRamp(delta, valves);
                break;
            case FALLING:
                moveWhileFalling(delta);
                break;
        }
    }

    public void render(ShapeRenderer renderer) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(ballColor);
        renderer.circle(position.x, position.y, Constants.BALL_SIZE);
        renderer.end();
    }

    private void moveInChute(float delta) {
        velocity.x = 0.0f;
        velocity.y = Constants.BALL_SPEED;
        position.mulAdd(velocity, delta);
        //start moving to the right if the ball has reached the top of the chute
        if (position.y > Constants.CHUTE_HEIGHT + Constants.BALL_SIZE) {
            ballState = Enums.BallState.RAMP;
        }
    }

    private void moveOnRamp(float delta, Array<Valve> valves) {
        velocity.x = Constants.BALL_SPEED;
        velocity.y = 0.0f;
        position.mulAdd(velocity, delta);
        //start falling if the ball is on top of an open valve or at the end of the ramp
        if (fellThroughGap(valves)) {
            ballState = Enums.BallState.FALLING;
        }
    }

    private void moveWhileFalling(float delta) {
        velocity.x = 0.0f;
        velocity.y -= Constants.GRAVITY;
        position.mulAdd(velocity, delta);
        //flag the ball for removal by BallSortScreen if it's caught or missed
        //BallSortScreen handles any scoring that's needed
        if (position.y <= 0.0f) {
            if (inCorrectBucket()) {
                ballState = Enums.BallState.CAUGHT;
            } else {
                ballState = Enums.BallState.MISSED;
            }
        }
    }

    //TODO clean up this method
    private boolean fellThroughGap(Array<Valve> valves) {
        //if it fell through the left valve or a powerup says it should
        if (position.x > (Constants.CHUTE_MARGIN + Constants.RAMP_WIDTH + Constants.BALL_SIZE) &&
                position.x < (Constants.CHUTE_MARGIN + Constants.RAMP_WIDTH + Constants.VALVE_WIDTH)) {
            if (ballSortScreen.isPowerupActive()) {
                if (ballColor.equals(Constants.BUCKET_LEFT_COLOR)) {
                    if (valves.get(0).valveState == Enums.ValveState.CLOSED) valves.get(0).switchValveState();
                    fellThru = Enums.BallFellThru.LEFT_VALVE;
                    return true;
                } else {
                    if (valves.get(0).valveState == Enums.ValveState.OPEN) valves.get(0).switchValveState();
                    return false;
                }
            } else {
                if (valves.get(0).valveState == Enums.ValveState.OPEN) {
                    fellThru = Enums.BallFellThru.LEFT_VALVE;
                    return true;
                } else {
                    return false;
                }
            }
        }
        //if it fell through the right valve or a powerup says it should
        else if (position.x > (Constants.CHUTE_MARGIN + (2.0f * Constants.RAMP_WIDTH) + Constants.VALVE_WIDTH + Constants.BALL_SIZE) &&
                position.x < (Constants.CHUTE_MARGIN + (2.0f * Constants.RAMP_WIDTH) + (2.0f * Constants.VALVE_WIDTH))) {
            if (ballSortScreen.isPowerupActive()) {
                if (ballColor.equals(Constants.BUCKET_MIDDLE_COLOR)) {
                    if (valves.get(1).valveState == Enums.ValveState.CLOSED) valves.get(1).switchValveState();
                    fellThru = Enums.BallFellThru.RIGHT_VALVE;
                    return true;
                } else {
                    if (valves.get(1).valveState == Enums.ValveState.OPEN) valves.get(1).switchValveState();
                    return false;
                }
            } else {
                if (valves.get(1).valveState == Enums.ValveState.OPEN) {
                    fellThru = Enums.BallFellThru.RIGHT_VALVE;
                    return true;
                } else {
                    return false;
                }
            }
        }
        //if it fell off the end of the ramp
        else if (position.x > (Constants.CHUTE_MARGIN + (3.0f * Constants.RAMP_WIDTH) + (2.0f * Constants.VALVE_WIDTH) + Constants.BALL_SIZE)) {
            fellThru = Enums.BallFellThru.END;
            return true;
        }
        //if it hasn't fallen off of anything yet
        else {
            return false;
        }
    }

    private boolean inCorrectBucket() {
        //powerups are always in the correct bucket
        if (ballType == Enums.BallType.POWERUP) {
            ballSortScreen.startPowerup();
            return true;
        } else {
            switch (fellThru) {
                case NONE:
                    return false;
                case LEFT_VALVE:
                    return ballColor.equals(Constants.BUCKET_LEFT_COLOR);
                case RIGHT_VALVE:
                    return ballColor.equals(Constants.BUCKET_MIDDLE_COLOR);
                case END:
                    return ballColor.equals(Constants.BUCKET_RIGHT_COLOR);
                default:
                    return false;
            }
        }
    }

}
