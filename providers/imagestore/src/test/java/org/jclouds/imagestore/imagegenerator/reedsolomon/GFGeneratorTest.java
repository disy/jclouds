/**
 * 
 */
package org.jclouds.imagestore.imagegenerator.reedsolomon;

import java.util.Random;

import org.testng.annotations.Test;

/**
 * 
 * Generator for GaloisField as test.
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public final class GFGeneratorTest {

    @Test
    public void testFlexibleGenerator() {
        Random random = AbstractReedSolomonTestCase.getRandom();

        for (int i = 2; i < 32; i++) {
            // Setup
            byte[] original = new byte[i << 1];
            random.nextBytes(original);

            // Getting encoders with either minimal GaloisField or maximalGaloisFields
            GenericGF minField = GenericGF.GenericGFs.QR_CODE_FIELD_256.mGf;
            GenericGF maxField = GenericGF.GenericGFs.AZTEC_DATA_8.mGf;
            ReedSolomonEncoder minimalEncoder = new ReedSolomonEncoder(minField);
            ReedSolomonEncoder maximalEncoder = new ReedSolomonEncoder(maxField);

            byte[] minInput = new byte[minField.getPrimitive()];
            byte[] maxInput = new byte[maxField.getPrimitive()];

            System.arraycopy(original, 0, minInput, 0, original.length);
            System.arraycopy(original, 0, maxInput, 0, original.length);

            int[] minIntInput = ReedSolomonEncoder.castToInt(minInput);
            int[] maxIntInput = ReedSolomonEncoder.castToInt(maxInput);

            minimalEncoder.encode(minIntInput, minInput.length - original.length);
            maximalEncoder.encode(maxIntInput, maxInput.length - original.length);

            AbstractReedSolomonTestCase.corrupt(minIntInput, 1, random);
            AbstractReedSolomonTestCase.corrupt(maxIntInput, 1, random);

        }

    }
}
