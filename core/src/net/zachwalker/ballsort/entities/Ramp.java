package net.zachwalker.ballsort.entities;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.zachwalker.ballsort.util.Constants;

//TODO consider making chute and ramp derive from a common superclass since they behave so similarly
public class Ramp {

    float positionX;
    float positionY;
    float width;
    float height;

    public Ramp() {
        positionX = Constants.CHUTE_MARGIN;
        positionY = Constants.CHUTE_HEIGHT;
        width = Constants.RAMP_WIDTH;
        height = Constants.CHUTE_WIDTH;
    }

    //no update method on the Ramp, since it isn't going anywhere

    public void render(ShapeRenderer renderer) {
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.GRAY);
        renderer.rect(positionX, positionY, width, height);
        renderer.end();
    }
}
