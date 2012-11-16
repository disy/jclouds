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
import static org.testng.AssertJUnit.fail;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.jclouds.imagestore.benchmarks.TestAndBenchmarkHelper;
import org.jclouds.imagestore.imagegenerator.reedsolomon.ReedSolomon;
import org.jclouds.imagestore.imagehoster.IImageHost;
import org.jclouds.imagestore.imagehoster.facebook.ImageHostFacebook;
import org.jclouds.imagestore.imagehoster.file.ImageHostFile;
import org.jclouds.imagestore.imagehoster.flickr.ImageHostFlickr;
import org.jclouds.imagestore.imagehoster.picasa.ImageHostGoogleDataApiPicasa;
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

    // /** The path to the test input file. */
    // private static final String RAWFILEURI = "src" + File.separator + "test" + File.separator + "resources"
    // + File.separator + "Linie9C.pdf";
    /** The test blob. */
    private static final byte[] RAWFILEBYTES;

    // static {
    // try {
    // RAWFILEBYTES = loadBytesFromFile(new File(RAWFILEURI));
    // } catch (IOException e) {
    // throw new RuntimeException(e);
    // }
    // }

    static {
        RAWFILEBYTES = new byte[4288];
        // RAWFILEBYTES = new byte[200];
        new Random(12l).nextBytes(RAWFILEBYTES);
    }

    /**
     * Invokes tests for all byte painters local.
     * 
     * @param painters
     *            The different byte painter instances.
     * @param hosts
     *            The local host instances.
     * @throws NoSuchAlgorithmException
     *             Signals that a NoSuchAlgorithm exception has occurred.
     * @throws CertificateException
     *             Signals that a certificate exception has occurred.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Test(dataProvider = "allPainters", groups = "localTests")
    public void testOnFile(final Class<IBytesToImagePainter> painterClazz,
        final IBytesToImagePainter[] painters, final Class<IEncoder> encoderClazz, final IEncoder[] encoders)
        throws NoSuchAlgorithmException, CertificateException, IOException, InstantiationException,
        IllegalAccessException, ClassNotFoundException {
        check(painters, encoders, new ImageHostFile(Files.createTempDir().getAbsolutePath()));
    }

    /**
     * Invokes tests for all byte painters on Picasa.
     * 
     * @param painters
     *            The different byte painter instances.
     * @param hosts
     *            The local host instances.
     * @throws NoSuchAlgorithmException
     *             Signals that a NoSuchAlgorithm exception has occurred.
     * @throws CertificateException
     *             Signals that a certificate exception has occurred.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Test(dataProvider = "allPainters", groups = "remoteTests")
    public void testOnPicasa(final Class<IBytesToImagePainter> painterClazz,
        final IBytesToImagePainter[] painters, final Class<IEncoder> encoderClazz, final IEncoder[] encoders)
        throws NoSuchAlgorithmException, CertificateException, IOException, InstantiationException,
        IllegalAccessException, ClassNotFoundException {
        check(painters, encoders, new ImageHostGoogleDataApiPicasa());
    }

    /**
     * Returns an Object with byte painters.
     * 
     * @return Object withall byte painters.
     */
    @DataProvider(name = "allPainters")
    public Object[][] allPainters() {
        List<IBytesToImagePainter> painters = TestAndBenchmarkHelper.getAllPainters();

        Object[][] returnVal =
            {
                {
                    IBytesToImagePainter.class, painters.toArray(new IBytesToImagePainter[painters.size()]),
                    IEncoder.class, new IEncoder[] {
                        new IEncoder.DummyEncoder(), new ReedSolomon()

                    }
                }
            };
        return returnVal;
    }

    /**
     * Invokes tests for all byte painters on Picasa.
     * 
     * @param painters
     *            The different byte painter instances.
     * @param hosts
     *            The local host instances.
     * @throws NoSuchAlgorithmException
     *             Signals that a NoSuchAlgorithm exception has occurred.
     * @throws CertificateException
     *             Signals that a certificate exception has occurred.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Test(dataProvider = "flickrPainters", groups = "remoteTests")
    public void testOnFlickr(final Class<IBytesToImagePainter> painterClazz,
        final IBytesToImagePainter[] painters, final Class<IEncoder> encoderClazz, final IEncoder[] encoders)
        throws NoSuchAlgorithmException, CertificateException, IOException, InstantiationException,
        IllegalAccessException, ClassNotFoundException {
        check(painters, encoders, new ImageHostFlickr());
    }

    /**
     * Returns an Object with all remote hosts and all byte painters.
     * 
     * @return Object with all remote hosts and all byte painters.
     */
    @DataProvider(name = "flickrPainters")
    public Object[][] flickrPainters() {

        List<IBytesToImagePainter> painters = TestAndBenchmarkHelper.getPaintersForFlickr();

        Object[][] returnVal =
            {
                {
                    IBytesToImagePainter.class, painters.toArray(new IBytesToImagePainter[painters.size()]),
                    IEncoder.class, new IEncoder[] {
                        new IEncoder.DummyEncoder(), new ReedSolomon()
                    }
                }
            };
        return returnVal;
    }

    /**
     * Invokes tests for all byte painters on Picasa.
     * 
     * @param painters
     *            The different byte painter instances.
     * @param hosts
     *            The local host instances.
     * @throws NoSuchAlgorithmException
     *             Signals that a NoSuchAlgorithm exception has occurred.
     * @throws CertificateException
     *             Signals that a certificate exception has occurred.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Test(dataProvider = "facebookPainters", groups = "remoteTests")
    public void testOnFacebook(final Class<IBytesToImagePainter> painterClazz,
        final IBytesToImagePainter[] painters, final Class<IEncoder> encoderClazz, final IEncoder[] encoders)
        throws NoSuchAlgorithmException, CertificateException, IOException, InstantiationException,
        IllegalAccessException, ClassNotFoundException {
        check(painters, encoders, new ImageHostFacebook());
    }

    /**
     * Returns an Object with all remote hosts and all byte painters.
     * 
     * @return Object with all remote hosts and all byte painters.
     */
    @DataProvider(name = "facebookPainters")
    public Object[][] facebookPainters() {

        List<IBytesToImagePainter> painters = TestAndBenchmarkHelper.getPaintersForFacebook();

        Object[][] returnVal =
            {
                {
                    IBytesToImagePainter.class, painters.toArray(new IBytesToImagePainter[painters.size()]),
                    IEncoder.class, new IEncoder[] {
                        new IEncoder.DummyEncoder(), new ReedSolomon()
                    }
                }
            };
        return returnVal;
    }

    /**
     * The Tests.
     * 
     * @param painters
     *            The different byte painter instances.
     * @param hosts
     *            The different host instances.
     * @throws NoSuchAlgorithmException
     *             Signals that a NoSuchAlgorithm exception has occurred.
     * @throws CertificateException
     *             Signals that a certificate exception has occurred.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private void check(final IBytesToImagePainter[] painters, final IEncoder[] encoders,
        final IImageHost... hosts) throws NoSuchAlgorithmException, CertificateException, IOException,
        InstantiationException, IllegalAccessException, ClassNotFoundException {

        for (IImageHost host : hosts) {
            host.deleteImageSet(CONTAINER);
            for (IEncoder enc : encoders) {
                for (IBytesToImagePainter pa : painters) {
                    ImageGenerator generator =
                        new ImageGenerator(pa, enc, host.getMaxImageWidth(), host.getMaxImageHeight());
                    final BufferedImage imageToSend = generator.createImageFromBytes(RAWFILEBYTES);
                    final String blobName = "blob_" + System.currentTimeMillis();
                    assertTrue(host.uploadImage(CONTAINER, blobName, imageToSend));

                    byte[] toCheck = generator.getBytesFromImage(imageToSend);

                    if (!Arrays.equals(RAWFILEBYTES, toCheck)) {
                        fail(new StringBuilder("Failed: ImageHost: ").append(host.toString()).append(
                            " and Painter: ").append(pa.toString()).append(" and Encoder: ").append(
                            enc.toString()).toString());
                    }

                    final BufferedImage imageReceived = host.downloadImage(CONTAINER, blobName);
                    byte[] receivedBytes = generator.getBytesFromImage(imageReceived);

                    if (!Arrays.equals(RAWFILEBYTES, receivedBytes)) {
                        fail(new StringBuilder("Failed: ImageHost: ").append(host.toString()).append(
                            " and Painter: ").append(pa.toString()).append(" and Encoder: ").append(
                            enc.toString()).toString());
                    }

                }
            }
        }
    }
    // /**
    // * Returns content of a file as byte array.
    // *
    // * @param f
    // * The file to read from.
    // * @return The file's content as byte array.
    // * @throws IOException
    // * Signals that an I/O exception has occurred.
    // */
    // private static byte[] loadBytesFromFile(final File f) throws IOException {
    // if (!f.isFile())
    // return new byte[0];
    //
    // long len = f.length();
    //
    // if (len > Integer.MAX_VALUE) {
    // try {
    // throw new IllegalArgumentException("File too large!");
    // } catch (IllegalArgumentException e) {
    // new RuntimeException(e);
    // }
    // }
    //
    // byte[] bs = new byte[(int)len];
    // FileInputStream is = new FileInputStream(f);
    //
    // is.read(bs);
    // is.close();
    // return bs;
    // }
}
