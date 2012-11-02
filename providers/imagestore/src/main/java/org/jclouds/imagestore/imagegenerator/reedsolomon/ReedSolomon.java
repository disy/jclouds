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

    private final int mEcSize;

    public ReedSolomon() {
        this(0);
    }

    public ReedSolomon(int pEcSize) {
        mEcSize = pEcSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] encode(byte[] param) {
        int[] decoded;
        if (mEcSize == 0) {
            decoded = new int[(Math.min(256 + param.length, Math.round(param.length * ratio.floatValue())))];
        } else {
            decoded = new int[param.length + mEcSize];
        }
        int[] convertedInt = castToInt(param);
        System.arraycopy(convertedInt, 0, decoded, 0, param.length);
        encoder.encode(decoded, decoded.length - param.length);
        return castToByte(decoded);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] decode(byte[] param) {
        int datasize;
        if (mEcSize == 0) {
            datasize =
                (param.length - (param.length / ratio.floatValue())) > 256 ? param.length - 256 : Math
                    .round(param.length / ratio.floatValue());
        } else {
            datasize = param.length - mEcSize;
        }
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
