package net.zachwalker.ballsort;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import net.zachwalker.ballsort.util.Constants;
import net.zachwalker.ballsort.entities.Ball;


public class BallSortScreen extends ScreenAdapter {

    private Viewport viewport;
    private ShapeRenderer renderer;
    private DelayedRemovalArray<Ball> balls;

    public BallSortScreen() {
        //the superclass doesn't do anything in its constructor either
        //the superclass is really just an interface
        super();
    }

    @Override
    public void show() {
        viewport = new ExtendViewport(Constants.WORLD_SIZE, Constants.WORLD_SIZE);
        renderer = new ShapeRenderer();
        balls = new DelayedRemovalArray<Ball>();
    }

    @Override
    public void render(float delta) {
        //update everything's position, visibility, scores, etc. all game logic.
        if (MathUtils.random() < delta * Constants.BALLS_PER_SECOND) {
            balls.add(new Ball());
        }

        for (Ball ball : balls) {
            ball.update(delta);
        }

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

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        //make sure to dispose of any texture / sound assets here in the future
    }
}
