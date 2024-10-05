package minezomboid;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Frustum {
    private Vector4f[] planes = new Vector4f[6];

    public void update(Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        Matrix4f clipMatrix = Matrix4f.mul(projectionMatrix, viewMatrix, null);

        planes[0] = new Vector4f(clipMatrix.m30 + clipMatrix.m00, clipMatrix.m31 + clipMatrix.m01, clipMatrix.m32 + clipMatrix.m02, clipMatrix.m33 + clipMatrix.m03);
        planes[1] = new Vector4f(clipMatrix.m30 - clipMatrix.m00, clipMatrix.m31 - clipMatrix.m01, clipMatrix.m32 - clipMatrix.m02, clipMatrix.m33 - clipMatrix.m03);
        planes[2] = new Vector4f(clipMatrix.m30 + clipMatrix.m10, clipMatrix.m31 + clipMatrix.m11, clipMatrix.m32 + clipMatrix.m12, clipMatrix.m33 + clipMatrix.m13);
        planes[3] = new Vector4f(clipMatrix.m30 - clipMatrix.m10, clipMatrix.m31 - clipMatrix.m11, clipMatrix.m32 - clipMatrix.m12, clipMatrix.m33 - clipMatrix.m13);
        planes[4] = new Vector4f(clipMatrix.m30 + clipMatrix.m20, clipMatrix.m31 + clipMatrix.m21, clipMatrix.m32 + clipMatrix.m22, clipMatrix.m33 + clipMatrix.m23);
        planes[5] = new Vector4f(clipMatrix.m30 - clipMatrix.m20, clipMatrix.m31 - clipMatrix.m21, clipMatrix.m32 - clipMatrix.m22, clipMatrix.m33 - clipMatrix.m23);

        for (Vector4f plane : planes) {
            float length = (float) Math.sqrt(plane.x * plane.x + plane.y * plane.y + plane.z * plane.z);
            plane.x /= length;
            plane.y /= length;
            plane.z /= length;
            plane.w /= length;
        }
    }

    public boolean isBoxInFrustum(Vector3f min, Vector3f max) {
        for (Vector4f plane : planes) {
            if (plane.x * min.x + plane.y * min.y + plane.z * min.z + plane.w > 0) continue;
            if (plane.x * max.x + plane.y * max.y + plane.z * max.z + plane.w > 0) continue;
            if (plane.x * min.x + plane.y * min.y + plane.z * max.z + plane.w > 0) continue;
            if (plane.x * max.x + plane.y * max.y + plane.z * min.z + plane.w > 0) continue;
            if (plane.x * min.x + plane.y * max.y + plane.z * max.z + plane.w > 0) continue;
            if (plane.x * max.x + plane.y * min.y + plane.z * min.z + plane.w > 0) continue;
            if (plane.x * max.x + plane.y * min.y + plane.z * max.z + plane.w > 0) continue;
            if (plane.x * min.x + plane.y * max.y + plane.z * min.z + plane.w > 0) continue;
            return false;
        }
        return true;
    }
}
