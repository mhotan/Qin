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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.moodstocks.android.core.ApiSearch;
import com.moodstocks.android.core.Loader;
import com.moodstocks.android.core.Sync;

import android.content.Context;

/** Scanner class.
 * <p>
 * The scanner offers an unified interface to perform:
 * <ul>
 * <li>
 * offline cache synchronization,
 * </li>
 * <li>
 * search over the local cache of image records,
 * </li>
 * <li>
 * remote search on Moodstocks API,
 * </li>
 * <li>
 * 1D/2D barcode decoding.
 * </li>
 * </ul>
 */
public final class Scanner {

  /**
   * Enum listing the possible options for the {@link Scanner#search2(Image, int, int)}
   * and {@link Scanner#match2(Image, Result, int, int)} methods.
   */
  public static final class Flags {
    /**
     * Default mode.
     */
    public static final int DEFAULT     = 0;
    /**
     * Disables "partial matching".
     * Use this flag to avoid returning false positive results due to partial
     * matching, for example if several of the indexed images share the exact same
     * logo.
     */
    public static final int NOPARTIAL   = 1 << 0;
    /**
     * Allows small image recognition.
     * Use this flag to boost the scale invariance so that smaller or farther images
     * can be recognized. Slightly slower that default mode.
     */
    public static final int SMALLTARGET = 1 << 1;
  }

  private static Scanner instance = null;
  private List<WeakReference<SyncListener>> extra_listeners = null;

  private ThreadPoolExecutor api_threadpool = null;
  private ExecutorService sync_thread = null;

  private static final String DBFilename = "ms.db";

  private int ptr = 0;

  /**
   * Interface implemented by the caller of a Moodstocks SDK synchronization.
   * <p>
   * Implementing this interface is required in order to be able to receive callbacks
   * on the progress of the synchronization process.
   */
  public static interface SyncListener {
    /**
     * Notifies the caller that a Sync has been launched
     */
    public void onSyncStart();
    /**
     * Notifies the caller that a Sync has successfully ended
     */
    public void onSyncComplete();
    /**
     * Notifies the caller that a Sync has failed.
     * @param e the {@link MoodstocksError} that caused the sync to fail.
     *          Some possible errors:
     *          {@link MoodstocksError.Code#APIKEY},
     *          {@link MoodstocksError.Code#NOCONN},
     *          {@link MoodstocksError.Code#SLOWCONN},
     *          {@link MoodstocksError.Code#TIMEOUT}
     */
    public void onSyncFailed(MoodstocksError e);
    /**
     * Notifies the caller of the sync progression
     * @param total     the total number of objects being synchronized
     * @param current   the number of objects that have currently been synchronized
     */
    public void onSyncProgress(int total, int current);
  }

  /**
   * Interface implemented by the caller of a Moodstocks API search.
   * <p>
   * Implementing this interface is required in order to be able to receive
   * callbacks from the API search.
   */
  public static interface ApiSearchListener {
    /**
     * Notifies the caller that an ApiSearch has been launched
     */
    public void onApiSearchStart();
    /**
     * Notifies the caller that an ApiSearch has successfully ended.
     * @param result    The {@link Result} found by this API search,
     *                  or null if nothing was found.
     */
    public void onApiSearchComplete(Result result);
    /**
     * Notifies the caller that an ApiSearch has failed.
     * @param e         The {@link MoodstocksError} that caused the API search
     *                  to fail.
     *                  Some possible errors:
     *                  {@link MoodstocksError.Code#NOCONN},
     *                  {@link MoodstocksError.Code#SLOWCONN},
     *                  {@link MoodstocksError.Code#TIMEOUT}
     */
    public void onApiSearchFailed(MoodstocksError e);
  }

  static {
    Loader.load();
    if (isCompatible()) init();
  }

  /**
   * Private constructor.
   */
  private Scanner() {
    super();
    // stores the extra Sync listeners.
    this.extra_listeners = new ArrayList<WeakReference<SyncListener>>();
    // ThreadPool / Thread handling the Asynchronous Sync and API Searches.
    this.api_threadpool = (ThreadPoolExecutor)Executors.newCachedThreadPool();
    this.sync_thread = Executors.newSingleThreadExecutor();
  }

  /**
   * Singleton accessor
   * @return  the singleton {@link Scanner} instance.
   * @throws MoodstocksError if any error occurred.
   */
  public static Scanner get()
      throws MoodstocksError {
    if (Scanner.instance == null) {
      synchronized(Scanner.class) {
        if (Scanner.instance == null) {
          Scanner.instance = new Scanner();
          if (isCompatible()) Scanner.instance.initialize();
        }
      }
    }
    return Scanner.instance;
  }

  /**
   * Destructor. Must be called before exiting the application.
   */
  public void destroy() {
    Scanner.instance.destruct();
    Scanner.instance = null;
  }

  /**
   * Opens the scanner and connects it to the database file.
   * <p>
   * It also checks <b>at runtime</b> that the device is compatible with the
   * Moodstocks SDK, a.k.a. that it runs Android 2.3 or more, and features
   * an ARM or x86 CPU.
   * <p>
   * We advise you to design your applications so it won't try to
   * use the scanner in such case, as the SDK will probably crash.
   * @param context the application context
   * @param key the API key
   * @param secret  the API secret
   * @throws MoodstocksError  if any error occured.
   */
  public void open(Context context, String key, String secret)
      throws MoodstocksError {
    this.open(context, key, secret, DBFilename);
  }

  /**
   * Similar to {@link #open(Context, String, String)}, but specifies the database filename.
   * <p>
   * You should only have to use it in an advanced context where the application needs
   * to manage several different API key/secret pairs.
   * @param context the application context
   * @param key the API key
   * @param secret  the API secret
   * @param filename the filename to use, without extension.
   * @throws MoodstocksError  if any error occured.
   */
  public void open(Context context, String key, String secret, String filename)
      throws MoodstocksError {
    if (!isCompatible()) {
      throw new RuntimeException("DEVICE IS NOT COMPATIBLE WITH MOODSTOCKS SDK");
    }
    String path = context.getFilesDir().getAbsolutePath();
    this.open(path + "/" + filename + ".db", key, secret);
  }

  /**
   * Closes the scanner and disconnects it from the database file
   * @throws MoodstocksError if any error occured.
   */
  public native void close()
      throws MoodstocksError;

  /* removes database */
  /**
   * Removes the database file related to the scanner.
   * <p>
   * This is a convenient utility provided for <b>extraordinary</b> situations.
   * @param context the application context
   * @throws MoodstocksError if any error occured
   */
  public static void clean(Context context)
      throws MoodstocksError {
    String path = context.getFilesDir().getAbsolutePath() + "/" + DBFilename;
    clean(path);
  }

  /**
   * Asynchronously synchronizes the cache.
   * <p>
   * This method runs in the background so you can safely call it from the UI thread.
   * Caller must implement Scanner.SyncListener interface. It will receive notifications
   * for this Sync only.
   * <p>
   * NOTE: this method requires an internet connection.
   * @param listener the {@link Scanner.SyncListener} to notify.
   * @return  false if a sync is already running, true otherwise.
   */
  public boolean sync(SyncListener listener) {
    if (!isSyncing()) {
      sync_thread.submit(new Sync(listener, extra_listeners));
      return true;
    }
    return false;
  }

  /**
   * Adds an extra {@link Scanner.SyncListener} to the scanner.
   * <p>
   * It will be notified every time a new sync is launched, until
   * it is removed using {@link #removeExtraSyncListener(SyncListener)}.
   * @param listener the new {@link Scanner.SyncListener} to notify.
   */
  public void addExtraSyncListener(SyncListener listener) {
    Iterator<WeakReference<SyncListener>> it = extra_listeners.iterator();
    while(it.hasNext()) {
      WeakReference<SyncListener> l = it.next();
      if (l.get() == listener)
        return;
    }
    this.extra_listeners.add(new WeakReference<SyncListener>(listener));
  }

  /**
   * Removes an extra {@link Scanner.SyncListener} added with {@link #addExtraSyncListener(SyncListener)}.
   * <p>
   * It will not be notified anymore of any sync.
   * @param listener the {@link Scanner.SyncListener} to remove.
   */
  public void removeExtraSyncListener(SyncListener listener) {
    Iterator<WeakReference<SyncListener>> it = extra_listeners.iterator();
    while(it.hasNext()) {
      WeakReference<SyncListener> l = it.next();
      if (l.get() == listener)
        it.remove();
    }
  }

  /**
   * Cancels as soons as possible any pending synchronization.
   */
  public void syncCancel() {
    Sync.cancelAll();
  }

  /**
   * Informs the caller of whether a sync is currently running.
   * @return true if currently syncing, false otherwise.
   */
  public boolean isSyncing() {
    return Sync.isSyncing();
  }

  /**
   * Asynchronously performs a remote image search on Moodstocks API.
   * <p>
   * This method runs in the background so you can safely call it from the UI thread.
   * <p>
   * The caller must implement Scanner.ApiSearchListener to receive notifications and result.
   * <p>
   * NOTE: this method requires an internet connection.
   * @param listener the {@link Scanner.ApiSearchListener} to notify.
   * @param qry the query {@link Image}
   */
  public void apiSearch(ApiSearchListener listener, Image qry) {
    api_threadpool.submit(new ApiSearch(listener, qry));
  }

  /**
   * Cancels any pending remote API Search.
   */
  public void apiSearchCancel() {
    ApiSearch.cancelAll();
  }

  /**
   * Returns the total number of images recorded into the local database
   * @return  the number of images in the local database
   * @throws MoodstocksError  if any error occurred.
   */
  public native int count()
      throws MoodstocksError;

  /**
   * Returns the list of all images IDs found in the local database.
   * @return the {@link List} of IDs.
   * @throws MoodstocksError if any error occurred.
   */
  public native List<byte[]> info()
      throws MoodstocksError;

  /**
   * Performs an offline image search among the local database.
   * <p>
   * Use a bitwise-OR combination of flags among {@link Result.Extra} to get
   * the corners, homograpghy and/or dimensions of the matched image, if any.
   * @param qry the query {@link Image}
   * @param extras the bitwise-OR combination of {@link Result.Extra} to compute
   * @return the {@link Result} if any, null otherwise.
   * @throws MoodstocksError if any error occured. Some possible errors:
   *                         {@link MoodstocksError.Code#EMPTY},
   *                         {@link MoodstocksError.Code#MISUSE},
   *                         {@link MoodstocksError.Code#IMG}
   */
  public native Result search(Image qry, int extras)
      throws MoodstocksError;

  /**
   * Similar to {@link #search(Image,int)}, but with additional options.
   * @param qry the query {@link Image}
   * @param extras the bitwise-OR combination of {@link Result.Extra} to compute
   * @param options the bitwise-OR combination of {@link Scanner.Flags} options to use.
   * @return the {@link Result} if any, null otherwise.
   * @throws MoodstocksError if any error occured. Some possible errors:
   *                         {@link MoodstocksError.Code#EMPTY},
   *                         {@link MoodstocksError.Code#MISUSE},
   *                         {@link MoodstocksError.Code#IMG}
   */
  public native Result search2(Image qry, int extras, int options)
      throws MoodstocksError;

  /**
   * Performs barcode decoding.
   * @param qry the query {@link Image}
   * @param formats the bitwise-OR combination of {@link Result.Type} defining the
   *                formats to try decoding.
   * @param extras  {@link Result.Extra#CORNERS} if you want the barcode corners to
   *                be computed, {@link Result.Extra#NONE} otherwise.
   * @return the decoded {@link Result} if any, null otherwise.
   * @throws MoodstocksError if any error occured.
   */
  public native Result decode(Image qry, int formats, int extras)
      throws MoodstocksError;

  /**
   * Checks if the query image matches the given Result from the local database.
   * <p>
   * Use a bitwise-OR combination of flags among {@link Result.Extra} to get
   * the corners, homograpghy and/or dimensions of the matched image, if any.
   * @param qry the query {@link Image}
   * @param ref the {@link Result} containing the ID of the reference to match against.
   * @param extras the bitwise-OR combination of {@link Result.Extra} to compute
   * @return the {@link Result} (with updated geometrical information if requested with extras)
   *         if any, null otherwise.
   * @throws MoodstocksError if any error occured. Some possible errors:
   *                         {@link MoodstocksError.Code#EMPTY},
   *                         {@link MoodstocksError.Code#MISUSE},
   *                         {@link MoodstocksError.Code#IMG},
   *                         {@link MoodstocksError.Code#NOREC}
   */
  public native Result match(Image qry, Result ref, int extras)
      throws MoodstocksError;

  /**
   * Similar to {@link #match(Image,Result,int)}, but with additonal options.
   * @param qry the query {@link Image}
   * @param ref the {@link Result} containing the ID of the reference to match against.
   * @param extras the bitwise-OR combination of {@link Result.Extra} to compute.
   * @param options the bitwise-OR combination of {@link Scanner.Flags} options to use.
   * @return the {@link Result} (with updated geometrical information if requested with extras)
   *         if any, null otherwise.
   * @throws MoodstocksError if any error occured. Some possible errors:
   *                         {@link MoodstocksError.Code#EMPTY},
   *                         {@link MoodstocksError.Code#MISUSE},
   *                         {@link MoodstocksError.Code#IMG},
   *                         {@link MoodstocksError.Code#NOREC}
   */
  public native Result match2(Image qry, Result ref, int extras, int options)
      throws MoodstocksError;

  /**
   * Checks that the current device is compatible with the Moodstocks SDK.
   * <p>
   * <b> You should not let the users access the scanner if this function returns false,
   * as the SDK will most probably crash!</b>
   * <p>
   * A device is compatible if it runs Android 2.3+ and features an ARM or x86 CPU.
   * @return true if compatible, false otherwise.
   */
  public static boolean isCompatible() {
    return Loader.isCompatible();
  }

  /**
   * Native function performing class-level initialization
   */
  private static native void init();

  /**
   * Native function initializing a Scanner instance.
   * @throws MoodstocksError if any error occurred.
   */
  private native void initialize()
      throws MoodstocksError;

  /**
   * Native function destroying a Scanner instance.
   */
  private native void destruct();

  /**
   * Native function opening the scanner.
   * @param path the path to the database file
   * @param key the API key
   * @param secret the API secret
   * @throws MoodstocksError if any error occurs.
   */
  private native void open(String path, String key, String secret)
      throws MoodstocksError;

  /**
   * Native function removing the scanner database file
   * @param path  the path to the database file
   * @throws MoodstocksError if any error occurred.
   */
  private static native void clean(String path)
      throws MoodstocksError;

}
