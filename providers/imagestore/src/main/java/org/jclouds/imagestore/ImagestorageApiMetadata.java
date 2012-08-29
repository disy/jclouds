package org.jclouds.imagestore;

import java.net.URI;

import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.imagestore.config.ImagestoreContextModule;

public class ImagestorageApiMetadata extends BaseApiMetadata {

    /** The serialVersionUID */
    private static final long serialVersionUID = -7092309348450484397L;

    public static Builder builder() {
        return new Builder();
    }

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

        @Override
        public ImagestorageApiMetadata build() {
            return new ImagestorageApiMetadata(this);
        }

    }

}
