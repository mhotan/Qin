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

import com.moodstocks.android.core.CameraManager;
import com.moodstocks.android.core.OrientationListener;
import com.moodstocks.android.core.CameraManager.CameraError;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.SurfaceView;

/**
 * High-level helper class in charge of:
 * <ul>
 * <li>
 * Receiving and displaying the camera frames,
 * </li>
 * <li>
 * Processing them according to the set options,
 * </li>
 * <li>
 * Notifying the caller when a result is found or an error occurs.
 * </li>
 * </ul>
 */
public class ScannerSession implements CameraManager.Listener {
  private Activity parent;
  private Scanner scanner = null;
  private WeakReference<Listener> listener;
  private WorkerThread worker;
  private SessionHandler handler;

  private boolean front_facing = false;
  private int frame_width;
  private int frame_height;
  private boolean running = false;
  private boolean snap = false;

  /** default options: cache image recognition only. */
  protected int options = Result.Type.IMAGE;
  /** default geometrical extras: None */
  protected int extras = Result.Extra.NONE;

  /**
   * If true, the camera frames are re-oriented according to the current device orientation, e.g if the
   * device is in landscape mode, the frame is rotated by 90 degrees to make sure the scanner input
   * image reflects what the user sees on screen.
   * <p>
   * If false (default), the device orientation is ignored, and thus the frame is *always* handled with
   * portrait orientation.
   */
  public boolean useDeviceOrientation = false;

  /**
   * If true, "partial matching" will be disabled.
   * <p>
   * Set this flag to `true` to avoid returning false positive results due to partial
   * matching, for example if several of the indexed images share the exact same
   * logo.
   * By default, this value is set to `false`.
   */
  public boolean noPartialMatching = false;

  /**
   * The flag to enable recognition on smaller or farther images.
   *
   * Set this flag to `true` to boost the scale invariance of the Scanner, in order
   * to be able to recognize smaller images or images held farther from the phone
   * than in default mode.
   *
   * Slightly slower than the default mode.
   *
   * By default, this value is set to `false`.
   */
  public boolean smallTargetSupport = false;

  /**
   * Interface that must be implemented to receive callbacks from the ScannerSession.
   * <p>
   * Note that it extends the {@link Scanner.ApiSearchListener} interface.
   */
  public static interface Listener extends Scanner.ApiSearchListener {
    /**
     * Notifies the listener that a scan has ended.
     * @param result the {@link Result} if any, null otherwise.
     */
    public void onScanComplete(Result result);
    /**
     * Notified the listener that a scan has failed.
     * @param error the {@link MoodstocksError} that caused the scan to fail.
     */
    public void onScanFailed(MoodstocksError error);
  }

  /**
   * Constructor.
   * @param parent the caller {@link Activity}
   * @param listener the {@link Listener} to notify
   * @param preview the {@link SurfaceView} on which to display the camera preview.
   * @throws MoodstocksError if any error occurred.
   */
  public ScannerSession(Activity parent, Listener listener, SurfaceView preview) throws MoodstocksError {
    this.listener = new WeakReference<Listener>(listener);
    this.scanner = Scanner.get();
    this.parent = parent;
    this.worker = new WorkerThread();
    this.handler = new SessionHandler(this);
    OrientationListener.init(parent);
    OrientationListener.get().enable();
    CameraManager.get().start(parent, this, preview);
    worker.start();
  }

  /**
   * Sets the operations you want the scanner to perform.
   * @param options the list of bitwise-OR separated {@link Result.Type} flags
   *                specifying the operation to perform among image recognition
   *                and various formats of barcode decoding.
   */
  public void setOptions(int options) {
    this.options = options;
  }

  /**
   * Sets the extra geometrical information to compute when a result is found.
   * @param extras the list of bitwise-OR separated {@link Result.Extra} flags
   *               specifying which geometrical information to compute.
   */
  public void setExtras(int extras) {
    this.extras = extras;
  }

  /**
   * Starts or restarts scanning the camera frames.
   * @return false if the session was already running.
   */
  public boolean resume() {
    if (!running) {
      worker.reset();
      running = true;
      CameraManager.get().requestNewFrame();
      return true;
    }
    return false;
  }

  /**
   * Stops scanning the camera frames.
   * @return false if the session was already paused.
   */
  public boolean pause() {
    if (running) {
      running = false;
      return true;
    }
    return false;
  }

  /**
   * Closes the ScannerSession.
   * <p>
   * Must be called before exiting the enclosing {@link Activity}.
   */
  public void close() {
    pause();
    cancel();
    OrientationListener.get().disable();
    CameraManager.get().stop();
    finishWorker(500L);
  }

  /**
   * Launches a remote image search on the Moodstocks API.
   * @return false if the operation could not be performed, either because the
   *         session is currently paused, or because a previous call to this
   *         method has not ended yet.
   */
  public boolean snap() {
    if (running && !snap) {
      snap = true;
      return true;
    }
    return false;
  }

  /**
   * Cancels any running API search launched using {@link #snap()}.
   * @return false if the operation could not be performed, either because the
   *         session is currently paused, or because there is no API Search
   *         currently running.
   */
  public boolean cancel() {
    scanner.apiSearchCancel();
    if (running && snap) {
      CameraManager.get().requestNewFrame();
      snap = false;
      return true;
    }
    snap = false;
    return false;
  }

  /**
   * Closes the worker thread.
   * @param t the time in milliseconds allowed for the thread to end.
   */
  private void finishWorker(long t) {
    worker.getHandler().obtainMessage(MsgCode.QUIT).sendToTarget();
    try {
      worker.join(t);
    } catch (InterruptedException e) {

    }
  }

  /**
   * <i>{@link com.moodstocks.android.core.CameraManager.Listener} implementation used to receive information on the camera in use.</i>
   */
  @Override
  public void onPreviewInfoFound(int w, int h, boolean front_facing) {
    this.frame_width = w;
    this.frame_height = h;
    this.front_facing = front_facing;
  }

  /**
   * <i>{@link com.moodstocks.android.core.CameraManager.Listener} implementation used to receive critical errors from the camera.</i>
   * <p>
   * This callback is here for the rare cases where the camera is not available.
   * Most often, this is caused by a past crash in another application (sometimes
   * the Android Camera app itself) that could not release the camera correctly.
   * In such a case, the only fix is to reboot the device: we inform the user and
   * exit the app.
   */
  @Override
  public void onCameraOpenFailed(int e) {
    AlertDialog.Builder builder = new AlertDialog.Builder(parent);
    builder.setCancelable(false);
    builder.setTitle("Camera Unavailable!");
    if (e == CameraError.NO_CAMERA)
      builder.setMessage("There seem to be no camera on your device.");
    else if (e == CameraError.OPEN_ERROR)
      builder.setMessage("If this problem persists, please reboot your device in order to fix it.");
    builder.setNeutralButton("Quit", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        parent.finish();
      }
    });
    builder.show();
  }

  /**
   * <i>{@link com.moodstocks.android.core.CameraManager.Listener} implementation used to receive the camera frames</i>.
   */
  @Override
  public void onPreviewFrame(byte[] data, Camera camera) {
    if (running) {
      if (snap) {
        if (CameraManager.get().isFocussed()) {
          worker.getHandler().obtainMessage(MsgCode.SNAP, data).sendToTarget();
        }
        else {
          CameraManager.get().requestFocus();
          CameraManager.get().requestNewFrame();
        }
      }
      else {
        worker.getHandler().obtainMessage(MsgCode.SCAN, data).sendToTarget();
      }
    }
  }

  /**
   * <i>Message passing utility class</i>
   */
  private static class SessionHandler extends Handler {
    private WeakReference<ScannerSession> s = null;

    public SessionHandler(ScannerSession s) {
      this.s = new WeakReference<ScannerSession>(s);
    }

    public void handleMessage(Message msg) {
      ScannerSession session = s.get();
      if (session != null) {
        Listener l = session.listener.get();
        boolean newFrame = true;

        switch(msg.what) {

          case MsgCode.SUCCESS:
            if (l != null)
              l.onScanComplete((Result)msg.obj);
            break;

          case MsgCode.FAILED:
            if (l != null)
              l.onScanFailed((MoodstocksError)msg.obj);
            break;

          case MsgCode.API_START:
            if (l != null)
              l.onApiSearchStart();
            newFrame = false;
            break;

          case MsgCode.API_SUCCESS:
            session.snap = false;
            if (l != null)
              l.onApiSearchComplete((Result)msg.obj);
            break;

          case MsgCode.API_FAILED:
            session.snap = false;
            MoodstocksError error = (MoodstocksError)msg.obj;
            if (error.getErrorCode() != MoodstocksError.Code.ABORT && l != null)
              l.onApiSearchFailed(error);
            break;

          default:
            break;

        }

        if (newFrame && session.running)
          CameraManager.get().requestNewFrame();
      }
    }
  }

  /**
   * <i>Background thread processing the camera frames.</i>
   */
  private class WorkerThread extends Thread implements Scanner.ApiSearchListener {

    private Handler w_handler;
    // locking values:
    private Result _result = null;
    private int _losts = 0;

    @Override
    public void run() {
      Looper.prepare();
      w_handler = new WorkerHandler(this);
      Looper.loop();
    }

    private Handler getHandler() {
      return w_handler;
    }

    private void reset() {
      _result = null;
      _losts = 0;
    }

    private void quit() {
      Looper.myLooper().quit();
    }

    private void scan(byte[] data) {
      Result result = null;
      MoodstocksError error = null;
      int ori = OrientationListener.Orientation.UP;
      try {
        if (useDeviceOrientation)
          ori = OrientationListener.get().getOrientation();
        if (front_facing)
          ori = (6-ori)%4;
        result = scan(new Image(data, frame_width, frame_height, frame_width, ori));
      } catch (MoodstocksError e) {
        error = e;
      }
      if (error != null) {
        handler.obtainMessage(MsgCode.FAILED, error).sendToTarget();
      }
      else {
        if (result != null && (extras & Result.Extra.IMAGE) != 0) {
          Bitmap bmp = Image.bufferToBitmap(data, frame_width, frame_height, frame_width);
          result.setImage(bmp, ori);
        }
        handler.obtainMessage(MsgCode.SUCCESS, result).sendToTarget();
      }
    }

    /* Performs a search in the local cache, as well as
     * barcode decoding, according to the options previously set.
     */
    private Result scan(Image qry)
        throws MoodstocksError {

      qry.retain();
      Result result = null;
      int flags = (noPartialMatching ? Scanner.Flags.NOPARTIAL : 0) |
                  (smallTargetSupport ? Scanner.Flags.SMALLTARGET : 0);
      //----------
      // LOCKING
      //----------
      try {
        Result rlock = null;
        boolean lock = false;
        if (_result != null && _losts < 2) {
          int found = 0;
          switch (_result.getType()) {
            case Result.Type.IMAGE:
              if (flags != Scanner.Flags.DEFAULT)
                rlock = scanner.match2(qry, _result, extras, flags);
              else
                rlock = scanner.match(qry, _result, extras);
              found = 1;
              break;
            case Result.Type.QRCODE:
              rlock = scanner.decode(qry, Result.Type.QRCODE, extras);
              found = 1;
              break;
            case Result.Type.DATAMATRIX:
              rlock = scanner.decode(qry, Result.Type.DATAMATRIX, extras);
              found = 1;
              break;
            default:
              break;
          }

          if (found == 1) {
            if (rlock != null) {
              found = rlock.getValue().equals(_result.getValue()) ? 1 : -1;
            }
            else {
              found = -1;
            }
          }


          if (found == 1) {
            lock = true;
            _losts = 0;
          }
          else if (found == -1) {
            _losts++;
            lock = (_losts >= 2) ? false : true;
          }
        }
        if (lock) {
          result = rlock;
        }
      } catch (MoodstocksError e) {
        e.log();
      }

      //---------------
      // IMAGE SEARCH
      //---------------
      try {
        if (result == null && ((options & Result.Type.IMAGE) != 0)) {
          if (flags != Scanner.Flags.DEFAULT)
            result = scanner.search2(qry, extras, flags);
          else
            result = scanner.search(qry, extras);
          if (result != null) {
            _losts = 0;
          }
        }
      } catch (MoodstocksError e) {
        if (e.getErrorCode() != MoodstocksError.Code.EMPTY)
          throw e;
      }


      //-------------------
      // BARCODE DECODING
      //-------------------
      if (result == null &&
         ( (options & (Result.Type.QRCODE|Result.Type.EAN13|
                       Result.Type.EAN8|Result.Type.DATAMATRIX) ) != 0)) {
        result = scanner.decode(qry, options, extras);
        if (result != null) {
          _losts = 0;
        }
      }

      //----------------
      // Locking update
      //---------------
      _result = result;

      qry.release();
      return result;
    }

    private void snap(byte[] data) {
      scanner.apiSearch(this, new Image(data, frame_width, frame_height, frame_width, OrientationListener.Orientation.NONE));
    }

    @Override
    public void onApiSearchStart() {
      handler.obtainMessage(MsgCode.API_START).sendToTarget();
    }

    @Override
    public void onApiSearchComplete(Result result) {
      handler.obtainMessage(MsgCode.API_SUCCESS, result).sendToTarget();
    }

    @Override
    public void onApiSearchFailed(MoodstocksError e) {
      handler.obtainMessage(MsgCode.API_FAILED, e).sendToTarget();
    }

  }

  /**
   * <i>Internal message passing utility</i>
   */
  private static class WorkerHandler extends Handler {

    private final WeakReference<WorkerThread> worker;

    private WorkerHandler(WorkerThread worker) {
      super();
      this.worker = new WeakReference<WorkerThread>(worker);
    }

    @Override
    public void handleMessage(Message msg) {

      WorkerThread w = worker.get();

      if (w != null) {
        switch(msg.what) {

          case MsgCode.SCAN:
            w.scan((byte[])msg.obj);
            break;

          case MsgCode.SNAP:
            w.snap((byte[])msg.obj);
            break;

          case MsgCode.QUIT:
            w.quit();
            break;

          default:
            break;

        }
      }
    }
  }

  /**
   * <i>Internal message passing codes</i>
   */
  private static final class MsgCode {
    public static final int SCAN = 0;
    public static final int SNAP = 1;
    public static final int QUIT = 2;
    public static final int SUCCESS = 3;
    public static final int FAILED = 4;
    public static final int API_SUCCESS = 5;
    public static final int API_FAILED = 6;
    public static final int API_START = 7;
  }

}
