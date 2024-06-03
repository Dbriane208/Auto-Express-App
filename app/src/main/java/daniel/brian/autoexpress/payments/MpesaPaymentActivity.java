package daniel.brian.autoexpress.payments;

import static daniel.brian.autoexpress.payments.utils.Constants.CALLBACKURL;
import static daniel.brian.autoexpress.payments.utils.Constants.PASSKEY;
import static daniel.brian.autoexpress.payments.utils.Constants.TRANSACTION_TYPE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

import daniel.brian.autoexpress.R;
import daniel.brian.autoexpress.databinding.ActivityMpesaPaymentBinding;
import daniel.brian.autoexpress.payments.model.AccessToken;
import daniel.brian.autoexpress.payments.model.STKPush;
import daniel.brian.autoexpress.payments.services.DarajaApiClient;
import daniel.brian.autoexpress.payments.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MpesaPaymentActivity extends AppCompatActivity {
    ActivityMpesaPaymentBinding binding;
    private DarajaApiClient mApiClient;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMpesaPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        int totalPrice = intent.getIntExtra("totalPrice",0);

        progressDialog = new ProgressDialog(this);
        mApiClient = new DarajaApiClient();
        mApiClient.setIsDebug(true);

        getAccessToken();

        binding.btnPay.setOnClickListener(v -> {
            // accessing the buttons
            String till = Objects.requireNonNull(binding.businessCode.getText()).toString().trim();
            String phoneNumber = Objects.requireNonNull(binding.phoneNumber.getText()).toString().trim();
            performSTKPush(till, String.valueOf(totalPrice),phoneNumber);
        });
    }

    private void performSTKPush(String till, String amount, String phoneNumber) {
        progressDialog.setMessage("Processing your request");
        progressDialog.setTitle("Please Wait..");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        String timestamp = Utils.getTimestamp();
        STKPush stkPush = new STKPush(
                till,
                Utils.getPassword(till,PASSKEY, timestamp),
                timestamp,
                TRANSACTION_TYPE,
                amount,
                Utils.validatePhoneNumber(phoneNumber),
                till,
                Utils.validatePhoneNumber(phoneNumber),
                CALLBACKURL,
                "AutoExpress",
                "Payment"

        );
        mApiClient.setGetAccessToken(false);

        mApiClient.mpesaService().sendPush(stkPush).enqueue(new Callback<STKPush>() {
            @Override
            public void onResponse(Call<STKPush> call, Response<STKPush> response) {
                progressDialog.dismiss();
                try{
                    if(response.isSuccessful()){
                        Timber.d("Post Submitted to API. %s",response.body());
                    }else{
                        Timber.e("Response %s",response.errorBody().string());
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<STKPush> call, Throwable t) {

            }
        });
    }

    private void getAccessToken() {
        mApiClient.setGetAccessToken(true);

        mApiClient.mpesaService().getAccessToken().enqueue(new Callback<AccessToken>(){
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                if(response.isSuccessful()){
                    mApiClient.setAuthToken(response.body().accessToken);
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}