package minezomboid;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Player {
    private Camera camera;
    private boolean aiming;
    private double movespeedforward = 0.02;
    private double movespeedsides = 0.02;
    private double movespeedforward2 = -0.02;
    private double movespeedsides1 = -0.02;

    public Player(Camera camera) {
        this.camera = camera;
        aiming = false;
    }

    public void handleInput() {
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            movespeedforward = -0.025;
            movespeedsides = 0.025;
            movespeedforward2 = 0.025;
            movespeedsides1 = -0.025;
            if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
                camera.moveForward((float) movespeedforward);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
                camera.moveForward((float) movespeedforward2);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
                camera.moveRight((float) movespeedsides1);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
                camera.moveRight((float) movespeedsides);
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
            movespeedforward = -0.04;
            movespeedsides = 0.04;
            movespeedforward2 = 0.04;
            movespeedsides1 = -0.04;
            if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
                camera.moveForward((float) movespeedforward);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
                camera.moveForward((float) movespeedforward2);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
                camera.moveRight((float) movespeedsides1);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
                camera.moveRight((float) movespeedsides);
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            System.exit(0);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            aiming = true;
            if (aiming) {
                System.out.println("AIMING!");
            } else {
                aiming = false;
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
            System.out.println("test");
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_F1)) {
            System.out.println("help?");
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            camera.moveForward(-0.02f);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            camera.moveForward(0.02f);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            camera.moveRight(-0.02f);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            camera.moveRight(0.02f);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
        }
        if (Mouse.isGrabbed()) {
            float mouseDX = Mouse.getDX() * 0.16f;
            float mouseDY = Mouse.getDY() * 0.16f;

            camera.rotateYaw(mouseDX);
            camera.rotatePitch(-mouseDY);
        }
        if (Mouse.isButtonDown(0)) {
            Mouse.setGrabbed(true);
        }
    }
}
