package gay.oss.cw3.renderer.shaders;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Lighting {
    private Vector4f lightDirection;
    private Vector3f lightAmbient;
    private Vector3f lightDiffuse;

    public Lighting() {
        this.lightDirection = new Vector4f(0, 40, 0, 0);
        this.lightAmbient = new Vector3f(0.3f, 0.3f, 0.3f);
        this.lightDiffuse = new Vector3f(0.7f, 0.7f, 0.7f);
    }

    public void upload() {
        ShaderProgram.setUniform("light.direction", (Object) this.lightDirection);
        ShaderProgram.setUniform("light.ambient", (Object) this.lightAmbient);
        ShaderProgram.setUniform("light.diffuse", (Object) this.lightDiffuse);
    }

    public void setLightDirection(Vector4f direction) {
        this.lightDirection = direction;
    }

    public void setLightAmbient(Vector3f ambient) {
        this.lightAmbient = ambient;
    }

    public void setLightDiffuse(Vector3f diffuse) {
        this.lightDiffuse = diffuse;
    }
}
