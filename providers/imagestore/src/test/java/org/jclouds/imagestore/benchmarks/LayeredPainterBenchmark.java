/**
 * 
 */
package org.jclouds.imagestore.benchmarks;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.imagestore.SyncImageBlobStore;
import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.IEncoder;
import org.jclouds.imagestore.imagegenerator.bytepainter.BytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.bytepainter.LayeredBytesToImagePainter;
import org.jclouds.imagestore.imagehoster.IImageHost;
import org.jclouds.imagestore.imagehoster.flickr.ImageHostFlickr;
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

    private static File CSVOUTPUT = new File(System.getProperty("user.home"), "csv");

    private static Map<String, FileWriter> SIZES = new HashMap<String, FileWriter>();
    private static Map<String, FileWriter> FAILURES = new HashMap<String, FileWriter>();

    private final static String containerName = "benchContainer";

    static {
        CSVOUTPUT.mkdirs();
    }

    private byte[] data;

    int dataFactor = 10;

    int currentRun = 0;

    int name = 0;

    public void uploadFirst() {
        name = 0;
        setUpData(dataFactor + currentRun);
        currentRun++;
        saveOrigImage();
        // stabilizing the system
        store.deleteContainer(containerName);
        store.createContainerInLocation(null, containerName);
        upload();
        name++;
        upload();
    }

    public void uploadEach() {
        this.name++;
    }

    public void downloadFirst() {
        store.deleteContainer(containerName);
        store.createContainerInLocation(null, containerName);
        name = 0;
        upload();
        // stabilizing the system
        download();
        download();
    }

    private void upload() {
        String name =
            new StringBuilder(((SyncImageBlobStore)store).getImageGenerator().getPainter().toString())
                .append(":").append(currentRun).append(":").append(this.name).toString();

        BlobBuilder blobbuilder = store.blobBuilder(name);
        Blob blob = blobbuilder.build();
        blob.setPayload(data);
        store.putBlob(containerName, blob);

    }

    private void download() {
        String name =
            new StringBuilder(((SyncImageBlobStore)store).getImageGenerator().getPainter().toString())
                .append(":").append(currentRun).append(":").append(this.name).toString();

        Blob blob = store.getBlob(containerName, name);
        InputStream in = blob.getPayload().getInput();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ByteStreams.copy(in, out);
            data = out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void downloadLast() {
        saveHostedImage();
    }

    @Bench(beforeFirstRun = "uploadFirst", beforeEachRun = "uploadEach")
    public void upload10() {
        upload();
    }

    @Bench(beforeFirstRun = "downloadFirst", afterLastRun = "downloadLast")
    public void download10() {
        download();
    }

    @Bench(beforeFirstRun = "uploadFirst", beforeEachRun = "uploadEach")
    public void upload11() {
        upload();
    }

    @Bench(beforeFirstRun = "downloadFirst", afterLastRun = "downloadLast")
    public void download11() {
        download();
    }

    @Bench(beforeFirstRun = "uploadFirst", beforeEachRun = "uploadEach")
    public void upload12() {
        upload();
    }

    @Bench(beforeFirstRun = "downloadFirst", afterLastRun = "downloadLast")
    public void download12() {
        download();
    }

    @Bench(beforeFirstRun = "uploadFirst", beforeEachRun = "uploadEach")
    public void upload13() {
        upload();
    }

    @Bench(beforeFirstRun = "downloadFirst", afterLastRun = "downloadLast")
    public void download13() {
        download();
    }

    @Bench(beforeFirstRun = "uploadFirst", beforeEachRun = "uploadEach")
    public void upload14() {
        upload();
    }

    @Bench(beforeFirstRun = "downloadFirst", afterLastRun = "downloadLast")
    public void download14() {
        download();
    }

    @Bench(beforeFirstRun = "uploadFirst", beforeEachRun = "uploadEach")
    public void upload15() {
        upload();
    }

    @Bench(beforeFirstRun = "downloadFirst", afterLastRun = "downloadLast")
    public void download15() {
        download();
    }

    @Bench(beforeFirstRun = "uploadFirst", beforeEachRun = "uploadEach")
    public void upload16() {
        upload();
    }

    @Bench(beforeFirstRun = "downloadFirst", afterLastRun = "downloadLast")
    public void download16() {
        download();
    }

    @Bench(beforeFirstRun = "uploadFirst", beforeEachRun = "uploadEach")
    public void upload17() {
        upload();
    }

    @Bench(beforeFirstRun = "downloadFirst", afterLastRun = "downloadLast")
    public void download17() {
        download();
    }

    @Bench(beforeFirstRun = "uploadFirst", beforeEachRun = "uploadEach")
    public void upload18() {
        upload();
    }

    @Bench(beforeFirstRun = "downloadFirst", afterLastRun = "downloadLast")
    public void download18() {
        download();
    }

    @Bench(beforeFirstRun = "uploadFirst", beforeEachRun = "uploadEach")
    public void upload19() {
        upload();
    }

    @Bench(beforeFirstRun = "downloadFirst", afterLastRun = "downloadLast")
    public void download19() {
        download();
    }

    @Bench(beforeFirstRun = "uploadFirst", beforeEachRun = "uploadEach")
    public void upload20() {
        upload();
    }

    @Bench(beforeFirstRun = "downloadFirst", afterLastRun = "downloadLast")
    public void download20() {
        download();
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
        String painter = blobStore.getImageGenerator().getPainter().toString();
        String host = blobStore.getImageHost().getClass().getSimpleName();

        File rawStore = new File(IMAGESTORE.getAbsolutePath(), host);
        rawStore.mkdirs();

        String name =
            new StringBuilder(((SyncImageBlobStore)store).getImageGenerator().getPainter().toString())
                .append(":").append(currentRun).append(":").append(this.name).toString();

        Set<String> images = blobStore.getImageHost().imageSetContent(containerName);
        int i = 0;
        long size = 0;
        for (String image : images) {
            if (image.startsWith(name)) {
                BufferedImage img = blobStore.getImageHost().downloadImage(containerName, image);
                File toStore =
                    new File(rawStore.getAbsolutePath(), painter + ":" + (dataFactor + currentRun) + ":" + i
                        + ".png");
                size = size + saveBufferedImage(toStore, img);
                i++;
            }
        }

        try {

            if (!SIZES.containsKey(painter)) {
                File file = new File(CSVOUTPUT, painter.toString() + "Sizes.csv");

                FileWriter writer = new FileWriter(file);
                writer.write("Input Size");
                writer.write(",");
                writer.write("File Size");
                writer.write("\n");
                SIZES.put(painter, writer);

            }
            SIZES.get(painter).write(Integer.toString(1 << (dataFactor + currentRun)));
            SIZES.get(painter).write(",");
            SIZES.get(painter).write(Long.toString(size));
            SIZES.get(painter).write("\n");
            SIZES.get(painter).flush();
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    private static long saveBufferedImage(final File file, final BufferedImage img) {
        try {
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file);
            ImageIO.write(img, "png", fos);
            fos.flush();
            fos.close();
            return file.length();
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    public static void main(String[] args) {

        Class<? extends IImageHost> host = ImageHostFlickr.class;
        List<IBytesToImagePainter> painters = TestAndBenchmarkHelper.getPaintersForFlickr();

        System.out.println("=================================");
        System.out.println(host.getSimpleName());
        for (final IBytesToImagePainter painter : painters) {
            File paintercsv = new File(CSVOUTPUT, painter.toString());
            paintercsv.mkdirs();

            System.out.println("+++++++++++" + host.getSimpleName() + "+++++++++++");
            System.out.println(painter.toString());
            System.out.println("++++++++++++++++++++++++++++++++++++");
            if (painter instanceof LayeredBytesToImagePainter) {
                int layers = ((LayeredBytesToImagePainter)painter).getNumSys();
                store =
                    TestAndBenchmarkHelper.createContext(host, painter.getClass(), ENCODER, layers)
                        .getBlobStore();
            } else if (painter instanceof BytesToImagePainter) {
                int layers = ((BytesToImagePainter)painter).getNumSys();
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
            CSVOutput output2 = new CSVOutput(paintercsv);
            output.visitBenchmark(res);
            output2.visitBenchmark(res);
        }

        for (FileWriter writer : SIZES.values()) {
            try {
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for (FileWriter writer : FAILURES.values()) {
            try {
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class BenchmarkConf extends AbstractConfig {

        private final static int RUNS = 50;
        private final static Set<AbstractMeter> METERS = new HashSet<AbstractMeter>();

        private final static Set<AbstractOutput> OUTPUT = new HashSet<AbstractOutput>();
        private final static KindOfArrangement ARRAN = KindOfArrangement.NoArrangement;
        private final static double GCPROB = 1.0d;

        static {
            METERS.add(new TimeMeter(Time.MilliSeconds));
            OUTPUT.add(new TabularSummaryOutput());
        };

        /**
         * Public constructor.
         */
        public BenchmarkConf() {
            super(RUNS, METERS, OUTPUT, ARRAN, GCPROB);

        }

    }

}
