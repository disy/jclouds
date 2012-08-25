/**
 * 
 */
package org.jclouds.imagestore.blobstore;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.awt.image.BufferedImage;

import org.jclouds.imagestore.blobstore.imagehoster.file.ImageHostFile;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.io.Files;

/**
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public class ImageHostTest {

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
     */
    @Test(dataProvider = "instantiateHosts")
    public void testImageHost(Class<ImageHost> clazz, ImageHost[] pHandlers) {

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
        String set1 = "set1";
        for (ImageHost host : pHandlers) {
            assertFalse(host.imageSetExists(set1));
            assertTrue(host.createImageSet(set1));
            assertTrue(host.imageSetExists(set1));
            host.deleteImageSet(set1);
            assertFalse(host.imageSetExists(set1));
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

    private static BufferedImage createBufferedImage() {
        
        
        
    }
}
