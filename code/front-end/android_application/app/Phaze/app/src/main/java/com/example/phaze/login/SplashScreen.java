package com.example.phaze.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phaze.MainActivity;
import com.example.phaze.classes.User;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SplashScreen extends AppCompatActivity {

    public Response response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initiate User and set up HTTP client
        User.context = getApplicationContext();
        User.startClient();

        // Check if can auto login
        Boolean flag = User.autoLogin();

        if (flag){
            // Try auto login
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

            // If logged in successfully
            if (response.code() == 200) {
                try {
                    // Update user information
                    User.getServerInfo();
                    User.putSaveLocal();
                }
                catch (Exception e){
                    // If fails, get local saved data
                    e.printStackTrace();
//                    User.getSaveLocal();
                }

                // Start Main Activity
                Intent intent = new Intent(getApplication(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return;
            }
            else {
                // Go to default login screen
                defaultLogin();
            }
        }
        else {
            // Go to default login screen
            defaultLogin();
        }
    }

    // Tries to log in to User account
    public void login()
    {
        OkHttpClient client = new OkHttpClient();

        // Creates a request to the database
        RequestBody newFormBody = new FormBody.Builder()
                .add("Username", User.username)
                .add("Password", User.password)
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

    // Start the Login Activity
    public void defaultLogin()
    {
        Intent intent = new Intent(getApplication(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        return;
    }
}
