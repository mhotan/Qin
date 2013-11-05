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

/** Errors thrown if the C library encounters an error, for example:
 * No internet connexion,
 * Invalid use of the library,
 * etc.
 * <p>
 * It provides methods to get an error code and an error message to help
 * you solve the problem.
 */
public class MoodstocksError extends java.lang.Throwable {
  private static final long serialVersionUID = 1L;
  private int mErrorCode = 0;

  /**
   * Flag allowing the use of {@link #log()} method.
   * <p>
   * If true, calling the {@link #log()} method will print a full
   * stack trace in Logcat. Otherwise, this method is a no-op.
   * <p>
   * It is advised to set this flag to false before switching to production!
   */
  public static final boolean DEBUG = true;

  /**
   * Enum listing the possible errors.
   */
  public static final class Code {
    /** Success */
    public static final int SUCCESS = 0;
    /** Unspecified error */
    public static final int ERROR = 1;
    /** Invalid use of the library */
    public static final int MISUSE = 2;
    /** Access permission denied */
    public static final int NOPERM = 3;
    /** File not found */
    public static final int NOFILE = 4;
    /** Database file locked */
    public static final int BUSY = 5;
    /** Database file corrupted */
    public static final int CORRUPT = 6;
    /** Empty database */
    public static final int EMPTY = 7;
    /** Authorization denied */
    public static final int AUTH = 8;
    /** No internet connection */
    public static final int NOCONN = 9;
    /** Operation timeout */
    public static final int TIMEOUT = 10;
    /** Threading error */
    public static final int THREAD = 11;
    /** Credentials mismatch */
    public static final int CREDMISMATCH = 12;
    /** Internet connection too slow */
    public static final int SLOWCONN = 13;
    /** Record not found */
    public static final int NOREC = 14;
    /** Operation aborted */
    public static final int ABORT = 15;
    /** Resource temporarily unavailable */
    public static final int UNAVAIL = 16;
    /** Image size or format not supported */
    public static final int IMG = 17;
    /** Wrong API key or no offline image */
    public static final int APIKEY = 18;
  }

  /**
   * Constructor
   * @param message The error message
   * @param code    The error code
   */
  public MoodstocksError(String message, int code) {
    super(message);
    mErrorCode = code;
  }

  /**
   * Get the error code
   * @return  the error code among the {@link MoodstocksError.Code} flags.
   */
  public int getErrorCode() {
    return mErrorCode;
  }

  /**
   * Logs an error and its stack, if {@link #DEBUG} is true.
   */
  public void log() {
    if (DEBUG) printStackTrace();
  }

}
