/**
 * 
 */
package org.jclouds.imagestore.imagegenerator.reedsolomon;

import java.io.IOException;
import java.util.Random;

import org.jclouds.imagestore.imagegenerator.reedsolomon.GenericGF.GenericGFs;
import org.jclouds.imagestore.imagehoster.IImageHost;
import org.jclouds.imagestore.imagehoster.file.ImageHostFile;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.io.Files;

/**
 * 
 * Generator for GaloisField as test.
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public final class GFGeneratorTest {

    @Test(dataProvider = "fieldGenerator")
    public void testGenerator(final Class<GenericGFs> clazz, final GenericGFs[] pHandlers) throws IOException {

        Random random = AbstractReedSolomonTestCase.getRandom();
        for (GenericGFs gf : pHandlers) {
            for (int i = 2; i < 32; i++) {
                // Setup
                byte[] original = new byte[i << 1];
                random.nextBytes(original);
                byte[] minInput = new byte[gf.mGf.getPrimitive()];
                System.arraycopy(original, 0, minInput, 0, original.length);

                ReedSolomonEncoder minimalEncoder = new ReedSolomonEncoder(gf.mGf);
                int[] minIntInput = ReedSolomonEncoder.castToInt(minInput);
                minimalEncoder.encode(minIntInput, minInput.length - original.length);
                AbstractReedSolomonTestCase.corrupt(minIntInput, 1, random);

            }
        }

    }

    @DataProvider(name = "fieldGenerator")
    public Object[][] fieldGenerator() {

        Object[][] returnVal = {
            {
                GenericGFs.class, GenericGFs.values()
            }
        };
        return returnVal;
    }

}
