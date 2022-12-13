package com.divtec.blatnoa.scannerqr;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.common.Barcode;

import java.util.zip.Inflater;

public class QrScanner extends AppCompatActivity {
    private BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE).build();

    private CameraView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cameraView = findViewById(R.id.cameraView);

    }
}