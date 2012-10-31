/**
 * 
 */
package org.jclouds.imagestore.imagegenerator.reedsolomon;

import java.math.BigDecimal;

import org.jclouds.imagestore.imagegenerator.IEncoder;
import org.jclouds.imagestore.imagegenerator.reedsolomon.GenericGF.GenericGFs;

/**
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public class ReedSolomon implements IEncoder {

    private final static GenericGF field = GenericGFs.QR_CODE_FIELD_256.mGf;

    private final static ReedSolomonEncoder encoder = new ReedSolomonEncoder(field);
    private final static ReedSolomonDecoder decoder = new ReedSolomonDecoder(field);
    static final BigDecimal ratio;
    static {
        ratio = new BigDecimal(field.getPrimitive()).divide(new BigDecimal(field.getSize()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] encode(byte[] param) {
        int entireSize = Math.min(256 + param.length, Math.round(param.length * ratio.floatValue()));
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
        int datasize =
            (param.length - (param.length / ratio.floatValue())) > 256 ? param.length - 256 : Math
                .round(param.length / ratio.floatValue());
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
