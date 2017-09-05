package com.example.martinstromolsen.goq;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by MartinStromOlsen on 11/04/2017.
 */

/**
 * Logic related to converting an image to a <code>byte[][]</code>.
 */
public class ImageConverter {

    private final static float REDCOEFF = 0.299f;
    private final static float GREENCOEFF = 0.587f;
    private final static float BLUECOEFF = 0.114f;

    private final static float CONSTANT_1_16 = 0.0625f;
    private final static float CONSTANT_3_16 = 0.1875f;
    private final static float CONSTANT_5_16 = 0.3125f;
    private final static float CONSTANT_7_16 = 0.4375f;

    /**
     * Generates a <code>byte[][]</code> representation of an image through "halftoning". The target
     * width and height are then determined using the <code>scaleFactor</code> and the Bitmap
     * representation of the image. The image orientation is extracted through the Exif tag of the
     * image and rotated with an identity matrix based on the orientation collected from the Exif
     * tag. The actual rotation, if the image needed to be rotated, happens in the method
     * <code>rotateBitmap</code> which returns the rotated Bitmap. The rotated Bitmap is used as a
     * parameter in method <code>createImageArrayInt</code>, the <code>int[][]</code> returned from
     * <code>createImageArrayInt</code> is then used as a parameter in method
     * <code>ditheringOnImageArrayInt</code> which returns the processed <code>byte[][]</code>
     * containing the "halftoned" version of the image.
     *
     * Source for matrix-rotation: https://dzone.com/articles/android-rotate-and-scale
     * Source for the ExifInterface:
     * "https://teamtreehouse.com/community/how-to-rotate-images-to-the-correct-orientation-
     * portrait-by-editing-the-exif-data-once-photo-has-been-taken"
     * @param imagePath the filepath for the image that is to be converted
     * @param scaleFactor a percentage represented with a <code>float</code> between [0, 1]
     * @return the "halftoned" <code>byte[][]</code> representing the image
     * @see #createImageArrayInt(Bitmap)
     * @see #ditheringOnImageArrayInt(int[][])
     */
    protected byte[][] generateByteArray(String imagePath, float scaleFactor)
    {
        int orientation = ExifInterface.ORIENTATION_NORMAL;
        try
        {
            ExifInterface exifInterface = new ExifInterface(imagePath);
            orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException ioe)
        {
            orientation = ExifInterface.ORIENTATION_NORMAL;
        }

        int bitmapWidth = Math.round(BitmapFactory.decodeFile(imagePath).getWidth()*scaleFactor);
        int bitmapHeight = Math.round(BitmapFactory.decodeFile(imagePath).getHeight()*scaleFactor);

        Bitmap photoBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imagePath),
                bitmapWidth, bitmapHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        if(orientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            Matrix rotationIdentity = new Matrix();
            rotationIdentity.setRotate(90);
            photoBitmap = rotateBitmap(photoBitmap, rotationIdentity);
        } else if(orientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            Matrix rotationIdentity = new Matrix();
            rotationIdentity.setRotate(180);
            photoBitmap = rotateBitmap(photoBitmap, rotationIdentity);
        }

        int[][] imageArray = createImageArrayInt(photoBitmap);

        photoBitmap = null;

        return ditheringOnImageArrayInt(imageArray);

    }

    /**
     * Rotates the parameter <code>tempBitmap</code> based on the parameter
     * <code>rotationMatrix</code> and returns the rotated Bitmap of the same size.
     * @param tempBitmap Bitmap to be rotated
     * @param rotationMatrix Matrix representing the needed rotation
     * @return the rotated Bitmap
     */
    private Bitmap rotateBitmap(Bitmap tempBitmap, Matrix rotationMatrix)
    {
        return Bitmap.createBitmap(tempBitmap, 0, 0, tempBitmap.getWidth(), tempBitmap.getHeight(), rotationMatrix, true);
    }

    /**
     * Iterates through the parameter <code>photoBitmap</code> and determines the greyscale-value of
     * each pixel in the <code>photoBitmap</code> from [0, 255] and assigns this value to the
     * corresponding coordinates in the <code>int[][]</code> of the same size as the
     * <code>photoBitmap</code>.
     * @param photoBitmap the Bitmap to evaluate
     * @return the <code>int[][]</code> representing the Bitmap with greyscale-values [0, 255]
     */
    private int[][] createImageArrayInt(Bitmap photoBitmap)
    {
        int[][] imageArray = new int[photoBitmap.getHeight()][photoBitmap.getWidth()];

        for (int i = 0; i < photoBitmap.getWidth(); i++) {
            for (int j = 0; j < photoBitmap.getHeight(); j++) {
                imageArray[j][i] = determineGreyscaleInt(Color.red(photoBitmap.getPixel(i, j)), Color.green(photoBitmap.getPixel(i, j)), Color.blue(photoBitmap.getPixel(i, j)));
            }
        }
        return imageArray;
    }

    /**
     * Determines the greyscale of from the RGB components with weights from the
     * "luminans-komponent".
     * This could also be done with the average (1/3) weights.
     *
     * Source for the coefficients:
     * http://www.uio.no/studier/emner/matnat/ifi/INF1040/h09/foiler/9_farger.pdf
     *
     * Source for the color - components:
     * https://developer.android.com/reference/android/graphics/Color.html
     *
     * @param red the red component of the pixel
     * @param green the green component of the pixel
     * @param blue the blue component of the pixel
     * @return the greyscale-value [0, 255]
     */
    protected int determineGreyscaleInt(int red, int green, int blue)
    {
        int greyScale1 = (red+green+blue)/3;
        int greyScale2 = Math.round(REDCOEFF*red + GREENCOEFF*green + BLUECOEFF*blue);

        return greyScale2;
    }

    /**
     * Creates a <code>byte[][]</code> of the same size as the parameter in order to fill with 1
     * (representing black in the byteArray) and 0 (representing white in the byteArray).
     *
     * Performs Floyd-Steinberg dithering on each greyscale-value [0, 255]. The double for-loop
     * iterates through the <code>int[][]</code> representing each pixel of the image. If the
     * greyscale-value is over 127 it is parsed as white (255) else it is parsed as black (0), the
     * representation of this is placed in the <code>byte[][]</code>-representation
     * of the image. The constants from the Floyd-Steinberg dithering are given as class instance members.
     *
     * Source for the Floyd-Steinberg dithering:
     * https://en.wikipedia.org/wiki/Floyd%E2%80%93Steinberg_dithering
     *
     * @param imageArray the array of greyscale-values [0, 255]
     * @return a <code>byte[][]</code> representing the black and white pixels of the image after
     * Floyd-Steinberg dithering
     */
    protected byte[][] ditheringOnImageArrayInt(int[][] imageArray)
    {
        byte[][] byteArray = new byte[imageArray.length][imageArray[0].length];

        for(int row = 0; row < imageArray.length; row++)
        {
            for(int column = 0; column < imageArray[0].length; column++)
            {
                int currentValue = imageArray[row][column];
                int processedValue;
                byte byteValue;

                if(imageArray[row][column] > 127)
                {
                    processedValue = 255;
                    byteValue = 0;

                } else {
                    processedValue = 0;
                    byteValue = 1;
                }


                byteArray[row][column] = byteValue;
                imageArray[row][column] = processedValue;
                int colorDelta = currentValue - processedValue;

                if(row + 1 < imageArray.length && column - 1 > 0)
                {
                    imageArray[row+1][column-1] += colorDelta*CONSTANT_3_16;
                }

                if(column + 1 < imageArray[0].length)
                {
                    imageArray[row][column+1] += colorDelta*CONSTANT_7_16;
                }

                if(row + 1 < imageArray.length && column + 1 < imageArray[0].length)
                {
                    imageArray[row+1][column+1] += colorDelta*CONSTANT_1_16;
                }

                if(row + 1 < imageArray.length)
                {
                    imageArray[row+1][column] += colorDelta*CONSTANT_5_16;
                }
            }
        }
        return byteArray;
    }

    /**
     * Determines the greyscale of from the RGB components with weights from the
     * "luminans-komponent". This could also be done with the average (1/3) weights.
     *
     * Source for the coefficients:
     * http://www.uio.no/studier/emner/matnat/ifi/INF1040/h09/foiler/9_farger.pdf
     *
     * Source for the color - components:
     * https://developer.android.com/reference/android/graphics/Color.html
     *
     * @param red the red component of the pixel
     * @param green the green component of the pixel
     * @param blue the blue component of the pixel
     * @return the greyscale-value [0, 1]
     */
    @Deprecated
    private double determineGreyscaleDouble(int red, int green, int blue)
    {
        double redValue = (double)red/255;
        double greenValue = (double)green/255;
        double blueValue = (double)blue/255;

        double greyScale2 = REDCOEFF*redValue + GREENCOEFF*greenValue + BLUECOEFF*blueValue;

        return greyScale2;

    }

    /**
     * Iterates through the parameter <code>photoBitmap</code> and determines the greyscale-value of
     * each pixel in the <code>photoBitmap</code> from [0, 1] and assigns this value to the
     * corresponding coordinates in the <code>double[][]</code> of the same size as the
     * <code>photoBitmap</code>.
     * @param photoBitmap the Bitmap to evaluate
     * @return the <code>double[][]</code> representing the Bitmap with greyscale-values [0, 1]
     */
    @Deprecated
    private double[][] createImageArray(Bitmap photoBitmap)
    {
        double[][] imageArray = new double[photoBitmap.getHeight()][photoBitmap.getWidth()];

        for (int i = 0; i < photoBitmap.getWidth(); i++) {
            for (int j = 0; j < photoBitmap.getHeight(); j++) {
                imageArray[j][i] = determineGreyscaleDouble(Color.red(photoBitmap.getPixel(i, j)), Color.green(photoBitmap.getPixel(i, j)), Color.blue(photoBitmap.getPixel(i, j)));
            }
        }
        return imageArray;
    }
}
