package org.jclouds.imagestore;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import org.jclouds.imagestore.imagegenerator.IEncoder;
import org.jclouds.imagestore.imagegenerator.ImageGenerator;
import org.jclouds.imagestore.imagegenerator.bytepainter.BytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.bytepainter.DihectpenthexagonLayeredBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.bytepainter.LayeredBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.reedsolomon.ReedSolomon;

public class TestImageGenerator {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        byte[] data = new byte[18];
        new Random(16l).nextBytes(data);

        ImageGenerator generator =
            new ImageGenerator(LayeredBytesToImagePainter.PainterType.BINARY_LAYERED.getPainter(),
                new IEncoder.DummyEncoder(), 8, 5);

        BufferedImage img = generator.createImageFromBytes(data);

        File imageFile = new File("/Users/sebi/Desktop/testimg.png");

        if (imageFile.exists()) {
            imageFile.delete();
        }

        FileOutputStream fos = new FileOutputStream(imageFile);
        ImageIO.write(img, "PNG", fos);
        fos.close();

    }
}
