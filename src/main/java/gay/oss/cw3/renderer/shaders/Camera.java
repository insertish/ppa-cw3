package gay.oss.cw3.renderer.shaders;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

import org.joml.Matrix4f;

import gay.oss.cw3.renderer.Window;
import gay.oss.cw3.renderer.interfaces.ICursorPosCallback;
import gay.oss.cw3.renderer.interfaces.IMouseButtonCallback;
import gay.oss.cw3.renderer.interfaces.IScrollCallback;

public class Camera {
    private Matrix4f viewProjection;

    private double fov = 0.42 * Math.PI;
    
    private float x = 0;
    private float y = 0;
    private float z = 0;

    private double zoom = 40.0;
    private double viewAngle = 0.0;
    private double groundAngle = Math.PI / 3;

    public void calculate(float aspectRatio) {
        // 1. Calculate the zoom modifier and find the distance and height from this point.
        double zoomModifier = 5 + Math.pow(1.1, this.zoom);
        double distance = zoomModifier * Math.cos(this.groundAngle);
        double height = zoomModifier * Math.sin(this.groundAngle);

        // 2. Find the camera position by taking an offset from the center and
        //    finding the distance in each X and Z axis according to the view angle.
        float cameraX = this.x + (float) (distance * Math.cos(this.viewAngle));
        float cameraY = this.y + (float) height;
        float cameraZ = this.z + (float) (distance * Math.sin(this.viewAngle));

        // 3. Create view projection.
        this.viewProjection = new Matrix4f()
            .perspective((float) fov, aspectRatio, 0.01f, 1000.0f)
            .lookAt(cameraX, cameraY, cameraZ,
                    this.x, this.y, this.z,
                    0.0f, 1.0f, 0.0f);
    }

    public IScrollCallback createScrollCallback() {
        return (x, y) -> {
            this.setZoom(Math.max(this.getZoom() - y, 1.0));
        };
    }

    boolean lmbHeld = false;
    public IMouseButtonCallback createMouseButtonCallback(Window window) {
        return (button, action, modifiers) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                if (action == GLFW_PRESS) {
                    lmbHeld = true;
                } else {
                    lmbHeld = false;
                }
            }

            if (lmbHeld) {
                window.grabMouse();
            } else {
                window.freeMouse();
            }
        };
    }


    private double lastX = 0;
    private double lastY = 0;
    public ICursorPosCallback createCursorPosCallback() {
        return (x, y) -> {
            double dx = lastX - x, dy = lastY - y;
            if (lmbHeld) {
                this.setViewAngle(this.getViewAngle() - dx * 0.01);
                this.setGroundAngle(
                    Math.max(
                        Math.min(
                            this.getGroundAngle() - dy * 0.01,
                            Math.PI / 2 - 0.01
                        ),
                        0
                    )
                );
            }

            lastX = x;
            lastY = y;
        };
    }

    public void registerEvents(Window window) {
        window.setScrollCallback(this.createScrollCallback());
        window.setMouseButtonCallback(this.createMouseButtonCallback(window));
        window.setCursorPosCallback(this.createCursorPosCallback());
    }

    public double getFov() {
        return fov;
    }

    public void setFov(double fov) {
        this.fov = fov;
    }

    public void upload() {
        Camera.upload(this.viewProjection, null);
    }

    public void upload(Matrix4f transformation) {
        Camera.upload(this.viewProjection, transformation);
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public double getViewAngle() {
        return viewAngle;
    }

    public void setViewAngle(double viewAngle) {
        this.viewAngle = viewAngle;
    }

    public double getGroundAngle() {
        return groundAngle;
    }

    public void setGroundAngle(double groundAngle) {
        this.groundAngle = groundAngle;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public static void upload(Matrix4f viewProjection, Matrix4f transformation) {
        var program = ShaderProgram.getCurrent();
        program.setUniform("viewProjection", viewProjection);

        if (transformation != null) {
            program.setUniform("model", transformation);
            program.setUniform("modelViewProjection", new Matrix4f(viewProjection).mul(transformation));
        }
    }
}
