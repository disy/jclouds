/**
 * 
 */
package org.jclouds.imagestore.benchmarks;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jclouds.imagestore.TestAndBenchmarkHelper;
import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.IEncoder;
import org.jclouds.imagestore.imagegenerator.ImageExtractor;
import org.jclouds.imagestore.imagegenerator.ImageGenerator;
import org.perfidix.AbstractConfig;
import org.perfidix.Benchmark;
import org.perfidix.annotation.Bench;
import org.perfidix.element.KindOfArrangement;
import org.perfidix.meter.AbstractMeter;
import org.perfidix.meter.Time;
import org.perfidix.meter.TimeMeter;
import org.perfidix.ouput.AbstractOutput;
import org.perfidix.ouput.CSVOutput;
import org.perfidix.ouput.TabularSummaryOutput;
import org.perfidix.result.BenchmarkResult;

/**
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public class PainterBenchmark {

    private static final Random RAN = new Random(12l);

    private static ImageGenerator generator;
    
    private final static ImageExtractor extractor = new ImageExtractor();

    private byte[] data;

    private BufferedImage image;

    int dataFactor = 10;

    int currentRun = 0;

    int name = 0;

    public void setUp() {
        name = 0;
        setUpData(dataFactor + currentRun);
        currentRun++;
        // stabilizing the system
        encode();
        name++;
        encode();
        name++;
    }

    public void decodeSetUp() {
        // stabilizing the system
        name--;
        encode();
        encode();
    }

    @Bench(beforeFirstRun = "setUp")
    public void encode10() {
        encode();
        name++;
    }

    @Bench(beforeFirstRun = "decodeSetUp")
    public void decode10() {
        name--;
        decode();
    }

    @Bench(beforeFirstRun = "setUp")
    public void encode11() {
        encode();
        name++;
    }

    @Bench(beforeFirstRun = "decodeSetUp")
    public void decode11() {
        name--;
        decode();
    }

    @Bench(beforeFirstRun = "setUp")
    public void encode12() {
        encode();
        name++;
    }

    @Bench(beforeFirstRun = "decodeSetUp")
    public void decode12() {
        name--;
        decode();
    }

    @Bench(beforeFirstRun = "setUp")
    public void encode13() {
        encode();
        name++;
    }

    @Bench(beforeFirstRun = "decodeSetUp")
    public void decode13() {
        name--;
        decode();
    }

    @Bench(beforeFirstRun = "setUp")
    public void encode14() {
        encode();
        name++;
    }

    @Bench(beforeFirstRun = "decodeSetUp")
    public void decode14() {
        name--;
        decode();
    }

    @Bench(beforeFirstRun = "setUp")
    public void encode15() {
        encode();
        name++;
    }

    @Bench(beforeFirstRun = "decodeSetUp")
    public void decode15() {
        name--;
        decode();
    }

    @Bench(beforeFirstRun = "setUp")
    public void encode16() {
        encode();
        name++;
    }

    @Bench(beforeFirstRun = "decodeSetUp")
    public void decode16() {
        name--;
        decode();
    }

    @Bench(beforeFirstRun = "setUp")
    public void encode17() {
        encode();
        name++;
    }

    @Bench(beforeFirstRun = "decodeSetUp")
    public void decode17() {
        name--;
        decode();
    }

    @Bench(beforeFirstRun = "setUp")
    public void encode18() {
        encode();
        name++;
    }

    @Bench(beforeFirstRun = "decodeSetUp")
    public void decode18() {
        name--;
        decode();
    }

    @Bench(beforeFirstRun = "setUp")
    public void encode19() {
        encode();
        name++;
    }

    @Bench(beforeFirstRun = "decodeSetUp")
    public void decode19() {
        name--;
        decode();
    }

    @Bench(beforeFirstRun = "setUp")
    public void encode20() {
        encode();
        name++;
    }

    @Bench(beforeFirstRun = "decodeSetUp")
    public void decode20() {
        name--;
        decode();
    }

    private void encode() {
        image = generator.createImageFromBytes(data);
    }

    private void decode() {
        data = extractor.getBytesFromImage(image);
    }

    private void setUpData(int size) {
        data = new byte[1 << size];
        RAN.nextBytes(data);
    }

    public static void main(String[] args) {
        File csvStore = new File(System.getProperty("user.home"), "painters");
        List<IBytesToImagePainter> painters = TestAndBenchmarkHelper.getNormalPainters();

        IEncoder encoder = new IEncoder.DummyEncoder();

        for (final IBytesToImagePainter painter : painters) {

            generator = new ImageGenerator(painter, encoder, 16384, 16384);

            File paintercsv = new File(csvStore, painter.toString());
            paintercsv.mkdirs();

            System.out.println("++++++++++++++++++++++++++++++++++++");
            System.out.println(painter.toString());
            System.out.println("++++++++++++++++++++++++++++++++++++");
            Benchmark bench = new Benchmark(new BenchmarkConf());
            bench.add(PainterBenchmark.class);
            BenchmarkResult res = bench.run();
            TabularSummaryOutput output = new TabularSummaryOutput();
            CSVOutput output2 = new CSVOutput(paintercsv);
            output.visitBenchmark(res);
            output2.visitBenchmark(res);
        }

    }

    static class BenchmarkConf extends AbstractConfig {

        private final static int RUNS = 5;
        private final static Set<AbstractMeter> METERS = new HashSet<AbstractMeter>();

        private final static Set<AbstractOutput> OUTPUT = new HashSet<AbstractOutput>();
        private final static KindOfArrangement ARRAN = KindOfArrangement.NoArrangement;
        private final static double GCPROB = 1.0d;

        static {
            METERS.add(new TimeMeter(Time.MilliSeconds));
        };

        /**
         * Public constructor.
         */
        public BenchmarkConf() {
            super(RUNS, METERS, OUTPUT, ARRAN, GCPROB);

        }

    }

}
