/**
 * 
 */
package org.jclouds.imagestore.blobstore.imagehoster;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jclouds.imagestore.blobstore.IImageHost;
import org.jclouds.imagestore.blobstore.imagehoster.file.ImageHostFile;
import org.jclouds.imagestore.blobstore.imagehoster.flickr.ImageHostFlickr;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.io.Files;

/**
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public class ImageHostTest {

    private final static String SET1 = "set1";
    private final static String IMAGE1 = "image1";
    private final static String IMAGE2 = "image2";

    /**
     * Test method for {@link org.jclouds.imagestore.blobstore.IImageHost#ImageHostFile(java.io.File)} and
     * {@link org.jclouds.imagestore.blobstore.IImageHost#imageExists(java.lang.String, java.lang.String)} and
     * {@link org.jclouds.imagestore.blobstore.IImageHost#deleteImage(java.lang.String, java.lang.String)} and
     * {@link org.jclouds.imagestore.blobstore.IImageHost#uploadImage(java.lang.String, java.lang.String, java.awt.image.BufferedImage)}
     * and
     * {@link org.jclouds.imagestore.blobstore.IImageHost#uploadImage(java.lang.String, java.awt.image.BufferedImage)}
     * and
     * {@link org.jclouds.imagestore.blobstore.IImageHost#downloadImage(java.lang.String, java.lang.String)}
     * and {@link org.jclouds.imagestore.blobstore.IImageHost#countImagesInSet(java.lang.String)} and
     * {@link org.jclouds.imagestore.blobstore.IImageHost#clearImageSet(java.lang.String)}.
     * 
     * @param clazz
     *            to be tested with
     * @param pHandlers
     *            to be tested with
     * @throws IOException
     */
    @Test(dataProvider = "instantiateHosts")
    public void testImage(Class<IImageHost> clazz, IImageHost[] pHandlers) throws IOException {
        BufferedImage[] images = createBufferedImage();

        for (IImageHost host : pHandlers) {
            host.deleteImageSet(SET1);
            assertFalse(host.imageExists(SET1, IMAGE1));
            assertFalse(host.imageSetExists(SET1));
            assertTrue(host.createImageSet(SET1));
            host.uploadImage(SET1, IMAGE1, images[0]);
            assertTrue(host.imageExists(SET1, IMAGE1));
            BufferedImage img = host.downloadImage(SET1, IMAGE1);
            host.deleteImage(SET1, IMAGE1);
            assertFalse(host.imageExists(SET1, IMAGE1));
            host.uploadImage(SET1, IMAGE1, images[0]);
            host.uploadImage(SET1, IMAGE2, images[1]);
            assertTrue(host.imageExists(SET1, IMAGE1));
            assertTrue(host.imageExists(SET1, IMAGE2));
            host.clearImageSet(SET1);
            assertFalse(host.imageExists(SET1, IMAGE1));
            assertFalse(host.imageExists(SET1, IMAGE2));
            host.uploadImage(SET1, IMAGE1, images[0]);
            host.uploadImage(SET1, IMAGE2, images[1]);
            assertTrue(host.imageExists(SET1, IMAGE1));
            assertTrue(host.imageExists(SET1, IMAGE2));
            host.deleteImageSet(SET1);
            assertFalse(host.imageSetExists(SET1));
        }

    }

    /**
     * Set method for {@link org.jclouds.imagestore.blobstore.IImageHost#createImageSet(java.lang.String)} and
     * {@link org.jclouds.imagestore.blobstore.IImageHost#imageSetExists(java.lang.String)} and
     * {@link org.jclouds.imagestore.blobstore.IImageHost#deleteImageSet(java.lang.String)}
     * 
     * @param clazz
     *            to be tested with
     * @param pHandlers
     *            to be tested with
     */
    @Test(dataProvider = "instantiateHosts")
    public void testImageHostSets(Class<IImageHost> clazz, IImageHost[] pHandlers) {
        for (IImageHost host : pHandlers) {
            host.deleteImageSet(SET1);
            assertFalse(host.imageSetExists(SET1));
            assertTrue(host.createImageSet(SET1));
            assertTrue(host.imageSetExists(SET1));
            host.deleteImageSet(SET1);
            assertFalse(host.imageSetExists(SET1));
        }
    }

    /**
     * Providing different implementations of the {@link IImageHost} as Dataprovider to the test class.
     * 
     * @return different classes of the {@link IImageHost}
     */
    @DataProvider(name = "instantiateHosts")
    public Object[][] instantiateHosts() {

        Object[][] returnVal = {
            {
                IImageHost.class, new IImageHost[] {
                    new ImageHostFile(Files.createTempDir()), new ImageHostFlickr()
                }
            }
        };
        return returnVal;
    }

    private static BufferedImage[] createBufferedImage() throws IOException {
        final File imageFile =
            new File("src" + File.separator + "test" + File.separator + "resources" + File.separator
                + "pictures");
        BufferedImage[] returnVals = new BufferedImage[imageFile.listFiles().length];
        int i = 0;
        for (File file : imageFile.listFiles()) {
            FileInputStream fis = new FileInputStream(file);
            returnVals[i] = ImageIO.read(fis);
            fis.close();
            i++;
        }
        return returnVals;

    }
}
