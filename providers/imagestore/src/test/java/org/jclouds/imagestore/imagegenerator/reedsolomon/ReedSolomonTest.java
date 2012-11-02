/**
 * 
 */
package org.jclouds.imagestore.imagegenerator.reedsolomon;

import java.util.Random;

import org.jclouds.imagestore.imagegenerator.reedsolomon.GenericGF.GenericGFs;
import org.testng.annotations.Test;

/**
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public class ReedSolomonTest {
    final static Random ran = new Random(12l);
    final static int size = 128;
    final static int ecBytes = 32;
    final static int toCorrupt = 16;
    static byte[] data = new byte[size];
    static int[][] corruptedBytes = new int[2][toCorrupt];
    static {
        ran.nextBytes(data);
        for (int i = 0; i < toCorrupt; i++) {
            corruptedBytes[0][i] = ran.nextInt(data.length);
            corruptedBytes[1][i] = ran.nextInt(256);
        }
    }

    @Test
    public void testByte() {
        ReedSolomon tool = new ReedSolomon(ecBytes);
        byte[] encoded = tool.encode(data);
        byte[] corrupted = AbstractReedSolomonTestCase.corrupt(encoded, toCorrupt, ran);
        byte[] decoded = tool.decode(corrupted);
        AbstractReedSolomonTestCase.assertArraysEqual(data, 0, decoded, 0, data.length);
    }

    @Test
    public void testInt() throws ReedSolomonException {

        int[] bytes = ReedSolomon.castToInt(data);
        int[] encoded = new int[bytes.length + ecBytes];
        System.arraycopy(bytes, 0, encoded, 0, bytes.length);

        ReedSolomonEncoder tool = new ReedSolomonEncoder(GenericGFs.QR_CODE_FIELD_256.mGf);
        ReedSolomonDecoder tool2 = new ReedSolomonDecoder(GenericGFs.QR_CODE_FIELD_256.mGf);
        tool.encode(encoded, ecBytes);
        AbstractReedSolomonTestCase.corrupt(encoded, toCorrupt, ran);
        tool2.decode(encoded, ecBytes);
        AbstractReedSolomonTestCase.assertArraysEqual(bytes, 0, encoded, 0, bytes.length);
    }

}
