package com.divtec.blatnoa.scannerqr;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.SessionConfiguration;
import android.media.ImageReader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

public class CameraViewOLD extends SurfaceView implements SurfaceHolder.Callback2 {

    private static final String TAG = "CameraView";
    private ImageReader imageReader;
    private Surface previewSurface;
    private Surface imageSurface;
    private CameraDevice cameraDevice;
    private List<OutputConfiguration> targets;

    public CameraViewOLD(Context context, AttributeSet attrs) {
        super(context, attrs);

        getHolder().addCallback(this);
        imageReader = ImageReader.newInstance(640, 480, ImageFormat.JPEG, 1);

        previewSurface = getHolder().getSurface();
        imageSurface = imageReader.getSurface();

        OutputConfiguration previewOutput = new OutputConfiguration(previewSurface);
        OutputConfiguration imageOutput = new OutputConfiguration(imageSurface);
        targets = Arrays.asList(previewOutput, imageOutput);

        SessionConfiguration config = new SessionConfiguration(
                SessionConfiguration.SESSION_REGULAR,
                targets,
                null,
                new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                        try {
                            CaptureRequest.Builder captureRequest = cameraCaptureSession.getDevice().createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                            captureRequest.addTarget(previewSurface);                    captureRequest.addTarget(imageSurface);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                        Log.d(TAG, "Failed to configure camera");
                        Log.e(TAG, "Failed to configure camera");
                    }
                });

        try {
            cameraDevice.createCaptureSession(config);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceRedrawNeeded(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }
}
