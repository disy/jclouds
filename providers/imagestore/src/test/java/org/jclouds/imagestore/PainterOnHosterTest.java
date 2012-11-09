package org.jclouds.imagestore;

import java.awt.image.BufferedImage;
import java.util.Random;

import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.IEncoder;
import org.jclouds.imagestore.imagegenerator.IEncoder.DummyEncoder;
import org.jclouds.imagestore.imagegenerator.ImageGenerator;
import org.jclouds.imagestore.imagegenerator.bytepainter.LayeredBytesToImagePainter;
import org.jclouds.imagestore.imagehoster.IImageHost;
import org.jclouds.imagestore.imagehoster.facebook.ImageHostFacebook;

public class PainterOnHosterTest {

    static byte[] TESTBYTES;

    static {
        TESTBYTES = new byte[10000];
        new Random(12l).nextBytes(TESTBYTES);
    }

    public static void main(String args[]) throws InterruptedException {

        final IEncoder dEncoder = new DummyEncoder();

        final String setTitle = "TestSet_" + System.currentTimeMillis();

        IImageHost[] iha = new IImageHost[] {
            // new ImageHostFile(Files.createTempDir().getPath())
            // new ImageHostFlickr()
            new ImageHostFacebook()
        // new ImageHostGoogleDataApiPicasa()
            };

        for (IImageHost ih : iha) {

            System.out.println("\n<<<<<<<<<<<<< " + ih.getClass().getName() + " >>>>>>>>>>>>>>\n");

            for (int numSys = 2; numSys <= 16; numSys++) {

                IBytesToImagePainter ip = new LayeredBytesToImagePainter(numSys);

                // IBytesToImagePainter ip = new BinaryBytesToImagePainter();
                int[] dim =
                    getWidhtAndHeight(ip.pixelsPerByte(), TESTBYTES.length, ih.getMaxImageWidth(), ih
                        .getMaxImageHeight());

                System.out.println("3layered painter with numeral system " + numSys);

                ImageGenerator ig = new ImageGenerator(ip, dEncoder, dim[0], dim[1]);

                final BufferedImage bi = ig.createImageFromBytes(TESTBYTES);

                System.out.println("image-size: " + bi.getWidth() + " X " + bi.getHeight());

                final long timeMillis = System.currentTimeMillis();

                final String imageTitle = "ns_" + numSys + "_tm_" + timeMillis;
                // final String imageTitle = "ns_" + 1 + "_tm_" + timeMillis;

                ih.uploadImage(setTitle, imageTitle, bi);

                BufferedImage backImage = ih.downloadImage(setTitle, imageTitle);

                byte[] backB = ig.getBytesFromImage(backImage);

                // : " works not on " + ih.toString() + "!!"));
                compareByteArrays(backB, TESTBYTES);

                System.out.println("\n######################################\n");
            }
        }
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
            System.out.println("Failure!! Array size = 0");
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
        System.out.println("Matches: " + match + " Errors: " + notMatch);
    }
}
