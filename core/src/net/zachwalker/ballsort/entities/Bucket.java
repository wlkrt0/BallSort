package net.zachwalker.ballsort.entities;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.zachwalker.ballsort.util.Constants;

public class Bucket {

    float positionX;
    float positionY;
    float width;
    float height;

    public Bucket() {
        positionX = Constants.CHUTE_MARGIN + Constants.RAMP_WIDTH + Constants.BALL_SIZE - (Constants.BUCKET_WIDTH / 2.0f);
        positionY = 0.0f;
        width = Constants.BUCKET_WIDTH;
        height = Constants.BUCKET_HEIGHT;
    }

    //no update method on the Bucket, since it isn't going anywhere

    public void render(ShapeRenderer renderer) {
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.RED);
        renderer.rect(positionX, positionY, width, height);
        renderer.end();
    }
}
