package net.zachwalker.ballsort.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import net.zachwalker.ballsort.util.Constants;
import net.zachwalker.ballsort.util.Enums;


public class Ball {

    private Enums.BallType ballType;
    private Vector2 position;
    private Vector2 velocity;

    public Ball() {
        //each new ball gets a random type with equal probability
        Enums.BallType[] ballTypes = Enums.BallType.values();
        int numBallTypes = ballTypes.length;
        ballType = ballTypes[MathUtils.random(numBallTypes - 1)];

        //each new ball gets the same starting position with zero velocity
        position = new Vector2(100, 100);
        velocity = new Vector2();
    }

    public void update(float delta) {
        velocity.y -= Constants.GRAVITY;
        position.mulAdd(velocity, delta);
    }

    public void render(ShapeRenderer renderer) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(getBallColor());
        renderer.circle(position.x, position.y, Constants.BALL_SIZE);
        renderer.end();
    }

    private Color getBallColor() {
        switch(ballType) {
            case RED:
                return Color.RED;
            case YELLOW:
                return Color.YELLOW;
            case BLUE:
                return Color.BLUE;
            default:
                return Color.WHITE;
        }
    }

}
