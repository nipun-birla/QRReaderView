package com.nipunbirla.qrreaderview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;


/**
 * Created by Nipun on 1/13/2017.
 */

public class QRCodeReaderView extends TextureView
        implements TextureView.SurfaceTextureListener, Camera.PreviewCallback {

    private static final String TAG = QRCodeReaderView.class.getName();

    public interface OnQRCodeReadListener {

        void onQRCodeRead(String text, PointF[] points);
    }

    /**
     * QRReaderView Class which uses ZXING lib and let you easily integrate a QR decoder view.
     * Made some modifications in the QRCodeReaderView by David Lázaro.
     *
     * @author Nipun Birla
     */

    private OnQRCodeReadListener mOnQRCodeReadListener;


    private QRCodeReader mQRCodeReader;
    private int mPreviewWidth, mSurfaceWidth;
    private int mPreviewHeight, mSurfaceHeight;
    private CameraManager mCameraManager;
    private boolean mQrDecodingEnabled = true;
    private boolean mAdjustRatio = false;
    private DecodeFrameTask decodeFrameTask;
    private int mCameraId;

    public QRCodeReaderView(Context context) {
        this(context, null);
    }

    public QRCodeReaderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode()) {
            return;
        }
        //set back camera id by defaut
        mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        checkCamHardware();
    }

    private void checkCamHardware(){
        if (checkCameraHardware()) {
            mCameraManager = new CameraManager(getContext());
            mCameraManager.setPreviewCallback(this);

            this.setSurfaceTextureListener(this);
        } else {
            throw new RuntimeException("Error: Camera not found");
        }
    }

    /**
     * Set the callback to return decoding result
     *
     * @param onQRCodeReadListener the listener
     */
    public void setOnQRCodeReadListener(OnQRCodeReadListener onQRCodeReadListener) {
        mOnQRCodeReadListener = onQRCodeReadListener;
    }

    /**
     * Set QR decoding enabled/disabled.
     * default value is true
     *
     * @param qrDecodingEnabled decoding enabled/disabled.
     */
    public void setQRDecodingEnabled(boolean qrDecodingEnabled) {
        this.mQrDecodingEnabled = qrDecodingEnabled;
    }

    /**
     * Starts camera preview and decoding
     */
    public void startCamera() {

        //Check added here as surface is already created if initially app does not have camera permission but driver fails to open,
        //Once permission is granted, try open driver again on start camera
        if(!mCameraManager.isOpen()){
            try {
                mCameraManager.openDriver(getSurfaceTexture(), this.getWidth(), this.getHeight());
                mPreviewWidth = mCameraManager.getPreviewSize().x;
                mPreviewHeight = mCameraManager.getPreviewSize().y;
                mQRCodeReader = new QRCodeReader();
            } catch (IOException e) {
                Log.e(TAG, "Can not openDriver: " + e.getMessage());
                mCameraManager.closeDriver();
            }
        }

        mCameraManager.startPreview();
        setPreviewCameraId(mCameraId);
        mCameraManager.setPreviewCallback(this);
    }

    /**
     * Stop camera preview and decoding
     */
    public void stopCamera() {
        mCameraManager.stopPreview();
    }

    /**
     * Set Camera autofocus interval value
     * default value is 5000 ms.
     *
     * @param autofocusIntervalInMs autofocus interval value
     */
    public void setAutofocusInterval(long autofocusIntervalInMs) {
        if (mCameraManager != null) {
            mCameraManager.setAutofocusInterval(autofocusIntervalInMs);
        }
    }

    /**
     * Set Torch enabled/disabled.
     * default value is false
     *
     * @param enabled torch enabled/disabled.
     */
    public void setTorchEnabled(boolean enabled) {
        if (mCameraManager != null) {
            mCameraManager.setTorchEnabled(enabled);
        }
    }

    /**
     * Allows user to specify the camera ID, rather than determine
     * it automatically based on available cameras and their orientation.
     *
     * @param cameraId camera ID of the camera to use. A negative value means "no preference".
     */
    public void setPreviewCameraId(int cameraId) {
        mCameraId = cameraId;
        mCameraManager.setPreviewCameraId(cameraId);
    }

    /**
     * Camera preview from device back camera
     */
    public void setBackCamera() {
        mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        setPreviewCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    /**
     * Camera preview from device front camera
     */
    public void setFrontCamera() {
        mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        setPreviewCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    @Override public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (decodeFrameTask != null) {
            decodeFrameTask.cancel(true);
            decodeFrameTask = null;
        }
    }

    /****************************************************
     * SurfaceHolder.Callback,Camera.PreviewCallback
     ****************************************************/

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "surfaceCreated : w = " + width + " : h = " + height);

        try {
            // Indicate camera, our View dimensions
            mCameraManager.openDriver(surface, this.getWidth(), this.getHeight());
            mPreviewWidth = mCameraManager.getPreviewSize().x;
            mPreviewHeight = mCameraManager.getPreviewSize().y;
            mSurfaceWidth = height;
            mSurfaceHeight = width;
            mQRCodeReader = new QRCodeReader();

            adjustRatio();

            mCameraManager.startPreview();

        } catch (Exception e) {
            Log.e(TAG, "Can not openDriver: " + e.getMessage());
            mCameraManager.closeDriver();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.d(TAG, "surfaceDestroyed");

        mCameraManager.setPreviewCallback(null);
        mCameraManager.stopPreview();
        mCameraManager.closeDriver();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        Log.d(TAG, "surfaceChanged");

        if (surface == null) {
            Log.e(TAG, "Error: preview surface does not exist");
            return;
        }

        adjustRatio();

        mCameraManager.setPreviewCallback(this);

        if (mCameraManager.getPreviewSize() == null) {
            Log.e(TAG, "Error: preview size does not exist");

            try {
                // Indicate camera, our View dimensions
                mCameraManager.openDriver(surface, this.getWidth(), this.getHeight());
                mPreviewWidth = mCameraManager.getPreviewSize().x;
                mPreviewHeight = mCameraManager.getPreviewSize().y;
                mQRCodeReader = new QRCodeReader();

                mCameraManager.startPreview();

            } catch (Exception e) {
                Log.e(TAG, "Can not openDriver: " + e.getMessage());
                mCameraManager.closeDriver();
            }
        }
    }

    public void setAdjustRatio(boolean val){
        mAdjustRatio = val;
    }

    private void adjustRatio(){

        if(!mAdjustRatio) return;

        float requiredAR = (float) mPreviewWidth/(float) mPreviewHeight;
        float curAR = (float) mSurfaceWidth/(float) mSurfaceHeight;

        float ratio;

        if(curAR > requiredAR){
            ratio = requiredAR/curAR;
        } else {
            ratio = curAR/requiredAR;
        }

        // calculate transformation matrix
        Matrix matrix = new Matrix();

        matrix.setScale(ratio, 1);
        this.setTransform(matrix);
    }

    private Camera.Size getBestPreviewSize(int w, int h, Camera.Parameters parameters){
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    // Called when camera take a frame
    @Override public void onPreviewFrame(byte[] data, Camera camera) {
        if (!mQrDecodingEnabled || (decodeFrameTask != null
                && decodeFrameTask.getStatus() == AsyncTask.Status.RUNNING)) {
            return;
        }

        decodeFrameTask = new DecodeFrameTask(this);
        decodeFrameTask.execute(data);
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware() {
        if (getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else if (getContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            // this device has a front camera
            return true;
        } else if (getContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            // this device has any camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Fix for the camera Sensor on some devices (ex.: Nexus 5x)
     * http://developer.android.com/intl/pt-br/reference/android/hardware/Camera.html#setDisplayOrientation(int)
     */
    @SuppressWarnings("deprecation") private int getCameraDisplayOrientation() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.GINGERBREAD) {
            return 90;
        }

        Camera.CameraInfo info = new Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(mCameraManager.getPreviewCameraId(), info);
        WindowManager windowManager =
                (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    private static class DecodeFrameTask extends AsyncTask<byte[], Void, Result> {

        private final WeakReference<QRCodeReaderView> viewRef;

        public DecodeFrameTask(QRCodeReaderView view) {
            viewRef = new WeakReference<>(view);
        }

        @Override
        protected Result doInBackground(byte[]... params) {
            final QRCodeReaderView view = viewRef.get();
            if (view == null) {
                return null;
            }

            final PlanarYUVLuminanceSource source =
                    view.mCameraManager.buildLuminanceSource(params[0], view.mPreviewWidth,
                            view.mPreviewHeight);

            final HybridBinarizer hybBin = new HybridBinarizer(source);
            final BinaryBitmap bitmap = new BinaryBitmap(hybBin);

            try {
                return view.mQRCodeReader.decode(bitmap);
            } catch (ChecksumException e) {
                Log.d(TAG, "ChecksumException", e);
            } catch (NotFoundException e) {
                Log.d(TAG, "No QR Code found");
            } catch (FormatException e) {
                Log.d(TAG, "FormatException", e);
            } catch (Exception e) {
                Log.d(TAG, "Exception", e);
            } finally {
                view.mQRCodeReader.reset();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Result result) {
            super.onPostExecute(result);

            final QRCodeReaderView view = viewRef.get();

            // Notify we found a QRCode
            if (view != null && result != null && view.mOnQRCodeReadListener != null) {
                // Transform resultPoints to View coordinates
                final PointF[] transformedPoints =
                        transformToViewCoordinates(view, result.getResultPoints());
                view.mOnQRCodeReadListener.onQRCodeRead(result.getText(), transformedPoints);
            }
        }

        /**
         * Transform result to surfaceView coordinates
         * <p>
         * This method is needed because coordinates are given in landscape camera coordinates when
         * device is in portrait mode and different coordinates otherwise.
         *
         * @return a new PointF array with transformed points
         */
        private PointF[] transformToViewCoordinates(QRCodeReaderView view, ResultPoint[] resultPoints) {
            int orientation = view.getCameraDisplayOrientation();
            if (orientation == 90 || orientation == 270) {
                return transformToPortraitViewCoordinates(view, resultPoints);
            } else {
                return transformToLandscapeViewCoordinates(view, resultPoints);
            }
        }

        private PointF[] transformToLandscapeViewCoordinates(QRCodeReaderView view,
                                                             ResultPoint[] resultPoints) {
            PointF[] transformedPoints = new PointF[resultPoints.length];
            int index = 0;
            float origX = view.mCameraManager.getPreviewSize().x;
            float origY = view.mCameraManager.getPreviewSize().y;
            float scaleX = view.getWidth() / origX;
            float scaleY = view.getHeight() / origY;

            for (ResultPoint point : resultPoints) {
                PointF transformedPoint = new PointF(view.getWidth() - point.getX() * scaleX,
                        view.getHeight() - point.getY() * scaleY);
                if (view.mCameraManager.getPreviewCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    transformedPoint.x = view.getWidth() - transformedPoint.x;
                }
                transformedPoints[index] = transformedPoint;
                index++;
            }

            return transformedPoints;
        }

        private PointF[] transformToPortraitViewCoordinates(QRCodeReaderView view,
                                                            ResultPoint[] resultPoints) {
            PointF[] transformedPoints = new PointF[resultPoints.length];

            int index = 0;
            float previewX = view.mCameraManager.getPreviewSize().x;
            float previewY = view.mCameraManager.getPreviewSize().y;
            float scaleX = view.getWidth() / previewY;
            float scaleY = view.getHeight() / previewX;

            for (ResultPoint point : resultPoints) {
                PointF transformedPoint =
                        new PointF((previewY - point.getY()) * scaleX, point.getX() * scaleY);
                if (view.mCameraManager.getPreviewCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    transformedPoint.y = view.getHeight() - transformedPoint.y;
                }
                transformedPoints[index] = transformedPoint;
                index++;
            }
            return transformedPoints;
        }
    }
}
