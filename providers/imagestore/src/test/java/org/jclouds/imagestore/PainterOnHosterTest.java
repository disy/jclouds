package org.jclouds.imagestore;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.IEncoder;
import org.jclouds.imagestore.imagegenerator.IEncoder.DummyEncoder;
import org.jclouds.imagestore.imagegenerator.ImageGenerator;
import org.jclouds.imagestore.imagehoster.IImageHost;
import org.jclouds.imagestore.imagehoster.file.ImageHostFile;

public class PainterOnHosterTest {

    static Random RAN = new Random(12l);

    static File csvStore = new File(System.getProperty("user.home"), "failures");

    static File painterStore;

    public static void main(String args[]) throws InterruptedException {

        File imageStore = new File(System.getProperty("user.home"), "fileHostImages");
        imageStore.mkdirs();

        final IEncoder dEncoder = new DummyEncoder();
        final String setTitle = "TestSet";

        IImageHost ih = new ImageHostFile(imageStore.getAbsolutePath());

        ih.clearImageSet(setTitle);

        painterStore = new File(csvStore, ih.toString());
        painterStore.mkdirs();

        Map<String, FileWriter> painterWriter = new HashMap<String, FileWriter>();

        try {
            for (int i = 10; i <= 20; i++) {

                byte[] TESTBYTES = new byte[1 << i];
                RAN.nextBytes(TESTBYTES);

                System.out.println("\n<<<<<<<<<<<<< " + ih.getClass().getName() + " >>>>>>>>>>>>>>\n");

                List<IBytesToImagePainter> painters = TestAndBenchmarkHelper.getAllPainters();
                for (IBytesToImagePainter ip : painters) {

                    FileWriter writer;

                    if (!painterWriter.containsKey(ip.toString())) {
                        File painter = new File(painterStore, ip.toString() + ".csv");
                        if (!painter.exists()) {
                            painter.createNewFile();
                        }
                        writer = new FileWriter(painter);
                        writer.write("Input Size");
                        writer.write(",");
                        writer.write("Width");
                        writer.write(",");
                        writer.write("Height");
                        writer.write(",");
                        writer.write("File Size");
                        writer.write("\n");
                        writer.flush();
                        painterWriter.put(ip.toString(), writer);
                    }
                    writer = painterWriter.get(ip.toString());

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

                        writer.write(Integer.toString(TESTBYTES.length));
                        writer.write(",");
                        writer.write(Integer.toString(bi.getWidth()));
                        writer.write(",");
                        writer.write(Integer.toString(bi.getHeight()));
                        writer.write(",");

                        if (ih.uploadImage(setTitle, imageTitle, bi)) {

                            System.out
                                .println("time to upload: " + (System.currentTimeMillis() - timeMillis));

                            long fileSize = getFileSize(imageTitle, new File(imageStore, setTitle));
                            writer.write(Long.toString(fileSize));
                            writer.write("\n");
                            writer.flush();

                            BufferedImage backImage = ih.downloadImage(setTitle, imageTitle);

                            byte[] backB = ig.getBytesFromImage(backImage);

                            System.out.println("time to upload and download: "
                                + (System.currentTimeMillis() - timeMillis));

                            // : " works not on " + ih.toString() + "!!"));
                            compareByteArrays(backB, TESTBYTES);

                        } else {
                            System.out.println("Upload not successful");
                            writer.write(Integer.toString(0));
                            writer.write("\n");
                            writer.flush();

                        }
                        System.out.println("\n######################################\n");
                    } catch (RuntimeException exc) {
                        exc.printStackTrace();
                    }
                }
            }

            for (FileWriter writer : painterWriter.values()) {
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

    static void compareByteArrays(byte[] bs1, byte[] bs2) {
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

        File forByte = new File(painterStore, Integer.toString(bs1.length) + ".csv");
        try {
            if (!forByte.exists()) {
                forByte.createNewFile();
            }
            FileWriter writer = new FileWriter(forByte);
            writer.write(Integer.toString(match));
            writer.write(",");
            writer.write(Integer.toString(notMatch));
            writer.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        if (notMatch > 0) {
            System.err.println("Matches: " + match + " Errors: " + notMatch);
        } else {

        }

    }
}
