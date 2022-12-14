package com.divtec.blatnoa.scannerqr;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

public class QrScanner implements ImageAnalysis.Analyzer {

    public static class QrResult {
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

    private final BarcodeScanner scanner;

    private QrResult result = new QrResult();
    private Context context;

    BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE).build();


    public QrScanner(Context context) {
        scanner = BarcodeScanning.getClient(options);
        this.context = context;
    }

    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {
        scanImage(imageProxy);
    }

    /**
     * Scan the image in the given image proxy
     * @param imageProxy The image proxy
     */
    private void scanImage(ImageProxy imageProxy) {
        // Get image from proxy
        @OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
        Image image = imageProxy.getImage();

        if (image != null) { // If image is not null
            // Create input image
            InputImage inputImage = InputImage.fromMediaImage(image, imageProxy.getImageInfo().getRotationDegrees());

            // Scan the image
            scanner.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                        @Override
                        public void onSuccess(List<Barcode> barcodes) { // When the scan is successful
                            if (barcodes.size() > 0) { // If barcodes have been found
                                // Get the first barcode
                                result = convertBarcode(barcodes.get(0));
                                OpenDialog();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) { // When the scan fails
                            // Log the error
                            Log.d("QrScanner", "Error scanning image");
                            Log.e("QrScanner", e.getMessage());
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<Barcode>> task) { // When the scan is complete
                            // Close the image proxy
                            imageProxy.close();
                        }
                    });
        }
    }

    /**
     * Convert a barcode to a QrResult
     * @param barcode The barcode
     * @return The QrResult
     */
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

    /**
     * Get the result of the last scan containing a barcode
     * @return The latest result
     */
    public QrResult getResult() {
        return result;
    }

    private void OpenDialog() {
        Dialog test = new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_message + result.value)
                .setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Respond to positive button press
                    }
                })
                .show();
    }
}
