package org.jclouds.imagestore;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Set;

import javax.inject.Named;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.internal.BlobBuilderImpl;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.domain.Location;
import org.jclouds.encryption.internal.JCECrypto;
import org.jclouds.filesystem.reference.FilesystemConstants;
import org.jclouds.imagestore.config.BytePainterAndHosterModule;
import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.IEncoder;
import org.jclouds.imagestore.imagegenerator.ImageGenerator;
import org.jclouds.imagestore.imagehoster.IImageHost;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class AsyncImageBlobStore implements AsyncBlobStore {


    private final static String DEL = "%";

    /** The image host instance. */
    private final IImageHost ih;
    /** The image generator instance. */
    private final ImageGenerator ig;
    /** The blob builder. */
    private BlobBuilder bb;

    /**
     * ImageBlobStore constructor.
     * 
     * @param pImageHoster
     *            The class-name of the image host to be used.
     * @param pBytePainter
     *            The image generator to be used.
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     */
    @Inject
    public AsyncImageBlobStore(@Named(ImageStoreConstants.PROPERTY_IMAGEHOSTER) final String pImageHoster,
        @Named(ImageStoreConstants.PROPERTY_BYTEPAINTER) final String pBytePainter,
        @Named(ImageStoreConstants.PROPERTY_LAYERS) final String pLayers,
        @Named(ImageStoreConstants.PROPERTY_ENCODER) final String pEncoder,
        @Named(FilesystemConstants.PROPERTY_BASEDIR) final String pStorageParameter) {
        Injector inj =
            Guice.createInjector(new BytePainterAndHosterModule(pImageHoster, pBytePainter, pLayers,
                pEncoder, pStorageParameter));
        ih = inj.getInstance(IImageHost.class);
        IBytesToImagePainter painter = inj.getInstance(IBytesToImagePainter.class);
        IEncoder encoder = inj.getInstance(IEncoder.class);
        ig = new ImageGenerator(painter, encoder, ih.getMaxImageWidth(), ih.getMaxImageHeight());

        try {
            bb = new BlobBuilderImpl(new JCECrypto());
        } catch (NoSuchAlgorithmException e) {
            new RuntimeException(e);
        } catch (CertificateException e) {
            new RuntimeException(e);
        }
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public BlobStoreContext getContext() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BlobBuilder blobBuilder(final String name) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<Set<? extends Location>> listAssignableLocations() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<PageSet<? extends StorageMetadata>> list() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<Boolean> containerExists(final String container) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<Boolean> createContainerInLocation(@Nullable final Location location,
        final String container) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<Boolean> createContainerInLocation(@Nullable final Location location,
        final String container, final CreateContainerOptions options) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<PageSet<? extends StorageMetadata>> list(final String container) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<PageSet<? extends StorageMetadata>> list(final String container,
        final ListContainerOptions options) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<Void> clearContainer(final String container) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<Void> clearContainer(final String container, final ListContainerOptions options) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<Void> deleteContainer(final String container) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<Boolean> directoryExists(final String container, final String directory) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<Void> createDirectory(final String container, final String directory) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<Void> deleteDirectory(final String containerName, final String name) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<Boolean> blobExists(final String container, final String name) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<String> putBlob(final String container, final Blob blob) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<String>
        putBlob(final String container, final Blob blob, final PutOptions options) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<BlobMetadata> blobMetadata(final String container, final String key) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<Blob> getBlob(final String container, final String key) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<Blob> getBlob(final String container, final String key, final GetOptions options) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<Void> removeBlob(final String container, final String key) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<Long> countBlobs(final String container) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<Long> countBlobs(final String container, final ListContainerOptions options) {
        // TODO Auto-generated method stub
        return null;
    }

}
