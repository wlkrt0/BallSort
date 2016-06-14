package net.zachwalker.ballsort.entities;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import net.zachwalker.ballsort.util.Constants;
import net.zachwalker.ballsort.util.Enums;


public class Valve extends InputAdapter {

    Viewport viewport;
    float positionX;
    float positionY;
    float width;
    float height;
    Enums.ValveState valveState;

    //note that we need to pass the viewport to the valve since it will be receiving touches
    //and will need to unproject the touch input using the viewport
    public Valve(Viewport viewport) {
        this.viewport = viewport;
        positionX = Constants.CHUTE_MARGIN + Constants.RAMP_WIDTH;
        positionY = Constants.CHUTE_HEIGHT;
        width = Constants.VALVE_WIDTH;
        height = Constants.VALVE_HEIGHT;
        valveState = Enums.ValveState.CLOSED;
    }

    public void render(ShapeRenderer renderer) {
        switch (valveState) {
            case CLOSED:
                drawClosedValve(renderer);
                break;
            case OPEN:
                drawOpenValve(renderer);
                break;
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector2 viewportPosition = viewport.unproject(new Vector2(screenX, screenY));
        if (viewportPosition.dst(positionX, positionY) < Constants.VALVE_WIDTH) {
            switch (valveState) {
                case CLOSED:
                    valveState = Enums.ValveState.OPEN;
                    break;
                case OPEN:
                    valveState = Enums.ValveState.CLOSED;
                    break;
            }
        }
        return true;
    }

    private void drawClosedValve(ShapeRenderer renderer) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.RED);
        renderer.rect(positionX, positionY, width, height);
        renderer.end();
    }

    private void drawOpenValve(ShapeRenderer renderer) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.RED);
        renderer.rect(positionX, positionY, 0.0f, 0.0f, width, height, 1.0f, 1.0f, Constants.VALVE_ROTATION);
        //note that height and width are intentionally switched here
        //renderer.rect(positionX - Constants.VALVE_WIDTH, positionY - Constants.VALVE_WIDTH, height, width);
        renderer.end();
    }

}
