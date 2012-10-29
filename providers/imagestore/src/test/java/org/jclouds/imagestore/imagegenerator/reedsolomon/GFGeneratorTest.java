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

    @Test(enabled = false)
    public void testFlexibleGenerator() {
        Random random = AbstractReedSolomonTestCase.getRandom();

        for (int i = 2; i < 32; i++) {
            // Setup
            byte[] original = new byte[i << 1];
            random.nextBytes(original);

            // Getting encoders with either minimal GaloisField or maximalGaloisFields
            GenericGF minField = GenericGF.generateMinimal(i);
            GenericGF maxField = GenericGF.generateMaximal(i);
            ReedSolomonEncoder minimalEncoder = new ReedSolomonEncoder(minField);
            ReedSolomonEncoder maximalEncoder = new ReedSolomonEncoder(maxField);

            byte[] minInput = new byte[minField.getPrimitive()];
            byte[] maxInput = new byte[maxField.getPrimitive()];

            System.arraycopy(original, 0, minInput, 0, original.length);
            System.arraycopy(original, 0, maxInput, 0, original.length);

            minimalEncoder.encode(ReedSolomonEncoder.castToInt(minInput), minInput.length - original.length);
            maximalEncoder.encode(ReedSolomonEncoder.castToInt(maxInput), maxInput.length - original.length);

        }

    }
}
