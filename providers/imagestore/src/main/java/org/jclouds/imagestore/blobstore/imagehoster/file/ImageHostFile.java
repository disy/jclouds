package org.jclouds.imagestore.blobstore.imagehoster.file;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jclouds.imagestore.blobstore.IImageHost;

public class ImageHostFile implements IImageHost {

    /** Location of this hoster. */
    private final File mFile;

    /** Standard container for the file. */
    private final static String STANDARDCONTAINER = "tmp";

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
        return new File(mFile, imageSetTitle).mkdir();
    }

    @Override
    public boolean imageExists(String imageSetTitle, String imageTitle) {
        final File set = new File(mFile, imageSetTitle);
        if (set.exists()) {
            final File image = new File(set, imageTitle);
            if (image.exists()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    @Override
    public boolean imageSetExists(String imageSetTitle) {
        return new File(mFile, imageSetTitle).exists();
    }

    @Override
    public void deleteImage(String imageSetTitle, String imageTitle) {
        final File set = new File(mFile, imageSetTitle);
        for (File singleFile : set.listFiles()) {
            if (singleFile.getName().equals(imageTitle)) {
                singleFile.delete();
                break;
            }
        }

    }

    @Override
    public void deleteImageSet(String imageSetTitle) {
        final File set = new File(mFile, imageSetTitle);
        if (set.exists()) {
            for (File singleFile : set.listFiles()) {
                singleFile.delete();
            }
            set.delete();
        }
    }

    @Override
    public String uploadImage(String imageSetTitle, String imageTitle, BufferedImage image)
        throws IOException {
        final File set = new File(mFile, imageSetTitle);
        set.mkdirs();
        final File imageFile = new File(set, imageTitle);
        imageFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(imageFile);
        ImageIO.write(image, "png", fos);
        fos.flush();
        fos.close();
        return imageFile.getAbsolutePath();
    }

    @Override
    public String uploadImage(String imageTitle, BufferedImage image) throws IOException {
        return uploadImage(STANDARDCONTAINER, imageTitle, image);
    }

    @Override
    public BufferedImage downloadImage(String imageSetTitle, String imageTitle) throws IOException {
        final File set = new File(mFile, imageSetTitle);
        final File imageFile = new File(set, imageTitle);
        FileInputStream fis = new FileInputStream(imageFile);
        BufferedImage returnVal = ImageIO.read(fis);
        fis.close();
        return returnVal;
    }

    @Override
    public int countImagesInSet(String imageSetTitle) {
        final File set = new File(mFile, imageSetTitle);
        return set.list().length;
    }

    @Override
    public void clearImageSet(String imageSetTitle) {
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
