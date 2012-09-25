package org.jclouds.imagestore.imagehoster.file;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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

    /** Location of this hoster. */
    private final File mFile;

    /** Standard container for the file. */
    private static final String STANDARDCONTAINER = "tmp";

    /**
     * Constructor.
     * 
     * @param pFile
     */
    @Inject
    public ImageHostFile(@Named(FilesystemConstants.PROPERTY_BASEDIR) String baseDir) {
        mFile = new File(baseDir);
    }

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
        return new File(mFile, imageSetTitle).mkdir();
    }

    @Override
    public boolean imageExists(final String imageSetTitle, final String imageTitle) {
        final File set = new File(mFile, imageSetTitle);
        if (set.exists()) {
            final File image = new File(set, imageTitle);
            return image.exists();
        } else {
            return false;
        }

    }

    @Override
    public boolean imageSetExists(final String imageSetTitle) {
        return new File(mFile, imageSetTitle).exists();
    }

    @Override
    public void deleteImage(final String imageSetTitle, final String imageTitle) {
        final File set = new File(mFile, imageSetTitle);
        for (File singleFile : set.listFiles()) {
            if (singleFile.getName().equals(imageTitle)) {
                singleFile.delete();
                break;
            }
        }

    }

    @Override
    public void deleteImageSet(final String imageSetTitle) {
        final File set = new File(mFile, imageSetTitle);
        if (set.exists()) {
            for (File singleFile : set.listFiles()) {
                singleFile.delete();
            }
            set.delete();
        }
    }

    @Override
    public String uploadImage(final String imageSetTitle, final String imageTitle, final BufferedImage image) {
        final File set = new File(mFile, imageSetTitle);
        set.mkdirs();
        final File imageFile = new File(set, imageTitle);
        try {
            imageFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(imageFile);
            ImageIO.write(image, "png", fos);
            fos.flush();
            fos.close();
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String uploadImage(final String imageTitle, final BufferedImage image) {
        return uploadImage(STANDARDCONTAINER, imageTitle, image);
    }

    @Override
    public BufferedImage downloadImage(final String imageSetTitle, final String imageTitle) {
        final File set = new File(mFile, imageSetTitle);
        final File imageFile = new File(set, imageTitle);
        try {
            FileInputStream fis = new FileInputStream(imageFile);
            BufferedImage returnVal = ImageIO.read(fis);
            fis.close();
            return returnVal;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int countImagesInSet(final String imageSetTitle) {
        final File set = new File(mFile, imageSetTitle);
        return set.list().length;
    }

    @Override
    public void clearImageSet(final String imageSetTitle) {
        final File set = new File(mFile, imageSetTitle);
        if (set.exists()) {
            for (File singleFile : set.listFiles()) {
                if (!singleFile.delete()) {
                    throw new IllegalStateException(new StringBuilder("File ").append(
                        singleFile.getAbsolutePath()).append(" could not be deleted!").toString());
                }
            }
        }
    }
}
