package com.rasel.smsread;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

////sms consent api
//    implementation 'com.google.android.gms:play-services-auth:17.0.0'
//    implementation 'com.google.android.gms:play-services-auth-api-phone:17.1.0'

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.HintRequest;

public class MainActivity extends AppCompatActivity {

    private static final int RESOLVE_HINT = 456;
    private static final String TAG = "rsl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            requestHint();
        } catch (IntentSender.SendIntentException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }
    }

    private static final int CREDENTIAL_PICKER_REQUEST = 1;  // Set to an unused request code

    // Construct a request for phone numbers and show the picker
    private void requestHint() throws IntentSender.SendIntentException {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
        PendingIntent intent = Credentials.getClient(this).getHintPickerIntent(hintRequest);
        startIntentSenderForResult(intent.getIntentSender(), RESOLVE_HINT, null, 0, 0, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CREDENTIAL_PICKER_REQUEST:
                // Obtain the phone number from the result
                if (resultCode == RESULT_OK) {
                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    // credential.getId();  <-- will need to process phone number string
                    Log.d(TAG, "onActivityResult: "+credential.toString());
                }
                break;
            case RESOLVE_HINT:
                if (resultCode == RESULT_OK) {
                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    // credential.getId();  <-- will need to process phone number string
                    Log.d(TAG, "onActivityResult: "+credential.getId());
                }
            // ...
        }
    }
}
