precision highp float;
uniform sampler2D sampler;
varying vec2 texPos;

void main() {
    vec4 texColor = texture2D(sampler, texPos);

    if (texColor.a < 0.1)
        discard;

    gl_FragColor = texColor;
}