/**
 * 
 */
package org.jclouds.imagestore.benchmarks.bytepainter;

import java.awt.image.BufferedImage;
import java.util.Random;

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

    static int SIZE = 4096;
    static int RUNS = 25;

    byte[] data;
    BufferedImage image;

    int i = 22;

    public void before() {
        data = initializeData(i);
        // i++;
    }

    /**
     * Colored, 8 Pixel/1Byte
     */
    @Bench(beforeEachRun = "before")
    public void generateBinary() {
        IBytesToImagePainter painter = new BinaryBytesToImagePainter();
        store(painter);
    }

    /**
     * 2 Pixel/1Byte
     */
    @Bench(beforeEachRun = "before")
    public void generateHexadecimal() {
        IBytesToImagePainter painter = new HexadecimalBytesToImagePainter();
        store(painter);
    }

    // /**
    // * Colored, 1/2Pixel/1Byte, not working
    // */
    // @Bench(beforeEachRun = "before")
    // public void generateHexadecimalLayered() {
    // IBytesToImagePainter painter = new HexadecimalLayeredBytesToImagePainter();
    // store(painter);
    // }

    /**
     * 1 Pixel/ 1Byte, only serializing working
     */
    @Bench(beforeEachRun = "before")
    public void generateOctalLayeredColorAlternating() {
        IBytesToImagePainter painter = new OctalLayeredColorAlternatingBytesToImagePainter();
        store(painter);
    }

    /**
     * 4 Pixel/1Byte
     */
    @Bench(beforeEachRun = "before")
    public void generateQuaternary() {
        IBytesToImagePainter painter = new QuaternaryBytesToImagePainter();
        store(painter);
    }

    /**
     * 4/3 Pixel/1Byte
     */
    @Bench(beforeEachRun = "before")
    public void generateQuaternaryLayered() {
        IBytesToImagePainter painter = new QuaternaryLayeredBytesToImagePainter();
        store(painter);
    }

    /**
     * 3 Pixel/1Byte
     */
    @Bench(beforeEachRun = "before")
    public void generateSeptenary() {
        IBytesToImagePainter painter = new SeptenaryBytesToImagePainter();
        store(painter);
    }

    /**
     * 1 Pixel/1Byte
     */
    @Bench(beforeEachRun = "before")
    public void generateSeptenaryLayered() {
        IBytesToImagePainter painter = new SeptenaryLayeredBytesToImagePainter();
        store(painter);
    }

    private void store(IBytesToImagePainter painter) {
        ImageGenerator generator = new ImageGenerator(painter, SIZE, SIZE);
        image = generator.createImageFromBytes(data);
    }

    public byte[] initializeData(int i) {
        Random ran = new Random();
        byte[] data = new byte[1 << i];
        int j = data.length;
        ran.nextBytes(data);
        return data;
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
