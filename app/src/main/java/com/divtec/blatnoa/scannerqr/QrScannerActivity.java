package com.divtec.blatnoa.scannerqr;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Size;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.common.Barcode;

import java.net.URL;
import java.util.concurrent.Executor;

public class QrScannerActivity extends AppCompatActivity {

    /**
     * Enum for the different states of the camera.
     */
    private enum CameraState {
        UNINITIALIZED,
        RUNNING,
        PAUSED
    }

    /**
     * Constants
     */
    private final ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
            .setTargetResolution(new Size(1280,720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build();
    private final Preview preview = new Preview.Builder()
            .build();
    private final CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
    private final String GOOGLE_SEARCH = "https://www.google.com/search?q=";

    /**
     * Variables
     */
    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private QrScanner scanner;
    private ProcessCameraProvider cameraProvider;
    private CameraState state = CameraState.UNINITIALIZED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the preview view
        previewView = findViewById(R.id.previewView);

        // Set the scanner
        scanner = new QrScanner(this);

        // Request camera permissions
        checkForCameraPermission();

        // Initialize the camera provider future
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        // Add the camera provider listener
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();

                // Bind the provider to the preview
                bindPreview(cameraProvider);

                // Executor to run on a background thread
                Executor executor = new Executor() {
                    @Override
                    public void execute(@NonNull Runnable command) {
                        command.run();
                    }
                };

                // Run the image analysis on a background thread
                imageAnalysis.setAnalyzer(executor, scanner);

                // Bind the provider to the analysis
                cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageAnalysis);

                // Set the state to running
                state = CameraState.RUNNING;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        resumeCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();

        pauseCamera();
    }

    /**
     * Bind the camera provider to the preview view
     * @param cameraProvider the camera provider to bind
     */
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        // Set the surface provider
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Bind the provider to the preview
        cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview);
    }


    /**
     * Checks if the app has permission to access the camera.
     * If not, requests the permission.
     */
    private void checkForCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) { // If camera permission is not granted
            // Request camera permissions
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    /**
     * Ask permission to open the link from the QR code
     * @param qrCode the result of the QR code
     */
    public void openQrLink(Barcode qrCode) {
        pauseCamera();

        // Get if the qrcode contains coordinates
        boolean isLocation = isLocationQrCode(qrCode);

        String message;
        double latitude = 0;
        double longitude = 0;

        if (isLocation) { // If the qrcode contains coordinates
            message = getResources().getString(R.string.dialog_message_geo) + "\n";
            if (qrCode.getValueType() == Barcode.TYPE_GEO) { // If the qrcode is a geo qrcode
                message += qrCode.getGeoPoint().getLat() + ", " + qrCode.getGeoPoint().getLng();
                latitude = qrCode.getGeoPoint().getLat();
                longitude = qrCode.getGeoPoint().getLng();
            } else { // If the qrcode is a text qrcode
                message += qrCode.getRawValue();

                // Clean up and split string containing coordinates
                String[] numbers = qrCode.getRawValue().replaceAll(" ", "")
                        .replaceAll("[^0-9. ]", " ").trim().split(" ");
                latitude = Double.parseDouble(numbers[0]);
                longitude = Double.parseDouble(numbers[1]);
            }
        } else { // If the qrcode contains a link
            message = getResources().getString(R.string.dialog_message_URL) + "\n"
                    + qrCode.getRawValue();
        }

        // Create effectively final variables
        double finalLongitude = longitude;
        double finalLatitude = latitude;

        // Create the dialog
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isLocation) { // If the qrcode contains coordinates
                            // Open a map in the app
                            startMapsActivity(finalLatitude, finalLongitude);
                        } else {
                            // Try to open the qr code as a link
                            startBrowserActivity(qrCode.getRawValue());
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        resumeCamera();
                    }
                })
                .setCancelable(false)
                .show();
    }

    /**
     * Check if the QR code contains coordinates
     * @param qrCode the result of the QR code
     * @return true if the QR code contains coordinates
     */
    private boolean isLocationQrCode(Barcode qrCode) {
        switch (qrCode.getValueType()) {
            case Barcode.TYPE_GEO: // If the QR code is a geo code
                return true;
            case Barcode.TYPE_TEXT: // If the QR code is a text
                String text = qrCode.getRawValue();
                if (text.isEmpty()) { // If the text is empty
                    return false;
                }
                // Check if the text contains coordinates
                return text.matches("^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?)\\s*,\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$");
            default:
                return false;
        }
    }

    /**
     * Start the maps activity with a marker on the coordinates
     * @param longitude the longitude of the location
     * @param latitude the latitude of the location
     */
    private void startMapsActivity(double latitude, double longitude) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        startActivity(intent);
    }

    /**
     * Start the default browser with the link from the QR code
     * @param url the link from the QR code
     */
    private void startBrowserActivity(String url) {
        if (!URLUtil.isValidUrl(url)) {
            url = GOOGLE_SEARCH + url;
        }
        Uri uri = Uri.parse(url);

        // Start the default browser
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    /**
     * Pause the camera
     */
    private void pauseCamera() {
        if (state == CameraState.RUNNING) {
            cameraProvider.unbindAll();
            state = CameraState.PAUSED;
        }
    }

    /**
     * Resume the camera if it was paused
     */
    private void resumeCamera() {
        if (state == CameraState.PAUSED) {
            cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageAnalysis);
            cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview);
            state = CameraState.RUNNING;
        }
    }


}