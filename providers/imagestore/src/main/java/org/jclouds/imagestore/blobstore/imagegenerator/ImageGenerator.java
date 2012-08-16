/*
 * 
 */
package org.jclouds.imagestore.blobstore.imagegenerator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.zip.Deflater;

import javax.imageio.ImageIO;

import org.jclouds.imagestore.blobstore.ImageHost;
import org.jclouds.imagestore.blobstore.flickr.FlickrOAuth;
import org.jclouds.imagestore.blobstore.flickr.ImageHostFlickr;
import org.jclouds.imagestore.blobstore.imagegenerator.bytepainter.HeptalLayeredBytesToImagePainter;
import org.json.JSONException;
import org.xml.sax.SAXException;

import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.uploader.UploadMetaData;

// TODO: Auto-generated Javadoc
/**
 * The Class ImageGenerator.
 */
public class ImageGenerator {

    /** The image width. */
    private int IMAGE_WIDTH = 5;

    /** The image height. */
    private int IMAGE_HEIGHT = 2;

    /** The bp. */
    private final BytesToImagePainter bp;


    /**
     * Instantiates a new image generator.
     * 
     * @param bytePainter
     *            the byte painter
     * @param host
     *            the host
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws FlickrException
     *             the flickr exception
     */
    public ImageGenerator(final BytesToImagePainter bytePainter) {
        bp = bytePainter;
    }

    /**
     * Compress data.
     * 
     * @param bs
     *            the bs
     * @return the byte[]
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public byte[] compressData(byte[] bs) throws IOException {
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        deflater.setInput(bs);
        deflater.finish();

        ByteArrayOutputStream bos = new ByteArrayOutputStream(bs.length);

        byte[] buffer = new byte[1024];

        /*
         * Use
         * 
         * boolean finished() method of Deflater class to determine whether end
         * of compressed data output stream reached.
         */
        while (!deflater.finished()) {
            /*
             * use int deflate(byte[] buffer) method to fill the buffer with the
             * compressed data.
             * 
             * This method returns actual number of bytes compressed.
             */

            int bytesCompressed = deflater.deflate(buffer);
            bos.write(buffer, 0, bytesCompressed);
        }

        return bos.toByteArray();
    }

    /**
     * Creates the buffered image.
     * 
     * @return the buffered image
     */
    BufferedImage createBufferedImage() {
        BufferedImage img =
            new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        return img;
    }

    public int [] getImageWidthAndHeight(int byteArrayLength){
        int w = 2048;
        int h = (int) (byteArrayLength / (float) w) + 1;
        return new int[]{w, h};
    }
    
    public BufferedImage createImageFromBytes(byte [] bs){
        int [] dim = getImageWidthAndHeight(bs.length);
        this.IMAGE_WIDTH = dim[0];
        this.IMAGE_HEIGHT = dim[1];
        return bp.storeBytesInImage(createBufferedImage(), bs);
    }
    
    public byte[] getBytesFromImage(BufferedImage img){
        return bp.getBytesFromImage(img);
    }
    
}
