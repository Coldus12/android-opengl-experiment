attribute vec2 vPosition;
uniform mat4 MVPMatrix;
varying vec2 texPos;

void main() {
    texPos = vec2((vPosition.x + 1.0)/2.0, (vPosition.y + 1.0)/2.0);
    gl_Position = vec4(vPosition.x, vPosition.y, 0, 1) * MVPMatrix;
}