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

    public void render(SpriteBatch batch, long score, int level, int combo) {
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        //always show the score
        String scoreText = Constants.SCORE_LABEL + score + "\n" + Constants.LEVEL_LABEL + level;
        font.draw(
                batch,
                scoreText,
                viewport.getWorldWidth() - Constants.CHUTE_MARGIN,
                viewport.getWorldHeight() - Constants.CHUTE_MARGIN,
                0,
                Align.right,
                false
        );

        //if the player has 2x or more combos, show the combo text with the current multiplier
        if (combo >= 2) {
            String comboText = combo + Constants.COMBO_LABEL;
            font.draw(
                    batch,
                    comboText,
                    Constants.CHUTE_MARGIN,
                    viewport.getWorldHeight() - Constants.CHUTE_MARGIN
            );
        }

        batch.end();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
        font.getData().setScale(Math.min(width, height) / Constants.SCORE_FONT_SCALE);
    }
}
