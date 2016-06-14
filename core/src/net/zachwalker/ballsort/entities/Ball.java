package net.zachwalker.ballsort.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import net.zachwalker.ballsort.util.Constants;
import net.zachwalker.ballsort.util.Enums;


public class Ball {

    private boolean inPlay;
    private Enums.BallType ballType;
    public Enums.BallState ballState;
    private Color ballColor;
    private Vector2 position;
    private Vector2 velocity;

    public Ball() {
        inPlay = true;
        //each new ball gets a random type with equal probability
        Enums.BallType[] ballTypes = Enums.BallType.values();
        int numBallTypes = ballTypes.length;
        ballType = ballTypes[MathUtils.random(numBallTypes - 1)];

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
            default:
                ballColor = Color.WHITE;
                break;
        }

        //each new ball gets the same starting position with zero velocity and ballState = CHUTE
        position = new Vector2(Constants.CHUTE_MARGIN + Constants.BALL_SIZE, Constants.BALL_SIZE);
        velocity = new Vector2();
        ballState = Enums.BallState.CHUTE;
    }

    public void update(float delta, Valve valve) {
        switch (ballState) {
            case CHUTE:
                moveInChute(delta);
                break;
            case RAMP:
                moveOnRamp(delta, valve);
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
        if (position.y > Constants.CHUTE_HEIGHT + Constants.BALL_SIZE) {
            ballState = Enums.BallState.RAMP;
        }
    }

    private void moveOnRamp(float delta, Valve valve) {
        velocity.x = Constants.BALL_SPEED;
        velocity.y = 0.0f;
        position.mulAdd(velocity, delta);
        //check if the ball is on top of a valve AND the valve is currently open
        if (isInValve() && valve.valveState == Enums.ValveState.OPEN) {
            ballState = Enums.BallState.FALLING;
        }
    }

    private void moveWhileFalling(float delta) {
        velocity.x = 0.0f;
        velocity.y -= Constants.GRAVITY;
        position.mulAdd(velocity, delta);
        if (position.y <= Constants.BUCKET_HEIGHT) {
            if (isInValve()) {
                ballState = Enums.BallState.CAUGHT;

            } else {
                ballState = Enums.BallState.MISSED;
            }
        }
    }

    private boolean isInValve() {
        return (position.x > (Constants.CHUTE_MARGIN + Constants.RAMP_WIDTH + Constants.BALL_SIZE)) &&
                (position.x < (Constants.CHUTE_MARGIN + Constants.RAMP_WIDTH + Constants.VALVE_WIDTH));
    }

}
