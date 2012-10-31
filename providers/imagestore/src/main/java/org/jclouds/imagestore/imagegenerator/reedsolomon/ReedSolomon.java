/**
 * 
 */
package org.jclouds.imagestore.imagegenerator.reedsolomon;

import org.jclouds.imagestore.imagegenerator.IEncoder;
import org.jclouds.imagestore.imagegenerator.reedsolomon.GenericGF.GenericGFs;

/**
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public class ReedSolomon implements IEncoder {

    private final static GenericGF field = GenericGFs.AZTEC_DATA_12.mGf;
    private final float ratio = field.getPrimitive() / field.getSize();

    private final static ReedSolomonEncoder encoder = new ReedSolomonEncoder(field);
    private final static ReedSolomonDecoder decoder = new ReedSolomonDecoder(field);

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] encode(byte[] param) {
        if (param.length > field.getSize()) {
            throw new IllegalArgumentException("Max bytes permitted: " + field.getSize());
        }
        int entireSize = Math.round(param.length * ratio);
        int[] convertedInt = castToInt(param);
        int[] convertedWithECC = new int[entireSize];
        System.arraycopy(convertedInt, 0, convertedWithECC, 0, convertedInt.length);
        encoder.encode(convertedWithECC, entireSize - param.length);
        return castToByte(convertedWithECC);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] decode(byte[] param) {
        if (param.length > field.getPrimitive()) {
            throw new IllegalArgumentException("Max bytes permitted: " + field.getPrimitive());
        }
        int datasize = Math.round(param.length / ratio);
        int[] convertedInt = castToInt(param);
        try {
            decoder.decode(convertedInt, param.length - datasize);
        } catch (ReedSolomonException exc) {
            throw new RuntimeException(exc);
        }
        int[] data = new int[datasize];
        System.arraycopy(convertedInt, 0, data, 0, data.length);
        return castToByte(data);
    }

    public static int[] castToInt(final byte[] pInt) {
        int[] returnVal = new int[pInt.length];
        for (int i = 0; i < pInt.length; i++) {
            returnVal[i] = pInt[i] & 0xFF;
        }
        return returnVal;
    }

    public static byte[] castToByte(final int[] pInt) {
        byte[] returnVal = new byte[pInt.length];
        for (int i = 0; i < pInt.length; i++) {
            returnVal[i] = (byte)(pInt[i] & 0xFF);
        }
        return returnVal;
    }

}
