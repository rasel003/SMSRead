package com.rasel.smsread;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

////sms consent api
//    implementation 'com.google.android.gms:play-services-auth:17.0.0'
//    implementation 'com.google.android.gms:play-services-auth-api-phone:17.1.0'

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.rasel.smsread.retrofitnetwork.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RESOLVE_HINT = 456;
    private static final String TAG = "rsl";
    private TextInputEditText etMobileNumber;
    private MaterialButton materialButtonSubmit;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etMobileNumber = findViewById(R.id.etMobileNumber);
        materialButtonSubmit = findViewById(R.id.btnSubmit);

        try {
            requestHint();
        } catch (IntentSender.SendIntentException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }
        materialButtonSubmit.setOnClickListener(this);
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

                    String mobileNumber = credential.getId().substring(3);

                    etMobileNumber.setText(mobileNumber);
                    etMobileNumber.setSelection(mobileNumber.length());
                }
            // ...
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnSubmit){
            hideKeyboard();
            username = etMobileNumber.getText().toString();
            if (validateUsername(username)) {

                // Start listening for SMS User Consent broadcasts from senderPhoneNumber
                // The Task<Void> will be successful if SmsRetriever was able to start
                // SMS User Consent, and will error if there was an error starting.
                Task<Void> task = SmsRetriever.getClient(MainActivity.this).startSmsUserConsent(null /* or sender number */);



                Call<ForgetPasswordResponse> call = RetrofitClient.getInstance().getApi().forgetpassword(username);
                call.enqueue(new Callback<ForgetPasswordResponse>() {
                    @Override
                    public void onResponse(Call<ForgetPasswordResponse> call, Response<ForgetPasswordResponse> response) {

                        if (!response.isSuccessful() && response.code() != 400) {
                            Log.d(TAG, "onResponse: ForgetPassword Code: " + response.code());
                            return;
                        }
                        ForgetPasswordResponse frgtPassResponse = response.body();
                        if (frgtPassResponse != null) {
                            Log.d(TAG, "onResponse: frgtPassResponse Is:-"+frgtPassResponse.toString());
                            Log.d(TAG, "onResponse: userdetais Is:-"+frgtPassResponse.getDetails());
                            if (frgtPassResponse.getDetails() != null && frgtPassResponse.getDetails().equalsIgnoreCase("an OTP sent to requested USER NO.")) {
                                Toast.makeText(MainActivity.this, "An OTP Request code is sent, Please check your mobile", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, ForgetPasswordConfirm.class));
                            } else {
                                Log.d(TAG, "onResponse: Forget password didn't match with any case");
                            }
                        } else {
                            Log.d(TAG, "Response body is null");
                            Toast.makeText(MainActivity.this, "Invalid Request", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ForgetPasswordResponse> call, Throwable t) {
                        Log.d(TAG, "onFailure: ForgetPassword Response Error " + t.getMessage());
                    }
                });
            }
        }
    }
    private void hideKeyboard() {

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        findViewById(R.id.mainLayout).requestFocus();
        View view = getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private boolean validateUsername(String username) {

        if (username.isEmpty()) {
            Toast.makeText(this, "Username is Empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (username.length() != 11) {
            Toast.makeText(this, "Invalid username", Toast.LENGTH_SHORT).show();
            return false;
        } else return true;
    }
}
