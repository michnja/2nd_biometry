package pl.edu.pb.wi.histogram;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public abstract class HistogramOperations {

    public static List<double[]> calculateHistograms(BufferedImage image) {
        double[] histogramRed = new double[256];
        double[] histogramGreen = new double[256];
        double[] histogramBlue = new double[256];
        double[] histogramCombined = new double[256];

        for (int w = 0; w < image.getWidth(); w++) {
            for (int h = 0; h < image.getHeight(); h++) {
                Color color = new Color(image.getRGB(w, h));
                histogramRed[color.getRed()]++;
                histogramGreen[color.getGreen()]++;
                histogramBlue[color.getBlue()]++;
            }
        }

        for (int i = 0; i < histogramCombined.length; i++) {
            histogramCombined[i] = (histogramRed[i] + histogramGreen[i] + histogramBlue[i]) / 3;
        }

        List<double[]> histograms = new ArrayList<>();
        histograms.add(histogramRed);
        histograms.add(histogramGreen);
        histograms.add(histogramBlue);
        histograms.add(histogramCombined);

        return histograms;
    }
}
