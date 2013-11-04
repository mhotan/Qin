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

import com.moodstocks.android.core.Loader;
import com.moodstocks.android.core.OrientationListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Class holding the result of a scan.
 */
public class Result {

  /**
   * Enum defining the type of a result. Can be combined using a binary OR "|".
   */
  public static final class Type {
    /** Unknown result type. */
    public static final int NONE        = 0;
    /** EAN8 linear barcode */
    public static final int EAN8        = 1 << 0;
    /** EAN13 linear barcode */
    public static final int EAN13       = 1 << 1;
    /** QR Code 2B barcode */
    public static final int QRCODE      = 1 << 2;
    /** Datamatrix 2D barcode */
    public static final int DATAMATRIX  = 1 << 3;
    /** Image recognition result. */
    public static final int IMAGE       = 1 << 31;
  }

  /**
   * Enum defining the possible extra information to compute. Can be combined using a binary OR "|".
   */
  public static final class Extra {
    /** Require no geometrical information */
    public static final int NONE       = 0;
    /** Require the recognized image or barcode corners. */
    public static final int CORNERS    = 1 << 0;
    /** Require the homography linking the query frame and the recognized reference image. */
    public static final int HOMOGRAPHY = 1 << 1;
    /** Require the dimensions of the recognized reference image. */
    public static final int DIMENSIONS = 1 << 2;
    /** Require a copy of the query image */
    public static final int IMAGE      = 1 << 3;
    /**
     * Require a straightened up version of the matched image in the query frame.
     *
     * This corresponds to stretching the query image from the 4 corners of the matched
     * reference image, in a new image of the same aspect ratio as the matched reference image.
     *<p>
     * This flag is a convenient flag corresponding to '{@link #HOMOGRAPHY}|{@link #DIMENSIONS}|{@link #IMAGE}'.
     */
    public static final int WARPED_IMAGE = HOMOGRAPHY | DIMENSIONS | IMAGE;
  }

  static {
    Loader.load();
  }

  private int     type;
  private byte[]  bytes;
  private int     length;
  private float[] corners     = null;
  private float[] homography  = null;
  private int[]   dimensions  = null;
  private Bitmap  image       = null;
  private int     orientation = Image.ExifOrientation.UNDEFINED;

  /**
   * Constructor, called only from JNI layer
   */
  private Result(int type, byte[] bytes, int length,
                 float[] corners, float[] homography, int[] dimensions) {
    this.type = type;
    this.bytes = bytes.clone();
    this.length = length;
    this.corners = corners;
    this.homography = homography;
    this.dimensions = dimensions;
  }

  /**
   * Get the result type
   * @return  the result type among {@link Result.Type} flags.
   */
  public int getType() {
    return type;
  }

  /**
   * Get the result data as a string with UTF-8 encoding.
   * <p>
   * Use `getData` if you intend to create a string with another
   * encoding or just want to interact with the raw bytes
   * @return the value of this result interpreted as an UTF-8 string.
   */
  public String getValue() {
    return new String(this.bytes);
  }

  /**
   * Get the result data as raw bytes.
   * @return the byte array of this result data.
   */
  public byte[] getData() {
    return this.bytes;
  }

  /**
   * Get the recognized image or decoded barcode corners.
   * <p>
   * If available, get the (x, y) coordinates of the corner points that delimit the area of the
   * recognized content within the query frame domain in its initial orientation, i.e. as the
   * frame is physically provided by the camera.
   * <p>
   * The coordinates are provided as a ratio computed with the query frame dimensions, i.e. they
   * are within the range [-1, 1] when the point is found inside the query frame. Note that they
   * are not clamped with the query frame boundaries and could thus take values outside the
   * range [-1, 1].
   * <p>
   * WARNING: not available if the {@link Result.Extra#CORNERS} flag has not been added to the
   * {@link ScannerSession} using {@link ScannerSession#setExtras(int)}.
   * @return  8 sized array [x1, y1, x2, ...] containing the coordinates, or NULL
   *          if these coordinates are not available or an error occurred.
   */
  public float[] getCorners() {
    return this.corners;
  }

  /**
   * Similar to the {@link #getCorners()} method, but re-orients the coordinates to fit
   * the current screen orientation.
   * @return  8 sized array [x1, y1, x2, ...] containing the coordinates, or NULL
   *          if these coordinates are not available or an error occurred.
   */
  public float[] getOrientedCorners(Context c) {
    int r = ((WindowManager) c.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
    float[] result = null;
    if (this.corners != null) {
      switch (r) {
      case Surface.ROTATION_0:
        result = new float[8];
        for (int i = 0; i < 4; ++i) {
          result[2*i]   = -this.corners[2*i+1];
          result[2*i+1] = this.corners[2*i];
        }
        break;
      case Surface.ROTATION_180:
        result = new float[8];
        for (int i = 0; i < 4; ++i) {
          result[2*i]   = this.corners[2*i+1];
          result[2*i+1] = -this.corners[2*i];
        }
        break;
      case Surface.ROTATION_270:
        result = new float[8];
        for (int i = 0; i < 8; ++i) {
          result[i] = -this.corners[i];
        }
        break;
      case Surface.ROTATION_90:
      default:
        result = this.corners.clone();
        break;
      }
    }
    return result;
  }

  /**
   * Get the homography linking the recognized image and the query frame.
   * <p>
   * Works for result of type {@link Result.Type#IMAGE} only.
   * <p>
   * Compute the homography between the reference image and the query frame in its
   * initial orientation, (i.e. as the frame is physically provided by the camera)
   * to retrieve the related perspective transform.
   * <p>
   * In order to make abstraction of the frame and reference image dimensions,
   * this homography is computed with the assumption that both the frame and the
   * reference image coordinates system are in the [-1, 1] range.
   * This homography can be used to project reference image points into the query
   * frame domain by using homegenous coordinates, i.e.:
   * <p>
   *   P' = H x P
   * <p>
   *   with: P = [x, y, 1]t and P' = [u, v, w]t
   * <p>
   * WARNING: not available if the {@link Result.Extra#HOMOGRAPHY} flag has not been added
   * to the {@link ScannerSession} using {@link ScannerSession#setExtras(int)}.
   * @return  the 3x3 homography Matrix if available, NULL if it is not available
   *          or if an error occurred.
   */
  public Matrix getHomography() {
    if (this.homography != null) {
      Matrix m = new Matrix();
      m.setValues(this.homography);
      return m;
    }
    return null;
  }

  /**
   * Get the dimensions of the matched reference image.
   * <p>
   * Works for result of type {@link Result.Type#IMAGE} only.
   * <p>
   * WARNING: not available if the {@link Result.Extra#DIMENSIONS} flag has not been added to the
   * {@link ScannerSession} using {@link ScannerSession#setExtras(int)}.
   * @return  the dimensions in pixels of the reference image matched, as [width, height],
   *          or null if not available or if an error occurred.
   */
  public int[] getDimensions() {
    return this.dimensions;
  }

  /**
   * Sets the query image as a {@link Bitmap}
   * @param bmp the bitmap
   * @param ori the orientation flag among {@link com.moodstocks.android.core.OrientationListener.Orientation}
   */
  protected void setImage(Bitmap bmp, int ori) {
    this.image = bmp;
    this.orientation = ori;
  }

  /**
   * Get the query image corresponding to this result, as physically provided by the camera,
   * i.e not re-oriented.
   * <p>
   * WARNING: not available if the {@link Result.Extra#IMAGE} flag has not been added to the
   * {@link ScannerSession} using {@link ScannerSession#setExtras(int)}.
   * @return the query image as a {@link Bitmap} object.
   */
  public Bitmap getImage() {
    return this.image;
  }

  /**
   * Get the query image, re-oriented according to the physical orientation of the device,
   * as allowed using {@link ScannerSession#useDeviceOrientation}.
   * <p>
   * WARNING: not available if the {@link Result.Extra#IMAGE} flag has not been added to the
   * {@link ScannerSession} using {@link ScannerSession#setExtras(int)}.
   * @return the re-oriented query image as a {@link Bitmap} object.
   * @see ScannerSession#useDeviceOrientation
   */
  public Bitmap getOrientedImage() {
    if (this.image == null)
      return null;
    float d = 0;
    switch(this.orientation) {
    case OrientationListener.Orientation.UP:
      d = 90;
      break;
    case OrientationListener.Orientation.RIGHT:
      d = 180;
      break;
    case OrientationListener.Orientation.DOWN:
      d = 270;
      break;
    default:
      break;
    }
    Bitmap res = null;
    if (d != 0) {
      Matrix m = new Matrix();
      m.postRotate(d);
      res = Bitmap.createBitmap(this.image, 0, 0, this.image.getWidth(), this.image.getHeight(), m, true);
    }
    else {
      res = this.image;
    }
    return res;
  }

  /**
   * Get the warped image recognized object from the query frame, at the maximum possible resolution.
   * <p>
   * This methods crops and straightens up the detected region of interest from the query image, and
   * gives it the aspect ratio of the original reference image, while keeping the best possible
   * resolution.
   * <p>
   * This method can be used to pipe the results of the scanner to any third-party
   * library requesting high quality frames, such as an optical character recognition
   * (OCR) library.
   * <p>
   * Given the fact that this method tries to retrieve the best possible quality, it
   * can be quite time-consuming and should be run asynchronously.
   * <p>
   * WARNING: not available if the {@link Result.Extra#WARPED_IMAGE} flag has not been added to the
   * {@link ScannerSession} using {@link ScannerSession#setExtras(int)}.
   * @return the warped query frame at the best possible resolution.
   */
  public Bitmap getWarped() {
    if (this.image == null || this.dimensions == null || this.homography == null)
      return null;
    return Image.warp(this.image, this.getHomography(), this.dimensions[0], this.dimensions[1]);
  }

  /**
   * Similar to {@link #getWarped()}, but with a specified result size.
   * <p>
   * Unlike {@link #getWarped()}, the returned bitmap is guaranteed to
   * be exactly of the matched reference image, rescaled according to
   * the {@code scale} scaling factor.
   * @param scale the scaling factor, in the [0..1] range. If not in this
   * range, it is clamped to fit into it.
   * @return the warped query frame, at the specified size.
   */
  public Bitmap getWarped(float scale) {
    if (this.image == null || this.dimensions == null || this.homography == null)
      return null;
    return Image.warp(this.image, this.getHomography(), this.dimensions[0], this.dimensions[1], scale);
  }

  /**
   * Get the result data decoded using Base64url without padding.
   * @return the byte array of the decoded data
   */
  public native byte[] getDataFromBase64URL();

  /**
   * Utility function to decode a string as Base64url without padding.
   * @return the byte array of the decoded data
   */
  public static native byte[] dataFromBase64URLString(String s)
      throws MoodstocksError;

  /**
   * Checks that two results are of the same type and contain the same data.
   * @return true if identical type and data, false otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (o == null) return false;
    if (o.getClass()!=this.getClass())
      return false;
    Result r = (Result)o;
    return ((r.type==this.type) && (r.getValue().equals(this.getValue())) &&
            (r.length==this.length));
  }

}
