/**
 * 
 */
package org.jclouds.imagestore.imagehoster;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jclouds.imagestore.imagehoster.file.ImageHostFile;
import org.jclouds.imagestore.imagehoster.flickr.ImageHostFlickr;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.io.Files;

/**
 * This class provides tests for the image host functionalities.
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public class ImageHostTest {

    /** The image set name. */
    private static final String SET1 = "set1";
    /** The first test image. */
    private static final String IMAGE1 = "image1";
    /** The second test image. */
    private static final String IMAGE2 = "image2";

    /**
     * Test method for {@link org.jclouds.imagestore.imagehoster.blobstore.IImageHost#ImageHostFile(java.io.File)} and
     * {@link org.jclouds.imagestore.imagehoster.blobstore.IImageHost#imageExists(java.lang.String, java.lang.String)} and
     * {@link org.jclouds.imagestore.imagehoster.blobstore.IImageHost#deleteImage(java.lang.String, java.lang.String)} and
     * {@link org.jclouds.imagestore.imagehoster.blobstore.IImageHost#uploadImage(java.lang.String, java.lang.String, java.awt.image.BufferedImage)}
     * and
     * {@link org.jclouds.imagestore.imagehoster.blobstore.IImageHost#uploadImage(java.lang.String, java.awt.image.BufferedImage)}
     * and
     * {@link org.jclouds.imagestore.imagehoster.blobstore.IImageHost#downloadImage(java.lang.String, java.lang.String)}
     * and {@link org.jclouds.imagestore.imagehoster.blobstore.IImageHost#countImagesInSet(java.lang.String)} and
     * {@link org.jclouds.imagestore.imagehoster.blobstore.IImageHost#clearImageSet(java.lang.String)}.
     * 
     * @param clazz
     *            to be tested with
     * @param pHandlers
     *            to be tested with
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * 
     */
    @Test(dataProvider = "fileHost", groups = "localTests")
    public void testImageLocal(final Class<IImageHost> clazz, final IImageHost[] pHandlers)
        throws IOException {
        checkImage(clazz, pHandlers);
    }

    /**
     * 
     * @param clazz
     * @param pHandlers
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test(dataProvider = "remoteHosts", groups = "remoteTests")
    public void testImageRemote(Class<IImageHost> clazz, IImageHost[] pHandlers) throws IOException {
        checkImage(clazz, pHandlers);
    }

    /**
     * The image checks.
     * 
     * @param clazz
     * @param pHandlers
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void checkImage(final Class<IImageHost> clazz, final IImageHost[] pHandlers) throws IOException {
        BufferedImage image = createBufferedImage();

        for (IImageHost host : pHandlers) {
            host.deleteImageSet(SET1);
            assertFalse(host.imageExists(SET1, IMAGE1));
            assertFalse(host.imageSetExists(SET1));
            assertTrue(host.createImageSet(SET1));
            host.uploadImage(SET1, IMAGE1, image);
            assertTrue(host.imageExists(SET1, IMAGE1));
            BufferedImage download = host.downloadImage(SET1, IMAGE1);
            compareImages(image, download);
            host.deleteImage(SET1, IMAGE1);
            assertFalse(host.imageExists(SET1, IMAGE1));
            host.uploadImage(SET1, IMAGE1, image);
            host.uploadImage(SET1, IMAGE2, image);
            assertTrue(host.imageExists(SET1, IMAGE1));
            assertTrue(host.imageExists(SET1, IMAGE2));
            host.clearImageSet(SET1);
            assertFalse(host.imageExists(SET1, IMAGE1));
            assertFalse(host.imageExists(SET1, IMAGE2));
            host.uploadImage(SET1, IMAGE1, image);
            host.uploadImage(SET1, IMAGE2, image);
            assertTrue(host.imageExists(SET1, IMAGE1));
            assertTrue(host.imageExists(SET1, IMAGE2));
            host.deleteImageSet(SET1);
            assertFalse(host.imageSetExists(SET1));
        }
    }

    /**
     * Compares the colors of two images.
     * 
     * @param img1
     *            The first image.
     * @param img2
     *            The second image.
     */
    private static void compareImages(final BufferedImage img1, final BufferedImage img2) {
        assertEquals(img1.getWidth(), img2.getWidth());
        assertEquals(img1.getHeight(), img2.getHeight());
        int columns = img1.getWidth();
        int rows = img1.getHeight();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                assertEquals(img1.getRGB(col, row), img2.getRGB(col, row));
            }
        }
    }

    /**
     * Set method for {@link org.jclouds.imagestore.imagehoster.blobstore.IImageHost#createImageSet(java.lang.String)} and
     * {@link org.jclouds.imagestore.imagehoster.blobstore.IImageHost#imageSetExists(java.lang.String)} and
     * {@link org.jclouds.imagestore.imagehoster.blobstore.IImageHost#deleteImageSet(java.lang.String)}.
     * 
     * @param clazz
     *            to be tested with
     * @param pHandlers
     *            to be tested with
     */
    @Test(dataProvider = "fileHost", groups = "localTests")
    public void testImageHostSetsLocal(final Class<IImageHost> clazz, final IImageHost[] pHandlers) {
        check(clazz, pHandlers);
    }
    
    /**
     * 
     * @param clazz
     * @param pHandlers
     */
    @Test(dataProvider = "remoteHosts", groups = "remoteTests")
    public void testImageHostSetsRemote(final Class<IImageHost> clazz, final IImageHost[] pHandlers) {
        check(clazz, pHandlers);
    }

    /**
     * The host checks.
     * 
     * @param clazz
     * @param pHandlers
     */
    private void check(final Class<IImageHost> clazz, final IImageHost[] pHandlers) {
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
     * Return an Object with the local host.
     * 
     * @return Object with the local host.
     */
    @DataProvider(name = "fileHost")
    public Object[][] fileHost() {

        Object[][] returnVal = {
            {
                IImageHost.class, new IImageHost[] {
                    new ImageHostFile(Files.createTempDir())
                }
            }
        };
        return returnVal;
    }

    /**
     * Return an Object with all remote hosts.
     * 
     * @return Object with all remote hosts.
     */
    @DataProvider(name = "remoteHosts")
    public Object[][] remoteHosts() {

        Object[][] returnVal = {
            {
                IImageHost.class, new IImageHost[] {
                    new ImageHostFlickr()
                }
            }
        };
        return returnVal;
    }

    /**
     * Returns a new BufferedImage.
     * 
     * @return The created BufferedImage.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private static BufferedImage createBufferedImage() throws IOException {
        final File imageFile =
            new File("src" + File.separator + "test" + File.separator + "resources" + File.separator
                + "pictures" + File.separator + "black.png");
        FileInputStream fis = new FileInputStream(imageFile);
        BufferedImage returnVals = ImageIO.read(fis);
        fis.close();
        return returnVals;

    }
}
