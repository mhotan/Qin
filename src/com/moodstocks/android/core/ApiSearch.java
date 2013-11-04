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

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.moodstocks.android.Image;
import com.moodstocks.android.MoodstocksError;
import com.moodstocks.android.Result;
import com.moodstocks.android.Scanner;

import android.os.Handler;
import android.os.Message;

/**
 * Runnable class handling the Moodstocks API search operation.
 */
public class ApiSearch extends Handler implements Runnable {

  private static Set<ApiSearch> pending = new HashSet<ApiSearch>();
  private WeakReference<Scanner.ApiSearchListener> listener;
  private Image qry;
  private boolean cancelled = false;

  private int ptr = 0;

  static {
    Loader.load();
    if (Scanner.isCompatible()) init();
  }

  /**
   * Internal message passing codes.
   */
  private static final class MsgCode {
    private static final int START = 1;
    private static final int END = 2;
  }

  /**
   * ApiSearch constructor
   * <p>
   * Creates a new ApiSearch object to be executed in a separate thread.
   * @param listener    The {@link com.moodstocks.android.Scanner.ApiSearchListener} object to notify.
   * @param qry         The {@link Image} on which to perform an ApiSearch.
   */
  public ApiSearch(Scanner.ApiSearchListener listener, Image qry) {
    super();
    this.listener = new WeakReference<Scanner.ApiSearchListener>(listener);
    this.qry = qry;
    qry.retain();
    pending.add(this);
  }
  
  /**
   * Cancels the ApiSearch
   */
  public void cancel() {
    cancelled = true;
    cancel_native();
  }
  
  /**
   * Cancels all pending or running ApiSearch objects
   */
  public static void cancelAll() {
    Iterator<ApiSearch> it = pending.iterator();
    while(it.hasNext()) {
      it.next().cancel();
    }
  }
  

  /**
   * <i>Runnable method</i>
   */
  @Override
  public void run() {
    if (!cancelled) {
      startMessage();
      Result r = null;
      MoodstocksError err = null;
      try {
        r = search(Scanner.get(), qry);
      } catch (MoodstocksError e) {
        err = e;
      }
      qry.release();
      endMessage(r, err);
    }
    else
      qry.release();
  }
  
  /**
   * Used to send the {@link com.moodstocks.android.Scanner.ApiSearchListener#onApiSearchStart()} callback from the thread.
   */
  private void startMessage() {
    Message.obtain(this, MsgCode.START).sendToTarget();
  }
  
  /**
   * Used to send the {@link com.moodstocks.android.Scanner.ApiSearchListener#onApiSearchComplete(Result)} 
   * and {@link com.moodstocks.android.Scanner.ApiSearchListener#onApiSearchFailed(MoodstocksError)} callbacks from the thread.
   * @param r   The {@link Result} found by the ApiSearch, if any.
   * @param e   The {@link MoodstocksError} returned by the ApiSearch, if any.
   */
  private void endMessage(Result r, MoodstocksError e) {
    Message.obtain(this, MsgCode.END, new ApiSearchMsg(r, e)).sendToTarget();
  }

  
  /**
   * <i>Internal message passing method.</i>
   */
  @Override
  public void handleMessage(Message msg) {
    Scanner.ApiSearchListener l = listener.get();
    switch (msg.what) {
      case MsgCode.START:
        if (l != null)
          l.onApiSearchStart();
        break;
      case MsgCode.END:
        pending.remove(this);
        ApiSearchMsg m = (ApiSearchMsg)msg.obj;
        if (l != null && !cancelled) {
          if (m.error == null)
            l.onApiSearchComplete(m.result);
          else
            l.onApiSearchFailed(m.error);
        }
        break;
      default:
        break;
    }
  }
  
  /**
   * An internal class used to pass ApiSearch return values. 
   */
  private class ApiSearchMsg {
    public Result result;
    public MoodstocksError error;

    private ApiSearchMsg(Result r, MoodstocksError e) {
      super();
      this.result = r;
      this.error = e;
    }
  }
  
  /**
   * Native function initializing an ApiSearch.
   */
  private static native void init();
  
  /**
   * Native function performing the actual ApiSearch synchronously.
   * <p>
   * This function is blocking and should be run asynchronously. 
   * @param s     The {@link Scanner} currently in use.
   * @param qry   The {@link Image} to use as a query.
   * @return      The found {@link Result} if any, null otherwise.
   * @throws MoodstocksError  if any error occurs during the operation.
   */
  private native Result search(Scanner s, Image qry)
      throws MoodstocksError;
  
  /**
   * Native function to cancel an ApiSearch.
   */
  private native void cancel_native();

}
