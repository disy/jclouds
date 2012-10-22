/**
 * 
 */
package org.jclouds.imagestore.benchmarks.bytepainter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;

import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.ImageGenerator;
import org.jclouds.imagestore.imagegenerator.bytepainter.BinaryBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.bytepainter.HexadecimalBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.bytepainter.QuaternaryBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.bytepainter.QuaternaryLayeredBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.bytepainter.SeptenaryBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.bytepainter.SeptenaryLayeredBytesToImagePainter;
import org.perfidix.AbstractConfig;
import org.perfidix.Benchmark;
import org.perfidix.annotation.AfterEachRun;
import org.perfidix.annotation.BeforeBenchClass;
import org.perfidix.annotation.Bench;
import org.perfidix.element.KindOfArrangement;
import org.perfidix.meter.AbstractMeter;
import org.perfidix.meter.Time;
import org.perfidix.meter.TimeMeter;
import org.perfidix.ouput.AbstractOutput;
import org.perfidix.ouput.TabularSummaryOutput;
import org.perfidix.result.BenchmarkResult;

/**
 * Benchmark for different Image Generators
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public class ImageGeneratorBenchmark {

    // size, determining width as well as height
    static int SIZE = 1024;
    // runs of this benchmark
    static int RUNS = 10;

    // size of input-data, is power of 2
    static int BYTESIZE = 17;
    // path to store the pictures to
    static File PICFOLDER = new File("/Users/sebi/Desktop/images");
    static {
        new File(PICFOLDER, new StringBuilder(BYTESIZE).toString()).mkdirs();
    }

    // intermediate variables, needed for passing data between methods, just for convenience
    byte[] data;
    byte[] deserializedData;
    IBytesToImagePainter painter;
    BufferedImage image;
    String methodJustBenched = "";

    /**
     * Simple setting up the data.
     */
    @BeforeBenchClass
    public void before() {
        Random ran = new Random();
        data = new byte[1 << BYTESIZE];
        ran.nextBytes(data);
    }

    /**
     * Storing the data to a folder with a suitable name based on the painter utilized.
     */
    @AfterEachRun
    public void tearDown() {
        final File toStore = new File(PICFOLDER, new StringBuilder(BYTESIZE).toString());
        final File imageFile =
            new File(toStore, new StringBuilder(methodJustBenched).append(".png").toString());
        try {
            if (imageFile.exists()) {
                imageFile.delete();
            }
            imageFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(imageFile);
            ImageIO.write(image, "png", fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // ////////////////////////////////////////
    // // Benchmarking methods for generation
    // ////////////////////////////////////////
    /**
     * Colored, 8 Pixel/1Byte
     */
    @Bench
    public void generateBinary() {
        methodJustBenched = "binary";
        IBytesToImagePainter painter = new BinaryBytesToImagePainter();
        image = new ImageGenerator(painter, SIZE, SIZE).createImageFromBytes(data);
    }

    /**
     * 2 Pixel/1Byte
     */
    @Bench
    public void generateHexadecimal() {
        methodJustBenched = "hexadecimal";
        IBytesToImagePainter painter = new HexadecimalBytesToImagePainter();
        image = new ImageGenerator(painter, SIZE, SIZE).createImageFromBytes(data);
    }

    /**
     * 4 Pixel/1Byte
     */
    @Bench
    public void generateQuaternary() {
        methodJustBenched = "quaternary";
        IBytesToImagePainter painter = new QuaternaryBytesToImagePainter();
        image = new ImageGenerator(painter, SIZE, SIZE).createImageFromBytes(data);
    }

    /**
     * 4/3 Pixel/1Byte
     */
    @Bench
    public void generateQuaternaryLayered() {
        methodJustBenched = "quaternaryLayered";
        IBytesToImagePainter painter = new QuaternaryLayeredBytesToImagePainter();
        image = new ImageGenerator(painter, SIZE, SIZE).createImageFromBytes(data);
    }

    /**
     * 3 Pixel/1Byte
     */
    @Bench
    public void generateSeptenary() {
        methodJustBenched = "septenary";
        IBytesToImagePainter painter = new SeptenaryBytesToImagePainter();
        image = new ImageGenerator(painter, SIZE, SIZE).createImageFromBytes(data);
    }

    /**
     * 1 Pixel/1Byte
     */
    @Bench
    public void generateSeptenaryLayered() {
        methodJustBenched = "septenaryLayered";
        IBytesToImagePainter painter = new SeptenaryLayeredBytesToImagePainter();
        image = new ImageGenerator(painter, SIZE, SIZE).createImageFromBytes(data);
    }

    // ////////////////////////////////////////
    // // Benchmarking methods for degeneration
    // ////////////////////////////////////////

    /**
     * Colored, 8 Pixel/1Byte
     */
    @Bench(beforeEachRun = "setUpBinary", afterEachRun = "check")
    public void degenerateBinary() {
        methodJustBenched = "binary";
        painter = new BinaryBytesToImagePainter();
        deserializedData = new ImageGenerator(painter, SIZE, SIZE).getBytesFromImage(image);
    }

    /**
     * 2 Pixel/1Byte
     */
    @Bench(beforeEachRun = "setUpHexadecimal", afterEachRun = "check")
    public void degenerateHexadecimal() {
        methodJustBenched = "hexadecimal";
        painter = new HexadecimalBytesToImagePainter();
        deserializedData = new ImageGenerator(painter, SIZE, SIZE).getBytesFromImage(image);
    }

    /**
     * 4 Pixel/1Byte
     */
    @Bench(beforeEachRun = "setupQuaternary", afterEachRun = "check")
    public void degenerateQuaternary() {
        methodJustBenched = "quaternary";
        painter = new QuaternaryBytesToImagePainter();
        deserializedData = new ImageGenerator(painter, SIZE, SIZE).getBytesFromImage(image);
    }

    /**
     * 4/3 Pixel/1Byte
     */
    @Bench(beforeEachRun = "setupQuaternaryLayered", afterEachRun = "check")
    public void degenerateQuaternaryLayered() {
        methodJustBenched = "quaternaryLayered";
        painter = new QuaternaryLayeredBytesToImagePainter();
        deserializedData = new ImageGenerator(painter, SIZE, SIZE).getBytesFromImage(image);
    }

    /**
     * 3 Pixel/1Byte
     */
    @Bench(beforeEachRun = "generateSeptenary", afterEachRun = "check")
    public void degenerateSeptenary() {
        methodJustBenched = "septenary";
        painter = new SeptenaryBytesToImagePainter();
        deserializedData = new ImageGenerator(painter, SIZE, SIZE).getBytesFromImage(image);
    }

    // ////////////////////////////////////////
    // // setup for degeneration
    // ////////////////////////////////////////

    public void setUpBinary() {
        generateBinary();
        tearDown();
        deserialize();
    }

    public void setUpHexadecimal() {
        generateHexadecimal();
        tearDown();
        deserialize();
    }

    public void setupQuaternary() {
        generateQuaternary();
        tearDown();
        deserialize();
    }

    public void setupQuaternaryLayered() {
        generateQuaternaryLayered();
        tearDown();
        deserialize();
    }

    public void setupSeptenary() {
        generateSeptenary();
        tearDown();
        deserialize();
    }

    private void deserialize() {
        final File toStore = new File(PICFOLDER, new StringBuilder(BYTESIZE).toString());
        final File imageFile =
            new File(toStore, new StringBuilder(methodJustBenched).append(".png").toString());
        try {
            FileInputStream fis = new FileInputStream(imageFile);
            image = ImageIO.read(fis);
            fis.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void check() {
        if (!Arrays.equals(data, deserializedData)) {
            throw new RuntimeException(new StringBuilder("Arrays differ for painter ").append(
                painter.toString()).toString());
        }
    }

    // ////////////////////////////////////////
    // // Main and Config for Perfidix
    // ////////////////////////////////////////

    public static void main(String[] args) {
        Benchmark benchmark = new Benchmark(new Config());
        benchmark.add(ImageGeneratorBenchmark.class);
        final BenchmarkResult res = benchmark.run();
        new TabularSummaryOutput().visitBenchmark(res);
    }

    static class Config extends AbstractConfig {

        static AbstractMeter[] METERS = {
            new TimeMeter(Time.MilliSeconds)
        };
        static AbstractOutput[] OUTPUT = {};
        static KindOfArrangement ARRAN = KindOfArrangement.SequentialMethodArrangement;
        static double GCPROB = 1.0d;

        /**
         * Public constructor.
         */
        public Config() {
            super(RUNS, METERS, OUTPUT, ARRAN, GCPROB);

        }

    }

}
