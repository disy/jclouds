package org.jclouds.imagestore;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Semaphore;

import org.jclouds.imagestore.benchmarks.TestAndBenchmarkHelper;
import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.IEncoder;
import org.jclouds.imagestore.imagegenerator.ImageExtractor;
import org.jclouds.imagestore.imagegenerator.ImageGenerator;
import org.jclouds.imagestore.imagegenerator.reedsolomon.ReedSolomon;
import org.jclouds.imagestore.imagehoster.IImageHost;
import org.jclouds.imagestore.imagehoster.facebook.ImageHostFacebook;
import org.jclouds.imagestore.imagehoster.file.ImageHostFile;

import com.google.common.io.Files;

public class PainterOnHosterTest {

    static Random RAN = new Random(89);

    static File csvStore = new File(System.getProperty("user.home"), "failures");

    public static void main(String args[]) throws InterruptedException {

        // File imageStore = new File(System.getProperty("user.home"), "fileHostImages");
        // imageStore.mkdirs();

        final IEncoder dEncoder = new ReedSolomon();
        final ImageExtractor ie = new ImageExtractor();
        final String setTitle = "TestSet";

        IImageHost ih = new ImageHostFile(Files.createTempDir().getAbsolutePath());

        ih.clearImageSet(setTitle);
       

        csvStore.mkdirs();
        Map<String, FileWriter> writers = new HashMap<String, FileWriter>();
        try {
            for (int i = 10; i <= 20; i++) {

                byte[] TESTBYTES = new byte[1 << i];
                RAN.nextBytes(TESTBYTES);

                System.out.println("\n<<<<<<<<<<<<< " + i + " >>>>>>>>>>>>>>\n");

                List<IBytesToImagePainter> painters = TestAndBenchmarkHelper.getLayeredPainters();
                for (IBytesToImagePainter ip : painters) {

                    if (!writers.containsKey(ip.toString())) {

                        File failureOutput = new File(csvStore, ip.toString() + "Failures.csv");
                        if (!failureOutput.exists()) {
                            failureOutput.createNewFile();
                        }

                        FileWriter failureWriter = new FileWriter(failureOutput);
                        failureWriter.write("Input Size");
                        failureWriter.write(",");
                        failureWriter.write("Correct");
                        failureWriter.write(",");
                        failureWriter.write("False");
                        failureWriter.write("\n");
                        failureWriter.flush();
                        writers.put(ip.toString(), failureWriter);
                    }
                    try {

                        float ppb = ip.pixelsPerByte();
                        System.out.println("painter " + ip.toString());

                        ImageGenerator ig =
                            new ImageGenerator(ip, dEncoder, ih.getMaxImageHeight(), ih.getMaxImageWidth());

                        byte[][] toUpload = splitUp(TESTBYTES, ih, ppb);
                        BufferedImage[] bi = new BufferedImage[toUpload.length];
                        int falseNumber = 0;
                        for (int j = 0; j < toUpload.length; j++) {

                            bi[j] = ig.createImageFromBytes(toUpload[j]);

                            System.out.println("image-size: " + bi[j].getWidth() + " X " + bi[j].getHeight());

                            final long timeMillis = System.currentTimeMillis();

                            final String imageTitle =
                                "painter_" + ip.toString() + "_size_" + i + "_part_" + j;

                            // sizeWriters.get(ip.toString()).write(",");

                            if (ih.uploadImage(setTitle, imageTitle, bi[j])) {

                                System.out.println("time to upload: "
                                    + (System.currentTimeMillis() - timeMillis));

                                BufferedImage backImage = ih.downloadImage(setTitle, imageTitle);
                                try {
                                    byte[] backB = ie.getBytesFromImage(backImage);

                                    System.out.println("time to upload and download: "
                                        + (System.currentTimeMillis() - timeMillis));

                                    // : " works not on " + ih.toString() + "!!"));
                                    falseNumber = falseNumber + compareByteArrays(backB, toUpload[j]);
                                } catch (NullPointerException exc) {
                                    falseNumber = falseNumber + toUpload[j].length;
                                }

                            } else {
                                System.out.println("Upload not successful");
                            }

                        }

                        writers.get(ip.toString()).write(Integer.toString(TESTBYTES.length));
                        writers.get(ip.toString()).write(",");
                        writers.get(ip.toString()).write(Integer.toString(TESTBYTES.length - falseNumber));
                        writers.get(ip.toString()).write(",");
                        writers.get(ip.toString()).write(Integer.toString(falseNumber));
                        writers.get(ip.toString()).write("\n");
                        writers.get(ip.toString()).flush();

                        System.out.println("\n######################################\n");
                    } catch (RuntimeException exc) {
                        exc.printStackTrace();
                    }
                }
            }

            for (FileWriter writer : writers.values()) {
                writer.close();
            }

        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    static byte[][] splitUp(byte[] bs, IImageHost ih, float bpp) {
        int bytesPerImage = (int)((ih.getMaxImageHeight() - 1) * ih.getMaxImageWidth() / bpp);

        // Splitting in multiple images if necessary
        int numberOfImages = (int)Math.ceil(new Double(bs.length) / new Double(bytesPerImage));
        byte[][] returnVal = new byte[numberOfImages][];
        for (int i = 0; i < numberOfImages; i++) {
            byte[] imagePerByte =
                new byte[bs.length - i * bytesPerImage > bytesPerImage ? bytesPerImage : bs.length - i
                    * bytesPerImage];
            System.arraycopy(bs, i * bytesPerImage, imagePerByte, 0, imagePerByte.length);
            returnVal[i] = imagePerByte;
        }
        return returnVal;
    }

    static long getFileSize(String imageTitle, File store) {
        long size = 0;
        for (File content : store.listFiles()) {
            if (content.getName().contains(imageTitle)) {
                size = size + content.length();
            }
        }

        return size;
    }

    static Semaphore blocker = new Semaphore(1);

    static int compareByteArrays(byte[] bs1, byte[] bs2) {
        int match = 0, notMatch = 0;
        try {
            blocker.acquire();

            int len = bs1.length > bs2.length ? bs2.length : bs1.length;

            if (len == 0) {
                System.err.println("Failure!! Array size = 0");
                match = -1;
                notMatch = -1;
            } else {

                for (int i = 0; i < len; i++) {

                    if (bs1[i] == bs2[i]) {
                        // System.out.println(i + ". match: " + bs1[i] + " = " + bs2[i]);

                        match++;
                    } else {
                         System.out.print("\n" + i + ". not match " + bs1[i] + " != " + bs2[i]
                         + " !!!!!!!!!!!!!!!!!!!!!!!!");
                        notMatch++;
                    }
                }
            }
            System.out.println("\n----------------");
            if (notMatch > 0) {
                System.err.println("Matches: " + match + " Errors: " + notMatch);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        blocker.release();
        return notMatch;
    }
}
