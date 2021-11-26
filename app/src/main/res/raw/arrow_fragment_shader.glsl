precision mediump float;
varying vec2 texPos;
uniform sampler2D sampler;

void main() {
    vec4 texColor = texture2D(sampler, texPos);

    if (texColor.a < 0.1)
        discard;

    gl_FragColor = texColor;
}