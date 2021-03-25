package com.example.phaze.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.phaze.login.LoginActivity;
import com.example.phaze.R;
import com.example.phaze.classes.User;
import com.example.phaze.meals.Breakfast;
import com.example.phaze.meals.Dinner;
import com.example.phaze.meals.Lunch;
import com.example.phaze.meals.Snacks;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class SettingsFragment extends Fragment {
    public static SettingsFragment newInstance(){
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Sets up the delete account click listener
        CardView account = view.findViewById(R.id.account);
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If button is clicked, delete account
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        deleteAccount();
                    }});

                thread.start();
                try {
                    thread.join();
                    // Log out
                    LogOut();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Sets up privacy on click listener
        CardView privacy = view.findViewById(R.id.privacy);
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Links user to privacy policy
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.privacypolicies.com/live/1262b0a2-1e18-4c19-81f2-49d6c51b9f46"));
                startActivity(browserIntent);
                CameraFragment.turnOffCamera();
            }
        });

        // Sets up help on click listener
        CardView help = view.findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Links user to contact us web page
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://website-env.eba-3mhdshx7.eu-west-1.elasticbeanstalk.com/#contact"));
                startActivity(browserIntent);
                CameraFragment.turnOffCamera();
            }
        });

        // Sets up about on click listener
        CardView about = view.findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Links user with the about web page
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://website-env.eba-3mhdshx7.eu-west-1.elasticbeanstalk.com/#about"));
                startActivity(browserIntent);
                CameraFragment.turnOffCamera();
            }
        });

        // Sets up logout on click listener
        CardView mLogout = view.findViewById(R.id.logout);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Logs out
                LogOut();
            }
        });


        return view;
    }

    // Code for logging out
    private void LogOut() {
        CameraFragment.turnOffCamera();
        User.resetUser();
        Breakfast.reset();
        Lunch.reset();
        Dinner.reset();
        Snacks.reset();

        // Goes back to Login Activity
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        return;
    }

    // Deletes the account
    public void deleteAccount()
    {
        OkHttpClient client = new OkHttpClient();

        // Request information from database
        RequestBody newFormBody = new FormBody.Builder()
                .add("Username", User.username)
                .build();
        Request request = new Request.Builder()
                .url("http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/delete_account")
                .post(newFormBody)
                .build();

        try {
            // Send info
            Response response = client.newCall(request).execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
