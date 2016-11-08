package com.example.jwetesko.bsm_zad1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChangeActivity extends AppCompatActivity {

    private Context context = this;

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
        setContentView(R.layout.activity_change);

        initUI();
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void goToStart() {
        startActivity(new Intent(this, LoginActivity.class));
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.package.ACTION_LOGOUT");
        sendBroadcast(broadcastIntent);
    }

    private void initUI() {
        final Button confirmChangeBtn = (Button) findViewById(R.id.confirm_change_button);
        final EditText newPasswordET = (EditText) findViewById(R.id.new_password);
        final EditText newRepeatPasswordET = (EditText) findViewById(R.id.new_repeat_password);
        final EditText newMessageET = (EditText) findViewById(R.id.new_message);

        confirmChangeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!(newPasswordET.getText().toString().equals(""))) {

                    if (newPasswordET.getText().toString().equals(newRepeatPasswordET.getText().toString())) {

                        String message = LoginActivity.psp.getString("msg", null);

                        LoginActivity.psp = new PrivateSharedPreferences(
                                context, context.getSharedPreferences(LoginActivity.MY_PREFS_FILE_NAME, Context.MODE_PRIVATE), newPasswordET.getText().toString());

                        LoginActivity.psp.edit().putString("psw", newPasswordET.getText().toString()).apply();
                        LoginActivity.psp.edit().putString("msg", message).apply();

                        if (!(newMessageET.getText().toString().equals("")))
                            LoginActivity.psp.edit().putString("msg", newMessageET.getText().toString()).apply();
                        goToMain();

                    } else Toast.makeText(ChangeActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (!(newMessageET.getText().toString().equals(""))) {
                        LoginActivity.psp.edit().putString("msg", newMessageET.getText().toString()).apply();
                        goToMain();
                    }
                    else Toast.makeText(ChangeActivity.this, "No changes made", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
