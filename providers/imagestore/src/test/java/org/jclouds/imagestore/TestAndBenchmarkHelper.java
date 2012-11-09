package org.jclouds.imagestore;

import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.filesystem.reference.FilesystemConstants;
import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.IEncoder;
import org.jclouds.imagestore.imagehoster.IImageHost;

import com.google.common.io.Files;

public class TestAndBenchmarkHelper {

    public static BlobStoreContext createContext(Class<? extends IImageHost> host,
        final Class<? extends IBytesToImagePainter> painter, final Class<? extends IEncoder> encoder) {
        return createContext(host, painter, encoder, 2);
    }

    public static BlobStoreContext createContext(Class<? extends IImageHost> host,
        final Class<? extends IBytesToImagePainter> painter, final Class<? extends IEncoder> encoder,
        int layers) {

        String identity = "user";
        String credential = "pass";

        Properties properties = new Properties();
        properties.setProperty(FilesystemConstants.PROPERTY_BASEDIR, Files.createTempDir().getAbsolutePath());
        properties.setProperty(ImageStoreConstants.PROPERTY_BYTEPAINTER, painter.getName());
        properties.setProperty(ImageStoreConstants.PROPERTY_ENCODER, encoder.getName());
        properties.setProperty(ImageStoreConstants.PROPERTY_IMAGEHOSTER, host.getName());
        properties.setProperty(ImageStoreConstants.PROPERTY_LAYERS, Integer.toString(layers));

        return ContextBuilder.newBuilder("imagestore").credentials(identity, credential)
            .overrides(properties).buildView(BlobStoreContext.class);
    }
}
