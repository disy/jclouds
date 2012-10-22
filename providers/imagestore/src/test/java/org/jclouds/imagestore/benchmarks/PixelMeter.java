package org.jclouds.imagestore.benchmarks;

import java.awt.image.BufferedImage;

import org.perfidix.meter.AbstractMeter;

public class PixelMeter extends AbstractMeter {

    private BufferedImage mImage;

    public PixelMeter(BufferedImage pImage) {
        mImage = pImage;
    }

    @Override
    public double getValue() {
        int width = mImage.getWidth();
        int height = mImage.getHeight();

        return width * height;
    }

    @Override
    public String getUnit() {
        return "px";
    }

    @Override
    public String getUnitDescription() {
        return "Number of Pixels";
    }

    @Override
    public String getName() {
        return "Pixels";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mImage == null) ? 0 : mImage.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PixelMeter other = (PixelMeter)obj;
        if (mImage == null) {
            if (other.mImage != null)
                return false;
        } else if (!mImage.equals(other.mImage))
            return false;
        return true;
    }

}
