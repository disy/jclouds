package org.jclouds.imagestore.config;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.config.BlobStoreMapModule;
import org.jclouds.blobstore.config.BlobStoreObjectModule;
import org.jclouds.imagestore.AsyncImageBlobStore;
import org.jclouds.imagestore.SyncImageBlobStore;
import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.bytepainter.HexadecimalBytesToImagePainter;
import org.jclouds.imagestore.imagehoster.IImageHost;
import org.jclouds.imagestore.imagehoster.file.ImageHostFile;

import com.google.inject.AbstractModule;

/**
 * 
 * @author Sebastian Graf, University of Konstanz
 */
public class ImagestoreContextModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IBytesToImagePainter.class).to(HexadecimalBytesToImagePainter.class);
        bind(IImageHost.class).to(ImageHostFile.class);
        bind(BlobStore.class).to(SyncImageBlobStore.class);
        bind(AsyncBlobStore.class).to(AsyncImageBlobStore.class);
        install(new BlobStoreObjectModule());
        install(new BlobStoreMapModule());
        bind(ConsistencyModel.class).toInstance(ConsistencyModel.STRICT);
    }
}
