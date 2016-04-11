package cs355.model.image;

import cs355.view.DrawingViewer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Observer;

/**
 * A DrawingImage that handles all of the basic operations that can take place on an image.
 */
public class DrawingImage extends CS355Image
{
    private static final int HSB_HUE = 0, HSB_SATURATION = 1, HSB_BRIGHTNESS = 2;
    private static final int RGB_RED = 0, RGB_GREEN = 1, RGB_BLUE = 2;
    private boolean drawImage;

    public DrawingImage()
    {
        super();
        this.drawImage = true;
    }

    @Override
    public BufferedImage getImage()
    {
        if (isImageDrawable())
        {
            BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            WritableRaster raster = image.getRaster();
            int[] rgb = new int[3];
            for (int y = 0; y < getHeight(); ++y)
            {
                for (int x = 0; x < getWidth(); ++x)
                {
                    getPixel(x, y, rgb);
                    raster.setPixel(x, y, rgb);
                }
            }
            return image;
        } else
            return null;
    }

    private boolean isImageDrawable()
    {
        return drawImage && getWidth() > 0 && getHeight() > 0;
    }

    @Override
    public void edgeDetection()
    {
        if (isImageDrawable())
        {
            for (int y = 0; y < getHeight(); ++y)
            {
                for (int x = 0; x < getWidth(); ++x)
                {
                    if (isPixelOnEdge(x, y))
                        setPixel(x, y, new int[]{0, 0, 0});
                    else
                    {
                        ImageKernel imageKernel = getSurroundingPixels(x, y);
                        int[] newPixels = imageKernel.edgeDetection();
                        setPixel(x, y, newPixels);
                    }
                }
            }
            this.notifyObservers();
        }
    }

    @Override
    public void sharpen()
    {
        if (isImageDrawable())
        {
            this.notifyObservers();
        }
    }

    @Override
    public void medianBlur()
    {
        if (isImageDrawable())
        {
            for (int y = 0; y < getHeight() - 1; ++y)
            {
                for (int x = 0; x < getWidth() - 1; ++x)
                {
                    if (isPixelOnEdge(x, y))
                        setPixel(x, y, new int[]{0, 0, 0});
                    else
                    {
                        ImageKernel imageKernel = getSurroundingPixels(x, y);
                        int[] newPixels = imageKernel.medianBlur();
                        setPixel(x, y, newPixels);
                    }
                }
            }
            this.notifyObservers();
        }
    }

    @Override
    public void uniformBlur()
    {
        if (isImageDrawable())
        {
            for (int y = 0; y < getHeight(); ++y)
            {
                for (int x = 0; x < getWidth() - 1; ++x)
                {
                    if (isPixelOnEdge(x, y))
                        setPixel(x, y, new int[]{0, 0, 0});
                    else
                    {
                        ImageKernel imageKernel = getSurroundingPixels(x, y);
                        int[] newPixels = imageKernel.uniformBlur();
                        setPixel(x, y, newPixels);
                    }
                }
            }
            this.notifyObservers();
        }
    }

    @Override
    public void grayscale()
    {
        if (isImageDrawable())
        {
            for (int y = 0; y < getHeight(); ++y)
            {
                for (int x = 0; x < getWidth(); ++x)
                {
                    float[] hsb = getPixelHSB(x, y);
                    hsb[HSB_SATURATION] = 0;
                    setPixelHSB(x, y, hsb);
                }
            }
            this.notifyObservers();
        }
    }

    @Override
    public void contrast(int amount)
    {
        if (isImageDrawable())
        {
            for (int y = 0; y < getHeight(); ++y)
            {
                for (int x = 0; x < getWidth(); ++x)
                {
                    float[] hsb = getPixelHSB(x, y);
                    float newBrightness = (float) ((Math.pow((double) ((amount + 100f) / 100f), 4) * (hsb[HSB_BRIGHTNESS] - 128f)) + 128f);
                    hsb[HSB_BRIGHTNESS] = newBrightness;
                    if (hsb[HSB_BRIGHTNESS] > 1.0)
                        hsb[HSB_BRIGHTNESS] = 1.0f;
                    else if (hsb[HSB_BRIGHTNESS] < 0.0)
                        hsb[HSB_BRIGHTNESS] = 0.0f;
                    setPixelHSB(x, y, hsb);
                }
            }
            this.notifyObservers();
        }
    }

    @Override
    public void brightness(int amount)
    {
        if (isImageDrawable())
        {
            float brightness = amount / 100f;
            for (int y = 0; y < getHeight(); ++y)
            {
                for (int x = 0; x < getWidth(); ++x)
                {
                    float[] hsb = getPixelHSB(x, y);
                    hsb[HSB_BRIGHTNESS] += brightness;
                    if (hsb[HSB_BRIGHTNESS] > 1.0)
                        hsb[HSB_BRIGHTNESS] = 1.0f;
                    else if (hsb[HSB_BRIGHTNESS] < 0.0)
                        hsb[HSB_BRIGHTNESS] = 0.0f;
                    setPixelHSB(x, y, hsb);
                }
            }
            this.notifyObservers();
        }
    }

    private float[] getPixelHSB(int x, int y)
    {
        int[] rgb = new int[3];
        float[] hsb = new float[3];
        getPixel(x, y, rgb);
        Color.RGBtoHSB(rgb[RGB_RED], rgb[RGB_GREEN], rgb[RGB_BLUE], hsb);
        return hsb;
    }

    private void setPixelHSB(int x, int y, float[] hsb)
    {
        int[] rgb = new int[3];
        Color c = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
        rgb[0] = c.getRed();
        rgb[1] = c.getGreen();
        rgb[2] = c.getBlue();
        setPixel(x, y, rgb);
    }

    @Override
    public void notifyObservers()
    {
        super.setChanged();
        super.notifyObservers();
    }

    private ImageKernel getSurroundingPixels(int x, int y)
    {
        //        if (x == 0)
        //            x = 1;
        //        else if (x == getWidth() - 1)
        //            x = getWidth() - 2;
        //        if (y == 0)
        //            y = 1;
        //        else if (y == getHeight() - 1)
        //            y = getHeight() - 2;
        int[] m00 = getTrueUpperLeft(x, y);
        int[] m01 = getTrueUpper(x, y);
        int[] m02 = getTrueUpperRight(x, y);
        int[] m10 = getTrueLeft(x, y);
        int[] m11 = getCenter(x, y);
        int[] m12 = getTrueRight(x, y);
        int[] m20 = getTrueBottomLeft(x, y);
        int[] m21 = getTrueBottom(x, y);
        int[] m22 = getTrueBottomRight(x, y);
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
        } else
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
            } else
            {
                return getTrueUpper(x, y);
            }
        } else
        {
            if (isYAtTopEdge(y))
            {
                return getTrueRight(x, y);
            } else
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
        } else
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
            } else
            {
                return getTrueBottom(x, y);
            }
        } else
        {
            if (isYAtBottomEdge(y))
            {
                return getTrueRight(x, y);
            } else
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
        } else
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
            } else
            {
                return getTrueBottom(x, y);
            }
        } else
        {
            if (isYAtBottomEdge(y))
            {
                return getTrueLeft(x, y);
            } else
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
        } else
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
        } else
        {
            //top edge, return left
            if (isYAtTopEdge(y))
            {
                return getTrueLeft(x, y);
            } else
            {
                return getTrueUpperLeft(x, y);
            }
        }
    }

    private boolean isXAtLeftEdge(int x)
    {
        return x - 1 <= 0;
    }

    private boolean isXAtRightEdge(int x)
    {
        return x + 1 >= getWidth();
    }

    private boolean isYAtTopEdge(int y)
    {
        return y - 1 <= 0;
    }

    private boolean isYAtBottomEdge(int y)
    {
        return y + 1 >= getHeight();
    }

    private boolean isPixelOnEdge(int x, int y)
    {
        return isXAtLeftEdge(x) || isXAtRightEdge(x) || isYAtTopEdge(y) || isYAtBottomEdge(y);
    }

    public void toggleBackgroundDisplay()
    {
        drawImage = !drawImage;
        if (isImageDrawable())
            this.notifyObservers();
    }

    @Override
    public synchronized void addObserver(Observer o)
    {
        super.addObserver(o);
        if (o instanceof DrawingViewer)
            ((DrawingViewer) o).setImage(this);
    }
}
