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

package com.moodstocks.android.core;

import android.app.Activity;
import android.view.OrientationEventListener;

/**
 * Singleton class used to detect the current physical orientation of the device.
 */
public class OrientationListener extends OrientationEventListener {

  private static OrientationListener instance = null;
  private static Activity parent = null;
  private int orientation;

  /**
   * Enum defining the possible device orientations.
   */
  public static final class Orientation {
    /**
     * Unknown or ignored orientation
     */
    public static final int NONE = -1;
    /**
     * The phone is held upright in portrait mode, microphone at the bottom, speaker at the top.
     */
    public static final int UP = 0;
    /**
     * The phone is held in landscape, microphone to the left, speaker to the right.
     */
    public static final int RIGHT = 1;
    /**
     * The phone is held upside-down in portrait mode, microphone at the top, speaker at the bottom.
     */
    public static final int DOWN = 2;
    /**
     * The phone is held in landscape, microphone to the right, speaker to the left.
     */
    public static final int LEFT = 3;
  }
  
  /**
   * Constructor
   * @param parent the caller Activity, to get a valid context.
   */
  private OrientationListener(Activity parent) {
    super(parent);
    orientation = Orientation.UP; //default if unknown
  }

  /**
   * Initialization function, <b>must</b> be called before any other operation.
   * @param parent  the parent Activity providing a context.
   */
  public static void init(Activity parent) {
    OrientationListener.parent = parent;
  }

  /**
   * Singleton accessor
   * @return  the OrientationListener instance.
   */
  public static OrientationListener get() {
    if (instance == null) {
      synchronized(OrientationListener.class) {
        if (instance == null) {
          if (parent != null) {
            instance = new OrientationListener(parent);
          }
          else {
            throw new RuntimeException("init() must be called before calling get()");
          }
        }
      }
    }
    return instance;
  }

  /**
   * Get the device orientation
   * @return  the device orientation among the {@link OrientationListener.Orientation} flags.
   */
  public int getOrientation() {
    return orientation;
  }
  
  /**
   * <i>Internal callback</i>
   */
  @Override
  public void onOrientationChanged(int degrees) {
    if (degrees != OrientationListener.ORIENTATION_UNKNOWN) {
      int ori = ((degrees+45)/90)%4; //corresponds to the given enum Orientation
      if (ori != orientation) {
        orientation = ori;
      }
    }
  }
}
