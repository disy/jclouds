package org.jclouds.imagestore.blobstore.imagehoster.facebook;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.jclouds.imagestore.blobstore.IImageHost;

public class ImageHostFacebook implements IImageHost{

    /** The maximum image width. */
    private static final int MAX_IMAGE_WIDTH = 2048;
    /** The maximum image height. */
    private static final int MAX_IMAGE_HEIGHT = 2048;
    
    
    @Override
    public int getMaxImageWidth() {
        return MAX_IMAGE_WIDTH;
    }

    @Override
    public int getMaxImageHeight() {
        return MAX_IMAGE_HEIGHT;
    }
    
    @Override
    public boolean createImageSet(final String imageSetTitle) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean imageExists(final String imageSetTitle, final String imageTitle) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean imageSetExists(final String imageSetTitle) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void deleteImage(final String imageSetTitle, final String imageTitle) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deleteImageSet(final String imageSetTitle) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String uploadImage(final String imageSetTitle, final String imageTitle, final BufferedImage image)
        throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String uploadImage(final String imageTitle, final BufferedImage image) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BufferedImage downloadImage(final String imageSetTitle, final String imageTitle) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int countImagesInSet(final String imageSetTitle) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void clearImageSet(final String imageSetTitle) {
        // TODO Auto-generated method stub
        
    }
}
