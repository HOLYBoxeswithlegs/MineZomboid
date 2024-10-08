package minezomboid;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Player {
    private Camera camera;
    private boolean aiming;
    public boolean onGround = true;
    private float movespeedforward = 0.02f;
    private float movespeedsides = 0.02f;
    private float jumpVelocity = 0.04f;
    private float verticalVelocity = 0.0f;
    private float gravity = -0.001f; // Adjust gravity for smoother fall
    private float groundY = 0.0f; // Ground level

    public Player(Camera camera) {
        this.camera = camera;
        aiming = false;
    }

    public void handleInput() {
        // Jump logic
        if (onGround && Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            verticalVelocity = jumpVelocity; // Set upward velocity
            onGround = false; // Player is now in the air
        }

        // Apply gravity if not on the ground
        if (!onGround) {
            verticalVelocity += gravity; // Apply gravity to vertical velocity
            camera.moveUp(verticalVelocity); // Move the camera based on vertical velocity
            
            // Check if player hits the ground
            if (camera.getPosition().y <= groundY) {
                camera.setPosition(camera.getPosition().x, groundY, camera.getPosition().z); // Snap to ground level
                onGround = true; // Player is on the ground
                verticalVelocity = 0.0f; // Reset vertical velocity
            }
        }

        // Movement handling
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            movespeedforward = -0.025f;
            movespeedsides = 0.025f;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
            movespeedforward = -0.04f;
            movespeedsides = 0.04f;
        } else {
            movespeedforward = -0.02f;
            movespeedsides = 0.02f;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            camera.moveForward(movespeedforward);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            camera.moveForward(-movespeedforward);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            camera.moveRight(-movespeedsides);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            camera.moveRight(movespeedsides);
        }

        // Other controls
        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            System.exit(0);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            aiming = true;
            System.out.println("AIMING!");
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
            System.out.println("test");
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_F1)) {
            System.out.println("help?");
        }

        // Mouse handling
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
