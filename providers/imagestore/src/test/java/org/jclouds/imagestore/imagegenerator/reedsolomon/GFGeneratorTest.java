/**
 * 
 */
package org.jclouds.imagestore.imagegenerator.reedsolomon;

import java.io.IOException;
import java.util.Random;

import org.jclouds.imagestore.imagegenerator.reedsolomon.GenericGF.GenericGFs;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * 
 * Generator for GaloisField as test.
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public final class GFGeneratorTest {

    @Test(dataProvider = "fieldGenerator")
    public void testGenerator(final Class<GenericGFs> clazz, final GenericGFs[] pHandlers)
        throws IOException, ReedSolomonException {
        Random ran = AbstractReedSolomonTestCase.getRandom();

        for (GenericGFs gf : pHandlers) {

            GenericGF field = gf.mGf;
            ReedSolomonEncoder encoder = new ReedSolomonEncoder(field);
            ReedSolomonDecoder decoder = new ReedSolomonDecoder(field);

            for (int i = 2; i <= field.getFieldSize(); i++) {
                int size = Math.max(i+1, ran.nextInt(1 << i));
                int ecBytes = Math.max(i, ran.nextInt(Math.max(1, Math.round(size / i))));
                int dataBytes = size - ecBytes;
                int[] input = dataSetup(size, dataBytes);
                int[] original = new int[dataBytes];
                int[] originalWithECC = new int[size];
                System.arraycopy(input, 0, original, 0, dataBytes);
                encoder.encode(input, ecBytes);
                System.arraycopy(input, 0, originalWithECC, 0, size);
                int toCorrupt = Math.max(1, ecBytes / 2);
                AbstractReedSolomonTestCase.corrupt(input, toCorrupt, ran);
                decoder.decode(input, ecBytes);
                AbstractReedSolomonTestCase.assertArraysEqual(original, 0, input, 0, dataBytes);
            }
        }

    }

    private int[] dataSetup(int arraySize, int dataSize) {
        byte[] original = new byte[arraySize];
        byte[] dataOnly = new byte[dataSize];
        AbstractReedSolomonTestCase.getRandom().nextBytes(dataOnly);
        System.arraycopy(dataOnly, 0, original, 0, dataSize);
        return ReedSolomonEncoder.castToInt(original);
    }

    @DataProvider(name = "fieldGenerator")
    public Object[][] fieldGenerator() {

        Object[][] returnVal = {
            {
                GenericGFs.class, new GenericGFs[] {
                    GenericGFs.AZTEC_DATA_12, GenericGFs.AZTEC_DATA_10, GenericGFs.QR_CODE_FIELD_256
                // GenericGFs.AZTEC_DATA_8
                }
            }
        };
        return returnVal;
    }

}
