package com.example.jwetesko.bsm_zad1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private Context context = this;
    public static SharedPreferences psp;
    public static String MY_PREFS_FILE_NAME = "SUPER_EXTRA_SECRET_STORAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");
        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive","Logout in progress");
                finish();
            }
        }, intentFilter);
        setContentView(R.layout.activity_login);
        initUI();
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void initUI() {

        final Button loginBtn = (Button) findViewById(R.id.login_button);
        final EditText passwordET = (EditText) findViewById(R.id.password_login);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                psp = new PrivateSharedPreferences(
                        context, context.getSharedPreferences(MY_PREFS_FILE_NAME, Context.MODE_PRIVATE), passwordET.getText().toString());
                String password = psp.getString("psw", null);
                if (password != null) {
                    if (password.equals(passwordET.getText().toString())) {
                        goToMain();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Wrong password",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
