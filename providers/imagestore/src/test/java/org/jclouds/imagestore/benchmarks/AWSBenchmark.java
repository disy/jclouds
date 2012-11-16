/**
 * 
 */
package org.jclouds.imagestore.benchmarks;

/**
 * 
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
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
public class AWSBenchmark {

    private static final Random RAN = new Random(12l);

    private static BlobStore store;

    private static File CSVOUTPUT = new File(System.getProperty("user.home"), "csv");

    static {
        CSVOUTPUT.mkdirs();
    }

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
        // stabilizing the system
        upload();
        name++;
        upload();
        name++;
    }

    public void downloadSetUp() {
        // stabilizing the system
        name--;
        download();
        download();
    }

    public void tearDown() {
        store.deleteContainer(new StringBuilder("grave9283").append(dataFactor + currentRun).toString());
    }

    @Bench(beforeFirstRun = "setUp")
    public void upload10() {
        upload();
        name++;
    }
    
    @Bench(beforeFirstRun = "downloadSetUp")
    public void download10() {
        name--;
        download();
    }

    @Bench(beforeFirstRun = "setUp")
    public void upload11() {
        upload();
        name++;
    }

    @Bench(beforeFirstRun = "downloadSetUp")
    public void download11() {
        name--;
        download();
    }

    @Bench(beforeFirstRun = "setUp")
    public void upload12() {
        upload();
        name++;
    }

    @Bench(beforeFirstRun = "downloadSetUp")
    public void download12() {
        name--;
        download();
    }

    @Bench(beforeFirstRun = "setUp")
    public void upload13() {
        upload();
        name++;
    }

    @Bench(beforeFirstRun = "downloadSetUp")
    public void download13() {
        name--;
        download();
    }

    @Bench(beforeFirstRun = "setUp")
    public void upload14() {
        upload();
        name++;
    }

    @Bench(beforeFirstRun = "downloadSetUp")
    public void download14() {
        name--;
        download();
    }

    @Bench(beforeFirstRun = "setUp")
    public void upload15() {
        upload();
        name++;
    }

    @Bench(beforeFirstRun = "downloadSetUp")
    public void download15() {
        name--;
        download();
    }

    @Bench(beforeFirstRun = "setUp")
    public void upload16() {
        upload();
        name++;
    }

    @Bench(beforeFirstRun = "downloadSetUp")
    public void download16() {
        name--;
        download();
    }

    @Bench(beforeFirstRun = "setUp")
    public void upload17() {
        upload();
        name++;
    }

    @Bench(beforeFirstRun = "downloadSetUp")
    public void download17() {
        name--;
        download();
    }

    @Bench(beforeFirstRun = "setUp")
    public void upload18() {
        upload();
        name++;
    }

    @Bench(beforeFirstRun = "downloadSetUp")
    public void download18() {
        name--;
        download();
    }

    @Bench(beforeFirstRun = "setUp")
    public void upload19() {
        upload();
        name++;
    }

    @Bench(beforeFirstRun = "downloadSetUp")
    public void download19() {
        name--;
        download();
    }

    @Bench(beforeFirstRun = "setUp")
    public void upload20() {
        upload();
        name++;
    }

    @Bench(beforeFirstRun = "downloadSetUp")
    public void download20() {
        name--;
        download();
    }

    private void upload() {
        String container = new StringBuilder("grave9283").append(dataFactor + currentRun).toString();
        // String blobname =
        // new StringBuilder("grave9283").append(":").append(
        // ((SyncImageBlobStore)store).getImageGenerator().getPainter().toString()).append(":").append(
        // name).toString();
        String blobname = new StringBuilder("grave9283").append(":").append(name).toString();

        BlobBuilder blobbuilder = store.blobBuilder(blobname);
        Blob blob = blobbuilder.build();
        blob.setPayload(data);
        store.putBlob(container, blob);

    }

    private void download() {
        String container = new StringBuilder("grave9283").append(dataFactor + currentRun).toString();
        // String blobname =
        // new StringBuilder("grave9283").append(":").append(
        // ((SyncImageBlobStore)store).getImageGenerator().getPainter().toString()).append(":").append(
        // name).toString();
        String blobname = new StringBuilder("grave9283").append(":").append(name).toString();
        Blob blob;
        do {
            blob = store.getBlob(container, blobname);
        } while (blob == null);
        InputStream in = blob.getPayload().getInput();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ByteStreams.copy(in, out);
            data = out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setUpData(int size) {
        data = new byte[1 << size];
        RAN.nextBytes(data);
    }

    public static void main(String[] args) {

        String[] awsCredentials = getCredentials();
        if (awsCredentials.length == 0) {
            System.out.println("Please set credentials in .imagestore!");
            System.exit(-1);
        }

        BlobStoreContext context =
            new BlobStoreContextFactory().createContext("aws-s3", awsCredentials[0], awsCredentials[1]);
        store = context.getBlobStore();
        File paintercsv = new File(CSVOUTPUT, "aws");
        paintercsv.mkdirs();
        Benchmark bench = new Benchmark(new BenchmarkConf());
        bench.add(AWSBenchmark.class);
        BenchmarkResult res = bench.run();
        TabularSummaryOutput output = new TabularSummaryOutput();
        CSVOutput output2 = new CSVOutput(paintercsv);
        output.visitBenchmark(res);
        output2.visitBenchmark(res);
        context.close();
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

    private static String[] getCredentials() {
        File userStore =
            new File(System.getProperty("user.home"), new StringBuilder(".imagecredentials").append(
                File.separator).append("aws.properties").toString());
        if (!userStore.exists()) {
            return new String[0];
        } else {
            Properties props = new Properties();
            try {
                props.load(new FileReader(userStore));
                return new String[] {
                    props.getProperty("access"), props.getProperty("secret")
                };

            } catch (IOException exc) {
                throw new RuntimeException(exc);
            }
        }

    }

}
