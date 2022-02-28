package gay.oss.cw3.renderer.shaders;

import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Helper class for defining lighting shader variables.
 */
public class Lighting {
    private Vector4f lightDirection;
    private Vector3f lightAmbient;
    private Vector3f lightDiffuse;

    /**
     * Construct a new Lighting helper.
     */
    public Lighting() {
        this.lightDirection = new Vector4f(0, 40, 0, 0);
        this.lightAmbient = new Vector3f(0.3f, 0.3f, 0.3f);
        this.lightDiffuse = new Vector3f(0.7f, 0.7f, 0.7f);
    }

    /**
     * Upload lighting data to the current shader.
     */
    public void upload() {
        ShaderProgram.setUniform("light.direction", (Object) this.lightDirection);
        ShaderProgram.setUniform("light.ambient", (Object) this.lightAmbient);
        ShaderProgram.setUniform("light.diffuse", (Object) this.lightDiffuse);
    }

    /**
     * Set the current global light direction or position.
     * Set w = 0.0 to use as position, otherwise it will be treated as a direction.
     * @param direction 3D direction or position vector, see method description.
     */
    public void setLightDirection(Vector4f direction) {
        this.lightDirection = direction;
    }

    /**
     * Set the current global light ambient colour.
     * @param ambient RGB colour space vector
     */
    public void setLightAmbient(Vector3f ambient) {
        this.lightAmbient = ambient;
    }

    /**
     * Set the current global light diffuse colour.
     * @param diffuse RGB colour space vector
     */
    public void setLightDiffuse(Vector3f diffuse) {
        this.lightDiffuse = diffuse;
    }
}
