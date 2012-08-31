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

import static org.testng.AssertJUnit.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.Apis;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.filesystem.reference.FilesystemConstants;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.Providers;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

public class BlobContextTester {

    private static byte[][] vals = new byte[1024][1024];

    static {
        Random ran = new Random();
        for (int i = 0; i < vals.length; i++) {
            ran.nextBytes(vals[i]);
        }
    }

    public static final Map<String, ApiMetadata> allApis = Maps.uniqueIndex(Apis
        .viewableAs(BlobStoreContext.class), Apis.idFunction());

    public static final Map<String, ProviderMetadata> appProviders = Maps.uniqueIndex(Providers
        .viewableAs(BlobStoreContext.class), Providers.idFunction());

    public static final Set<String> allKeys = ImmutableSet.copyOf(Iterables.concat(appProviders.keySet(),
        allApis.keySet()));

    // Test all registered providers (over dependency) with the given imagegenerator
    @Test
    public void test() throws IOException {

        for (String provider : allKeys) {
            String identity = "user";
            String credential = "pass";
            String containerName = "testcontainer";

            Properties mProperties = new Properties();
            mProperties.setProperty(FilesystemConstants.PROPERTY_BASEDIR, Files.createTempDir()
                .getAbsolutePath());
            mProperties.setProperty(ImageStoreConstants.PROPERTY_BYTEPAINTER,
                "org.jclouds.imagestore.imagegenerator.bytepainter.HexadecimalBytesToImagePainter");
            mProperties.setProperty(ImageStoreConstants.PROPERTY_IMAGEHOSTER,
                "org.jclouds.imagestore.imagehoster.file.ImageHostFile");
            mProperties.setProperty(ImageStoreConstants.PROPERTY_STORAGEPARAMETER, mProperties
                .getProperty(FilesystemConstants.PROPERTY_BASEDIR));

            // Init
            BlobStoreContext context =
                ContextBuilder.newBuilder(provider).credentials(identity, credential).overrides(mProperties)
                    .buildView(BlobStoreContext.class);

            // Create Container
            BlobStore blobStore = context.getBlobStore();
            blobStore.createContainerInLocation(null, containerName);

            for (int i = 0; i < vals.length; i++) {

                // add blob
                BlobBuilder blobbuilder =
                    blobStore.blobBuilder(new StringBuilder("test").append(i).toString());
                Blob blob = blobbuilder.build();
                blob.setPayload(vals[i]);
                blobStore.putBlob(containerName, blob);
            }
            context.close();

            // BlobStoreContext context2 =
            // ContextBuilder.newBuilder(provider).credentials(identity, credential).overrides(mProperties)
            // .buildView(BlobStoreContext.class);
            // blobStore = context2.getBlobStore();

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
            blobStore.deleteContainer(containerName);

            // close context
            // context2.close();
        }

    }
}
