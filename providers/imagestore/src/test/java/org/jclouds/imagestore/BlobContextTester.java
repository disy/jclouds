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

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.imagestore.imagegenerator.IEncoder;
import org.jclouds.imagestore.imagegenerator.bytepainter.BytesToImagePainter;
import org.jclouds.imagestore.imagehoster.file.ImageHostFile;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.io.ByteStreams;

public class BlobContextTester {

    private static byte[][] vals = new byte[5][];

    private static Random ran = new Random(12l);

    static {
        for (int i = 0; i < vals.length; i++) {
            // vals[i] = new byte[ran.nextInt(2000)];
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

                assertEquals(i, blobStore.countBlobs(containerName));

                // add blob
                BlobBuilder blobbuilder =
                    blobStore.blobBuilder(new StringBuilder("test").append(i).toString());
                Blob blob = blobbuilder.build();
                blob.setPayload(vals[i]);
                blobStore.putBlob(containerName, blob);

                assertEquals(i + 1, blobStore.countBlobs(containerName));

            }

            for (int i = 0; i < vals.length; i++) {
                Blob blobRetrieved =
                    blobStore.getBlob(containerName, new StringBuilder("test").append(i).toString());
                InputStream in = blobRetrieved.getPayload().getInput();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ByteStreams.copy(in, out);
                if (!Arrays.equals(out.toByteArray(), vals[i])) {
                    // byte[] stored = vals[i];
                    // byte[] received = out.toByteArray();
                    fail(new StringBuilder("Failed: ImageHost: ").append(
                        ((SyncImageBlobStore)blobStore).getImageHost().toString()).append(" and Painter: ")
                        .append(((SyncImageBlobStore)blobStore).getImageGenerator().getPainter().toString())
                        .toString());
                }
            }

            for (int i = 0; i < vals.length; i++) {
                assertEquals(vals.length - i, blobStore.countBlobs(containerName));
                blobStore.removeBlob(containerName, new StringBuilder("test").append(i).toString());
                assertEquals(vals.length - i - 1, blobStore.countBlobs(containerName));
            }
        }
    }

    @DataProvider(name = "blobContextProvider")
    public Object[][] blobContextProvider() {

        Object[][] returnVal =
            {
                {
                    BlobStoreContext.class,
                    new BlobStoreContext[] {
//                        TestAndBenchmarkHelper.createContext(ImageHostFacebook.class,
//                            BytesToImagePainter.PainterType.QUARTERNARY.getPainter().getClass(),
//                            IEncoder.DummyEncoder.class),
                        TestAndBenchmarkHelper.createContext(ImageHostFile.class,
                            BytesToImagePainter.PainterType.HEXADECIMAL.getPainter().getClass(), IEncoder.DummyEncoder.class),
//                        TestAndBenchmarkHelper.createContext(ImageHostGoogleDataApiPicasa.class,
//                            BytesToImagePainter.PainterType.HEXADECIMAL.getPainter().getClass(), IEncoder.DummyEncoder.class),
//                        TestAndBenchmarkHelper.createContext(ImageHostFlickr.class,
//                            BytesToImagePainter.PainterType.HEXADECIMAL.getPainter().getClass(),
//                            IEncoder.DummyEncoder.class),
                    }
                }
            };

        return returnVal;
    }
}
