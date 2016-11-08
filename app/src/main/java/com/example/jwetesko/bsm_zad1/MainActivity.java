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
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void goToStart() {
        startActivity(new Intent(this, LoginActivity.class));
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.package.ACTION_LOGOUT");
        sendBroadcast(broadcastIntent);
    }

    private void goToChange() {
        startActivity(new Intent(this, ChangeActivity.class));
    }

    private void initUI() {

        TextView messageTV = (TextView)findViewById(R.id.message_main);
        Button changeBtn = (Button)findViewById(R.id.change_message_password_button);
        Button logOutBtn = (Button)findViewById(R.id.log_out_button);

        messageTV.setText(LoginActivity.psp.getString("msg", null));

        changeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToChange();
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToStart();
            }
        });
    }


}
