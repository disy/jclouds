package org.jclouds.imagestore;

import org.jclouds.Context;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.InputStreamMap;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.Utils;

public class ImagestoreContext implements BlobStoreContext {

    @Override
    public com.google.common.reflect.TypeToken<?> getBackendType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <C extends Context> C unwrap(com.google.common.reflect.TypeToken<C> type)
        throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <C extends Context> C unwrap(Class<C> clazz) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <C extends Context> C unwrap() throws ClassCastException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlobRequestSigner getSigner() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStreamMap createInputStreamMap(String container, ListContainerOptions options) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStreamMap createInputStreamMap(String container) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlobMap createBlobMap(String container, ListContainerOptions options) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlobMap createBlobMap(String container) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AsyncBlobStore getAsyncBlobStore() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlobStore getBlobStore() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ConsistencyModel getConsistencyModel() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Utils getUtils() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Utils utils() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public
    <S, A> RestContext<S, A> getProviderSpecificContext() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public
    void close() {
        // TODO Auto-generated method stub
        
    }

}
