/**
 * 
 */
package org.jclouds.imagestore.blobstore;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jclouds.imagestore.blobstore.imagehoster.file.ImageHostFile;
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
     * Test method for {@link org.jclouds.imagestore.blobstore.ImageHost#ImageHostFile(java.io.File)} and
     * {@link org.jclouds.imagestore.blobstore.ImageHost#imageExists(java.lang.String, java.lang.String)} and
     * {@link org.jclouds.imagestore.blobstore.ImageHost#deleteImage(java.lang.String, java.lang.String)} and
     * {@link org.jclouds.imagestore.blobstore.ImageHost#uploadImage(java.lang.String, java.lang.String, java.awt.image.BufferedImage)}
     * and
     * {@link org.jclouds.imagestore.blobstore.ImageHost#uploadImage(java.lang.String, java.awt.image.BufferedImage)}
     * and
     * {@link org.jclouds.imagestore.blobstore.ImageHost#downloadImage(java.lang.String, java.lang.String)}
     * and {@link org.jclouds.imagestore.blobstore.ImageHost#countImagesInSet(java.lang.String)} and
     * {@link org.jclouds.imagestore.blobstore.ImageHost#clearImageSet(java.lang.String)}.
     * 
     * @param clazz
     *            to be tested with
     * @param pHandlers
     *            to be tested with
     * @throws IOException
     */
    @Test(dataProvider = "instantiateHosts")
    public void testImage(Class<ImageHost> clazz, ImageHost[] pHandlers) throws IOException {
        BufferedImage[] images = createBufferedImage();

        for (ImageHost host : pHandlers) {
            assertFalse(host.imageExists(SET1, IMAGE1));
            assertFalse(host.imageSetExists(SET1));
            assertTrue(host.createImageSet(SET1));
            host.uploadImage(SET1, IMAGE1, images[0]);
            assertTrue(host.imageExists(SET1, IMAGE1));
            BufferedImage img = host.downloadImage(SET1, IMAGE1);
            // assertTrue(img.getGraphics().hashCode() == images[0].getGraphics().hashCode());
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
     * Set method for {@link org.jclouds.imagestore.blobstore.ImageHost#createImageSet(java.lang.String)} and
     * {@link org.jclouds.imagestore.blobstore.ImageHost#imageSetExists(java.lang.String)} and
     * {@link org.jclouds.imagestore.blobstore.ImageHost#deleteImageSet(java.lang.String)}
     * 
     * @param clazz
     *            to be tested with
     * @param pHandlers
     *            to be tested with
     */
    @Test(dataProvider = "instantiateHosts")
    public void testImageHostSets(Class<ImageHost> clazz, ImageHost[] pHandlers) {
        for (ImageHost host : pHandlers) {
            assertFalse(host.imageSetExists(SET1));
            assertTrue(host.createImageSet(SET1));
            assertTrue(host.imageSetExists(SET1));
            host.deleteImageSet(SET1);
            assertFalse(host.imageSetExists(SET1));
        }
    }

    /**
     * Providing different implementations of the {@link ImageHost} as Dataprovider to the test class.
     * 
     * @return different classes of the {@link ImageHost}
     */
    @DataProvider(name = "instantiateHosts")
    public Object[][] instantiateHosts() {

        Object[][] returnVal = {
            {
                ImageHost.class, new ImageHost[] {
                    new ImageHostFile(Files.createTempDir())
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
