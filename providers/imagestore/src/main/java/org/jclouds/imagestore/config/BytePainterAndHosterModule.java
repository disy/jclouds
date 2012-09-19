/**
 * 
 */
package org.jclouds.imagestore.config;

import java.util.Properties;

import org.jclouds.filesystem.reference.FilesystemConstants;
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
        mProps.setProperty(FilesystemConstants.PROPERTY_BASEDIR, pStorageParameter);
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
