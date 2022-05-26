package pl.edu.pb.wi.shared;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ImageSharedOperations {
    private static int[] lookupTableRed = new int[256];
    private static int[] lookupTableGreen = new int[256];
    private static int[] lookupTableBlue = new int[256];

    public static BufferedImage loadImage(String path) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(path));
        } catch (IOException ex) {
            System.out.println("Error has occured during file reading: " + ex.getMessage());
        }
        return image;
    }

    public static void saveImage(BufferedImage img, String path) {
        try {
            ImageIO.write(img, "jpg", new File(path));
        } catch (IOException ex) {
            System.out.println("Error has occured during file writing: " + ex.getMessage());
        }
    }

    public static BufferedImage convertIconToImage(ImageIcon icon) {
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.createGraphics();
        icon.paintIcon(null, graphics, 0, 0);
        graphics.dispose();
        return image;
    }

    public static BufferedImage darkenImage(BufferedImage image) {
        int brightestValueRed = 0;
        int brightestValueGreen = 0;
        int brightestValueBlue = 0;

        for (int w = 0; w < image.getWidth(); w++) {
            for (int h = 0; h < image.getHeight(); h++) {
                Color color = new Color(image.getRGB(w, h));

                if (color.getRed() > brightestValueRed)
                    brightestValueRed = color.getRed();

                if (color.getGreen() > brightestValueGreen)
                    brightestValueGreen = color.getGreen();

                if (color.getBlue() > brightestValueBlue)
                    brightestValueBlue = color.getBlue();
            }
        }

        double normalizationFactorRed = 255 / Math.pow(brightestValueRed, 2);
        double normalizationFactorGreen = 255 / Math.pow(brightestValueGreen, 2);
        double normalizationFactorBlue = 255 / Math.pow(brightestValueBlue, 2);

        for (int i = 0; i < 256; i++) {
            int darkenedValueRed = (int) (normalizationFactorRed * Math.pow(i, 2));
            int darkenedValueGreen = (int) (normalizationFactorGreen * Math.pow(i, 2));
            int darkenedValueBlue = (int) (normalizationFactorBlue * Math.pow(i, 2));

            lookupTableRed[i] = darkenedValueRed;
            lookupTableGreen[i] = darkenedValueGreen;
            lookupTableBlue[i] = darkenedValueBlue;
        }

        List<int[]> lookupTables = Arrays.asList(lookupTableRed, lookupTableGreen, lookupTableBlue);

        return applyLutAtImage(image,lookupTables);
    }

    public static BufferedImage applyLutAtImage(BufferedImage image, List<int[]> lookupTables) {
        int[] lookupTableRed = lookupTables.get(0);
        int[] lookupTableGreen = lookupTables.get(1);
        int[] lookupTableBlue = lookupTables.get(2);

        for (int w = 0; w < image.getWidth(); w++) {
            for (int h = 0; h < image.getHeight(); h++) {
                Color color = new Color(image.getRGB(w, h));

                int red = lookupTableRed[color.getRed()];
                int green = lookupTableGreen[color.getGreen()];
                int blue = lookupTableBlue[color.getBlue()];

                Color newColor = new Color(red, green, blue);

                image.setRGB(w, h, newColor.getRGB());
            }
        }
        return image;
    }
}
