/*
  The MIT License

  Copyright (c) 2017 Shajeer Ahamed (info4shajeer@gmail.com)

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
 */
package com.droidev.app.scratchcard;


import android.graphics.Bitmap;

import java.nio.ByteBuffer;

/**
 * Author : Shajeer Ahamed KP
 * e-mail : info4shajeer@gmail.com
 * Date : 26-Sep-2018
 * Project : Ipru-Touch
 */

public class BitmapUtils {

    /**
     * Compares two bitmaps and gives the percentage of similarity
     *
     * @param bitmap1 input bitmap 1
     * @param bitmap2 input bitmap 2
     * @return a value between 0.0 to 1.0 . Note the method will return 0.0 if either of bitmaps are null nor of same size.
     */
    public static float compareEquivalance(Bitmap bitmap1, Bitmap bitmap2) {

        if (bitmap1 == null || bitmap2 == null || bitmap1.getWidth() != bitmap2.getWidth() || bitmap1.getHeight() != bitmap2.getHeight()) {
            return 0f;
        }


        ByteBuffer buffer1 = ByteBuffer.allocate(bitmap1.getHeight() * bitmap1.getRowBytes());
        bitmap1.copyPixelsToBuffer(buffer1);

        ByteBuffer buffer2 = ByteBuffer.allocate(bitmap2.getHeight() * bitmap2.getRowBytes());
        bitmap2.copyPixelsToBuffer(buffer2);

        byte[] array1 = buffer1.array();
        byte[] array2 = buffer2.array();

        int len = array1.length; // array1 and array2 will be of some length.
        int count = 0;

        for (int i = 0; i < len; i++) {
            if (array1[i] == array2[i]) {
                count++;
            }
        }

        return ((float) (count)) / len;
    }

    /**
     * Finds the percentage of pixels that do are empty.
     *
     * @param bitmap input bitmap
     * @return a value between 0.0 to 1.0 . Note the method will return 0.0 if either of bitmaps are null nor of same size.
     */
    public static float getTransparentPixelPercent(Bitmap bitmap) {

        if (bitmap == null) {
            return 0f;
        }

        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getHeight() * bitmap.getRowBytes());
        bitmap.copyPixelsToBuffer(buffer);

        byte[] array = buffer.array();

        int len = array.length;
        int count = 0;

        for (byte anArray : array) {
            if (anArray == 0) {
                count++;
            }
        }
        return ((float) (count)) / len;
    }
}