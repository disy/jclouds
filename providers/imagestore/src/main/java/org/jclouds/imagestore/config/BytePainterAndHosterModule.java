/**
 * 
 */
package org.jclouds.imagestore.config;

import java.util.Properties;

import org.jclouds.imagestore.ImageStoreConstants;
import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;
import org.jclouds.imagestore.imagehoster.IImageHost;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public class BytePainterAndHosterModule extends AbstractModule {

    private final String mImageHoster;
    private final String mBytePainter;
    private final Properties mProps;

    public BytePainterAndHosterModule(String pImageHoster, String pBytePainter, String pStorageParameter) {
        this.mBytePainter = pBytePainter;
        mImageHoster = pImageHoster;
        mProps = new Properties();
        mProps.setProperty(ImageStoreConstants.PROPERTY_STORAGEPARAMETER, pStorageParameter);
        mProps.setProperty(ImageStoreConstants.PROPERTY_FLICKR_APP_KEY, "3e6f5174edc3744e57c496db5d780ee8");
        mProps.setProperty(ImageStoreConstants.PROPERTY_FLICKR_SHARED_SECRET, "a23933fe38c54919");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        try {
            Names.bindProperties(binder(), mProps);
            bind(IBytesToImagePainter.class).to(
                Class.forName(mBytePainter).asSubclass(IBytesToImagePainter.class));
            bind(IImageHost.class).to(Class.forName(mImageHoster).asSubclass(IImageHost.class));
        } catch (ClassNotFoundException exc) {
            exc.fillInStackTrace();
        }
    }

}
