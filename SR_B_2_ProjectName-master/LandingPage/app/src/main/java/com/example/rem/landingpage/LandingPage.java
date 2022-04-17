package com.example.rem.landingpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
/**
* Landing page is the initial page our application launches to.
* This page contains three buttons: "Voter Info", "login", and "Register" 
* that link to the main screens of our application
*/
public class LandingPage extends AppCompatActivity implements View.OnClickListener {

    Button search, login, register;
    EditText address;
    TextView appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        appName = (TextView) findViewById(R.id.AppName);

        //address = (EditText) findViewById(R.id.addressSearch);

        search = (Button) findViewById(R.id.searchButton);
        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent intent = new Intent(LandingPage.this, webviewMaps.class);
                startActivity(intent);

            }
        });

        login = (Button) findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LandingPage.this, LoginPage.class);
                startActivity(intent);
            }
        });

        register = (Button) findViewById(R.id.registerButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LandingPage.this, RegisterPage.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {

    }
}