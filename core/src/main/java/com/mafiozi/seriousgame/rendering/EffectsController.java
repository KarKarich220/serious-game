package com.mafiozi.seriousgame.rendering;

public class EffectsController {
    private float shakeTime = 0f;
    private float shakeIntensity = 0f;

    private float fade = 0f;
    private float flash = 0f;
    private float damage = 0f;

    public void update(float delta) {
        if (shakeTime > 0) {
            shakeTime -= delta;
            if (shakeTime <= 0) shakeIntensity = 0;
        }

        fade = lerp(fade, 0f, 0.05f);
        flash = lerp(flash, 0f, 0.1f);
        damage = lerp(damage, 0f, 0.08f);
    }

    public void shake(float intensity, float duration) {
        this.shakeIntensity = intensity;
        this.shakeTime = duration;
    }

    public void flash(float strength) {
        this.flash = strength;
    }

    public void fadeIn() {
        this.fade = 1f;
    }

    public void damage() {
        this.damage = 1f;
    }

    public float getShakeIntensity() { return shakeIntensity; }
    public float getFade() { return fade; }
    public float getFlash() { return flash; }
    public float getDamage() { return damage; }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}