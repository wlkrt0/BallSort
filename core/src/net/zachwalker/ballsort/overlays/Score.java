package net.zachwalker.ballsort.overlays;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import net.zachwalker.ballsort.util.Constants;


public class Score {
    private Viewport viewport;
    private BitmapFont font;

    public Score() {
        viewport = new ScreenViewport();
        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    public void render(SpriteBatch batch, long currentScore, long highScore, int level, int combo) {
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        //always show the high score and current score
        String scoreText = Constants.SCORE_LABEL + currentScore + "\n"
                + Constants.HIGH_SCORE_LABEL + highScore;

        font.draw(
                batch,
                scoreText,
                viewport.getWorldWidth() - Constants.CHUTE_MARGIN,
                viewport.getWorldHeight() - Constants.CHUTE_MARGIN,
                0,
                Align.right,
                false
        );

        //always draw the level identifier
        String levelText = Constants.LEVEL_LABEL + level;
        font.draw(
                batch,
                levelText,
                Constants.CHUTE_MARGIN,
                viewport.getWorldHeight() - Constants.CHUTE_MARGIN
        );

        //if the player has 2x or more combos, show the combo text with the current multiplier
        if (combo >= 2) {
            String comboText = combo + Constants.COMBO_LABEL;
            font.draw(
                    batch,
                    comboText,
                    viewport.getWorldWidth() / 2.0f,
                    viewport.getWorldHeight() - Constants.CHUTE_MARGIN,
                    0,
                    Align.center,
                    false
            );
        }

        batch.end();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
        font.getData().setScale(Math.min(width, height) / Constants.SCORE_FONT_SCALE);
    }
}
