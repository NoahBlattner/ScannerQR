package com.divtec.blatnoa.scannerqr;

import android.content.Context;
import android.graphics.Camera;
import android.hardware.camera2.CameraDevice;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class CameraView extends SurfaceView  {

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setBackgroundColor(0xFF000000);
    }

}
