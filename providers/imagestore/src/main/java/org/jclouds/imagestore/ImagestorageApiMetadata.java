package org.jclouds.imagestore;

import java.net.URI;

import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.imagestore.config.ImagestoreContextModule;

public class ImagestorageApiMetadata extends BaseApiMetadata {

    public static Builder builder() {
        return new Builder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Builder toBuilder() {
        return Builder.class.cast(builder().fromApiMetadata(this));
    }

    public ImagestorageApiMetadata() {
        super(builder());
    }

    protected ImagestorageApiMetadata(Builder builder) {
        super(builder);
    }

    public static class Builder extends BaseApiMetadata.Builder {

        protected Builder() {
            id("imagestore").name("Imagestore-based BlobStore").identityName("Unused").defaultEndpoint(
                "http://localhost/transient").defaultIdentity(System.getProperty("user.name"))
                .defaultCredential("bar").version("1").documentation(
                    URI.create("http://www.jclouds.org/documentation/userguide/blobstore-guide")).view(
                    BlobStoreContext.class).defaultModule(ImagestoreContextModule.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ImagestorageApiMetadata build() {
            return new ImagestorageApiMetadata(this);
        }

        @Override
        protected org.jclouds.apis.internal.BaseApiMetadata.Builder self() {
            return this;
        }

    }

}
