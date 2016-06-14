package net.zachwalker.ballsort;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.zachwalker.ballsort.entities.Bucket;
import net.zachwalker.ballsort.entities.Chute;
import net.zachwalker.ballsort.entities.Ramp;
import net.zachwalker.ballsort.entities.Valve;
import net.zachwalker.ballsort.overlays.Score;
import net.zachwalker.ballsort.util.Constants;
import net.zachwalker.ballsort.entities.Ball;
import net.zachwalker.ballsort.util.Enums;


public class BallSortScreen extends ScreenAdapter {

    private Viewport viewport;
    private ShapeRenderer renderer;
    private SpriteBatch batch;
    private DelayedRemovalArray<Ball> balls;
    private Chute chute;
    private Ramp ramp;
    private Valve valve;
    private Bucket bucket;
    private Score score;
    private long currentScore;
    private long lastBallSpawnedTime;
    private float nextBallSpawnInterval;

    public BallSortScreen() {
        //the superclass doesn't do anything in its constructor either
        //the superclass is really just an interface
        super();
    }

    @Override
    public void show() {
        viewport = new ExtendViewport(Constants.WORLD_SIZE, Constants.WORLD_SIZE);
        renderer = new ShapeRenderer();
        batch = new SpriteBatch();
        balls = new DelayedRemovalArray<Ball>();
        chute = new Chute();
        ramp = new Ramp();
        //note that we need to pass the viewport to the valve since it will be receiving touches
        //and will need to unproject the touch input using the viewport
        valve = new Valve(viewport);
        bucket = new Bucket();
        score = new Score();
        Gdx.input.setInputProcessor(valve);
        lastBallSpawnedTime = TimeUtils.nanoTime();
        nextBallSpawnInterval = Constants.BALL_SPAWN_INTERVAL_MIN;
    }

    @Override
    public void render(float delta) {
        //update everything's position, visibility, scores, etc. all game logic.
        float elapsedSeconds = MathUtils.nanoToSec * (TimeUtils.nanoTime() - lastBallSpawnedTime);
        if (elapsedSeconds >= nextBallSpawnInterval) {
            balls.add(new Ball());
            lastBallSpawnedTime = TimeUtils.nanoTime();
            nextBallSpawnInterval = MathUtils.random(Constants.BALL_SPAWN_INTERVAL_MIN, Constants.BALL_SPAWN_INTERVAL_MAX);
        }

        balls.begin();
        for (Ball ball : balls) {
            //note that we need to pass the valve to each ball so that the balls can check whether
            //they've arrived at a valve which is open
            ball.update(delta, valve);
            if (ball.ballState == Enums.BallState.CAUGHT) {
                currentScore += 1;
                balls.removeValue(ball, false);
            } else if (ball.ballState == Enums.BallState.MISSED) {
                balls.removeValue(ball, false);
            }
        }
        balls.end();

        //clear the screen in preparation for rendering the updated positions / visibility of objects
        Gdx.gl.glClearColor(
                Constants.BACKGROUND_COLOR.r,
                Constants.BACKGROUND_COLOR.g,
                Constants.BACKGROUND_COLOR.b,
                Constants.BACKGROUND_COLOR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render everything that is still visible in its updated position
        viewport.apply();
        renderer.setProjectionMatrix(viewport.getCamera().combined);

        for (Ball ball : balls) {
            ball.render(renderer);
        }

        chute.render(renderer);
        ramp.render(renderer);
        valve.render(renderer);
        bucket.render(renderer);

        score.render(batch, currentScore);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        score.resize(width, height);
    }

    @Override
    public void dispose() {
        //make sure to dispose of any texture / sound assets here in the future
    }
}
