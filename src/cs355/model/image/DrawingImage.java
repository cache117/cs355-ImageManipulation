package cs355.model.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * Created by cstaheli on 3/30/2016.
 */
public class DrawingImage extends CS355Image
{
    @Override
    public BufferedImage getImage()
    {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = image.getRaster();
        for (int y = 0; y < getHeight(); y++)
        {
            for (int x = 0; x < getWidth(); x++)
            {
                int[] rgb = getPixel(x, y, new int[3]);
                raster.setPixel(x, y, rgb);
            }
        }
        return image;
    }

    @Override
    public void edgeDetection()
    {
        this.notifyObservers();
    }

    @Override
    public void sharpen()
    {
        this.notifyObservers();
    }

    @Override
    public void medianBlur()
    {
        for (int y = 0; y < getHeight(); y++)
        {
            for (int x = 0; x < getWidth(); x++)
            {
                ImageKernel imageKernel = getSurroundingPixels(x, y);
                int[] newPixels = imageKernel.medianBlur();
                setPixel(x, y, newPixels);
            }
        }
        this.notifyObservers();
    }

    @Override
    public void uniformBlur()
    {
        this.notifyObservers();
    }

    @Override
    public void grayscale()
    {
        this.notifyObservers();
    }

    @Override
    public void contrast(int amount)
    {
        this.notifyObservers();
    }

    @Override
    public void brightness(int amount)
    {
        float brightnessAmount = amount / 100f;
        int[] rgb = new int[3];
        float[] hsb = new float[3];
        for (int y = 0; y < getHeight(); y++)
        {
            for (int x = 0; x < getWidth(); x++)
            {
                getPixel(x, y, rgb);
                Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], hsb);

                float newBrightness = hsb[2] + brightnessAmount;
                hsb[2] = newBrightness;

                Color c = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
                rgb[0] = c.getRed();
                rgb[1] = c.getGreen();
                rgb[2] = c.getBlue();
                setPixel(x, y, rgb);
            }
        }
        this.notifyObservers();
    }

    @Override
    public void notifyObservers()
    {
        super.setChanged();
        super.notifyObservers();
    }

    private ImageKernel getSurroundingPixels(int x, int y)
    {
        int[] m00 = getUpperLeft(x, y);
        int[] m01 = getUpper(x, y);
        int[] m02 = getUpperRight(x, y);
        int[] m10 = getLeft(x, y);
        int[] m11 = getCenter(x, y);
        int[] m12 = getRight(x, y);
        int[] m20 = getBottomLeft(x, y);
        int[] m21 = getBottom(x, y);
        int[] m22 = getBottomRight(x, y);
        return new ImageKernel(m00, m01, m02, m10, m11, m12, m20, m21, m22);
    }

    private int[] getCenter(int x, int y)
    {
        return getPixel(x, y, new int[3]);
    }

    private int[] getTrueUpper(int x, int y)
    {
        return getPixel(x, y - 1, new int[3]);
    }

    private int[] getUpper(int x, int y)
    {
        if (isYAtTopEdge(y))
        {
            return getCenter(x, y);
        }
        else
        {
            return getTrueUpper(x, y);
        }
    }

    private int[] getTrueUpperRight(int x, int y)
    {
        return getPixel(x + 1, y - 1, new int[3]);
    }

    private int[] getUpperRight(int x, int y)
    {
        if (isXAtRightEdge(x))
        {
            if (isYAtTopEdge(y))
            {
                return getCenter(x, y);
            }
            else
            {
                return getTrueUpper(x, y);
            }
        }
        else
        {
            if (isYAtTopEdge(y))
            {
                return getTrueRight(x, y);
            }
            else
            {
                return getTrueUpperRight(x, y);
            }
        }
    }

    private int[] getTrueRight(int x, int y)
    {
        return getPixel(x + 1, y, new int[3]);
    }

    private int[] getRight(int x, int y)
    {
        if (isXAtRightEdge(x))
        {
            return getCenter(x, y);
        }
        else
        {
            return getTrueRight(x, y);
        }
    }

    private int[] getTrueBottomRight(int x, int y)
    {
        return getPixel(x + 1, y + 1, new int[3]);
    }

    private int[] getBottomRight(int x, int y)
    {
        if (isXAtRightEdge(x))
        {
            if (isYAtBottomEdge(y))
            {
                return getCenter(x, y);
            }
            else
            {
                return getTrueBottom(x, y);
            }
        }
        else
        {
            if (isYAtBottomEdge(y))
            {
                return getTrueRight(x, y);
            }
            else
            {
                return getTrueBottomRight(x, y);
            }
        }
    }

    private int[] getTrueBottom(int x, int y)
    {
        return getPixel(x, y + 1, new int[3]);
    }

    private int[] getBottom(int x, int y)
    {
        if (isYAtBottomEdge(y))
        {
            return getCenter(x, y);
        }
        else
        {
            return getTrueBottom(x, y);
        }
    }

    private int[] getTrueBottomLeft(int x, int y)
    {
        return getPixel(x - 1, y + 1, new int[3]);
    }

    private int[] getBottomLeft(int x, int y)
    {
        if (isXAtLeftEdge(x))
        {
            if (isYAtBottomEdge(y))
            {
                return getCenter(x, y);
            }
            else
            {
                return getTrueBottom(x, y);
            }
        }
        else
        {
            if (isYAtBottomEdge(y))
            {
                return getTrueLeft(x, y);
            }
            else
            {
                return getTrueBottomLeft(x, y);
            }
        }
    }

    private int[] getTrueLeft(int x, int y)
    {
        return getPixel(x - 1, y, new int[3]);
    }

    private int[] getLeft(int x, int y)
    {
        if (isXAtLeftEdge(x))
        {
            return getCenter(x, y);
        }
        else
        {
            return getTrueLeft(x, y);
        }
    }

    private int[] getTrueUpperLeft(int x, int y)
    {
        return getPixel(x - 1, y - 1, new int[3]);
    }

    private int[] getUpperLeft(int x, int y)
    {
        if (isXAtLeftEdge(x))
        {
            //upper-left corner, return center.
            if (isYAtTopEdge(y))
            {
                return getCenter(x, y);
            }
            //any other case, return above;
            else
            {
                return getTrueUpper(x, y);
            }
        }
        else
        {
            //top edge, return left
            if (isYAtTopEdge(y))
            {
                return getTrueLeft(x, y);
            }
            else
            {
                return getTrueUpperLeft(x, y);
            }
        }
    }

    private boolean isXAtLeftEdge(int x)
    {
        return x - 1 < 0;
    }

    private boolean isXAtRightEdge(int x)
    {
        return x + 1 > getWidth();
    }

    private boolean isYAtTopEdge(int y)
    {
        return y - 1 < 0;
    }

    private boolean isYAtBottomEdge(int y)
    {
        return y + 1 > getHeight();
    }

}
