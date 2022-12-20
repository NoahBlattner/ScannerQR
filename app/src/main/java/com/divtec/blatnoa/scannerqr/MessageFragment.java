package com.divtec.blatnoa.scannerqr;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
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
        // Get the bundle from the fragment manager
        getParentFragmentManager().setFragmentResultListener("latLng", this,
            (requestKey, result) -> {
                latLng = result;
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

        // Set the default message
        message.setText("Join me at: "
                + latLng.getDouble("latitude") + ", "
                + latLng.getDouble("longitude"));

        // Set the onClickListener for the button
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPhoneNum(phoneNum.getText().toString())) {
                    // TODO send the message by SMS
                } else {
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
            }
        });
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
}