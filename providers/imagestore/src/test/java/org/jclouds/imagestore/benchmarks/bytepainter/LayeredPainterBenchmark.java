/**
 * 
 */
package org.jclouds.imagestore.benchmarks.bytepainter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.imagestore.SyncImageBlobStore;
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
    private static Class<? extends IEncoder> ENCODER = IEncoder.DummyEncoder.class;
    private static final File IMAGESTORE = new File(System.getProperty("user.home"), "imagestore");

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
        saveOrigImage();
    }

    public void tearDown() {
        saveHostedImage();
        store.deleteContainer(new StringBuilder("grave9283").append(dataFactor + currentRun).toString());
    }

    @Bench(beforeFirstRun = "setUp")
    public void upload10() {
        upload();
        name++;
    }

    @Bench(afterLastRun = "tearDown")
    public void download10() {
        name--;
        download();
    }

    // @Bench(beforeFirstRun = "setUp")
    // public void upload11() {
    // upload();
    // name++;
    // }
    //
    // @Bench
    // public void download11() {
    // name--;
    // download();
    // }
    //
    // @Bench(beforeFirstRun = "setUp")
    // public void upload12() {
    // upload();
    // name++;
    // }
    //
    // @Bench
    // public void download12() {
    // name--;
    // download();
    // }
    //
    // @Bench(beforeFirstRun = "setUp")
    // public void upload13() {
    // upload();
    // name++;
    // }
    //
    // @Bench
    // public void download13() {
    // name--;
    // download();
    // }

    private void upload() {
        BlobBuilder blobbuilder =
            store.blobBuilder(new StringBuilder("grave9283").append(":").append(
                ((SyncImageBlobStore)store).getImageGenerator().getPainter().toString()).append(":").append(
                name).toString());
        Blob blob = blobbuilder.build();
        blob.setPayload(data);
        store.putBlob(new StringBuilder("grave9283").append(dataFactor + currentRun).toString(), blob);

    }

    private void download() {
        Blob blob =
            store.getBlob(new StringBuilder("grave9283").append(dataFactor + currentRun).toString(),
                new StringBuilder("grave9283").append(":").append(
                    ((SyncImageBlobStore)store).getImageGenerator().getPainter().toString()).append(":")
                    .append(name).toString());
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

    private void saveOrigImage() {
        // storing the actual image somewhere
        SyncImageBlobStore blobStore = (SyncImageBlobStore)store;
        BufferedImage img = blobStore.getImageGenerator().createImageFromBytes(data);
        File rawStore = new File(IMAGESTORE.getAbsolutePath(), "rawPictures");
        rawStore.mkdirs();
        File toStore =
            new File(rawStore.getAbsolutePath(), blobStore.getImageGenerator().getPainter().toString() + ":"
                + (dataFactor + currentRun) + ".png");
        saveBufferedImage(toStore, img);
    }

    private void saveHostedImage() {
        SyncImageBlobStore blobStore = (SyncImageBlobStore)store;

        File rawStore =
            new File(IMAGESTORE.getAbsolutePath(), blobStore.getImageHost().getClass().getSimpleName());
        rawStore.mkdirs();

        String imageSetTitle = new StringBuilder("grave9283").append(dataFactor + currentRun).toString();
        String imageTitle =
            new StringBuilder("grave9283").append(":").append(
                ((SyncImageBlobStore)store).getImageGenerator().getPainter().toString()).append(":").toString();

        Set<String> images = blobStore.getImageHost().imageSetContent(imageSetTitle);
        int i = 0;
        for (String image : images) {
            if (image.startsWith(imageTitle)) {
                BufferedImage img = blobStore.getImageHost().downloadImage(imageSetTitle, image);
                File toStore =
                    new File(rawStore.getAbsolutePath(), blobStore.getImageGenerator().getPainter()
                        .toString()
                        + ":" + (dataFactor + currentRun) + ":" + i + ".png");
                saveBufferedImage(toStore, img);
                i++;
            }
        }

    }

    private static void saveBufferedImage(final File file, final BufferedImage img) {
        try {
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file);
            ImageIO.write(img, "png", fos);
            fos.flush();
            fos.close();
        } catch (Exception exc) {
            exc.toString();
        }
    }

    public static void main(String[] args) {

        Class<? extends IImageHost> host = ImageHostFile.class;
        System.out.println("=================================");
        System.out.println(host.toString());
        for (final IBytesToImagePainter painter : TestAndBenchmarkHelper.getAllPainters()) {
            System.out.println("++++++++++++++++++++++++++++++++++++");
            System.out.println(painter.toString());
            System.out.println("++++++++++++++++++++++++++++++++++++");
            if (painter instanceof LayeredBytesToImagePainter) {
                int layers = ((LayeredBytesToImagePainter)painter).getNumSys();
                store =
                    TestAndBenchmarkHelper.createContext(host, painter.getClass(), ENCODER, layers)
                        .getBlobStore();
            } else {
                store =
                    TestAndBenchmarkHelper.createContext(host, painter.getClass(), ENCODER, 0).getBlobStore();
            }
            Benchmark bench = new Benchmark(new BenchmarkConf());
            bench.add(LayeredPainterBenchmark.class);
            BenchmarkResult res = bench.run();
            TabularSummaryOutput output = new TabularSummaryOutput();
            output.visitBenchmark(res);
        }

        // host = ImageHostGoogleDataApiPicasa.class;
        // System.out.println("=================================");
        // System.out.println(host.getName());
        // for (final IBytesToImagePainter painter : TestAndBenchmarkHelper.getAllPainters()) {
        // System.out.println("+++++++++++" + host.getName() + "+++++++++++");
        // System.out.println(painter.toString());
        // System.out.println("++++++++++++++++++++++++++++++++++++");
        // store = TestAndBenchmarkHelper.createContext(host, painter.getClass(), ENCODER, 2).getBlobStore();
        // Benchmark bench = new Benchmark(new BenchmarkConf());
        // bench.add(LayeredPainterBenchmark.class);
        // BenchmarkResult res = bench.run();
        // TabularSummaryOutput output = new TabularSummaryOutput();
        // output.visitBenchmark(res);
        // }
        //
        // host = ImageHostFacebook.class;
        // System.out.println("=================================");
        // System.out.println(host.getName());
        // for (final IBytesToImagePainter painter : TestAndBenchmarkHelper.getPaintersForFacebook()) {
        // System.out.println("+++++++++++" + host.getName() + "+++++++++++");
        // System.out.println(painter.toString());
        // System.out.println("++++++++++++++++++++++++++++++++++++");
        // store = TestAndBenchmarkHelper.createContext(host, painter.getClass(), ENCODER, 2).getBlobStore();
        // Benchmark bench = new Benchmark(new BenchmarkConf());
        // bench.add(LayeredPainterBenchmark.class);
        // BenchmarkResult res = bench.run();
        // TabularSummaryOutput output = new TabularSummaryOutput();
        // output.visitBenchmark(res);
        // }
        //
        // host = ImageHostFlickr.class;
        // System.out.println("=================================");
        // System.out.println(host.getName());
        // for (final IBytesToImagePainter painter : TestAndBenchmarkHelper.getPaintersForFlickr()) {
        // System.out.println("+++++++++++" + host.getName() + "+++++++++++");
        // System.out.println(painter.toString());
        // System.out.println("++++++++++++++++++++++++++++++++++++");
        // store = TestAndBenchmarkHelper.createContext(host, painter.getClass(), ENCODER, 2).getBlobStore();
        // Benchmark bench = new Benchmark(new BenchmarkConf());
        // bench.add(LayeredPainterBenchmark.class);
        // BenchmarkResult res = bench.run();
        // TabularSummaryOutput output = new TabularSummaryOutput();
        // output.visitBenchmark(res);
        // }

    }

    static class BenchmarkConf extends AbstractConfig {

        private final static int RUNS = 1;
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
