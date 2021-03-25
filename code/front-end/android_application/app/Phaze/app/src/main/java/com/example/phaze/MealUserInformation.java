package com.example.phaze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.phaze.classes.Food;
import com.example.phaze.classes.User;
import com.example.phaze.fragments.UserFragment;
import com.example.phaze.meals.Breakfast;
import com.example.phaze.meals.Dinner;
import com.example.phaze.meals.Lunch;
import com.example.phaze.meals.Snacks;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MealUserInformation extends AppCompatActivity {

    TextView title;

    RecyclerView recyclerView;
    MyEatenFoodAdapter.RecyclerViewClickListener listener;
    public static MyEatenFoodAdapter foodAdapter;

    static PieChart pieChart;

    static TextView legendProtein;
    static TextView legendCarbs;
    static TextView legendFat;

    public static TextView edit;
    public static CardView cardView;
    public static TextView delete;
    public static TextView foodRecorded;

    public static String selected;

    public static boolean updatedFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_user_information);

        // Instantiate UI objects
        edit = findViewById(R.id.edit);
        cardView = findViewById(R.id.cardview);
        delete = findViewById(R.id.delete);

        foodRecorded = findViewById(R.id.food_recorded);

        title = findViewById(R.id.title);

        setOnClickListener();
        recyclerView = findViewById(R.id.recyclerView);

        pieChart = findViewById(R.id.piechart);

        legendProtein = findViewById(R.id.protein);
        legendCarbs = findViewById(R.id.carbs);
        legendFat = findViewById(R.id.fat);

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
            }
        });

        getSelected();

    }

    // Retrieves the meal menu item selected in the User Fragment
    public void getSelected()
    {
        Intent intent = getIntent();
        selected = intent.getStringExtra("Meal");
        displayMeals();
    }

    // Displays the nutritional value of the selected meal
    public void displayMeals()
    {
        switch (selected){
            case "Breakfast":
                displayBreakfast();
                break;
            case "Lunch":
                displayLunch();
                break;
            case "Dinner":
                displayDinner();
                break;
            case "Snacks":
                displaySnacks();
                break;
        }
    }

    // Displays the breakfast information
    public void displayBreakfast()
    {
        title.setText("Breakfast");

        // Displays the information on the pie chart
        displayPieChart(Breakfast.getMeals());
        setCenterText(Breakfast.getCalories());

        // Adds the information to the recycler view
        foodAdapter = new MyEatenFoodAdapter(this, Breakfast.getMeals(), listener);
        recyclerView.setAdapter(foodAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        isFoodRecorded(Breakfast.getMeals().size());
    }

    // Displays the lunch information
    public void displayLunch()
    {
        title.setText("Lunch");

        // Displays the information on the pie chart
        displayPieChart(Lunch.getMeals());
        setCenterText(Lunch.getCalories());

        // Adds the information to the recycler view
        foodAdapter = new MyEatenFoodAdapter(this, Lunch.getMeals(), listener);
        recyclerView.setAdapter(foodAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        isFoodRecorded(Lunch.getMeals().size());
    }

    // Displays the dinner information
    public void displayDinner()
    {
        title.setText("Dinner");

        // Displays the information on the pie chart
        displayPieChart(Dinner.getMeals());
        setCenterText(Dinner.getCalories());

        // Adds the information to the recycler view
        foodAdapter = new MyEatenFoodAdapter(this, Dinner.getMeals(), listener);
        recyclerView.setAdapter(foodAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        isFoodRecorded(Dinner.getMeals().size());
    }

    // Displays the snacks information
    public void displaySnacks()
    {
        title.setText("Snacks");

        // Displays the information on the pie chart
        displayPieChart(Snacks.getMeals());
        setCenterText(Snacks.getCalories());

        // Adds the information to the recycler view
        foodAdapter = new MyEatenFoodAdapter(this, Snacks.getMeals(), listener);
        recyclerView.setAdapter(foodAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        isFoodRecorded(Snacks.getMeals().size());
    }

    // Displays the text "There is no food recorded" if no food is recorded
    public static void isFoodRecorded(int size)
    {
        if (size > 0){
            foodRecorded.setVisibility(View.GONE);
            edit.setTextColor(Color.BLACK);
        }
        else {
            foodRecorded.setVisibility(View.VISIBLE);
            edit.setTextColor(Color.LTGRAY);
        }
    }

    // Sets the on click listener for the recycler view items
    public void setOnClickListener()
    {
        listener = new MyEatenFoodAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                if (edit.getText().equals("Cancel")){
                }
                return;
            }
        };
    }

    // Displays the meal information on the pie chart
    public static void displayPieChart(List<Food> meals)
    {

        // Sets parameters for the pie chart
        pieChart.setDrawEntryLabels(false);
        pieChart.setDrawMarkers(false);

        pieChart.setTouchEnabled(false);
        pieChart.setTransparentCircleAlpha(0);

        pieChart.setHoleRadius(80f);
        pieChart.setRotationEnabled(false);

        Description desc = new Description();
        desc.setText("");
        pieChart.setDescription(desc);

        pieChart.getLegend().setEnabled(false);

        double protein = 0.0;
        double carbs = 0.0;
        double fat = 0.0;

        // If there are mre than 1 foods
        if (meals.size() > 0) {

            for (Food food : meals) {
                protein += food.getProtein();
                carbs += food.getCarbs();
                fat += food.getFat();
            }

            protein = Math.round(protein * 10.0) / 10.0;
            carbs = Math.round(carbs * 10.0) / 10.0;
            fat = Math.round(fat * 10.0) / 10.0;

            List<PieEntry> macros = new ArrayList<>();
            macros.add(new PieEntry((float) protein, "Protein"));
            macros.add(new PieEntry((float) carbs, "Carbohydrates"));
            macros.add(new PieEntry((float) fat, "Fat"));

            PieDataSet pieDataSet = new PieDataSet(macros, "Macro nutrients");
            pieDataSet.setSliceSpace(4f);

            Integer[] colors = {ColorTemplate.rgb("F86285"), ColorTemplate.rgb("FFE570"), ColorTemplate.rgb("71D6CA")};
            pieDataSet.setColors(Arrays.asList(colors));

            PieData pieData = new PieData(pieDataSet);

            pieChart.setData(pieData);

            pieChart.getData().setDrawValues(false);
        }

        // If there are no foods, set the pie chart dataset to 0
        else {
            List<PieEntry> empty = new ArrayList<>();
            empty.add(new PieEntry(1f, "Empty"));

            PieDataSet pieDataSet = new PieDataSet(empty, "empty");
            pieDataSet.setColor(ColorTemplate.rgb("F4F4F8"));

            PieData pieData = new PieData(pieDataSet);

            pieChart.setData(pieData);

            pieChart.getData().setDrawValues(false);
        }

        // Creates a legend for each macro nutrient
        legendProtein.setText(createLegend("Protein", protein));
        legendCarbs.setText(createLegend("Carbohydrates", carbs));
        legendFat.setText(createLegend("Fat", fat));

        // animates the pie chart
        pieChart.animateXY(500, 500);
    }

    // Builds the string in the center of the pie chart
    public static void setCenterText(int value)
    {
        String total = "Total Calories\n";
        String calories = value + "";

        SpannableStringBuilder builder = new SpannableStringBuilder();

        SpannableString sb1 = new SpannableString(total);
        SpannableString sb2 = new SpannableString(calories);

        ForegroundColorSpan fcs1 = new ForegroundColorSpan(Color.GRAY);
        ForegroundColorSpan fcs2 = new ForegroundColorSpan(Color.BLACK);

        sb1.setSpan(fcs1, 0, total.length(), 0);
        builder.append(sb1);

        sb2.setSpan(new RelativeSizeSpan(1.8f), 0, calories.length(), 0);
        sb2.setSpan(fcs2, 0, calories.length(), 0);
        builder.append(sb2);

        pieChart.setCenterText(TextUtils.concat(builder));
        pieChart.setCenterTextSize(16f);
    }

    // Creates a legend using the macro nutrients in the pie chart
    public static CharSequence createLegend(String macro, double value)
    {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        SpannableString l = new SpannableString(macro + "\n");
        SpannableString v = new SpannableString(value + "g");

        l.setSpan(new ForegroundColorSpan(Color.GRAY), 0, macro.length(), 0);
        l.setSpan(new RelativeSizeSpan(0.8f), 0, macro.length(), 0);
        builder.append(l);

        v.setSpan(new ForegroundColorSpan(Color.BLACK), 0, String.valueOf(value).length() + 1, 0);
        v.setSpan(new RelativeSizeSpan(1.2f), 0, String.valueOf(value).length() + 1, 0);
        builder.append(v);

        return TextUtils.concat(builder);
    }

    // Reveals the delete button when edit food is pressed
    public static void editFood(){
        if (cardView.getVisibility() == View.GONE){
            cardView.setVisibility(View.VISIBLE);
        }
        else {
            cardView.setVisibility(View.GONE);
            if (updatedFlag) {
                updateMeals();
            }
        }
    }

    // Updates the meals when a meal is deleted
    public static void updateMeals(){
        List<Food> foodList = foodAdapter.data;
        switch (selected) {
                case "Breakfast":
                    Breakfast.updateMeals(foodList);
                    displayPieChart(Breakfast.getMeals());
                    setCenterText(Breakfast.getCalories());
                    isFoodRecorded(Breakfast.getMeals().size());
                    break;
                case "Lunch":
                    Lunch.updateMeals(foodList);
                    displayPieChart(Lunch.getMeals());
                    setCenterText(Lunch.getCalories());
                    isFoodRecorded(Lunch.getMeals().size());
                    break;
                case "Dinner":
                    Dinner.updateMeals(foodList);
                    displayPieChart(Dinner.getMeals());
                    setCenterText(Dinner.getCalories());
                    isFoodRecorded(Dinner.getMeals().size());
                    break;
                case "Snacks":
                    Snacks.updateMeals(foodList);
                    displayPieChart(Snacks.getMeals());
                    setCenterText(Snacks.getCalories());
                    isFoodRecorded(Snacks.getMeals().size());
                    break;
        }

        // Updates the User information and sends the new information to the database
        User.update();
        User.putSaveLocal();
        User.updateServer("overwrite");
        User.updateServerMeals("overwrite");

        // Updates the pie chart on the User Fragment
        UserFragment.createPieChart();
        UserFragment.updateChart(User.getCaloriesEaten(), User.getTotalCalories(), User.getTotalProtein(), User.getTotalCarbs(), User.getTotalFat());
        UserFragment.createLegend();

        UserFragment.updateBreakfast();
        UserFragment.updateLunch();
        UserFragment.updateDinner();
        UserFragment.updateSnacks();

        updatedFlag = false;
    }
}