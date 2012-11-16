package org.jclouds.imagestore;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jclouds.imagestore.benchmarks.TestAndBenchmarkHelper;
import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.IEncoder;
import org.jclouds.imagestore.imagegenerator.IEncoder.DummyEncoder;
import org.jclouds.imagestore.imagegenerator.ImageGenerator;
import org.jclouds.imagestore.imagehoster.IImageHost;
import org.jclouds.imagestore.imagehoster.facebook.ImageHostFacebook;
import org.jclouds.imagestore.imagehoster.file.ImageHostFile;
import org.jclouds.imagestore.imagehoster.flickr.ImageHostFlickr;

import com.google.common.io.Files;

public class PainterOnHosterTest {

    static Random RAN = new Random(12l);

    static File csvStore = new File(System.getProperty("user.home"), "failures");

    public static void main(String args[]) throws InterruptedException {

        // File imageStore = new File(System.getProperty("user.home"), "fileHostImages");
        // imageStore.mkdirs();

        final IEncoder dEncoder = new DummyEncoder();
        final String setTitle = "TestSet";

        IImageHost ih = new ImageHostFacebook();

        ih.clearImageSet(setTitle);

        csvStore.mkdirs();

        Map<String, FileWriter> sizeWriters = new HashMap<String, FileWriter>();
        Map<String, FileWriter> failureWriters = new HashMap<String, FileWriter>();

        try {
            for (int i = 10; i <= 20; i++) {

                byte[] TESTBYTES = new byte[1 << i];
                RAN.nextBytes(TESTBYTES);

                System.out.println("\n<<<<<<<<<<<<< " + i + " >>>>>>>>>>>>>>\n");

                List<IBytesToImagePainter> painters = TestAndBenchmarkHelper.getAllPainters();
                for (IBytesToImagePainter ip : painters) {

                    if (!sizeWriters.containsKey(ip.toString())) {
                        File sizes = new File(csvStore, ip.toString() + "Sizes.csv");
                        if (!sizes.exists()) {
                            sizes.createNewFile();
                        }
                        FileWriter sizeWriter = new FileWriter(sizes);
                        sizeWriter.write("Input Size");
                        sizeWriter.write(",");
                        sizeWriter.write("Width");
                        sizeWriter.write(",");
                        sizeWriter.write("Height");
                        // sizeWriter.write(",");
                        // sizeWriter.write("File Size");
                        sizeWriter.write("\n");
                        sizeWriter.flush();
                        sizeWriters.put(ip.toString(), sizeWriter);

                        File failures = new File(csvStore, ip.toString() + "Failures.csv");
                        if (!failures.exists()) {
                            failures.createNewFile();
                        }

                        FileWriter failureWriter = new FileWriter(failures);
                        failureWriter.write("Input Size");
                        failureWriter.write(",");
                        failureWriter.write("Correct");
                        failureWriter.write(",");
                        failureWriter.write("False");
                        failureWriter.write("\n");
                        failureWriter.flush();
                        failureWriters.put(ip.toString(), failureWriter);

                    }

                    try {
                        int[] dim =
                            getWidhtAndHeight(ip.pixelsPerByte(), TESTBYTES.length, ih.getMaxImageWidth(), ih
                                .getMaxImageHeight());

                        System.out.println("painter " + ip.toString());

                        ImageGenerator ig = new ImageGenerator(ip, dEncoder, dim[0], dim[1]);

                        final BufferedImage bi = ig.createImageFromBytes(TESTBYTES);

                        System.out.println("image-size: " + bi.getWidth() + " X " + bi.getHeight());

                        final long timeMillis = System.currentTimeMillis();

                        final String imageTitle = "painter_" + ip.toString() + "_size_" + i;

                        sizeWriters.get(ip.toString()).write(Integer.toString(TESTBYTES.length));
                        sizeWriters.get(ip.toString()).write(",");
                        sizeWriters.get(ip.toString()).write(Integer.toString(bi.getWidth()));
                        sizeWriters.get(ip.toString()).write(",");
                        sizeWriters.get(ip.toString()).write(Integer.toString(bi.getHeight()));
                        // sizeWriters.get(ip.toString()).write(",");

                        if (ih.uploadImage(setTitle, imageTitle, bi)) {

                            System.out
                                .println("time to upload: " + (System.currentTimeMillis() - timeMillis));

                            // long fileSize = getFileSize(imageTitle, new File(imageStore, setTitle));
                            // sizeWriters.get(ip.toString()).write(Long.toString(fileSize));
                            sizeWriters.get(ip.toString()).write("\n");
                            sizeWriters.get(ip.toString()).flush();

                            BufferedImage backImage = ih.downloadImage(setTitle, imageTitle);

                            byte[] backB = ig.getBytesFromImage(backImage);

                            System.out.println("time to upload and download: "
                                + (System.currentTimeMillis() - timeMillis));

                            // : " works not on " + ih.toString() + "!!"));
                            compareByteArrays(backB, TESTBYTES, failureWriters.get(ip.toString()));

                        } else {
                            System.out.println("Upload not successful");
                            // writer.write(Integer.toString(0));
                            sizeWriters.get(ip.toString()).write("\n");
                            sizeWriters.get(ip.toString()).flush();

                        }
                        System.out.println("\n######################################\n");
                    } catch (RuntimeException exc) {
                        exc.printStackTrace();
                    }
                }
            }

            for (FileWriter writer : sizeWriters.values()) {
                writer.close();
            }
            for (FileWriter writer : failureWriters.values()) {
                writer.close();
            }

        } catch (IOException exc) {
            exc.printStackTrace();
        }
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

    static int[] getWidhtAndHeight(final float pixelPerByte, final int inputLength,
        final int maxImageHostWidth, final int maxImageHostHeight) {
        int w = maxImageHostWidth;
        int h = (int)((inputLength + 4) * pixelPerByte / (float)w) + 1;

        if (h > maxImageHostHeight) {
            try {
                throw new IllegalArgumentException(
                    "Byte array too large for image generation! Generated image would be out of image-host's maximum image size.");
            } catch (IllegalArgumentException e) {
                new RuntimeException(e);
            }
        }

        return new int[] {
            w, h
        };
    }

    static void compareByteArrays(byte[] bs1, byte[] bs2, FileWriter writer) {
        int match = 0, notMatch = 0;
        int len = bs1.length > bs2.length ? bs2.length : bs1.length;

        if (len == 0) {
            System.err.println("Failure!! Array size = 0");
            return;
        }

        for (int i = 0; i < len; i++) {

            if (bs1[i] == bs2[i]) {
                // System.out.println(i + ". match: " + bs1[i] + " = " + bs2[i]);

                match++;
            } else {
                // System.out.print("\n" + i + ". not match " + bs1[i] + " != " + bs2[i]
                // + " !!!!!!!!!!!!!!!!!!!!!!!!");
                notMatch++;
            }
        }
        System.out.println("\n----------------");

        try {
            writer.write(Integer.toString(bs1.length));
            writer.write(",");
            writer.write(Integer.toString(match));
            writer.write(",");
            writer.write(Integer.toString(notMatch));
            writer.write("\n");
            writer.flush();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        if (notMatch > 0) {
            System.err.println("Matches: " + match + " Errors: " + notMatch);
        } else {

        }

    }
}
