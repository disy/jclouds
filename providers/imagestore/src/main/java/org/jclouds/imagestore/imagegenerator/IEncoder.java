/**
 * 
 */
package org.jclouds.imagestore.imagegenerator;

/**
 * Interface for hanging multiple encoders in the byte-stream before and after serialization e.g. Reed
 * Solomon.
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public interface IEncoder {

    byte[] encode(byte[] param);

    byte[] decode(byte[] param);

    int getNumbersOfBytesWasted(int numberOfEntireBytes);

    String toString();
    
    boolean isDummy();

    static class DummyEncoder implements IEncoder {
        /**
         * {@inheritDoc}
         */
        @Override
        public byte[] encode(byte[] param) {
            return param;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public byte[] decode(byte[] param) {
            return param;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getNumbersOfBytesWasted(int numberOfEntireBytes) {
            return 0;
        }

        public String toString() {
            return "DummyEncoder";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isDummy() {
            return true;
        }

    }

}
