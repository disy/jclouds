/**
 * 
 */
package org.jclouds.imagestore.imagehoster.picasa;

import java.awt.image.BufferedImage;

import org.jclouds.imagestore.imagehoster.IImageHost;

/**
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public class ImageHostPicasa implements IImageHost {

    private PicasaClient pClient;

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxImageWidth() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxImageHeight() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createImageSet(String imageSetTitle) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean imageExists(String imageSetTitle, String imageTitle) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean imageSetExists(String imageSetTitle) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteImage(String imageSetTitle, String imageTitle) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteImageSet(String imageSetTitle) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String uploadImage(String imageSetTitle, String imageTitle, BufferedImage image) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImage downloadImage(String imageSetTitle, String imageTitle) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countImagesInSet(String imageSetTitle) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearImageSet(String imageSetTitle) {
        // TODO Auto-generated method stub

    }

}
