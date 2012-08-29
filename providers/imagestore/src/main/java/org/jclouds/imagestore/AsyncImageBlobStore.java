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

    @Override
    public BlobStoreContext getContext() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlobBuilder blobBuilder(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenableFuture<Set<? extends Location>> listAssignableLocations() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenableFuture<PageSet<? extends StorageMetadata>> list() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenableFuture<Boolean> containerExists(String container) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenableFuture<Boolean> createContainerInLocation(@Nullable Location location, String container) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenableFuture<Boolean> createContainerInLocation(@Nullable Location location, String container,
        CreateContainerOptions options) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenableFuture<PageSet<? extends StorageMetadata>> list(String container) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenableFuture<PageSet<? extends StorageMetadata>> list(String container,
        ListContainerOptions options) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenableFuture<Void> clearContainer(String container) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenableFuture<Void> clearContainer(String container, ListContainerOptions options) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenableFuture<Void> deleteContainer(String container) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenableFuture<Boolean> directoryExists(String container, String directory) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenableFuture<Void> createDirectory(String container, String directory) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenableFuture<Void> deleteDirectory(String containerName, String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenableFuture<Boolean> blobExists(String container, String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenableFuture<String> putBlob(String container, Blob blob) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenableFuture<String> putBlob(String container, Blob blob, PutOptions options) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenableFuture<BlobMetadata> blobMetadata(String container, String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenableFuture<Blob> getBlob(String container, String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenableFuture<Blob> getBlob(String container, String key, GetOptions options) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenableFuture<Void> removeBlob(String container, String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenableFuture<Long> countBlobs(String container) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenableFuture<Long> countBlobs(String container, ListContainerOptions options) {
        // TODO Auto-generated method stub
        return null;
    }

}
