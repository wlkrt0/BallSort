package net.zachwalker.ballsort.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import net.zachwalker.ballsort.util.Constants;


public class Chute {

    float positionX;
    float positionY;
    float width;
    float height;

    public Chute() {
        positionX = Constants.CHUTE_MARGIN;
        positionY = 0.0f;
        width = Constants.CHUTE_WIDTH;
        height = Constants.CHUTE_HEIGHT;
    }

    //no update method on the Chute, since it isn't going anywhere

    public void render(ShapeRenderer renderer) {
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.GRAY);
        renderer.rect(positionX, positionY, width, height);
        renderer.end();
    }

}
