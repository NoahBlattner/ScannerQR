package com.divtec.blatnoa.scannerqr;

import android.content.Context;
import android.media.Image;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

public class QrScanner implements ImageAnalysis.Analyzer {

    public class QrResult {
        int type;
        private String title;
        private String value;

        public int getType() {
            return type;
        }

        public String getTitle() {
            return title;
        }

        public String getValue() {
            return value;
        }
    }

    private QrResult result = new QrResult();

    BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE).build();

    private BarcodeScanner scanner;

    public QrScanner() {
        scanner = BarcodeScanning.getClient(options);
    }

    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {
        @OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
        Image image = imageProxy.getImage();
        if (image != null) {
            InputImage inputImage = InputImage.fromMediaImage(image, imageProxy.getImageInfo().getRotationDegrees());
            scanImage(inputImage);
        }
    }

    private void scanImage(InputImage image) {
        Task<List<Barcode>> results = scanner.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        result = convertBarcode(barcodes.get(0));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("QrScanner", "Error scanning image");
                        Log.e("QrScanner", e.getMessage());
                    }
                });
    }

    private QrResult convertBarcode(Barcode barcode) {
        QrResult result = new QrResult();

        switch (barcode.getValueType()){
            case Barcode.TYPE_WIFI:
                result.type = Barcode.TYPE_WIFI;
                result.title = barcode.getWifi().getSsid();
                result.value = barcode.getWifi().getPassword();
                break;
            case Barcode.TYPE_URL:
                result.type = Barcode.TYPE_URL;
                result.title = barcode.getUrl().getTitle();
                result.value = barcode.getUrl().getUrl();
                break;
        }

        return result;
    }
}
