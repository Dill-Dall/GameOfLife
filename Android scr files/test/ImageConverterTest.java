package com.example.martinstromolsen.goq;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by MartinStromOlsen on 30/04/2017.
 */
public class ImageConverterTest {
    @Test
    public void generateByteArray() throws Exception {
    }

    @Test
    public void determineGreyscaleInt() throws Exception {
        int red = 0;
        int green = 0;
        int blue = 0;

        int expResult = 0;
        ImageConverter test = new ImageConverter();
        int result = test.determineGreyscaleInt(red, green, blue);
        assertEquals(result, expResult);

        int red1 = 255;
        int green1 = 200;
        int blue1 = 78;

        int expResult1 = 203;
        int result1 = test.determineGreyscaleInt(red1, green1, blue1);
        assertEquals(result1, expResult1);

        int red2 = 10;
        int green2 = 199;
        int blue2 = 254;

        int expResult2 = 149;
        int result2 = test.determineGreyscaleInt(red2, green2, blue2);
        assertEquals(result2, expResult2);

        int red3 = 255;
        int green3 = 255;
        int blue3 = 255;

        int expResult3 = 255;
        int result3 = test.determineGreyscaleInt(red3, green3, blue3);
        assertEquals(result3, expResult3);
    }

    @Test
    public void ditheringOnImageArrayInt() throws Exception {
        int[][] inputImageArray = {{100, 200, 50},
                {50, 20, 38},
                {9, 8, 23},
                {120, 90, 23}};

        ImageConverter tester = new ImageConverter();

        byte[][] result = tester.ditheringOnImageArrayInt(inputImageArray);
        byte[][] expResult = {{1, 0, 1}, {1, 1, 1}, {1, 1, 1}, {0, 1, 1}};
        Assert.assertArrayEquals(result, expResult);

        int[][] inputImageArray1 = {{127, 127, 127, 127},
                                    {127, 127, 127, 127},
                                    {127, 127, 127, 127}};

        byte[][] result1 = tester.ditheringOnImageArrayInt(inputImageArray1);
        byte[][] expResult1 = {{1, 0, 1, 0}, {0, 1, 0, 1}, {1, 0, 1, 0}};
        Assert.assertArrayEquals(result1, expResult1);

    }

}