package gay.oss.cw3.renderer.shaders;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import gay.oss.cw3.renderer.Window;
import gay.oss.cw3.renderer.interfaces.ICursorPosCallback;
import gay.oss.cw3.renderer.interfaces.IMouseButtonCallback;
import gay.oss.cw3.renderer.interfaces.IScrollCallback;

/**
 * Helper class for calculating the view projection matrix.
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public class Camera {
    private Matrix4f view;
    private Matrix4f projection;
    private Matrix4f viewProjection;

    private double fov = 0.42 * Math.PI;
    
    private float x = 0;
    private float y = 0;
    private float z = 0;

    private double zoom = 40.0;
    private double viewAngle = 0.0;
    private double groundAngle = Math.PI / 3;

    /**
     * Calculate the view matrix.
     * @return View Matrix
     */
    public Matrix4f getViewMatrix() {
        // 1. Calculate eye position vector.
        Vector3f eyePosition = this.getEyePositionVector();

        // 2. Create view matrix.
        return new Matrix4f()
            .lookAt(eyePosition.x, eyePosition.y, eyePosition.z,
                    this.x, this.y, this.z,
                    0.0f, 1.0f, 0.0f);
    }

    /**
     * Calculate the projection matrix given the aspect ratio.
     * @param aspectRatio Current Window aspect ratio
     * @return Projection Matrix
     */
    public Matrix4f getProjectionMatrix(float aspectRatio) {
        return new Matrix4f()
            .perspective((float) fov, aspectRatio, 0.01f, 1000.0f);
    }

    /**
     * Given the current aspect ratio of the window, calculate the view projection matrix.
     * @param aspectRatio Current Window aspect ratio
     */
    public void calculate(float aspectRatio) {
        this.view = this.getViewMatrix();
        this.projection = this.getProjectionMatrix(aspectRatio);
        this.viewProjection = this
            .getProjectionMatrix(aspectRatio)
            .mul(this.getViewMatrix());
    }

    /**
     * Create a callback for handling mouse scroll events.
     * @return Scroll Callback
     */
    public IScrollCallback createScrollCallback() {
        return (x, y) -> {
            this.setZoom(Math.max(this.getZoom() - y, 1.0));
        };
    }

    /**
     * Keep track of whether the user is holding left mouse button.
     */
    boolean lmbHeld = false;

    /**
     * Create a callback for handling mouse button events.
     * @param window Window which this Camera applies to
     * @return Mouse Button Callback
     */
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

    /**
     * Keep track of last X position of the mouse.
     */
    private double lastX = 0;
    
    /**
     * Keep track of last Y position of the mouse.
     */
    private double lastY = 0;
    
    /**
     * Create a callback for handling changes in cursor position.
     * @return Cursor Position Callback
     */
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

    /**
     * Register all applicable Camera events to the given Window.
     * @param window Window
     */
    public void registerEvents(Window window) {
        window.setScrollCallback(this.createScrollCallback());
        window.setMouseButtonCallback(this.createMouseButtonCallback(window));
        window.setCursorPosCallback(this.createCursorPosCallback());
    }

    /**
     * Get the currently set Field of View.
     * @return FOV value (in radians)
     */
    public double getFov() {
        return fov;
    }

    /**
     * Set the Field of View used in the Camera.
     * @param fov FOV value (in radians)
     */
    public void setFov(double fov) {
        this.fov = fov;
    }

    /**
     * Upload the current view projection with no additional data.
     */
    public void upload() {
        Camera.upload(this.view, this.projection, this.viewProjection, null);
    }

    /**
     * Upload view projection, model and model view projection matrices.
     * @param transformation Model Transformation Matrix
     */
    public void upload(Matrix4f transformation) {
        Camera.upload(this.view, this.projection, this.viewProjection, transformation);
    }

    /**
     * Get the current zoom of the camera.
     * @return Linear zoom value
     */
    public double getZoom() {
        return zoom;
    }

    /**
     * Set the current zoom of the camera.
     * Any given value is scaled exponentially.
     * @param zoom Linear zoom value
     */
    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    /**
     * Get the current view angle, or otherwise the rotation around the centre.
     * @return View Angle (in radians)
     */
    public double getViewAngle() {
        return viewAngle;
    }

    /**
     * Set the current view angle, or otherwise the rotation around the centre.
     * @param viewAngle View Angle (in radians)
     */
    public void setViewAngle(double viewAngle) {
        this.viewAngle = viewAngle;
    }

    /**
     * Get the current ground angle, or otherwise the angle between the tangent and the ground plane.
     * @return Ground Angle (in radians)
     */
    public double getGroundAngle() {
        return groundAngle;
    }

    /**
     * Set the current ground angle, or otherwise the angle between the tangent and the ground plane.
     * @param groundAngle Ground Angle (in radians)
     */
    public void setGroundAngle(double groundAngle) {
        this.groundAngle = groundAngle;
    }

    /**
     * Get the world space X position of where the Camera is looking.
     * @return X position
     */
    public float getX() {
        return x;
    }

    /**
     * Set the world space X position of where the Camera is looking.
     * @param x X position
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Get the world space Y position of where the Camera is looking.
     * @return Y position
     */
    public float getY() {
        return y;
    }

    /**
     * Set the world space Y position of where the Camera is looking.
     * @param y Y position
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Get the world space Z position of where the Camera is looking.
     * @return Z position
     */
    public float getZ() {
        return z;
    }

    /**
     * Set the world space Z position of where the Camera is looking.
     * @param z Z position
     */
    public void setZ(float z) {
        this.z = z;
    }

    /**
     * Compute the Camera's eye position Vector.
     * @return Position Vector
     */
    public Vector3f getEyePositionVector() {
        // 1. Calculate the zoom modifier and find the distance and height from this point.
        double zoomModifier = 5 + Math.pow(1.1, this.zoom);
        double distance = zoomModifier * Math.cos(this.groundAngle);
        double height = zoomModifier * Math.sin(this.groundAngle);

        // 2. Find the camera position by taking an offset from the center and
        //    finding the distance in each X and Z axis according to the view angle.
        float cameraX = this.x + (float) (distance * Math.cos(this.viewAngle));
        float cameraY = this.y + (float) height;
        float cameraZ = this.z + (float) (distance * Math.sin(this.viewAngle));

        return new Vector3f(cameraX, cameraY, cameraZ);
    }

    /**
     * Compute the Camera's eye direction Vector.
     * @return Direction Vector
     */
    public Vector3f getEyeDirectionVector() {
        return this.getEyePositionVector()
            .sub(this.x, this.y, this.z)
            .normalize();
    }

    /**
     * Upload projection matrices.
     * @param viewProjection View Projection Matrix
     * @param transformation Model Transformation Matrix
     */
    public static void upload(Matrix4f viewProjection, Matrix4f transformation) {
        var program = ShaderProgram.getCurrent();
        program.setUniform("viewProjection", viewProjection);

        if (transformation != null) {
            program.setUniform("model", transformation);
            program.setUniform("modelViewProjection", new Matrix4f(viewProjection).mul(transformation));
        }
    }

    /**
     * Upload projection matrices.
     * @param view View Matrix
     * @param projection Projection Matrix
     * @param viewProjection View Projection Matrix
     * @param transformation Model Transformation Matrix
     */
    public static void upload(Matrix4f view, Matrix4f projection, Matrix4f viewProjection, Matrix4f transformation) {
        var program = ShaderProgram.getCurrent();
        program.setUniform("projection", projection);
        program.setUniform("viewProjection", viewProjection);

        Camera.upload(viewProjection, transformation);
    }
}
