attribute vec2 vPosition;
uniform mat4 MVPMatrix;
varying vec2 texPos;

void main() {
    texPos = vec2(vPosition.x + 0.5, vPosition.y + 0.5);
    gl_Position = vec4(vPosition.x, vPosition.y, 0, 1) * MVPMatrix;
}