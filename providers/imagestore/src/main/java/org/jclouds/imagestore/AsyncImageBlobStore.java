package org.jclouds.imagestore;

import java.util.Set;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.domain.Location;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.util.concurrent.ListenableFuture;

public class AsyncImageBlobStore implements AsyncBlobStore {

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
    public ListenableFuture<Boolean> createContainerInLocation(@Nullable final Location location, final String container) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<Boolean> createContainerInLocation(@Nullable final Location location, final String container,
        final CreateContainerOptions options) {
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
    public ListenableFuture<String> putBlob(final String container, final Blob blob, final PutOptions options) {
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
