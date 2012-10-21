/**
 * Copyright (c) 2012, University of Konstanz, Distributed Systems Group
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the University of Konstanz nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.jclouds.imagestore;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Set;

import javax.inject.Named;

import org.jclouds.blobstore.BlobStore;
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
import org.jclouds.imagestore.imagegenerator.ImageGenerator;
import org.jclouds.imagestore.imagehoster.IImageHost;
import org.jclouds.io.Payload;
import org.jclouds.javax.annotation.Nullable;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * This class implements the jClouds BlobStore interface and acts as adapter between jClouds and the image
 * store.
 * 
 * @author Wolfgang Miller
 */
public class SyncImageBlobStore implements BlobStore {

    /** The image host instance. */
    private final IImageHost ih;
    /** The image generator instance. */
    private final ImageGenerator ig;
    /** The blob builder. */
    private BlobBuilder bb;

    /**
     * Returns the maximum amount of bytes one image can hold with the given provider and byte painter.
     * 
     * @return the maximum amount of bytes
     */
    public int getMaximumBytesPerImage() {
        return ig.getMaximumBytesPerImage();
    }

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
    public SyncImageBlobStore(@Named(ImageStoreConstants.PROPERTY_IMAGEHOSTER) final String pImageHoster,
        @Named(ImageStoreConstants.PROPERTY_BYTEPAINTER) final String pBytePainter,
        @Named(FilesystemConstants.PROPERTY_BASEDIR) final String pStorageParameter) {
        Injector inj =
            Guice
                .createInjector(new BytePainterAndHosterModule(pImageHoster, pBytePainter, pStorageParameter));
        ih = inj.getInstance(IImageHost.class);
        IBytesToImagePainter painter = inj.getInstance(IBytesToImagePainter.class);
        ig = new ImageGenerator(painter, ih.getMaxImageWidth(), ih.getMaxImageHeight());

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
    public long countBlobs(final String container) {
        return ih.countImagesInSet(container);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countBlobs(final String container, final ListContainerOptions options) {
        return countBlobs(container);
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
    public BlobBuilder blobBuilder(final String blobName) {
        return bb.name(blobName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<? extends Location> listAssignableLocations() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageSet<? extends StorageMetadata> list() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containerExists(final String container) {
        return ih.imageSetExists(container);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createContainerInLocation(@Nullable final Location location, final String container) {
        return ih.createImageSet(container);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createContainerInLocation(@Nullable final Location location, final String container,
        final CreateContainerOptions options) {
        return createContainerInLocation(null, container);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageSet<? extends StorageMetadata> list(final String container) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageSet<? extends StorageMetadata>
        list(final String container, final ListContainerOptions options) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearContainer(final String container) {
        ih.clearImageSet(container);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearContainer(final String container, final ListContainerOptions options) {
        clearContainer(container);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteContainer(final String container) {
        ih.deleteImageSet(container);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean directoryExists(final String container, final String directory) {
        // TODO Directory??
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createDirectory(final String container, final String directory) {
        // TODO Directory??
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteDirectory(final String containerName, final String name) {
        // TODO Directory??

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean blobExists(final String container, final String name) {
        return ih.imageExists(container, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String putBlob(final String container, final Blob blob) {
        final Payload pl = blob.getPayload();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bs = null;
        try {
            pl.writeTo(baos);
            bs = baos.toByteArray();
            baos.flush();
            baos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BufferedImage bi = ig.createImageFromBytes(bs);

        return ih.uploadImage(container, blob.getMetadata().getName(), bi);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String putBlob(final String container, final Blob blob, final PutOptions options) {
        return putBlob(container, blob);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BlobMetadata blobMetadata(final String container, final String name) {
        // TODO Metadata??
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Blob getBlob(final String container, final String name) {
        BufferedImage bi = ih.downloadImage(container, name);
        final byte[] bs = ig.getBytesFromImage(bi);
        bb.payload(bs);
        return bb.build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Blob getBlob(final String container, final String name, final GetOptions options) {
        return getBlob(container, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeBlob(final String container, final String name) {
        ih.deleteImage(container, name);
    }

}
