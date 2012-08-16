        package org.jclouds.imagestore.blobstore;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Set;

import org.jclouds.crypto.Crypto;
import org.jclouds.domain.Location;
import org.jclouds.encryption.internal.JCECrypto;
import org.jclouds.imagestore.blobstore.flickr.FlickrOAuth;
import org.jclouds.imagestore.blobstore.flickr.ImageHostFlickr;
import org.jclouds.imagestore.blobstore.imagegenerator.ImageGenerator;
import org.jclouds.imagestore.blobstore.imagegenerator.bytepainter.HeptalLayeredBytesToImagePainter;
import org.jclouds.io.Payload;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.blobstore.domain.internal.BlobBuilderImpl;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.internal.BaseBlobStore;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.util.BlobUtils;

import com.google.common.base.Supplier;

public class ImageBlobStore implements BlobStore{
    
    /** ih the image host. */
    private final ImageHost ih;
    /** ig the image generator. */
    private final ImageGenerator ig;
    
    private BlobBuilder bb = null;

    public ImageBlobStore(){
        // TODO Auto-generated constructor stub
        ih = new ImageHostFlickr(new FlickrOAuth());
        ig = new ImageGenerator(new HeptalLayeredBytesToImagePainter());
        try {
            bb =  new BlobBuilderImpl(new JCECrypto());
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CertificateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public PageSet<? extends StorageMetadata> list() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean containerExists(final String container) {
        return ih.imageSetExists(container);
    }

    @Override
    public boolean createContainerInLocation(@Nullable final Location location, final String container) {
        return ih.createImageSet(container);
    }

    @Override
    public boolean createContainerInLocation(@Nullable final Location location, final String container,
        CreateContainerOptions options) {
        // TODO Options?
        return createContainerInLocation(null, container);
    }

    @Override
    public PageSet<? extends StorageMetadata> list(String container, ListContainerOptions options) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean blobExists(final String container, final String name) {
        return ih.imageExists(container, name);
    }

    @Override
    public String putBlob(final String container, final Blob blob) {
        final Payload pl = blob.getPayload();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            pl.writeTo(baos);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        };
        
        byte [] bs = baos.toByteArray();
        BufferedImage bi = ig.createImageFromBytes(bs);
                
        return ih.uploadImage(container, blob.getMetadata().getName(), bi);
    }

    @Override
    public String putBlob(final String container, final Blob blob, final PutOptions options) {
        // TODO Options?
        return putBlob(container, blob);
    }

    @Override
    public BlobMetadata blobMetadata(final String container, final String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Blob getBlob(final String container, final String name, final GetOptions options) {
        return getBlob(container, name);
    }
    
    @Override
    public Blob getBlob(String container, String name) {
        BufferedImage bi = ih.downloadImage(container, name);
        final byte[] bs = ig.getBytesFromImage(bi);
        bb.payload(bs);
        return bb.build();
    }

    @Override
    public void removeBlob(final String container, final String name) {
        ih.deleteImage(container, name);
    }

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
    public Set<? extends Location> listAssignableLocations() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PageSet<? extends StorageMetadata> list(String container) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void clearContainer(String container) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void clearContainer(String container, ListContainerOptions options) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deleteContainer(String container) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean directoryExists(String container, String directory) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void createDirectory(String container, String directory) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deleteDirectory(String containerName, String name) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public long countBlobs(String container) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long countBlobs(String container, ListContainerOptions options) {
        // TODO Auto-generated method stub
        return 0;
    }


}
