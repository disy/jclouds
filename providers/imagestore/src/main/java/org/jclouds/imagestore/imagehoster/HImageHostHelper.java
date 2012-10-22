package org.jclouds.imagestore.imagehoster;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public final class HImageHostHelper {

    /**
     * Private constructor. Protects helper class from being instantiated.
     */
    private HImageHostHelper() {
    };

    /**
     * Returns an InputStream from given BufferedImage.
     * 
     * @param image
     *            The image.
     * @return The byte array from image.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static InputStream getInputStreamFromImage(final BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        baos.flush();
        return new ByteArrayInputStream(baos.toByteArray());
    }
}
