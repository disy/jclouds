/**
 * 
 */
package org.jclouds.imagestore.imagegenerator.reedsolomon;

import java.util.Random;

import org.testng.annotations.Test;

/**
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public class ReedSolomonTest {

    @Test
    public void test() {
        byte[] bytes = new byte[669];
        new Random().nextBytes(bytes);
        ReedSolomon tool = new ReedSolomon();
        byte[] encoded = tool.encode(bytes);
        byte[] corrupted = AbstractReedSolomonTestCase.corrupt(encoded, 2, new Random());
        byte[] decoded = tool.decode(corrupted);
        AbstractReedSolomonTestCase.assertArraysEqual(bytes, 0, decoded, 0, bytes.length);

    }

}
