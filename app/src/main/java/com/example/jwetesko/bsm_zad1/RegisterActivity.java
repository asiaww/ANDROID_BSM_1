package com.example.jwetesko.bsm_zad1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    private Context context = this;
    public static SharedPreferences psp;
    private String MY_PREFS_FILE_NAME = "SUPER_EXTRA_SECRET_STORAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initUI();
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void initUI() {

        final Button registerBtn = (Button) findViewById(R.id.join_button);
        final Button goToLoginBtn = (Button) findViewById(R.id.go_to_login_button);
        final EditText passwordET = (EditText) findViewById(R.id.password);
        final EditText repeatPasswordET = (EditText) findViewById(R.id.repeat_password);
        final EditText messageET = (EditText) findViewById(R.id.message);
        final TextView warningTV = (TextView) findViewById(R.id.warning);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (passwordET.getText().toString().equals(repeatPasswordET.getText().toString())) {

                    psp = new PrivateSharedPreferences(
                            context, context.getSharedPreferences(MY_PREFS_FILE_NAME, Context.MODE_PRIVATE), passwordET.getText().toString());

                    psp.edit().putString("psw", passwordET.getText().toString()).apply();
                    psp.edit().putString("msg", messageET.getText().toString()).apply();

                    goToLogin();

                } else {
                    warningTV.setText("Passwords don't match");
                }
            }
        });

        goToLoginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToLogin();
            }
        });
    }
}
