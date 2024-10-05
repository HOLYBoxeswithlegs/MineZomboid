package minezomboid;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

public class Main {
    private Camera camera;
    private Player player;
    private int fps = 0;
    private int wallTextureID;
    private int spriteTextureID;
    private Vector3f spritePosition = new Vector3f(2, 1, -5); // Example sprite position
    private int width = 1920;
    private int height = 1080;

    public static void main(String[] argv) {
        Main main = new Main();
        main.start();
    }

    public void start() {
        try {
            
        	//Display.setDisplayMode(new DisplayMode(1280, 800));
            Display.setFullscreen(true);;
        	Display.setTitle("MineZomboid");
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        initGL();
        loadTextures();
        camera = new Camera(80, (float) Display.getWidth() / (float) Display.getHeight(), 0.3f, 1000);
        camera.setPosition(0, 0, 0);
        player = new Player(camera);

        while (!Display.isCloseRequested()) {
            player.handleInput();
            update();
            render();

            Display.update();
            Display.sync(60);
        }

        Display.destroy();
    }

    private void initGL() {
        System.out.println("Starting!");
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glClearColor(0.0f, 0.5f, 1f, 0.0f);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        System.out.println("GL INITIATED!");
    }

    private void loadTextures() {
        try {
            // Load wall texture
            InputStream wallStream = getClass().getClassLoader().getResourceAsStream("assets/textures/brick.png");
            BufferedImage wallImage = ImageIO.read(wallStream);
            wallTextureID = loadTexture(wallImage);

            // Load sprite texture
            InputStream spriteStream = getClass().getClassLoader().getResourceAsStream("assets/textures/sprite.png");
            BufferedImage spriteImage = ImageIO.read(spriteStream);
            spriteTextureID = loadTexture(spriteImage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int loadTexture(BufferedImage image) {
        int textureID = glGenTextures();

        // Convert BufferedImage to ByteBuffer
        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * image.getWidth() * image.getHeight());
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));  // Green component
                buffer.put((byte) (pixel & 0xFF));         // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component
            }
        }
        buffer.flip();

        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, textureID);

        // Setup texture wrapping and filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Send texture data to OpenGL
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        // Generate mipmaps
        GL30.glGenerateMipmap(GL_TEXTURE_2D);

        return textureID;
    }

    private void update() {
        fps++;
    }

    private void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glLoadIdentity();
        camera.applyTranslations();

        // Render walls
        renderWall();

        // Render sprite
        renderSprite();
        
        renderFloor();

        // Disable texture mapping
        glDisable(GL_TEXTURE_2D);
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
    
    private void renderFloor() {
    	glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, spriteTextureID);

        drawRectangularPrismWithTexture(new Vector3f(1, -1, -4), new Vector3f(1, 1, 2), new Vector3f(90, 0, 90), "assets/textures/brick.png");
        drawRectangularPrismWithTexture(new Vector3f(2, -1, -4), new Vector3f(1, 1, 2), new Vector3f(90, 0, 90), "assets/textures/brick.png");
        // Render a rectangular prism (2 square tall rectangle) with texture
        drawRectangularPrismWithTexture(new Vector3f(1, -1, -5), new Vector3f(1, 1, 2), new Vector3f(90, 0, 90), "assets/textures/brick.png");
        drawRectangularPrismWithTexture(new Vector3f(2, -1, -5), new Vector3f(1, 1, 2), new Vector3f(90, 0, 90), "assets/textures/brick.png");
        
        drawRectangularPrismWithTexture(new Vector3f(1, 1, -4), new Vector3f(1, 1, 2), new Vector3f(90, 0, 90), "assets/textures/brick.png");
        drawRectangularPrismWithTexture(new Vector3f(2, 1, -4), new Vector3f(1, 1, 2), new Vector3f(90, 0, 90), "assets/textures/brick.png");
        // Render a rectangular prism (2 square tall rectangle) with texture
        drawRectangularPrismWithTexture(new Vector3f(1, 1, -5), new Vector3f(1, 1, 2), new Vector3f(90, 0, 90), "assets/textures/brick.png");
        drawRectangularPrismWithTexture(new Vector3f(2, 1, -5), new Vector3f(1, 1, 2), new Vector3f(90, 0, 90), "assets/textures/brick.png");
    }
    
    private void renderWall() {
        // Enable texture mapping for wall texture
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, wallTextureID);

        // Render a rectangular prism (2 square tall rectangle) with texture
        drawRectangularPrismWithTexture(new Vector3f(1, -1, -5), new Vector3f(1, 2, 1), new Vector3f(0, 0, 0), "assets/textures/brick.png");
        drawRectangularPrismWithTexture(new Vector3f(0, -1, -5), new Vector3f(1, 2, 1), new Vector3f(0, 0, 0), "assets/textures/brick.png");
        drawRectangularPrismWithTexture(new Vector3f(0, -1, -5), new Vector3f(1, 2, 1), new Vector3f(0, -90, 0), "assets/textures/brick.png");
        drawRectangularPrismWithTexture(new Vector3f(0, -1, -4), new Vector3f(1, 2, 1), new Vector3f(0, -90, 0), "assets/textures/brick.png");
        drawRectangularPrismWithTexture(new Vector3f(0, -1, -3), new Vector3f(1, 2, 1), new Vector3f(0, 0, 0), "assets/textures/brick.png");
        drawRectangularPrismWithTexture(new Vector3f(1, -1, -3), new Vector3f(1, 2, 1), new Vector3f(0, 0, 0), "assets/textures/brick.png");
    }

    private void renderSprite() {
        // Enable texture mapping for sprite texture
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, spriteTextureID);

        // Render the sprite as a quad
        glBegin(GL_QUADS);
        glEnd();
    }

    public static int getFPS() {
        return 60;
    }
    
    public static void drawRectangularPrismWithTexture(Vector3f position, Vector3f dimensions, Vector3f rotation, String texturePath) {
        float x = position.x;
        float y = position.y;
        float z = position.z;
        float width = dimensions.x;
        float height = dimensions.y;
        float depth = dimensions.z;

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        GL11.glRotatef(rotation.x, 1, 0, 0);
        GL11.glRotatef(rotation.y, 0, 1, 0);
        GL11.glRotatef(rotation.z, 0, 0, 1);

        GL11.glBegin(GL11.GL_QUADS);
        
        // Front Face
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex3f(0, 0, 0);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex3f(width, 0, 0);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex3f(width, height, 0);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex3f(0, height, 0);
        
        GL11.glEnd();
        GL11.glPopMatrix();
    }
}
