/**
 * 
 */
package org.jclouds.imagestore.benchmarks.bytepainter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.imagestore.TestAndBenchmarkHelper;
import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.IEncoder;
import org.jclouds.imagestore.imagegenerator.bytepainter.LayeredBytesToImagePainter;
import org.jclouds.imagestore.imagehoster.IImageHost;
import org.jclouds.imagestore.imagehoster.picasa.ImageHostGoogleDataApiPicasa;
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

import com.google.common.io.ByteStreams;

/**
 * Benchmark for different Image Generators
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public class LayeredPainterBenchmark {

    private static final Random RAN = new Random(12l);

    // //SETTINGS TO SET MANUALLY
    private static final Class<? extends IImageHost> HOST = ImageHostGoogleDataApiPicasa.class;
    private static final Class<? extends IBytesToImagePainter> PAINTER = LayeredBytesToImagePainter.class;
    private static final Class<? extends IEncoder> ENCODER = IEncoder.DummyEncoder.class;

    private static BlobStore store;

    private byte[] data;

    int dataFactor = 10;

    int currentRun = 0;

    int name = 0;

    public void setUp() {
        name = 0;
        setUpData(dataFactor + currentRun);
        currentRun++;
        store.deleteContainer(new StringBuilder("grave9283").append(dataFactor + currentRun).toString());
        store.createContainerInLocation(null, new StringBuilder("grave9283").append(dataFactor + currentRun)
            .toString());
    }

    @Bench(beforeFirstRun = "setUp")
    public void upload10() {
        upload();
        name++;
    }

    @Bench
    public void download10() {
        name--;
        download();
    }

    @Bench(beforeFirstRun = "setUp")
    public void upload11() {
        upload();
        name++;
    }

    @Bench
    public void download11() {
        name--;
        download();
    }

    @Bench(beforeFirstRun = "setUp")
    public void upload12() {
        upload();
        name++;
    }

    @Bench
    public void download12() {
        name--;
        download();
    }

    @Bench(beforeFirstRun = "setUp")
    public void upload13() {
        upload();
        name++;
    }

    @Bench
    public void download13() {
        name--;
        download();
    }

    private void upload() {
        BlobBuilder blobbuilder =
            store.blobBuilder(new StringBuilder(PAINTER.getSimpleName()).append(":").append(
                ENCODER.getSimpleName()).append(":").append(name).toString());
        Blob blob = blobbuilder.build();
        blob.setPayload(data);
        store.putBlob(new StringBuilder("grave9283").append(dataFactor + currentRun).toString(), blob);
    }

    private void download() {
        Blob blob =
            store.getBlob(new StringBuilder("grave9283").append(dataFactor + currentRun).toString(),
                new StringBuilder(PAINTER.getSimpleName()).append(":").append(ENCODER.getSimpleName())
                    .append(":").append(name).toString());
        InputStream in = blob.getPayload().getInput();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ByteStreams.copy(in, out);
            data = out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setUpData(int size) {
        data = new byte[1 << size];
        RAN.nextBytes(data);
    }

    public static void main(String[] args) {

        store = TestAndBenchmarkHelper.createContext(HOST, PAINTER, ENCODER, 2).getBlobStore();
        // BlobStoreContext context =
        // new BlobStoreContextFactory().createContext("aws-s3", "",
        // "");
//        store = context.getBlobStore();
        // store.blobBuilder("bla");
        //
        // BlobBuilder blobbuilder =
        // store.blobBuilder(new StringBuilder(PAINTER.getSimpleName()).append(":").append(
        // ENCODER.getSimpleName()).append(":").append(1).toString());
        // Blob blob = blobbuilder.build();
        // byte[] blubb = {
        // 123, 123, 123, 123
        // };
        // blob.setPayload(blubb);
        // store.putBlob("grave9283", blob);
        //

        Benchmark bench = new Benchmark(new BenchmarkConf());
        bench.add(LayeredPainterBenchmark.class);
        BenchmarkResult res = bench.run();
        TabularSummaryOutput output = new TabularSummaryOutput();
        output.visitBenchmark(res);

//        context.close();
    }

    static class BenchmarkConf extends AbstractConfig {

        private final static int RUNS = 25;
        private final static AbstractMeter[] METERS = {
            new TimeMeter(Time.MilliSeconds)
        };
        private final static AbstractOutput[] OUTPUT = {/*
                                                         * new
                                                         * TabularSummaryOutput()
                                                         */};
        private final static KindOfArrangement ARRAN = KindOfArrangement.NoArrangement;
        private final static double GCPROB = 1.0d;

        /**
         * Public constructor.
         */
        public BenchmarkConf() {
            super(RUNS, METERS, OUTPUT, ARRAN, GCPROB);

        }

    }

}
