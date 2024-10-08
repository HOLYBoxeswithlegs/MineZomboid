package minezomboid;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector3f;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class World {

    public void loadWalls(String folderPath) {
        glEnable(GL_TEXTURE_2D);

        // Scan the folder for wall files
        List<String> wallFiles = scanFolder(folderPath);

        for (String file : wallFiles) {
            try {
                Map<String, String> data = loadConfigFile(folderPath + file);

                Vector3f position = parseVector(data.get("position"));
                Vector3f size = parseVector(data.get("size"));
                Vector3f rotation = parseVector(data.get("rotation"));
                String texture = data.get("texture");

                glBindTexture(GL_TEXTURE_2D, Main.wallTextureID);
                Main.drawRectangularPrismWithTexture(position, size, rotation, texture);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadFloors(String folderPath) {
        glEnable(GL_TEXTURE_2D);

        // Scan the folder for floor files
        List<String> floorFiles = scanFolder(folderPath);

        for (String file : floorFiles) {
            try {
                Map<String, String> data = loadConfigFile(folderPath + file);

                Vector3f position = parseVector(data.get("position"));
                Vector3f size = parseVector(data.get("size"));
                Vector3f rotation = parseVector(data.get("rotation"));
                String texture = data.get("texture");

                glBindTexture(GL_TEXTURE_2D, Main.spriteTextureID);
                Main.drawRectangularPrismWithTexture(position, size, rotation, texture);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Scans the given folder and returns a list of all .txt files
    private List<String> scanFolder(String folderPath) {
        try {
            return Files.list(Paths.get(folderPath))
                    .filter(Files::isRegularFile) // Only regular files (no directories)
                    .filter(path -> path.toString().endsWith(".txt")) // Only .txt files
                    .map(path -> path.getFileName().toString()) // Get file names
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList(); // Return empty list if error occurs
        }
    }

    private Map<String, String> loadConfigFile(String filePath) throws IOException {
        Map<String, String> data = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        for (String line : lines) {
            String[] parts = line.split("=");
            if (parts.length == 2) {
                data.put(parts[0].trim(), parts[1].trim());
            }
        }
        return data;
    }

    private Vector3f parseVector(String vectorString) {
        String[] parts = vectorString.split(",");
        float x = Float.parseFloat(parts[0]);
        float y = Float.parseFloat(parts[1]);
        float z = Float.parseFloat(parts[2]);
        return new Vector3f(x, y, z);
    }
}
