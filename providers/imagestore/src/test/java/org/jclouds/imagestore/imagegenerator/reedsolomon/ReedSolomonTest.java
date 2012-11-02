/**
 * 
 */
package org.jclouds.imagestore.imagegenerator.reedsolomon;

import java.util.Arrays;
import java.util.Random;

import org.jclouds.imagestore.imagegenerator.reedsolomon.GenericGF.GenericGFs;
import org.testng.annotations.Test;

/**
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public class ReedSolomonTest {
    final static Random ran = new Random(12l);
    final static int size = 256;
    final static int ecBytes = 32;
    final static int toCorrupt = ecBytes / 2;
    static byte[] data = new byte[size];
    static {
        ran.nextBytes(data);
    }

    int[][] corruptedBytes = {
        {
            31, 193, 257, 18, 64, 24, 133, 24, 168, 36, 270, 9, 50, 228, 130, 52
        }, {
            126, 226, 213, 46, 250, 127, 122, 159, 53, 96, 39, 212, 29, 213, 87, 68
        }
    };

    @Test
    public void testByte() {
        ReedSolomon tool = new ReedSolomon(ecBytes);
        byte[] encoded = tool.encode(data);
        byte[] corrupted = AbstractReedSolomonTestCase.corrupt(encoded, corruptedBytes);
        byte[] decoded = tool.decode(corrupted);
        AbstractReedSolomonTestCase.assertArraysEqual(data, 0, decoded, 0, data.length);
    }

    @Test
    public void testInt() throws ReedSolomonException {

        int[] bytes = ReedSolomon.castToInt(data);
        int[] bytes2 = new int[size - ecBytes];
        System.arraycopy(bytes, 0, bytes2, 0, bytes2.length);

        ReedSolomonEncoder tool = new ReedSolomonEncoder(GenericGFs.QR_CODE_FIELD_256.mGf);
        ReedSolomonDecoder tool2 = new ReedSolomonDecoder(GenericGFs.QR_CODE_FIELD_256.mGf);
        tool.encode(bytes, ecBytes);
        int[] corrupted = AbstractReedSolomonTestCase.corrupt(bytes, corruptedBytes);
        tool2.decode(corrupted, ecBytes);
        AbstractReedSolomonTestCase.assertArraysEqual(bytes, 0, corrupted, 0, corrupted.length);
    }
    
 
}
