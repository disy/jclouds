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
package org.jclouds.imagestore.imagegenerator;

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
import org.jclouds.imagestore.SyncImageBlobStore;
import org.jclouds.imagestore.imagegenerator.bytepainter.BinaryBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.bytepainter.HexadecimalBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.bytepainter.QuaternaryBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.bytepainter.QuaternaryLayeredBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.bytepainter.SeptenaryLayeredBytesToImagePainter;
import org.jclouds.imagestore.imagehoster.IImageHost;
import org.jclouds.imagestore.imagehoster.file.ImageHostFile;
import org.jclouds.imagestore.imagehoster.flickr.ImageHostFlickr;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.io.Files;

/**
 * This class provides test cases for the image generators.
 * 
 * @author Wolfgang Miller, Sebastian Graf, University of Konstanz.
 * 
 */
public class ImageGeneratorTest {

    /** The test container's name. */
    private static final String CONTAINER = "TestContainer";

    /** The path to the test input file. */
    private static final String RAWFILEURI = "src" + File.separator + "test" + File.separator + "resources"
        + File.separator + "Linie9C.pdf";
    /** The test blob. */
    private static final byte[] RAWFILEBYTES;

    static {
        try {
            RAWFILEBYTES = loadBytesFromFile(new File(RAWFILEURI));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Invokes tests for all byte painters on all remote hosts.
     * 
     * @param painterClazz
     *            The byte painters interface.
     * @param painters
     *            The different byte painter instances.
     * @param hostClazz
     *            The host interface.
     * @param hosts
     *            The different host instances.
     * @throws NoSuchAlgorithmException
     *             Signals that a NoSuchAlgorithm exception has occurred.
     * @throws CertificateException
     *             Signals that a certificate exception has occurred.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test(dataProvider = "remoteHostsAllPainters", groups = "remoteTests")
    public void testByteRepresentationRemoteHosts(final Class<IBytesToImagePainter> painterClazz,
        final IBytesToImagePainter[] painters, final Class<IImageHost> hostClazz, final IImageHost[] hosts)
        throws NoSuchAlgorithmException, CertificateException, IOException {
        check(painterClazz, painters, hostClazz, hosts);
    }

    /**
     * Invokes tests for all byte painters local.
     * 
     * @param painterClazz
     *            The byte painters interface.
     * @param painters
     *            The different byte painter instances.
     * @param hostClazz
     *            The host interface.
     * @param hosts
     *            The local host instances.
     * @throws NoSuchAlgorithmException
     *             Signals that a NoSuchAlgorithm exception has occurred.
     * @throws CertificateException
     *             Signals that a certificate exception has occurred.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test(dataProvider = "fileHostAllPainters", groups = "localTests")
    public void testByteRepresentationOnFileHost(final Class<IBytesToImagePainter> painterClazz,
        final IBytesToImagePainter[] painters, final Class<IImageHost> hostClazz, final IImageHost[] hosts)
        throws NoSuchAlgorithmException, CertificateException, IOException {
        check(painterClazz, painters, hostClazz, hosts);
    }

    /**
     * Deletes all created images and sets.
     * 
     * @param host
     *            The image host.
     */
    public void clean(final IImageHost host) {
        host.clearImageSet(CONTAINER);
        host.deleteImageSet(CONTAINER);
    }

    /**
     * Returns an Object with file host and all byte painters.
     * 
     * @return Object with file host and all byte painters.
     */
    @DataProvider(name = "fileHostAllPainters")
    public Object[][] fileHostAllPainters() {

        Object[][] returnVal =
            {
                {
                    IBytesToImagePainter.class,
                    new IBytesToImagePainter[] {
                        new BinaryBytesToImagePainter(), new HexadecimalBytesToImagePainter(),
                        new SeptenaryLayeredBytesToImagePainter(), new QuaternaryBytesToImagePainter(),
                        new QuaternaryLayeredBytesToImagePainter()
                    }, IImageHost.class, new IImageHost[] {
                        new ImageHostFile(Files.createTempDir())
                    }
                }
            };
        return returnVal;
    }

    /**
     * Returns an Object with all remote hosts and all byte painters.
     * 
     * @return Object with all remote hosts and all byte painters.
     */
    @DataProvider(name = "remoteHostsAllPainters")
    public Object[][] remoteHostsAllPainters() {

        Object[][] returnVal =
            {
                {
                    IBytesToImagePainter.class,
                    new IBytesToImagePainter[] {
                        new BinaryBytesToImagePainter(), new HexadecimalBytesToImagePainter(),
                        new SeptenaryLayeredBytesToImagePainter(), new QuaternaryBytesToImagePainter(),
                        new QuaternaryLayeredBytesToImagePainter()
                    }, IImageHost.class, new IImageHost[] {
                        new ImageHostFlickr()
                    }
                }
            };
        return returnVal;
    }

    /**
     * The Tests.
     * 
     * @param painterClazz
     *            The byte painters interface.
     * @param painters
     *            The different byte painter instances.
     * @param hostClazz
     *            The host interface.
     * @param hosts
     *            The different host instances.
     * @throws NoSuchAlgorithmException
     *             Signals that a NoSuchAlgorithm exception has occurred.
     * @throws CertificateException
     *             Signals that a certificate exception has occurred.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void check(final Class<IBytesToImagePainter> painterClazz, final IBytesToImagePainter[] painters,
        final Class<IImageHost> hostClazz, final IImageHost[] hosts) throws NoSuchAlgorithmException,
        CertificateException, IOException {

        for (IImageHost host : hosts) {
            clean(host);
            for (IBytesToImagePainter pa : painters) {
                final ImageGenerator ig = new ImageGenerator(pa);
                final SyncImageBlobStore ib = new SyncImageBlobStore(host, ig);
                final String blobName = "blob_" + System.currentTimeMillis();
                final BlobBuilder bb = ib.blobBuilder(blobName);
                bb.payload(RAWFILEBYTES);
                bb.name(blobName);
                final Blob testBlob = bb.build();

                ib.putBlob(CONTAINER, testBlob);
                final Blob reTestBlob = ib.getBlob(CONTAINER, blobName);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                reTestBlob.getPayload().writeTo(bos);
                byte[] bss = bos.toByteArray();
                bos.close();

                assertTrue(new StringBuilder("Check for ").append(pa.getClass().getName()).append(" failed.")
                    .toString(), Arrays.equals(RAWFILEBYTES, bss));
            }
            clean(host);
        }
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
    private static byte[] loadBytesFromFile(final File f) throws IOException {
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
