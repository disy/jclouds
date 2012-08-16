/*
 * 
 */
package org.jclouds.imagestore.blobstore.flickr;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.xml.sax.SAXException;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.uploader.UploadMetaData;
import com.googlecode.flickrjandroid.uploader.Uploader;

// TODO: Auto-generated Javadoc
/**
 * The Class FlickrUploader.
 */
public class FlickrUploader {

    /** The up. */
    private final Uploader up;

    /**
     * Instantiates a new flickr uploader.
     * 
     * @param f
     *            the f
     */
    public FlickrUploader(final Flickr f) {
        up = f.getUploader();
    }

    /**
     * Upload image.
     * 
     * @param imgName
     *            the img name
     * @param imgBytes
     *            the img bytes
     * @param meta
     *            the meta
     */
    public String uploadImage(final String imgName, final byte[] bi, final UploadMetaData meta) {
        try {
            return up.upload(imgName, bi, meta);
        } catch (FlickrException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Upload image.
     * 
     * @param imgName
     *            the img name
     * @param img
     *            the img
     * @param meta
     *            the meta
     * @return 
     */
    public String uploadImage(String imgName, BufferedImage img, final UploadMetaData meta) {
        try {
            return uploadBufferedImage(imgName, img, meta);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FlickrException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Upload buffered image.
     * 
     * @param imgName
     *            the img name
     * @param img
     *            the img
     * @param meta
     *            the meta
     * @return 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws FlickrException
     *             the flickr exception
     * @throws SAXException
     *             the sAX exception
     */
    private String
        uploadBufferedImage(final String imgName, final BufferedImage img, final UploadMetaData meta)
            throws IOException, FlickrException, SAXException {
        return uploadImage(imgName, getByteArrayFromImage(img), meta);
    }

    /**
     * Gets the byte array from image.
     * 
     * @param img
     *            the img
     * @return the byte array from image
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private byte[] getByteArrayFromImage(final BufferedImage img) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return imageInByte;
    }
}
