/**
 * 
 */
package org.jclouds.imagestore.benchmarks.bytepainter;

import java.util.Random;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.imagestore.TestAndBenchmarkHelper;
import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.IEncoder;
import org.jclouds.imagestore.imagegenerator.bytepainter.LayeredBytesToImagePainter;
import org.jclouds.imagestore.imagehoster.IImageHost;
import org.jclouds.imagestore.imagehoster.file.ImageHostFile;
import org.perfidix.AbstractConfig;
import org.perfidix.Benchmark;
import org.perfidix.annotation.Bench;
import org.perfidix.element.KindOfArrangement;
import org.perfidix.meter.AbstractMeter;
import org.perfidix.meter.MemMeter;
import org.perfidix.meter.Memory;
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
public class LayeredPainterBenchmark {

    private static final Random RAN = new Random(12l);

    // //SETTINGS TO SET MANUALLY
    private static final Class<? extends IImageHost> HOST = ImageHostFile.class;
    private static final Class<? extends IBytesToImagePainter> PAINTER = LayeredBytesToImagePainter.class;
    private static final Class<? extends IEncoder> ENCODER = IEncoder.DummyEncoder.class;

    private static BlobStore store;

    private byte[] data;

    static int i = 0;

    public void setUp10() {
        setUpData(10);
        store.createContainerInLocation(null, Integer.toString(10));
        i = 0;
    }
    
    public void tearDown10() {
        store.deleteContainer(Integer.toString(10));
    }

    @Bench(beforeFirstRun = "setUp10", afterLastRun="tearDown10")
    public void upload10() {
        BlobBuilder blobbuilder =
            store.blobBuilder(new StringBuilder(PAINTER.getSimpleName()).append(":").append(ENCODER.getSimpleName())
                .append(i++).toString());
        Blob blob = blobbuilder.build();
        blob.setPayload(data);
        store.putBlob(Integer.toString(10), blob);
    }

    @Bench(beforeFirstRun = "setUp10, upload10", afterLastRun="tearDown10")
    public void download10() {
        store.getBlob(Integer.toString(10), new StringBuilder(PAINTER.getSimpleName()).append(":").append(
            ENCODER.getSimpleName()).append(--i).toString());
    }

    public void setUp11() {
        setUpData(10);
        store.deleteContainer(Integer.toString(11));
        store.createContainerInLocation(null, Integer.toString(11));
        i = 0;
    }
    
    public void tearDown11() {
        store.deleteContainer(Integer.toString(10));
    }

    @Bench(beforeFirstRun = "setUp11", afterLastRun="tearDown11")
    public void upload11() {
        BlobBuilder blobbuilder =
            store.blobBuilder(new StringBuilder(PAINTER.getSimpleName()).append(":").append(ENCODER.getSimpleName())
                .append(i++).toString());
        Blob blob = blobbuilder.build();
        blob.setPayload(data);
        store.putBlob(Integer.toString(11), blob);
    }

    @Bench(beforeFirstRun = "setUp11, upload11", afterLastRun="tearDown11")
    public void download11() {
        store.getBlob(Integer.toString(11), new StringBuilder(PAINTER.getSimpleName()).append(":").append(
            ENCODER.getSimpleName()).append(--i).toString());
    }

    private static BlobStore setUpContext(int layers) {
        return TestAndBenchmarkHelper.createContext(HOST, PAINTER, ENCODER, layers).getBlobStore();
    }

    private void setUpData(int size) {
        data = new byte[1 << size];
        RAN.nextBytes(data);
    }

    public static void main(String[] args) {

        store = setUpContext(2);
        Benchmark bench = new Benchmark(new BenchmarkConf());
        bench.add(LayeredPainterBenchmark.class);
        BenchmarkResult res = bench.run();
        TabularSummaryOutput output = new TabularSummaryOutput();
        output.visitBenchmark(res);
    }

    static class BenchmarkConf extends AbstractConfig {

        private final static int RUNS = 5;
        private final static AbstractMeter[] METERS = {
            new TimeMeter(Time.MilliSeconds)
        };
        private final static AbstractOutput[] OUTPUT = {/*
                                                         * new
                                                         * TabularSummaryOutput()
                                                         */};
        private final static KindOfArrangement ARRAN = KindOfArrangement.SequentialMethodArrangement;
        private final static double GCPROB = 1.0d;

        /**
         * Public constructor.
         */
        public BenchmarkConf() {
            super(RUNS, METERS, OUTPUT, ARRAN, GCPROB);

        }

    }

}
