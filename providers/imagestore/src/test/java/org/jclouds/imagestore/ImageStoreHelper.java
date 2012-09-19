/**
 * 
 */
package org.jclouds.imagestore;

import java.util.Properties;

import org.jclouds.filesystem.reference.FilesystemConstants;

import com.google.common.io.Files;

/**
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public class ImageStoreHelper {
    public final static Properties getProps() {
        final Properties properties = new Properties();
        properties.setProperty(ImageStoreConstants.PROPERTY_FLICKR_APP_KEY,
            "3e6f5174edc3744e57c496db5d780ee8");
        properties.setProperty(ImageStoreConstants.PROPERTY_FLICKR_SHARED_SECRET, "a23933fe38c54919");
        properties.setProperty(FilesystemConstants.PROPERTY_BASEDIR, Files.createTempDir().getAbsolutePath());
        return properties;
    }
}
