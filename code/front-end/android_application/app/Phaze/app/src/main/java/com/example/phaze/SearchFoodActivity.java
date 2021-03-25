package com.example.phaze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.phaze.classes.Food;
import com.example.phaze.fragments.CameraFragment;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchFoodActivity extends AppCompatActivity {

    EditText input;
    TextView cancel;
    TextView loadMore;
    String query;

    RecyclerView recyclerView;
    MyFoodAdapter.RecyclerViewClickListener listener;

    RequestFood rf;

    List<Food> foodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);

        // Instantiates UI objects
        input = findViewById(R.id.inputfood);
        cancel = findViewById(R.id.cancel_button);
        loadMore = findViewById(R.id.load_more);

        recyclerView = findViewById(R.id.displayedfood);

        // Creates a request food object
        rf = new RequestFood();

        // Sets up the search bar feature
        input.setFocusableInTouchMode(true);
        input.requestFocus();
        input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If enter is pressed, then search food
                if (keyCode == 66){
                    query = "ingr=" + input.getText().toString();
                    hideKeyboard(SearchFoodActivity.this);
                    try {
                        getFood();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        // Sets cancel button on click listener
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Returns to camera fragment
                CameraFragment.clicked = false;
                onBackPressed();
            }
        });

    }

    // This hides the keyboard when enter is pressed
    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();

        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }

        // Hides the keyboard
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // Gets the food search results
    public void getFood() throws InterruptedException {
        if (query.length() > 0) {
            sendRequest();
            setOnClickListener();
            displayFood();

            // Sets up the load more button
            loadMore.setVisibility(View.VISIBLE);
            loadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rf.n += 10;
                    try {
                        sendRequest();
                        displayFood();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    // Send a search request to the food database
    public void sendRequest() throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    foodList = new ArrayList<>();
                    foodList = rf.Request(query);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NetworkErrorException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        thread.join();
    }

    // Displays the search results
    public void displayFood()
    {
        MyFoodAdapter myFoodAdapter = new MyFoodAdapter(this, foodList, listener);
        recyclerView.setAdapter(myFoodAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // Sets the on click listener for each food item
    public void setOnClickListener()
    {
        listener = new MyFoodAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {

                // Starts the Food Information Activity
                Intent intent = new Intent(SearchFoodActivity.this, FoodInformationActivity.class);
                intent.putExtra("Food", foodList.get(position));
                startActivity(intent);
            }
        };
    }
}