package com.mafiozi.seriousgame.rendering;

import com.badlogic.gdx.graphics.Pixmap;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.*;

public class PostProcessor {
    private VfxManager vfx;

    private VignettingEffect vignette;
    private BloomEffect bloom;

    public PostProcessor() {
        vfx = new VfxManager(Pixmap.Format.RGBA8888);

        vignette = new VignettingEffect(false);
        bloom = new BloomEffect();


        vfx.addEffect(vignette);
        vfx.addEffect(bloom);
    }

    public void begin(boolean enabled) {
        if (enabled) {
            vfx.cleanUpBuffers();
            vfx.beginInputCapture();
        }
    }

    public void end(boolean enabled) {
        if (enabled) {
            vfx.endInputCapture();
            vfx.applyEffects();
            vfx.renderToScreen();
        }
    }

    public void update(EffectsController fx) {
    }

    public void resize(int w, int h) {
        vfx.resize(w, h);
    }

    public void dispose() {
        vfx.dispose();
    }
}