package net.zachwalker.ballsort.entities;


import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.zachwalker.ballsort.BallSortScreen;
import net.zachwalker.ballsort.util.Assets;
import net.zachwalker.ballsort.util.Constants;


public class TouchTargets extends InputAdapter{

    private Viewport viewport;
    private Array<Chute> valveTargets;
    private Array<Valve> valves;
    private Assets assets;
    private Chute gotoLevelTarget;
    private Chute toggleSoundTarget;
    private BallSortScreen ballSortScreen;

    //note that touch targets must be initalized AFTER valves AND assets
    //also note that we need to pass the viewport to the touchtargets class since it will be
    //receiving touches and will need to unproject the touch input using the viewport
    //also need to pass in assets since it will be playing sounds when valves are toggled
    public TouchTargets(Viewport viewport, Array<Valve> valves, Assets assets, BallSortScreen ballSortScreen) {
        this.ballSortScreen = ballSortScreen;
        this.viewport = viewport;
        this.valves = valves;
        this.assets = assets;
        valveTargets = new Array<Chute>();
        //for every valve, add a corresponding touch target
        for (Valve valve : valves) {
            valveTargets.add(new Chute(
                    valve.positionX - (Constants.VALVE_WIDTH),
                    valve.positionY - (Constants.VALVE_WIDTH * 5.0f),
                    Constants.VALVE_WIDTH * 3.0f,
                    Constants.VALVE_WIDTH * 3.0f,
                    ShapeType.Line,
                    Constants.TOUCHTARGET_COLOR)
            );
        }
        gotoLevelTarget = new Chute(
                Constants.WORLD_WIDTH - Constants.CHUTE_MARGIN,
                Constants.WORLD_HEIGHT / 2.0f,
                Constants.VALVE_WIDTH,
                Constants.VALVE_WIDTH,
                ShapeType.Line,
                Constants.TOUCHTARGET_COLOR
        );

        toggleSoundTarget = new Chute(
                Constants.WORLD_WIDTH - Constants.CHUTE_MARGIN,
                Constants.WORLD_HEIGHT / 2.0f - 2.0f * Constants.VALVE_WIDTH,
                Constants.VALVE_WIDTH,
                Constants.VALVE_WIDTH,
                ShapeType.Line,
                Constants.TOUCHTARGET_COLOR
        );
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //get the coordinates of the touchDown event in OUR world coords (not screen coords)
        Vector2 viewportPosition = viewport.unproject(new Vector2(screenX, screenY));

        //check the gotoLevel target to see if it was hit (touch point was within its radius)
        if (targetWasHit(viewportPosition, gotoLevelTarget)) {
            assets.sounds.playSound(assets.sounds.button);
            ballSortScreen.gotoHighLevel();
        }

        //check the toggleSound target to see if it was hit (touch point was within its radius)
        if (targetWasHit(viewportPosition, toggleSoundTarget)) {
            if (toggleSoundTarget.color.equals(Constants.TOUCHTARGET_COLOR)) {
                toggleSoundTarget.color = Color.RED;
            } else {
                toggleSoundTarget.color = Constants.TOUCHTARGET_COLOR;
            }
            assets.sounds.playSound(assets.sounds.button);
            assets.sounds.toggleMute();
        }

        //check each touchTarget to see if any were hit (touch point was within its radius)
        for (int i = 0; i < valveTargets.size; i++) {
            if (targetWasHit(viewportPosition, valveTargets.get(i))) {
                //since our touchTargets and valves arrays are aligned, we can use the same iterator
                valves.get(i).switchValveState();
                assets.sounds.playSound(assets.sounds.valve);
            }
        }
        return true;
    }

    //no update method on the TouchTargets, since they aren't going anywhere

    public void render(ShapeRenderer renderer) {
        for (Chute valveTarget : valveTargets) {
            valveTarget.render(renderer);
        }
        gotoLevelTarget.render(renderer);
        toggleSoundTarget.render(renderer);
    }

    private boolean targetWasHit(Vector2 viewportPosition, Chute touchTarget) {
        Vector2 targetCenter = new Vector2(
                touchTarget.positionX + touchTarget.width / 2.0f,
                touchTarget.positionY + touchTarget.height / 2.0f
        );
        float targetRadius = Math.min(touchTarget.width, touchTarget.height) / 2.0f;
        //check the touchTarget to see if it was hit (touch point was within its radius)
        return (viewportPosition.dst(targetCenter) <= targetRadius);
    }

}
