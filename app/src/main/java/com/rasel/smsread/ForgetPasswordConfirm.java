package com.rasel.smsread;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.material.textfield.TextInputEditText;

public class ForgetPasswordConfirm extends AppCompatActivity {

    private static final String TAG = "rsl";
    private Context context;
    private TextInputEditText mSysTextViewOTPCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_confirm);

        mSysTextViewOTPCode = findViewById(R.id.sysTextViewOTPCode);

         context = this;

        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsVerificationReceiver, intentFilter);
    }

    private static final int SMS_CONSENT_REQUEST = 2;  // Set to an unused request code

    private final BroadcastReceiver smsVerificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                Status smsRetrieverStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                switch (smsRetrieverStatus.getStatusCode()) {
                    case CommonStatusCodes.SUCCESS:
                        // Get consent intent
                        Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
                        try {
                            // Start activity to show consent dialog to user, activity must be started in
                            // 5 minutes, otherwise you'll receive another TIMEOUT intent
                            startActivityForResult(consentIntent, SMS_CONSENT_REQUEST);
                        } catch (ActivityNotFoundException e) {
                            // Handle the exception ...
                        }
                        break;
                    case CommonStatusCodes.TIMEOUT:
                        // Time out occurred, handle the error.
                        break;
                }
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // ...
            case SMS_CONSENT_REQUEST:
                if (resultCode == RESULT_OK) {
                    // Get SMS message content
                    String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                    // Extract one-time code from the message and complete verification
                    // `sms` contains the entire text of the SMS message, so you will need
                    // to parse the string.

                    //String oneTimeCode = parseOneTimeCode(message); // define this function

                    message = message.substring(message.indexOf(':')+1);
                    mSysTextViewOTPCode.setText(message.trim());
                    mSysTextViewOTPCode.setSelection(message.length()-1);
                    Log.d(TAG, "onActivityResult: message --"+message);

                    // send one time code to the server
                } else {
                    // Consent canceled, handle the error ...
                }
                break;
        }
    }
}
