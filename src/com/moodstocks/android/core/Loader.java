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

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

/**
 * Helper class that handles the loading of Moodstocks SDK native libraries.
 */
public class Loader {
  private static boolean done = false;
  private static boolean compatible = true;

  /**
   * The different possible CPU architectures
   */
  public static final class Architecture {
    /**
     * Unsupported architecture.
     */
    public static final int UNSUPPORTED = -1;
    /**
     * ARMv5TE to ARMv6 (included)
     */
    public static final int ARMv6 = 0;
    /**
     * ARMv7 <b>without</b> NEON support
     */
    public static final int ARMv7 = 1;
    /**
     * ARMv7 <b>with</b> NEON support
     */
    public static final int ARMv7_NEON = 2;
    /**
     * x86
     */
    public static final int x86 = 3;
  }

  /**
   * Loads the right version of the Moodstocks SDK native library.
   * <p>
   * This function is a no-op if the architecture of the device is not supported,
   * or if this function has already been called.
   */
  public static synchronized void load() {
    if (done)
      return;

    if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
      System.loadLibrary("jmoodstocks-sdk");

      switch (getCpuArch()) {
        case Architecture.ARMv6: System.loadLibrary("jmoodstocks-sdk-core-armv6");
                                 break;
        case Architecture.ARMv7: System.loadLibrary("jmoodstocks-sdk-core-armv7");
                                 break;
        case Architecture.ARMv7_NEON: System.loadLibrary("jmoodstocks-sdk-core-armv7-neon");
                                      break;
        case Architecture.x86: System.loadLibrary("jmoodstocks-sdk-core-x86");
                               break;
        default: compatible = false;
                 break;
      }
    }
    else {
      compatible = false;
    }
    done = true;
  }

  /**
   * Checks the device compatibility with the Moodstocks SDK.
   * @return true if the device is compatible, false otherwise.
   */
  public static boolean isCompatible() {
    return compatible;
  }

  /**
   * Native funtion to retrieve the current device architecture.
   * @return a flag among the {@link Loader.Architecture} enum.
   */
  private static native int getCpuArch();
}
