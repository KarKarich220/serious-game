uniform sampler2D u_texture;

uniform vec4 u_colorFilter;
uniform float u_intensity;
uniform int u_effectType;
uniform float u_time;
uniform vec2 u_resolution;

varying vec2 v_texCoords;

vec3 applySepia(vec3 color) {
    float r = dot(color.rgb, vec3(0.393, 0.769, 0.189));
    float g = dot(color.rgb, vec3(0.349, 0.686, 0.168));
    float b = dot(color.rgb, vec3(0.272, 0.534, 0.131));
    return vec3(r, g, b);
}

vec3 applyNightVision(vec3 color) {
    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
    return vec3(gray * 0.2, gray * 0.9, gray * 0.1);
}

float vignette(vec2 uv, float intensity) {
    float dist = distance(uv, vec2(0.5));
    return 1.0 - dist * intensity;
}

float pulse(float time, float speed) {
    return 0.5 + 0.5 * sin(time * speed);
}

void main() {
    vec4 color = texture2D(u_texture, v_texCoords);
    vec3 result = color.rgb;

    if (u_effectType == 0) {
        result = color.rgb * u_colorFilter.rgb;
    } else if (u_effectType == 1) {
        result = mix(color.rgb, applySepia(color.rgb), u_intensity);
    } else if (u_effectType == 2) {
        result = mix(color.rgb, applyNightVision(color.rgb), u_intensity);
    }
    
    float vignetteAmount = vignette(v_texCoords, 0.4 + u_intensity * 0.3);
    result *= vignetteAmount;
    
    gl_FragColor = vec4(result, color.a);
}