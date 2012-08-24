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
package org.jclouds.imagestore.blobstore;

import static org.testng.AssertJUnit.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.domain.internal.BlobBuilderImpl;
import org.jclouds.encryption.internal.JCECrypto;
import org.jclouds.imagestore.blobstore.imagegenerator.BytesToImagePainter;
import org.jclouds.imagestore.blobstore.imagegenerator.ImageGenerator;
import org.jclouds.imagestore.blobstore.imagegenerator.bytepainter.SeptenaryLayeredBytesToImagePainter;
import org.jclouds.imagestore.blobstore.imagehoster.flickr.ImageHostFlickr;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ImageHostBlobStoreTest {

    /** The path to the test input file. */
    private final String testFileURI = "src/test/resources/Linie9C.pdf";
    /** The test blob. */
    private final byte [] bs;

    /**
     * 
     * 
     * @throws IOException
     */
    public ImageHostBlobStoreTest() throws IOException {
        bs = loadBytesFromFile(new File(testFileURI));
      
    }

    /**
     * 
     * @param clazz
     * @param pHandlers
     * @throws CertificateException 
     * @throws NoSuchAlgorithmException 
     * @throws IOException 
     */
    @Test(dataProvider = "instantiateBytePainters")
    public void testByteRepresentation(final Class<BytesToImagePainter> clazz, BytesToImagePainter[] painters) throws NoSuchAlgorithmException, CertificateException, IOException {
        
        for(BytesToImagePainter pa : painters) {
            final ImageGenerator ig = new ImageGenerator(pa);
            final ImageBlobStore ib = new ImageBlobStore(new ImageHostFlickr(), ig);
            final BlobBuilder bb = new BlobBuilderImpl(new JCECrypto());
            final String blobName = ig.getClass().getName() + System.currentTimeMillis();
            bb.payload(bs);
            bb.name(blobName);
            final Blob testBlob = bb.build();
            final String containerName = "TestContainer";
            ib.putBlob(containerName, testBlob);
            final Blob reTestBlob = ib.getBlob(containerName, blobName);
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            reTestBlob.getPayload().writeTo(bos);
            byte [] bss = bos.toByteArray();
            
            assertTrue(new StringBuilder("Check for ").append(pa.getClass().getName()).append(" failed.").toString(), Arrays.equals(bs, bss));            
        }
    }

    /**
     * 
     * @return
     */
    @DataProvider(name = "instantiateBytePainters")
    public Object[][] instantiateBytePainters() {

        Object[][] returnVal = {
            {
                BytesToImagePainter.class, new BytesToImagePainter[] {
                    new SeptenaryLayeredBytesToImagePainter()
                }
            }
        };
        return returnVal;
    }

    /**
     * Returns content of a file as byte array.
     * 
     * @param f
     *            The file to read from.
     * @return The file's content as byte array.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    byte[] loadBytesFromFile(final File f) throws IOException {
        if (!f.isFile())
            return new byte[0];

        long len = f.length();

        if (len > Integer.MAX_VALUE) {
            System.out.println("File too large");
            return new byte[0];
        }

        byte[] bs = new byte[(int)len];
        FileInputStream is = new FileInputStream(f);

        is.read(bs);
        is.close();
        return bs;
    }
}
