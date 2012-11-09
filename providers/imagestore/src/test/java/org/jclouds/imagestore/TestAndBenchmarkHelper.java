package org.jclouds.imagestore;

import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.filesystem.reference.FilesystemConstants;

import com.google.common.io.Files;

public class TestAndBenchmarkHelper {

    enum HOSTER {
        FACEBOOK, PICASA, FLICKR, FILE
    };

    public static BlobStoreContext createContext(HOSTER host) {

        String identity = "user";
        String credential = "pass";

        switch (host) {

        case FILE:
            Properties properties1 = new Properties();
            properties1.setProperty(FilesystemConstants.PROPERTY_BASEDIR, Files.createTempDir()
                .getAbsolutePath());
            properties1.setProperty(ImageStoreConstants.PROPERTY_BYTEPAINTER,
                "org.jclouds.imagestore.imagegenerator.bytepainter.BinaryBytesToImagePainter");
            properties1.setProperty(ImageStoreConstants.PROPERTY_ENCODER,
                "org.jclouds.imagestore.imagegenerator.IEncoder$DummyEncoder");
            properties1.setProperty(ImageStoreConstants.PROPERTY_IMAGEHOSTER,
                "org.jclouds.imagestore.imagehoster.file.ImageHostFile");
            return ContextBuilder.newBuilder("imagestore").credentials(identity, credential).overrides(
                properties1).buildView(BlobStoreContext.class);
        case PICASA:
            Properties properties2 = new Properties();
            properties2.setProperty(FilesystemConstants.PROPERTY_BASEDIR, Files.createTempDir()
                .getAbsolutePath());
            properties2.setProperty(ImageStoreConstants.PROPERTY_BYTEPAINTER,
                "org.jclouds.imagestore.imagegenerator.bytepainter.BinaryBytesToImagePainter");
            properties2.setProperty(ImageStoreConstants.PROPERTY_ENCODER,
                "org.jclouds.imagestore.imagegenerator.IEncoder$DummyEncoder");
            properties2.setProperty(ImageStoreConstants.PROPERTY_IMAGEHOSTER,
                "org.jclouds.imagestore.imagehoster.picasa.ImageHostGoogleDataApiPicasa");

            return ContextBuilder.newBuilder("imagestore").credentials(identity, credential).overrides(
                properties2).buildView(BlobStoreContext.class);

        case FLICKR:
            Properties properties3 = new Properties();
            properties3.setProperty(FilesystemConstants.PROPERTY_BASEDIR, Files.createTempDir()
                .getAbsolutePath());
            properties3.setProperty(ImageStoreConstants.PROPERTY_BYTEPAINTER,
                "org.jclouds.imagestore.imagegenerator.bytepainter.BinaryBytesToImagePainter");
            properties3.setProperty(ImageStoreConstants.PROPERTY_ENCODER,
                "org.jclouds.imagestore.imagegenerator.IEncoder$DummyEncoder");
            properties3.setProperty(ImageStoreConstants.PROPERTY_IMAGEHOSTER,
                "org.jclouds.imagestore.imagehoster.flickr.ImageHostFlickr");

            return ContextBuilder.newBuilder("imagestore").credentials(identity, credential).overrides(
                properties3).buildView(BlobStoreContext.class);

        case FACEBOOK:
            Properties properties4 = new Properties();
            properties4.setProperty(FilesystemConstants.PROPERTY_BASEDIR, Files.createTempDir()
                .getAbsolutePath());
            properties4.setProperty(ImageStoreConstants.PROPERTY_BYTEPAINTER,
                "org.jclouds.imagestore.imagegenerator.bytepainter.BinaryBytesToImagePainter");
            properties4.setProperty(ImageStoreConstants.PROPERTY_ENCODER,
                "org.jclouds.imagestore.imagegenerator.IEncoder$DummyEncoder");
            properties4.setProperty(ImageStoreConstants.PROPERTY_IMAGEHOSTER,
                "org.jclouds.imagestore.imagehoster.facebook.ImageHostFacebook");

            return ContextBuilder.newBuilder("imagestore").credentials(identity, credential).overrides(
                properties4).buildView(BlobStoreContext.class);

        default:
            return null;
        }

    }
}
