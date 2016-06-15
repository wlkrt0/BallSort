package net.zachwalker.ballsort.entities;


import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.zachwalker.ballsort.util.Assets;
import net.zachwalker.ballsort.util.Constants;


public class TouchTargets extends InputAdapter{

    Viewport viewport;
    Array<Chute> touchTargets;
    Array<Valve> valves;
    Assets assets;

    //note that touch targets must be initalized AFTER valves AND assets
    //also note that we need to pass the viewport to the touchtargets class since it will be
    //receiving touches and will need to unproject the touch input using the viewport
    //also need to pass in assets since it will be playing sounds when valves are toggled
    public TouchTargets(Viewport viewport, Array<Valve> valves, Assets assets) {
        this.viewport = viewport;
        this.valves = valves;
        this.assets = assets;
        touchTargets = new Array<Chute>();
        //for every valve, add a corresponding touch target
        for (Valve valve : valves) {
            touchTargets.add(new Chute(
                    valve.positionX - (Constants.VALVE_WIDTH),
                    valve.positionY - (Constants.VALVE_WIDTH * 5.0f),
                    Constants.VALVE_WIDTH * 3.0f,
                    Constants.VALVE_WIDTH * 3.0f,
                    ShapeType.Line,
                    Constants.TOUCHTARGET_COLOR)
            );
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //get the coordinates of the touchDown event in OUR world coords (not screen coords)
        Vector2 viewportPosition = viewport.unproject(new Vector2(screenX, screenY));
        //check each touchTarget to see if any were hit (within the radius)
        for (int i = 0; i < touchTargets.size; i++) {
            Vector2 touchTargetCenter = new Vector2(
                    touchTargets.get(i).positionX + (touchTargets.get(i).width / 2.0f),
                    touchTargets.get(i).positionY + (touchTargets.get(i).height / 2.0f)
            );
            float radius = Math.min(touchTargets.get(i).width, touchTargets.get(i).height) / 2.0f;
            if (viewportPosition.dst(touchTargetCenter) <= radius) {
                //since our touchTargets and valves arrays are aligned, we can use the same iterator
                valves.get(i).switchValveState();
                assets.sounds.valve.play();
            }
        }
        return true;
    }

    //no update method on the TouchTargets, since they aren't going anywhere

    public void render(ShapeRenderer renderer) {
        for (Chute touchTarget : touchTargets) {
            touchTarget.render(renderer);
        }
    }

}
