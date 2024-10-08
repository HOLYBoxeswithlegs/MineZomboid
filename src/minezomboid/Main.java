package minezomboid;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

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
    public static int wallTextureID;
    public static int spriteTextureID;
    public World world = new World();

    public static void main(String[] argv) {
        Main main = new Main();
        main.start();
    }

    public Main() {
    }
    
    public void start() {
        try {
            
        	Display.setDisplayMode(new DisplayMode(1280, 800));
            //Display.setFullscreen(true);
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
            render();           
            System.out.println("XYZ : " + camera.getPosition());

            Display.update();
            Display.sync(60);
        }

        Display.destroy();
    }

    private void initGL() {
        System.out.println("Starting!");
        System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
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

        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * image.getWidth() * image.getHeight());
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));         
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }
        buffer.flip();

        glBindTexture(GL_TEXTURE_2D, textureID);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        GL30.glGenerateMipmap(GL_TEXTURE_2D);

        return textureID;
    }
    
    private void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glLoadIdentity();
        camera.applyTranslations();
        world.loadWalls("assets/structures/walls/");
        world.loadFloors("assets/structures/floors/");

        glDisable(GL_TEXTURE_2D);
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
