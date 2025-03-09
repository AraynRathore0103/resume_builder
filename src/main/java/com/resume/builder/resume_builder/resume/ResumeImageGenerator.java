package com.resume.builder.resume_builder.resume;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ResumeImageGenerator {
    public static void generateResumeImage(String resumeText, String filePath) {
        try {
            int width = 600, height = 800;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 16));

            int y = 40;
            for (String line : resumeText.split("\n\n")) {
                if (line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    g.setFont(new Font("Arial", Font.BOLD, 14));  // Bold for headings
                    g.drawString(parts[0] + ":", 50, y);
                    g.setFont(new Font("Arial", Font.PLAIN, 14));  // Normal font for content
                    g.drawString(parts[1], 50, y + 20);
                    y += 40;
                } else {
                    g.setFont(new Font("Arial", Font.PLAIN, 14));
                    g.drawString(line, 50, y);
                    y += 20;
                }
            }

            g.dispose();
            ImageIO.write(image, "png", new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
