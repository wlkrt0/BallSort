package net.zachwalker.ballsort.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;


public class Assets implements Disposable, AssetErrorListener {

    private AssetManager manager;
    public Sounds sounds;

    public Assets() {
        manager = new AssetManager();
        manager.setErrorListener(this);
        manager.load(Constants.SOUND_NEWBALL, Sound.class);
        manager.load(Constants.SOUND_VALVE, Sound.class);
        manager.load(Constants.SOUND_MISSED, Sound.class);
        manager.load(Constants.SOUND_CAUGHT1, Sound.class);
        manager.load(Constants.SOUND_CAUGHT2, Sound.class);
        manager.load(Constants.SOUND_CAUGHT3, Sound.class);
        manager.load(Constants.SOUND_CAUGHT4, Sound.class);
        manager.load(Constants.SOUND_CAUGHT5, Sound.class);
        manager.load(Constants.SOUND_CAUGHT6, Sound.class);
        manager.load(Constants.SOUND_CAUGHT7, Sound.class);
        manager.load(Constants.SOUND_CAUGHT8, Sound.class);
        //set loading to be synchronous
        manager.finishLoading();

        sounds = new Sounds(manager);
    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        Gdx.app.error(Constants.LOG_TAG, "Couldn't load asset: " + asset.fileName, throwable);
    }

    @Override
    public void dispose() {
        manager.dispose();
    }

    public class Sounds {

        public Sound newBall;
        public Sound valve;
        public Sound missed;
        public Sound caught1;
        public Sound caught2;
        public Sound caught3;
        public Sound caught4;
        public Sound caught5;
        public Sound caught6;
        public Sound caught7;
        public Sound caught8;

        public Sounds(AssetManager manager) {
            newBall = manager.get(Constants.SOUND_NEWBALL, Sound.class);
            valve = manager.get(Constants.SOUND_VALVE, Sound.class);
            missed = manager.get(Constants.SOUND_MISSED, Sound.class);
            caught1 = manager.get(Constants.SOUND_CAUGHT1, Sound.class);
            caught2 = manager.get(Constants.SOUND_CAUGHT2, Sound.class);
            caught3 = manager.get(Constants.SOUND_CAUGHT3, Sound.class);
            caught4 = manager.get(Constants.SOUND_CAUGHT4, Sound.class);
            caught5 = manager.get(Constants.SOUND_CAUGHT5, Sound.class);
            caught6 = manager.get(Constants.SOUND_CAUGHT6, Sound.class);
            caught7 = manager.get(Constants.SOUND_CAUGHT7, Sound.class);
            caught8 = manager.get(Constants.SOUND_CAUGHT8, Sound.class);
        }
    }

}
