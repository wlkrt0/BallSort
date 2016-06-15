package net.zachwalker.ballsort;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import net.zachwalker.ballsort.entities.Bucket;
import net.zachwalker.ballsort.entities.Chute;
import net.zachwalker.ballsort.entities.TouchTargets;
import net.zachwalker.ballsort.entities.Valve;
import net.zachwalker.ballsort.overlays.Score;
import net.zachwalker.ballsort.util.Assets;
import net.zachwalker.ballsort.util.Constants;
import net.zachwalker.ballsort.entities.Ball;
import net.zachwalker.ballsort.util.Enums;


public class BallSortScreen extends ScreenAdapter {

    private Assets assets;
    private Viewport viewport;
    private ShapeRenderer renderer;
    private SpriteBatch batch;
    private DelayedRemovalArray<Ball> balls;
    private Array<Chute> chutes;
    private Array<Bucket> buckets;
    private Array<Valve> valves;
    private TouchTargets touchTargets;
    private Score score;
    private Enums.GameState gameState;
    private long currentScore;
    private long lastBallSpawnedTime;
    private float nextBallSpawnInterval;
    private int combo;
    private int level;
    private long levelChangeStartedTime;

    public BallSortScreen() {
        //the superclass doesn't do anything in its constructor either
        //the superclass is really just an interface
        super();
    }

    @Override
    public void show() {
        level = 19;
        assets = new Assets();
        viewport = new ExtendViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        renderer = new ShapeRenderer();
        batch = new SpriteBatch();
        balls = new DelayedRemovalArray<Ball>();
        chutes = new Array<Chute>();
        initializeChutes();
        buckets = new Array<Bucket>();
        //note that level must be set before initializing buckets since they use it to calc goal
        //note that this method initalizes static (empty outline) buckets in the Chutes array as well
        initializeBuckets();
        valves = new Array<Valve>();
        initializeValves();
        //note that touch targets must be initalized AFTER valves AND assets
        //also note that we need to pass the viewport to the touchtargets class since it will be
        //receiving touches and will need to unproject the touch input using the viewport
        //also need to pass in assets since it will be playing sounds when valves are toggled
        touchTargets = new TouchTargets(viewport, valves, assets);
        Gdx.input.setInputProcessor(touchTargets);
        score = new Score();
        gameState = Enums.GameState.PLAYING_NORMAL;
        lastBallSpawnedTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        //all game logic except touch inputs. update everything's position, visibility, scores, etc.
        switch(gameState) {
            case PLAYING_NORMAL:
                addNewBall();
                removeBalls(delta);
                break;
            case GOTO_NEXT_LEVEL:
                //reset balls, buckets, and combo. increment the level and start the timer.
                combo = 0;
                level += 1;
                balls.clear();
                for (Bucket bucket : buckets) {
                    bucket.reset(level * Constants.BUCKET_GOAL_PER_LEVEL);
                }
                levelChangeStartedTime = TimeUtils.nanoTime();
                gameState = Enums.GameState.STARTING_NEXT_LEVEL;
                break;
            case STARTING_NEXT_LEVEL:
                //just wait and increment the timer
                float elapsedSeconds = MathUtils.nanoToSec * (TimeUtils.nanoTime() - levelChangeStartedTime);
                if (elapsedSeconds >= Constants.LEVEL_START_DELAY) gameState = Enums.GameState.PLAYING_NORMAL;
                break;
            case GAME_OVER:
                //similar to GOTO_NEXT_LEVEL
                assets.sounds.gameOver.play();
                currentScore = 0;
                combo = 0;
                level = 1;
                balls.clear();
                for (Bucket bucket : buckets) {
                    bucket.reset(level * Constants.BUCKET_GOAL_PER_LEVEL);
                }
                levelChangeStartedTime = TimeUtils.nanoTime();
                gameState = Enums.GameState.STARTING_NEXT_LEVEL;
                break;
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

        for (Chute chute : chutes) {
            chute.render(renderer);
        }

        for (Bucket bucket : buckets) {
            bucket.render(renderer);
        }

        for (Valve valve : valves) {
            valve.render(renderer);
        }

        touchTargets.render(renderer);

        score.render(batch, currentScore, level, combo);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        score.resize(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    private void addNewBall() {
        float elapsedSeconds = MathUtils.nanoToSec * (TimeUtils.nanoTime() - lastBallSpawnedTime);
        if (elapsedSeconds >= nextBallSpawnInterval) {
            balls.add(new Ball());
            assets.sounds.newBall.play();
            lastBallSpawnedTime = TimeUtils.nanoTime();
            double x = (double) level;
            double nextMinBallSpawnInterval = Math.max(-0.01d * Math.pow(x, 3.0d) + 0.291d * Math.pow(x, 2.0d) - 2.796d * x + 10.516d, Constants.BALL_SPAWN_INTERVAL_MIN);
            double nextMaxBallSpawnInterval = Math.max(8.849d * Math.pow(Math.E, (x * -0.094d)), Constants.BALL_SPAWN_INTERVAL_MIN);
            nextBallSpawnInterval = MathUtils.random((float) nextMinBallSpawnInterval, (float) nextMaxBallSpawnInterval);
        }
    }

    private void removeBalls(float delta) {
        balls.begin();
        for (Ball ball : balls) {
            //note that we need to pass the valve to each ball so that the balls can check whether
            //they've arrived at a valve which is open
            ball.update(delta, valves);
            if (ball.ballState == Enums.BallState.CAUGHT) {
                combo += 1;
                currentScore += combo;
                playComboSound();
                balls.removeValue(ball, false);
                switch (ball.fellThru) {
                    case LEFT_VALVE:
                        if (buckets.get(0).caughtBall()) assets.sounds.full.play();
                        break;
                    case RIGHT_VALVE:
                        if (buckets.get(1).caughtBall()) assets.sounds.full.play();
                        break;
                    case END:
                        if (buckets.get(2).caughtBall()) assets.sounds.full.play();
                        break;
                }
                //if all three buckets are now full, start a new level
                if (buckets.get(0).isFull() && buckets.get(1).isFull() && buckets.get(2).isFull()) {
                    assets.sounds.newLevel.play();
                    gameState = Enums.GameState.GOTO_NEXT_LEVEL;
                }
            } else if (ball.ballState == Enums.BallState.MISSED) {
                assets.sounds.missed.play();
                balls.removeValue(ball, false);
                combo = 0;
                currentScore = Math.max(0, currentScore - 25);
                switch (ball.fellThru) {
                    case LEFT_VALVE:
                        if (buckets.get(0).missedBall()) gameState = Enums.GameState.GAME_OVER;
                        break;
                    case RIGHT_VALVE:
                        if (buckets.get(1).missedBall()) gameState = Enums.GameState.GAME_OVER;
                        break;
                    case END:
                        if (buckets.get(2).missedBall()) gameState = Enums.GameState.GAME_OVER;
                        break;
                }
            }
        }
        balls.end();
    }

    private void playComboSound() {
        switch (combo) {
            case 1:
                assets.sounds.caught1.play();
                break;
            case 2:
                assets.sounds.caught2.play();
                break;
            case 3:
                assets.sounds.caught3.play();
                break;
            case 4:
                assets.sounds.caught4.play();
                break;
            case 5:
                assets.sounds.caught5.play();
                break;
            case 6:
                assets.sounds.caught6.play();
                break;
            case 7:
                assets.sounds.caught7.play();
                break;
            default:
                assets.sounds.caught8.play();
                break;
        }
    }

    private void initializeValves() {
        //draw the left valve
        valves.add(new Valve(Constants.CHUTE_MARGIN + Constants.RAMP_WIDTH));
        //draw the right valve
        valves.add(new Valve(Constants.CHUTE_MARGIN + (2.0f * Constants.RAMP_WIDTH) + Constants.VALVE_WIDTH));
    }

    private void initializeBuckets() {
        //NOTE: this method initalizes static (empty outline) buckets in the Chutes array as well
        //draw the left DYNAMIC bucket (progress bar)
        float leftBucketX = Constants.CHUTE_MARGIN + Constants.RAMP_WIDTH + Constants.BALL_SIZE -
                (Constants.BUCKET_WIDTH / 2.0f);

        buckets.add(new Bucket(leftBucketX, Constants.BUCKET_LEFT_COLOR, level * Constants.BUCKET_GOAL_PER_LEVEL));

        //draw the middle DYNAMIC bucket (progress bar)
        float middleBucketX = Constants.CHUTE_MARGIN + (2.0f * Constants.RAMP_WIDTH) +
                Constants.VALVE_WIDTH + Constants.BALL_SIZE - (Constants.BUCKET_WIDTH / 2.0f);

        buckets.add(new Bucket(middleBucketX, Constants.BUCKET_MIDDLE_COLOR, level * Constants.BUCKET_GOAL_PER_LEVEL));

        //draw the right DYNAMIC bucket (progress bar)
        float rightBucketX = Constants.CHUTE_MARGIN + (3.0f * Constants.RAMP_WIDTH) +
                (2.0f * Constants.VALVE_WIDTH) + Constants.BALL_SIZE - (Constants.BUCKET_WIDTH / 2.0f);

        buckets.add(new Bucket(rightBucketX, Constants.BUCKET_RIGHT_COLOR, level * Constants.BUCKET_GOAL_PER_LEVEL));

        //draw the left STATIC (empty outline) bucket
        chutes.add(new Chute(
                leftBucketX,
                0.0f,
                Constants.BUCKET_WIDTH,
                Constants.BUCKET_HEIGHT,
                ShapeType.Line,
                Constants.BUCKET_LEFT_COLOR)
        );
        //draw the middle STATIC (empty outline) bucket
        chutes.add(new Chute(
                middleBucketX,
                0.0f,
                Constants.BUCKET_WIDTH,
                Constants.BUCKET_HEIGHT,
                ShapeType.Line,
                Constants.BUCKET_MIDDLE_COLOR)
        );
        //draw the right STATIC (empty outline) bucket
        chutes.add(new Chute(
                rightBucketX,
                0.0f,
                Constants.BUCKET_WIDTH,
                Constants.BUCKET_HEIGHT,
                ShapeType.Line,
                Constants.BUCKET_RIGHT_COLOR)
        );
    }

    private void initializeChutes() {
        //draw the vertical chute
        chutes.add(new Chute(
                Constants.CHUTE_MARGIN,
                0.0f,
                Constants.CHUTE_WIDTH,
                Constants.CHUTE_HEIGHT,
                ShapeType.Line,
                Color.GRAY)
        );
        //draw the left horizontal chute
        chutes.add(new Chute(
                Constants.CHUTE_MARGIN,
                Constants.CHUTE_HEIGHT,
                Constants.RAMP_WIDTH,
                Constants.CHUTE_WIDTH,
                ShapeType.Line,
                Color.GRAY)
        );
        //draw the middle horizontal chute
        chutes.add(new Chute(
                Constants.CHUTE_MARGIN + Constants.RAMP_WIDTH + Constants.VALVE_WIDTH,
                Constants.CHUTE_HEIGHT,
                Constants.RAMP_WIDTH,
                Constants.CHUTE_WIDTH,
                ShapeType.Line,
                Color.GRAY)
        );
        //draw the right horizontal chute
        chutes.add(new Chute(
                Constants.CHUTE_MARGIN + (2.0f * (Constants.RAMP_WIDTH + Constants.VALVE_WIDTH)),
                Constants.CHUTE_HEIGHT,
                Constants.RAMP_WIDTH,
                Constants.CHUTE_WIDTH,
                ShapeType.Line,
                Color.GRAY)
        );
    }

    private void gameOver() {
        balls.end();
        assets.sounds.gameOver.play();
        this.show();
    }
}
