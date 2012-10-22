/**
 * 
 */
package org.jclouds.imagestore.benchmarks.bytepainter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.ImageGenerator;
import org.jclouds.imagestore.imagegenerator.bytepainter.BinaryBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.bytepainter.HexadecimalBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.bytepainter.OctalLayeredColorAlternatingBytesToImagePainter;
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

    static int SIZE = 128;
    static int RUNS = 10;

    static int BYTESIZE = 9;
    static File PICFOLDER = new File("/Users/sebi/Desktop/images");
    static {
        new File(PICFOLDER, new StringBuilder(BYTESIZE).toString()).mkdirs();
    }

    byte[] data;
    BufferedImage image;
    String methodJustBenched = "";

    @BeforeBenchClass
    public void before() {
        Random ran = new Random();
        data = new byte[1 << BYTESIZE];
        ran.nextBytes(data);
    }

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

    /**
     * Colored, 8 Pixel/1Byte
     */
    @Bench
    public void generateBinary() {
        methodJustBenched = "binary";
        IBytesToImagePainter painter = new BinaryBytesToImagePainter();
        store(painter);
    }

    /**
     * 2 Pixel/1Byte
     */
    @Bench
    public void generateHexadecimal() {
        methodJustBenched = "hexadecimal";
        IBytesToImagePainter painter = new HexadecimalBytesToImagePainter();
        store(painter);
    }

    // /**
    // * Colored, 1/2Pixel/1Byte, not working
    // */
    // @Bench
    // public void generateHexadecimalLayered() {
    // IBytesToImagePainter painter = new HexadecimalLayeredBytesToImagePainter();
    // store(painter);
    // }

    /**
     * 1 Pixel/ 1Byte, only serializing working
     */
    @Bench
    public void generateOctalLayeredColorAlternating() {
        methodJustBenched = "octalLayeredColorAlternating";
        IBytesToImagePainter painter = new OctalLayeredColorAlternatingBytesToImagePainter();
        store(painter);
    }

    /**
     * 4 Pixel/1Byte
     */
    @Bench
    public void generateQuaternary() {
        methodJustBenched = "quaternary";
        IBytesToImagePainter painter = new QuaternaryBytesToImagePainter();
        store(painter);
    }

    /**
     * 4/3 Pixel/1Byte
     */
    @Bench
    public void generateQuaternaryLayered() {
        methodJustBenched = "quaternaryLayered";
        IBytesToImagePainter painter = new QuaternaryLayeredBytesToImagePainter();
        store(painter);
    }

    /**
     * 3 Pixel/1Byte
     */
    @Bench
    public void generateSeptenary() {
        methodJustBenched = "septenary";
        IBytesToImagePainter painter = new SeptenaryBytesToImagePainter();
        store(painter);
    }

    /**
     * 1 Pixel/1Byte
     */
    @Bench
    public void generateSeptenaryLayered() {
        methodJustBenched = "septenaryLayered";
        IBytesToImagePainter painter = new SeptenaryLayeredBytesToImagePainter();
        store(painter);
    }

    private void store(IBytesToImagePainter painter) {
        ImageGenerator generator = new ImageGenerator(painter, SIZE, SIZE);
        image = generator.createImageFromBytes(data);
    }

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
