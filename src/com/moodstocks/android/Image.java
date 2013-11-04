/*
 * Copyright (c) 2012 Moodstocks SAS
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.moodstocks.android;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.moodstocks.android.core.Loader;
import com.moodstocks.android.core.OrientationListener.Orientation;

/**
 *  Image class used as an input for the Moodstocks SDK.
 */
public class Image {

  /**
   * Specifies the color format and encoding for each pixel in the image.
   */
  public static final class PixelFormat {
    /**
     * Packed-pixel format handled in an endian-specific manner.
     * An RGBA color is packed in one 32-bit integer as follow:
     *   (A << 24) | (R << 16) | (G << 8) | B
     * This is stored as BGRA on little-endian CPU architectures (e.g. iPhone)
     * and ARGB on big-endian CPUs.
     */
    public static final int RGB32 = 0;
    /**
     * Specifies a 8-bit per pixel grayscale pixel format.
     */
    public static final int GRAY8 = 1;
    /**
     * Specifies the YUV pixel format with 1 plane for Y and 1 plane for the
     * UV components, which are interleaved: first byte V and the following byte U.
     */
    public static final int NV21 = 2;
    /**
     * <i>Internal code - do not use.</i>
     */
    public static final int NB = 3;
  }

  /**
   * Internal flags defining the real orientation of the image as found within
   * the EXIF specification
   * <p>
   * Each flag specifies where the origin (0,0) of the image is located.
   * Use 0 (undefined) to ignore or 1 (the default) to keep the
   * image unchanged.
   */
  public static final class ExifOrientation {
    /**
     * Undefined orientation (i.e image is kept unchanged)
     */
    public static final int UNDEFINED = 0;
    /**
     * 0th row is at the top, and 0th column is on the left (the default)
     */
    public static final int TOP_LEFT = 1;
    /**
     * 0th row is at the bottom, and 0th column is on the right
     */
    public static final int BOTTOM_RIGHT = 3;
    /**
     * 0th row is on the right, and 0th column is at the top
     */
    public static final int RIGHT_TOP = 6;
    /**
     * 0th row is on the left, and 0th column is at the bottom
     */
    public static final int LEFT_BOTTOM = 8;
  }

  private int ptr = 0;
  private int counter = 0;

  static {
    Loader.load();
  }

  /**
   * Constructor.
   * <p>
   * We recommend that you provide 1280x720 frames whenever
   * it is possible.
   * @param data          The image bytes.
   * @param w             The image width
   * @param h             The image height
   * @param bpr           The image stride: number of bytes per row.
   * @param orientation   The image orientation, among the {@link com.moodstocks.android.core.OrientationListener.Orientation} flags.
   */
  public Image(byte[] data, int w, int h, int bpr, int orientation) {
    /* NV21 is the default Android format */
    int fmt = PixelFormat.NV21;
    int ori = 0;
    switch(orientation) {
    case Orientation.UP: ori = ExifOrientation.LEFT_BOTTOM;
    break;
    case Orientation.RIGHT: ori = ExifOrientation.BOTTOM_RIGHT;
    break;
    case Orientation.DOWN: ori = ExifOrientation.RIGHT_TOP;
    break;
    case Orientation.LEFT: ori = ExifOrientation.TOP_LEFT;
    break;
    case Orientation.NONE: ori = ExifOrientation.UNDEFINED;
    break;
    }
    try {
      initialize(data, w, h, bpr, fmt, ori);
    } catch (MoodstocksError e) {
      e.log();
    }
  }

  /** Reference counting +1 function.
   * <p>
   * Any function taking an Image as an argument must call retain()
   * on it at at the beginning and call release() on it once it's
   * not needed anymore.
   */
  public void retain() {
    this.counter++;
  }

  /** Reference counting -1 function.
   * <p>
   * Any function taking an Image as an argument must call retain()
   * on it at at the beginning and call release() on it once it's
   * not needed anymore.
   */
  public void release() {
    this.counter--;
    if (counter <= 0) this.destruct();
  }

  /**
   * Converts an NV21 framebuffer (as provided by the camera) into a bitmap.
   * @param data the framebuffer
   * @param w the width of the framebuffer
   * @param h the height of the framebuffer
   * @param stride the stride of the framebuffer
   * @return the framebuffer as a bitmap.
   */
  public static Bitmap bufferToBitmap(byte[] data, int w, int h, int stride) {
    int[] argb = nv21ToARGB(data, w, h, stride);
    return Bitmap.createBitmap(argb, w, h, Bitmap.Config.ARGB_8888);
  }

  /**
   * Warps at the maximum possible resolution a bitmap using a perspective transform.
   * <p>
   * This function can be used to pipe the results of the scanner to any third-party
   * library requesting high quality frames, such as an optical character recognition
   * (OCR) library.
   * <p>
   * Given the fact that this function tries to retrieve the best possible quality, it
   * can be quite time-consuming and should be run asynchronously.
   * @param src the source bitmap to transform
   * @param m the perspective transform
   * @param w the requested output bitmap width
   * @param h the requested height bitmap height
   * @return the warped bitmap, at the maximum possible resolution given the inputs,
   * and preserving the {@code w/h} aspect ratio, or `null` if it could not be computed.
   */
  public static Bitmap warp(Bitmap src, Matrix m, int w, int h) {
    return warp(src, m, w, h, -1);
  }

  /**
   * Similar to {@link #warp(Bitmap, Matrix, int, int)}, but with a specified result size.
   * <p>
   * Unlike {@link #warp(Bitmap, Matrix, int, int)}, the returned bitmap is guaranteed to
   * be of size exactly {@code w*scale x h*scale}.
   * @param src the source bitmap to transform
   * @param m the perspective transform
   * @param w the requested output bitmap width
   * @param h the requested height bitmap height
   * @param scale the scale factor to apply to {@code w} and {@code h}, in the [0..1]
   * range. If not in this range, it is clamped to fit into it.
   * @return the warped bitmap of size {@code w*scale x h*scale}, or `null` if
   * it could not be computed.
   */
  public static Bitmap warp(Bitmap src, Matrix m, int w, int h, float scale) {
    float[] values = new float[9];
    m.getValues(values);
    return warp_native(src, values, w, h, scale);
  }

  /**
   * Destroys the native Image object
   */
  private native void destruct();

  /**
   * We override finalize() to ensure that
   * the garbage collector also destroys
   * the native Image object
   */
  @Override
  protected void finalize() throws Throwable {
    this.destruct();
    super.finalize();
  }

  /**
   * Native method to create an Image.
   * @param data      The image bytes
   * @param width     The image width
   * @param height    The image height
   * @param bpr       The image stride: number of bytes per row.
   * @param fmt       The image bytes format, among {@link Image.PixelFormat} flags.
   * @param ori       The image orientation, among the {@link Image.ExifOrientation} flags.
   * @throws MoodstocksError  if an error occurred.
   */
  private native void initialize(byte[] data, int width, int height,
      int bpr, int fmt, int ori)
          throws MoodstocksError;

  /**
   * Native function to convert NV21 buffers to ARGB.
   * @param data the buffer
   * @param width its width
   * @param height its height
   * @param stride it stride
   * @return the ARGB data, packed as an {@code int} array.
   */
  private native static int[] nv21ToARGB(byte[] data, int width, int height, int stride);

  /**
   * Native function to warp a Bitmap.
   * @param src the source bitmap
   * @param m the perspective transform, using the same conventions as {@link Result#getHomography()}.
   * @param w the requested output width
   * @param h the requested output height
   * @param scale if > 0, the output image will be of size {@code w*scale x h*scale}.
   * Otherwise, the output image will be of the maximum possible resolution
   * preserving the {@code w/h} aspect ratio.
   * @return the warped bitmap.
   */
  private native static Bitmap warp_native(Bitmap src, float[] m, int w, int h, float scale);
}
