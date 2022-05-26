package pl.edu.pb.wi.histogram;

import pl.edu.pb.wi.shared.ImageSharedOperations;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

public abstract class HistogramEquation {

    public static BufferedImage equate(BufferedImage image) {
        List<double[]> cumulativeDistributions = cumulativeDistribution(image);

        int[] lookupTableRed = new int[256];
        int[] lookupTableGreen = new int[256];
        int[] lookupTableBlue = new int[256];

        double[] cumulativeDistributionRed = cumulativeDistributions.get(0);
        double[] cumulativeDistributionGreen = cumulativeDistributions.get(1);
        double[] cumulativeDistributionBlue = cumulativeDistributions.get(2);

        double cumulativeDistributionRedMin = Double.MAX_VALUE;
        double cumulativeDistributionGreenMin = Double.MAX_VALUE;
        double cumulativeDistributionBlueMin = Double.MAX_VALUE;

        for (int i = 0; i < 256; i++) {
            if (cumulativeDistributionRed[i] < cumulativeDistributionRedMin && cumulativeDistributionRed[i] > 0)
                cumulativeDistributionRedMin = cumulativeDistributionRed[i];

            if (cumulativeDistributionGreen[i] < cumulativeDistributionGreenMin && cumulativeDistributionGreen[i] > 0)
                cumulativeDistributionGreenMin = cumulativeDistributionGreen[i];

            if (cumulativeDistributionBlue[i] < cumulativeDistributionBlueMin && cumulativeDistributionBlue[i] > 0)
                cumulativeDistributionBlueMin = cumulativeDistributionBlue[i];
        }

        for (int i = 0; i < 256; i++) {
            int modifiedRed = (int) (((cumulativeDistributionRed[i] - cumulativeDistributionRedMin) /
                    (1 - cumulativeDistributionRedMin)) * 255);
            int modifiedGreen = (int) (((cumulativeDistributionGreen[i] - cumulativeDistributionGreenMin) /
                    (1 - cumulativeDistributionGreenMin)) * 255);
            int modifiedBlue = (int) (((cumulativeDistributionBlue[i] - cumulativeDistributionBlueMin) /
                    (1 - cumulativeDistributionBlueMin)) * 255);

            lookupTableRed[i] = modifiedRed;
            lookupTableGreen[i] = modifiedGreen;
            lookupTableBlue[i] = modifiedBlue;
        }

        List<int[]> lookupTables = Arrays.asList(lookupTableRed, lookupTableGreen, lookupTableBlue);

        return ImageSharedOperations.applyLutAtImage(image, lookupTables);
    }

    private static List<double[]> cumulativeDistribution(BufferedImage image) {
        double[] cumulativeDistributionRed = new double[256];
        double[] cumulativeDistributionGreen = new double[256];
        double[] cumulativeDistributionBlue = new double[256];
        int pixelsInImage = image.getHeight() * image.getWidth();

        double cumulativeDistributionRedSum = 0;
        double cumulativeDistributionGreenSum = 0;
        double cumulativeDistributionBlueSum = 0;

        List<double[]> histograms = HistogramOperations.calculateHistograms(image);
        double[] histogramRed = histograms.get(0);
        double[] histogramGreen = histograms.get(1);
        double[] histogramBlue = histograms.get(2);

        for (int i = 0; i < 256; i++) {
            cumulativeDistributionRedSum = cumulativeDistributionRedSum + (histogramRed[i] / pixelsInImage);
            cumulativeDistributionRed[i] = cumulativeDistributionRedSum;

            cumulativeDistributionGreenSum = cumulativeDistributionGreenSum + (histogramGreen[i] / pixelsInImage);
            cumulativeDistributionGreen[i] = cumulativeDistributionGreenSum;

            cumulativeDistributionBlueSum = cumulativeDistributionBlueSum + (histogramBlue[i] / pixelsInImage);
            cumulativeDistributionBlue[i] = cumulativeDistributionBlueSum;
        }
        return Arrays.asList(cumulativeDistributionRed, cumulativeDistributionGreen, cumulativeDistributionBlue);
    }
}
