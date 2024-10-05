package minezomboid;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Camera {
    private Vector3f position = null;
    private float yaw; // Rotation around Y-axis
    private float pitch; // Rotation around X-axis
    private float fov;
    private float aspect;
    private float near;
    private float far;

    public Camera(float fov, float aspect, float near, float far) {
        position = new Vector3f(0, 0, 0);
        yaw = 0;
        pitch = 0;
        this.fov = fov;
        this.aspect = aspect;
        this.near = near;
        this.far = far;
        initProjection();
    }

    public void initProjection() {
        Matrix4f projectionMatrix = new Matrix4f();
        float yScale = (float) (1f / Math.tan(Math.toRadians(fov / 2f)));
        float xScale = yScale / aspect;
        float frustumLength = far - near;

        projectionMatrix.m00 = xScale;
        projectionMatrix.m11 = yScale;
        projectionMatrix.m22 = -((far + near) / frustumLength);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * near * far) / frustumLength);
        projectionMatrix.m33 = 0;

        FloatBuffer buffer = toFloatBuffer(projectionMatrix);
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(buffer);
        glMatrixMode(GL_MODELVIEW);
    }
    
    public void applyBillboardMatrix(Matrix4f modelviewMatrix) {
        Vector4f lookAt4f = new Vector4f(0, 0, -1, 0);
        Matrix4f.transform(modelviewMatrix, lookAt4f, lookAt4f);
        Vector3f lookAt = (Vector3f) new Vector3f(lookAt4f.x, lookAt4f.y, lookAt4f.z).negate();

        Vector4f up4f = new Vector4f(0, 1, 0, 0);
        Matrix4f.transform(modelviewMatrix, up4f, up4f);
        Vector3f up = new Vector3f(up4f.x, up4f.y, up4f.z);

        Vector3f right = new Vector3f();
        Vector3f.cross(up, lookAt, right);

        modelviewMatrix.setIdentity();
        modelviewMatrix.m00 = right.x;
        modelviewMatrix.m10 = right.y;
        modelviewMatrix.m20 = right.z;
        modelviewMatrix.m01 = up.x;
        modelviewMatrix.m11 = up.y;
        modelviewMatrix.m21 = up.z;
        modelviewMatrix.m02 = lookAt.x;
        modelviewMatrix.m12 = lookAt.y;
        modelviewMatrix.m22 = lookAt.z;
    }

    public void applyTranslations() {
        // Clamp pitch to avoid flipping
        if (pitch > 89.0f) {
            pitch = 89.0f;
        } else if (pitch < -89.0f) {
            pitch = -89.0f;
        }

        // Apply yaw and pitch for rotation
        glRotatef(pitch, 1, 0, 0); // Pitch (rotation around X-axis)
        glRotatef(yaw, 0, 1, 0);   // Yaw (rotation around Y-axis)

        // Apply position translation
        glTranslatef(-position.x, -position.y, -position.z);
    }

    public FloatBuffer toFloatBuffer(Matrix4f matrix) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        matrix.store(buffer);
        buffer.flip();
        return buffer;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    // Rotate the camera on the Y-axis (yaw)
    public void rotateYaw(float amount) {
        yaw += amount;
    }

    // Rotate the camera on the X-axis (pitch)
    public void rotatePitch(float amount) {
        pitch += amount;
    }

    public void moveForward(float distance) {
        position.z += distance * Math.cos(Math.toRadians(yaw));
        position.x -= distance * Math.sin(Math.toRadians(yaw));
    }

    public void moveRight(float distance) {
        position.z += distance * Math.sin(Math.toRadians(yaw));
        position.x += distance * Math.cos(Math.toRadians(yaw));
    }

    public void moveUp(float distance) {
        position.y += distance;
    }

    public void moveDown(float distance) {
        position.y -= distance;
    }
}
