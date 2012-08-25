package org.jclouds.imagestore.blobstore.imagehoster.file;

import java.awt.image.BufferedImage;
import java.io.File;

import org.jclouds.imagestore.blobstore.ImageHost;

public class ImageHostFile implements ImageHost {

    /** Location of this hoster. */
    private final File mFile;

    /**
     * Constructor.
     * 
     * @param pFile
     */
    public ImageHostFile(final File pFile) {
        mFile = pFile;
    }

    @Override
    public boolean createImageSet(String imageSetTitle) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean imageExists(String imageSetTitle, String imageTitle) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean imageSetExists(String imageSetTitle) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void deleteImage(String imageSetTitle, String imageTitle) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteImageSet(String imageSetTitle) {
        // TODO Auto-generated method stub

    }

    @Override
    public String uploadImage(String imageSetTitle, String imageTitle, BufferedImage image) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String uploadImage(String imageTitle, BufferedImage image) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BufferedImage downloadImage(String imageSetTitle, String imageTitle) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int countImagesInSet(String imageSetTitle) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void clearImageSet(String imageSetTitle) {
        // TODO Auto-generated method stub

    }

}
