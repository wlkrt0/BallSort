package net.zachwalker.ballsort.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;


public class Chute {

    //NOTE: properties are not private because they need to be accessed to check for touch event
    float positionX;
    float positionY;
    float width;
    float height;
    private ShapeType shapeType;
    private Color color;

    public Chute(float x, float y, float width, float height, ShapeType fillType, Color color) {
        positionX = x;
        positionY = y;
        this.width = width;
        this.height = height;
        this.shapeType = fillType;
        this.color = color;
    }

    //no update method on the Chute, since it isn't going anywhere

    public void render(ShapeRenderer renderer) {
        renderer.begin(shapeType);
        renderer.setColor(color);
        renderer.rect(positionX, positionY, width, height);
        renderer.end();
    }

    public float getWidth() {
        return width;
    }

}
