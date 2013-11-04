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
import java.util.List;
import java.util.Set;

import com.moodstocks.android.MoodstocksError;
import com.moodstocks.android.Scanner;

import android.os.Handler;
import android.os.Message;

/**
 * Runnable class handling the Moodstocks SDK synchronization operation.
 */
public class Sync extends Handler implements Runnable {

  private static Set<Sync> pending = new HashSet<Sync>();
  private WeakReference<Scanner.SyncListener> listener;
  private List<WeakReference<Scanner.SyncListener>> extra;
  private boolean cancelled = false;

  /**
   * <i>Internal message passing codes</i>
   */
  private static final class MsgCode {
    private static final int START = 1;
    private static final int END = 2;
    private static final int PROGRESS = 3;
  }

  /**
   * Constructor
   * <p>
   * Creates a new {@link Sync} object to be executed in a separate thread.
   * Several listeners can be passed, which can be useful to implement UI progress indications.
   * @param listener  The main {@link com.moodstocks.android.Scanner.SyncListener} object to notify of the sync progression.
   * @param extra     A list of extra {@link com.moodstocks.android.Scanner.SyncListener} objects to notify of the sync progression.
   */
  public Sync(Scanner.SyncListener listener, List<WeakReference<Scanner.SyncListener>> extra) {
    super();
    this.extra = extra;
    Iterator<WeakReference<Scanner.SyncListener>> it = extra.iterator();
    while (it.hasNext()) {
      WeakReference<Scanner.SyncListener> l = it.next();
      if (l.get() == listener)
        listener = null;
      // clean dead references
      if (l.get() == null)
        it.remove();
    }
    this.listener = new WeakReference<Scanner.SyncListener>(listener);
    pending.add(this);
  }

  /**
   * Cancels the sync.
   */
  public void cancel() {
    this.cancelled = true;
  }

  /**
   * Cancels all pending or running Sync.
   */
  public static void cancelAll() {
    Iterator<Sync> it = pending.iterator();
    while(it.hasNext()) {
      it.next().cancel();
    }
  }

  /**
   * Checks whether a sync is currently running.
   * @return  true if a sync is currently running, false otherwise.
   */
  public static boolean isSyncing() {
    if (pending.isEmpty()) {
      return false;
    }
    return true;
  }

  /**
   * <i>Runnable method</i>
   */
  @Override
  public void run() {
    if (!cancelled) {
      startMessage();
      MoodstocksError err = null;
      try {
        sync(Scanner.get());
      } catch (MoodstocksError e) {
        err = e;
      }
      endMessage(err);
    }
  }

  /**
   * Used to send the {@code onSyncStart()} callback from the thread.
   */
  private void startMessage() {
    Message.obtain(this, MsgCode.START).sendToTarget();
  }

  /**
   * Used to send the {@code onSyncComplete()} and {@code onSyncFailed()} callbacks from the thread.
   * @param e  the error that caused the sync to fail, if any. null otherwise.
   */
  private void endMessage(MoodstocksError e) {
    Message.obtain(this, MsgCode.END, e).sendToTarget();
  }

  /**
   * Used for cancelling and for the {@code onSyncProgress()} callback.
   */
  private int progressMessage(int total, int current) {
    if (cancelled)
      return -1;
    else if (total != -1)
      Message.obtain(this, MsgCode.PROGRESS, total, current).sendToTarget();
    return 0;
  }

  /**
   * <i>Internal message passing method</i>
   */
  @Override
  public void handleMessage(Message msg) {
    switch (msg.what) {
      case MsgCode.START:
        start();
        break;
      case MsgCode.END:
        end((MoodstocksError)msg.obj);
        break;
      case MsgCode.PROGRESS:
        progress(msg.arg1, msg.arg2);
        break;
      default:
        break;
    }
  }

  /**
   * Sends the {@code onSyncStart()} callback to listeners.
   */
  private void start() {
    if (listener.get() != null && !cancelled)
      listener.get().onSyncStart();
    Iterator<WeakReference<Scanner.SyncListener>> it = extra.iterator();
    while (it.hasNext()) {
    WeakReference<Scanner.SyncListener> l = it.next();
      if (l.get() == null)
        it.remove();
      else if (!cancelled)
        l.get().onSyncStart();
    }
  }

  /**
   * Sends the {@code onSyncComplete()} or {@code onSyncFailed() callbacks to listeners.
   */
  private void end(MoodstocksError e) {
    pending.remove(this);
    if (e == null) {
      if (listener.get() != null && !cancelled)
        listener.get().onSyncComplete();
      Iterator<WeakReference<Scanner.SyncListener>> it = extra.iterator();
      while (it.hasNext()) {
      WeakReference<Scanner.SyncListener> l = it.next();
        if (l.get() == null)
          it.remove();
        else if (!cancelled)
          l.get().onSyncComplete();
      }
    }
    else {
      if (listener.get() != null && !cancelled)
        listener.get().onSyncFailed(e);
      Iterator<WeakReference<Scanner.SyncListener>> it = extra.iterator();
      while (it.hasNext()) {
      WeakReference<Scanner.SyncListener> l = it.next();
        if (l.get() == null)
          it.remove();
        else if (!cancelled)
          l.get().onSyncFailed(e);
      }
    }
  }

  /**
   * Sends the {@code onSyncProgress()} callback to listeners.
   */
  private void progress(int total, int current) {
    if (listener.get() != null && !cancelled)
      listener.get().onSyncProgress(total, current);
    Iterator<WeakReference<Scanner.SyncListener>> it = extra.iterator();
    while (it.hasNext()) {
    WeakReference<Scanner.SyncListener> l = it.next();
      if (l.get() == null)
        it.remove();
      else if (!cancelled)
        l.get().onSyncProgress(total, current);
    }
  }

  /**
   * Native method performing a <b>synchronous</b> synchronization
   * @param s the {@link Scanner} to synchronize.
   * @throws MoodstocksError  if any error occured.
   */
  private native void sync(Scanner s)
      throws MoodstocksError;

}
