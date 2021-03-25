package com.example.phaze.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

public class SignUpActivity extends AppCompatActivity {

    public CardView mSignup;
    private EditText mName, mPassword;

    private Response response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // This sets up the back button and creates a larger press detection area
        ImageView backButton = findViewById(R.id.backbutton);
        View parent = findViewById(R.id.top);
        parent.post(new Runnable() {
            public void run() {
                // Creates a delegate around the backbutton
                Rect delegateArea = new Rect();
                ImageView delegate = backButton;
                delegate.getHitRect(delegateArea);
                delegateArea.top -= 50;
                delegateArea.bottom += 50;
                delegateArea.left -= 50;
                delegateArea.right += 50;
                TouchDelegate expandedArea = new TouchDelegate(delegateArea, delegate);

                if (View.class.isInstance(delegate.getParent())) {
                    ((View) delegate.getParent()).setTouchDelegate(expandedArea);
                }
            }
        });

        // Sets up back button click listener
        // Returns to previous page if pressed
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }});

        // Instantiates UI objects
        mSignup = findViewById(R.id.register);

        mName = findViewById(R.id.name);
        mPassword = findViewById(R.id.password);

        // Sets up click listener for sign up button
        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                // Begin registering account
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                                    register();
                                }});

                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // If register was successful
                if (response.code() == 200){

                    // Create a copy of username and password
                    User.username = mName.getText().toString();
                    User.username = mPassword.getText().toString();
                    try {

                        // Update user information
                        User.updateServer("overwrite");
                        User.putSaveLocal();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                    // Start Main Activity
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    return;
                }
                else {
                    // Display error message
                    Toast.makeText(getApplication(), "Sign in ERROR", Toast.LENGTH_SHORT).show();
                }
            }});
    }

    // Register account to database
    public void register()
    {
        String name = mName.getText().toString();
        String password = mPassword.getText().toString();

        OkHttpClient client = new OkHttpClient();

        // Send request to database
        RequestBody newFormBody = new FormBody.Builder()
                .add("Username", name)
                .add("Password", password)
                .build();
        Request getRequest = new Request.Builder()
                .url("http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/register")
                .post(newFormBody)
                .build();

        try {
            // Retrieve info
            response = client.newCall(getRequest).execute();

            response.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}