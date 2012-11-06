/**
 * 
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 * 
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.imagestore;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.filesystem.reference.FilesystemConstants;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

public class BlobContextTester {

    private static byte[][] vals = new byte[5][];

    private static Random ran = new Random(12l);

    static {
        for (int i = 0; i < vals.length; i++) {
            vals[i] = new byte[ran.nextInt(700000)];
            ran.nextBytes(vals[i]);
        }
    }

    // public static final Map<String, ApiMetadata> allApis = Maps.uniqueIndex(Apis
    // .viewableAs(BlobStoreContext.class), Apis.idFunction());
    //
    // public static final Map<String, ProviderMetadata> appProviders = Maps.uniqueIndex(Providers
    // .viewableAs(BlobStoreContext.class), Providers.idFunction());
    //
    // public static final Set<String> allKeys = ImmutableSet.copyOf(Iterables.concat(appProviders.keySet(),
    // allApis.keySet()));

    // Test all registered providers (over dependency) with the given imagegenerator
    @Test(dataProvider = "blobContextProvider")
    public void test(final Class<BlobStoreContext> clazz, final BlobStoreContext[] pContext)
        throws IOException {
        String containerName = "blobcontexttest";

        for (BlobStoreContext context : pContext) {

            // Create Container
            BlobStore blobStore = context.getBlobStore();
            blobStore.deleteContainer(containerName);
            assertTrue(blobStore.createContainerInLocation(null, containerName));

            for (int i = 0; i < vals.length; i++) {

                // add blob
                BlobBuilder blobbuilder =
                    blobStore.blobBuilder(new StringBuilder("test").append(i).toString());
                Blob blob = blobbuilder.build();
                blob.setPayload(vals[i]);
                blobStore.putBlob(containerName, blob);
            }

            for (int i = 0; i < vals.length; i++) {
                Blob blobRetrieved =
                    blobStore.getBlob(containerName, new StringBuilder("test").append(i).toString());
                InputStream in = blobRetrieved.getPayload().getInput();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ByteStreams.copy(in, out);
                if (!Arrays.equals(out.toByteArray(), vals[i])) {
                    fail();
                }
            }

//            blobStore.deleteContainer(containerName);
            context.close();
        }
    }

    @DataProvider(name = "blobContextProvider")
    public Object[][] blobContextProvider() {

        String identity = "user";
        String credential = "pass";

        Properties properties1 = new Properties();
        properties1
            .setProperty(FilesystemConstants.PROPERTY_BASEDIR, Files.createTempDir().getAbsolutePath());
        properties1.setProperty(ImageStoreConstants.PROPERTY_BYTEPAINTER,
            "org.jclouds.imagestore.imagegenerator.bytepainter.BinaryBytesToImagePainter");
        properties1.setProperty(ImageStoreConstants.PROPERTY_ENCODER,
            "org.jclouds.imagestore.imagegenerator.IEncoder$DummyEncoder");
        properties1.setProperty(ImageStoreConstants.PROPERTY_IMAGEHOSTER,
            "org.jclouds.imagestore.imagehoster.file.ImageHostFile");

        BlobStoreContext context1 =
            ContextBuilder.newBuilder("imagestore").credentials(identity, credential).overrides(properties1)
                .buildView(BlobStoreContext.class);

        Properties properties2 = new Properties();
        properties2
            .setProperty(FilesystemConstants.PROPERTY_BASEDIR, Files.createTempDir().getAbsolutePath());
        properties2.setProperty(ImageStoreConstants.PROPERTY_BYTEPAINTER,
            "org.jclouds.imagestore.imagegenerator.bytepainter.BinaryBytesToImagePainter");
        properties2.setProperty(ImageStoreConstants.PROPERTY_ENCODER,
            "org.jclouds.imagestore.imagegenerator.IEncoder$DummyEncoder");
        properties2.setProperty(ImageStoreConstants.PROPERTY_IMAGEHOSTER,
            "org.jclouds.imagestore.imagehoster.picasa.ImageHostGoogleDataApiPicasa");

        BlobStoreContext context2 =
            ContextBuilder.newBuilder("imagestore").credentials(identity, credential).overrides(properties2)
                .buildView(BlobStoreContext.class);

        Properties properties3 = new Properties();
        properties3
            .setProperty(FilesystemConstants.PROPERTY_BASEDIR, Files.createTempDir().getAbsolutePath());
        properties3.setProperty(ImageStoreConstants.PROPERTY_BYTEPAINTER,
            "org.jclouds.imagestore.imagegenerator.bytepainter.BinaryBytesToImagePainter");
        properties3.setProperty(ImageStoreConstants.PROPERTY_ENCODER,
            "org.jclouds.imagestore.imagegenerator.IEncoder$DummyEncoder");
        properties3.setProperty(ImageStoreConstants.PROPERTY_IMAGEHOSTER,
            "org.jclouds.imagestore.imagehoster.flickr.ImageHostFlickr");

        BlobStoreContext context3 =
            ContextBuilder.newBuilder("imagestore").credentials(identity, credential).overrides(properties3)
                .buildView(BlobStoreContext.class);
        
        Properties properties4 = new Properties();
        properties4
            .setProperty(FilesystemConstants.PROPERTY_BASEDIR, Files.createTempDir().getAbsolutePath());
        properties4.setProperty(ImageStoreConstants.PROPERTY_BYTEPAINTER,
            "org.jclouds.imagestore.imagegenerator.bytepainter.BinaryBytesToImagePainter");
        properties4.setProperty(ImageStoreConstants.PROPERTY_ENCODER,
            "org.jclouds.imagestore.imagegenerator.IEncoder$DummyEncoder");
        properties4.setProperty(ImageStoreConstants.PROPERTY_IMAGEHOSTER,
            "org.jclouds.imagestore.imagehoster.facebook.ImageHostFacebook");

        BlobStoreContext context4 =
            ContextBuilder.newBuilder("imagestore").credentials(identity, credential).overrides(properties4)
                .buildView(BlobStoreContext.class);

        Object[][] returnVal = {
            {
                BlobStoreContext.class, new BlobStoreContext[] {
                    context4, context2, context3, context1
                }
            }
        };

        return returnVal;
    }

}
