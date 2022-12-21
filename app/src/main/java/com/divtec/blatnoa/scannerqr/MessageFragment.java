package com.divtec.blatnoa.scannerqr;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MessageFragment extends Fragment {

    private EditText phoneNum;
    private EditText message;
    private Button btnSend;

    private Bundle latLng;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        latLng = getActivity().getIntent().getExtras();

        // Set a listener to get the new coordinates
        getParentFragmentManager().setFragmentResultListener("latLng", this,
            (requestKey, result) -> {
                if (message != null
                    && message.getText().toString().startsWith(getString(R.string.default_msg))) {
                    // Get the bundle from the fragment manager
                    latLng = result;
                    updateDefaultMessage();
                }
        });

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Get the components from the view
        phoneNum = view.findViewById(R.id.edit_phone);
        message = view.findViewById(R.id.edit_message);
        btnSend = view.findViewById(R.id.btn_send);

        // TODO Remove this
        phoneNum.setText("0766817585");

        // Set the default message
        updateDefaultMessage();

        // Set the onClickListener for the button
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPhoneNum(phoneNum.getText().toString())) {
                    sendSMS(phoneNum.getText().toString(), message.getText().toString());
                } else {
                    invalidPhoneNumDialog();
                }
            }
        });
    }

    private void updateDefaultMessage() {
        double roundedLatitude = roundTo(latLng.getDouble("latitude"), 4);
        double roundedLongitude = roundTo(latLng.getDouble("longitude"), 4);

        message.setText(getString(R.string.default_msg)
                + roundedLatitude + ", "
                + roundedLongitude);
    }

    /**
     * Checks if the phone number is valid
     * @param phoneNum the phone number to check
     * @return whether the phone number is valid or not
     */
    private boolean checkPhoneNum(String phoneNum) {
        // Check if the phone number is valid
        return PhoneNumberUtils.isGlobalPhoneNumber(phoneNum);
    }

    /**
     * Show a dialog informing the user that the phone number is invalid
     */
    private void invalidPhoneNumDialog() {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Invalid phone number")
                .setMessage("Please enter a valid phone number")
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        phoneNum.requestFocus();
                    }
                })
                .show();
    }

    /**
     * Send a message by SMS to a phone number
     * @param phoneNum the phone number to send the message to
     * @param message the message to send
     */
    private void sendSMS(String phoneNum, String message) {

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNum, null, message, null, null);
    }

    /**
     * Round a double to a certain number of decimals
     * @param number the number to round
     * @param decimals the number of decimals to round to
     * @return the rounded number
     */
    private double roundTo(double number,int decimals) {
        double multiplier = Math.pow(10, decimals);
        return Math.round(number * multiplier) / multiplier;
    }
}