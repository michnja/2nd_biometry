package pl.edu.pb.wi.viewer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYBarDataset;
import pl.edu.pb.wi.histogram.HistogramEquation;
import pl.edu.pb.wi.histogram.HistogramOperations;
import pl.edu.pb.wi.shared.ImageSharedOperations;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class Viewer extends JFrame {

    private final JMenuBar menuBar = new JMenuBar();
    private final JMenu files = new JMenu("File");
    private final JMenu histogram = new JMenu("Histograms");
    private final JMenuItem loadImage = new JMenuItem("Load image");
    private final JMenuItem saveImage = new JMenuItem("Save image");
    private final JMenuItem calculateHistograms = new JMenuItem("Calculate histograms");
    private final JMenuItem showHistograms = new JMenuItem("Show histograms");
    private final JMenuItem darkenImage = new JMenuItem("Darken Image");
    private final JMenuItem equateHistogram = new JMenuItem("Equate Histogram");
    private final JLabel imageLabel = new JLabel();

    public Viewer() {
        this.setLayout(new BorderLayout());
        this.setTitle("Podstawy Biometrii");
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);

        this.menuBar.add(this.files);
        this.menuBar.add(this.histogram);
        this.files.add(this.loadImage);
        this.files.add(this.saveImage);
        this.histogram.add(this.calculateHistograms);
        this.histogram.add(this.showHistograms);
        this.histogram.add(this.darkenImage);
        this.histogram.add(this.equateHistogram);

        this.add(this.menuBar, BorderLayout.NORTH);
        this.add(this.imageLabel, BorderLayout.CENTER);
        this.imageLabel.setHorizontalAlignment(JLabel.CENTER);
        this.imageLabel.setVerticalAlignment(JLabel.CENTER);

        this.loadImage.addActionListener((ActionEvent e) -> {
            JFileChooser imageOpener = new JFileChooser();
            imageOpener.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    String fileName = f.getName().toLowerCase();
                    if (fileName.endsWith(".jpg") || fileName.endsWith(".png")
                            || fileName.endsWith(".tiff") || fileName.endsWith(".jpeg")) {
                        return true;
                    } else return false;
                }

                @Override
                public String getDescription() {
                    return "Image files (.jpg, .png, .tiff)";
                }
            });

            int returnValue = imageOpener.showDialog(null, "Select image");
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                BufferedImage img = ImageSharedOperations.loadImage(imageOpener.getSelectedFile().getPath());
                this.imageLabel.setIcon(new ImageIcon(img));
            }
        });

        this.saveImage.addActionListener((ActionEvent e) -> {
            String path = "./image.jpg";
            BufferedImage img = ImageSharedOperations.convertIconToImage((ImageIcon) this.imageLabel.getIcon());
            ImageSharedOperations.saveImage(img, path);
        });

        this.calculateHistograms.addActionListener((ActionEvent e) -> {
            BufferedImage img = ImageSharedOperations.convertIconToImage((ImageIcon) this.imageLabel.getIcon());
            HistogramOperations.calculateHistograms(img);
        });

        this.showHistograms.addActionListener(e -> {
            BufferedImage image = ImageSharedOperations.convertIconToImage((ImageIcon) imageLabel.getIcon());
            List<double[]> histograms = HistogramOperations.calculateHistograms(image);

            showHistogram(histograms);

        });

        this.darkenImage.addActionListener(e -> {
            BufferedImage image = ImageSharedOperations.convertIconToImage((ImageIcon) imageLabel.getIcon());

            BufferedImage darkenedImage = ImageSharedOperations.darkenImage(image);
            imageLabel.setIcon(new ImageIcon(darkenedImage));

            List<double[]> darkenedHistograms = HistogramOperations.calculateHistograms(darkenedImage);
            showHistogram(darkenedHistograms);
        });

        this.equateHistogram.addActionListener(e -> {
            BufferedImage image = ImageSharedOperations.convertIconToImage((ImageIcon) imageLabel.getIcon());

            BufferedImage modifiedImage = HistogramEquation.equate(image);
            imageLabel.setIcon(new ImageIcon(modifiedImage));

            List<double[]> equatedHistograms = HistogramOperations.calculateHistograms(modifiedImage);
            showHistogram(equatedHistograms);
        });
    }


    private void showHistogram(List<double[]> histograms) {
        double[] x = new double[256];
        for (int i = 0; i < 256; i++) x[i] = i;

        double[][] seriesRed = new double[2][];
        double[][] seriesBlue = new double[2][];
        double[][] seriesGreen = new double[2][];
        double[][] seriesCombined = new double[2][];
        seriesRed[0] = x;
        seriesRed[1] = histograms.get(0);
        seriesBlue[0] = x;
        seriesBlue[1] = histograms.get(1);
        seriesGreen[0] = x;
        seriesGreen[1] = histograms.get(2);
        seriesCombined[0] = x;
        seriesCombined[1] = histograms.get(3);

        DefaultXYDataset dataset = new DefaultXYDataset();
        dataset.addSeries("Red", seriesRed);
        dataset.addSeries("Green", seriesGreen);
        dataset.addSeries("Blue", seriesBlue);
        dataset.addSeries("Combined", seriesCombined);

        XYBarDataset barDataset = new XYBarDataset(dataset,0.8);


        JFreeChart chart = ChartFactory.createXYBarChart(null, "colors", false, "count", barDataset,
                PlotOrientation.VERTICAL, true, true, false);

        XYPlot plot = (XYPlot) chart.getPlot();
        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardXYBarPainter());

        Paint[] paintArray = {
                new Color(0x80ff0000, true),
                new Color(0x8000ff00, true),
                new Color(0x800000ff, true),
                new Color(0xB6343434, true)
        };

        plot.setDrawingSupplier(new DefaultDrawingSupplier(
                paintArray,
                DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));

        ChartFrame frame = new ChartFrame("Histograms", chart);
        frame.pack();
        frame.setVisible(true);
    }
}
