package org.jclouds.imagestore.imagehoster.file;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.inject.Named;

import org.jclouds.filesystem.reference.FilesystemConstants;
import org.jclouds.imagestore.imagehoster.IImageHost;

import com.google.inject.Inject;

public class ImageHostFile implements IImageHost {

    /** The maximum image width. */
    private static final int MAX_IMAGE_WIDTH = 2048;

    /** The maximum image height. */
    private static final int MAX_IMAGE_HEIGHT = 2048;

    /** The compression method the image is stored with. */
    private static final String IMAGE_COMPRESSION = "png";

    /** Location of this hoster. */
    private final File mFile;

    /**
     * Constructor.
     * 
     * @param pFile
     */
    @Inject
    public ImageHostFile(@Named(FilesystemConstants.PROPERTY_BASEDIR) final String baseDir) {
        mFile = new File(baseDir);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxImageWidth() {
        return MAX_IMAGE_WIDTH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxImageHeight() {
        return MAX_IMAGE_HEIGHT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createImageSet(final String imageSetTitle) {
        return new File(mFile, imageSetTitle).mkdir();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean imageExists(final String imageSetTitle, final String imageTitle) {
        final File set = new File(mFile, imageSetTitle);
        if (set.exists()) {
            final File image = new File(set, imageTitle + "." + IMAGE_COMPRESSION);
            return image.exists();
        } else {
            return false;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean imageSetExists(final String imageSetTitle) {
        return new File(mFile, imageSetTitle).exists();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteImage(final String imageSetTitle, final String imageTitle) {
        final File set = new File(mFile, imageSetTitle);
        for (File singleFile : set.listFiles()) {
            if (singleFile.getName().equals(
                new StringBuilder(imageTitle).append(".").append(IMAGE_COMPRESSION).toString())) {
                return singleFile.delete();
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteImageSet(final String imageSetTitle) {
        final File set = new File(mFile, imageSetTitle);
        if (set.exists()) {
            for (File singleFile : set.listFiles()) {
                if (!singleFile.delete()) {
                    return false;
                }
            }
            return set.delete();
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean
        uploadImage(final String imageSetTitle, final String imageTitle, final BufferedImage image) {
        final File set = new File(mFile, imageSetTitle);
        set.mkdirs();
        final File imageFile = new File(set, imageTitle + "." + IMAGE_COMPRESSION);
        if (imageFile.exists()) {
            return false;
        }
        try {
            imageFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(imageFile);
            ImageIO.write(image, IMAGE_COMPRESSION, fos);
            fos.flush();
            fos.close();
            imageFile.getAbsolutePath();
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImage downloadImage(final String imageSetTitle, final String imageTitle) {
        final File set = new File(mFile, imageSetTitle);
        final File imageFile = new File(set, imageTitle + "." + IMAGE_COMPRESSION);
        if (!imageFile.exists()) {
            return null;
        }
        try {
            FileInputStream fis = new FileInputStream(imageFile);
            BufferedImage returnVal = ImageIO.read(fis);
            fis.close();
            return returnVal;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> imageSetContent(final String imageSetTitle) {
        final File set = new File(mFile, imageSetTitle);
        Set<String> returnVal = new HashSet<String>();
        if (set.list() != null) {
            for (String photo : set.list()) {
                returnVal.add(photo.substring(0, photo.length() - 4));
            }
        }
        return returnVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clearImageSet(final String imageSetTitle) {
        final File set = new File(mFile, imageSetTitle);
        if (set.exists()) {
            for (File singleFile : set.listFiles()) {
                if (!singleFile.delete()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
