package com.example.phaze.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.phaze.MainActivity;
import com.example.phaze.R;
import com.example.phaze.classes.User;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText mName, mPassword;
    private Response response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Instantiates UI objects
        CardView mLogin = findViewById(R.id.login);
        CardView mSignup = findViewById(R.id.register);

        mName = findViewById(R.id.name);
        mPassword = findViewById(R.id.password);

        // Set up login button click listener
        mLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // If login pressed, tries to login
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        login();
                    }});

                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // If login was successful
                if (response.code() == 200) {

                    // Create copy of username and password
                    User.username = mName.getText().toString();
                    User.password = mPassword.getText().toString();
                    try {
                        // Update user information
                        User.getServerInfo();
                        User.putSaveLocal();
                    }
                    catch (Exception e){
                        // If fails, get local saved data
                        e.printStackTrace();
//                        User.getSaveLocal();
                    }

                    // Begin Main Activity
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    return;
                }
                else{
                    // Show error message if login fails
                    Toast.makeText(LoginActivity.this, "Sign in ERROR", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set up register button click listener
        mSignup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // If pressed, start the Sign Up Activity
                Intent intent = new Intent(getApplication(), SignUpActivity.class);
                startActivity(intent);
                return;
            }
        });
    }

    // Tries to log in the user using the inputted name and password
    public void login()
    {
        String name = mName.getText().toString();
        String password = mPassword.getText().toString();

        OkHttpClient client = new OkHttpClient();

        // Send a request to the database using name and password
        RequestBody newFormBody = new FormBody.Builder()
                .add("Username", name)
                .add("Password", password)
                .build();
        Request getRequest = new Request.Builder()
                .url("http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/login")
                .post(newFormBody)
                .build();

        try {
            // Retrieve info
            response = client.newCall(getRequest).execute();
            System.out.println(response.code());

            response.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}