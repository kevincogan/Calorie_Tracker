package com.example.phaze.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.phaze.MainActivity;
import com.example.phaze.MealUserInformation;
import com.example.phaze.R;
import com.example.phaze.classes.User;
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class UserFragment extends Fragment {

    static View view;

    static PieChart pieChart;
    static Integer[] colors = {ColorTemplate.rgb("F86285"), ColorTemplate.rgb("FFE570"), ColorTemplate.rgb("71D6CA")};

    TextView counter;
    TextView caloriesBurned;

    LinearLayout pedometer;
    ImageView icon;

    public static UserFragment newInstance(){
        UserFragment fragment = new UserFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_user, container, false);

        // Gets the steps from step counter sensor and displays them
        counter = view.findViewById(R.id.stepcounter);
        counter.setText(String.valueOf((int) MainActivity.getCounter()));

        caloriesBurned = view.findViewById(R.id.caloriesburned);

        // Instantiates UI objects
        icon = view.findViewById(R.id.icon);

        // Switches between displaying number of steps or calories burned
        pedometer = view.findViewById(R.id.pedometer);
        pedometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter.getVisibility() == View.VISIBLE){
                    counter.setVisibility(View.GONE);
                    getCaloriesBurned();
                    caloriesBurned.setVisibility(View.VISIBLE);
                    icon.setBackground(getResources().getDrawable(R.drawable.fire));
                }
                else {
                    caloriesBurned.setVisibility(View.GONE);
                    counter.setVisibility(View.VISIBLE);
                    icon.setBackground(getResources().getDrawable(R.drawable.footprints));
                }
            }
        });

        pieChart = view.findViewById(R.id.macroChart);

        // Displays the current date
        displayDate();

        // Creates the pie chart
        createPieChart();
        updateChart(User.getCaloriesEaten(), User.getTotalCalories(), User.getTotalProtein(), User.getTotalCarbs(), User.getTotalFat());
        createLegend();

        // Updates the meals eaten
        updateBreakfast();
        updateLunch();
        updateDinner();
        updateSnacks();

        setButtons();

        return view;
    }

    // Displays the current date
    public void displayDate()
    {
        TextView date = view.findViewById(R.id.date);

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

        date.setText(currentDate);
    }

    // Creates the pie chart
    public static void createPieChart()
    {
        pieChart.setUsePercentValues(false);

        String str1 = "Total calories \n";
        String str2 = User.getCaloriesEaten() + "\n";
        String str3 = "of " + User.getTotalCalories();

        // Builds the String inside the pie chart
        SpannableStringBuilder builder = new SpannableStringBuilder();

        SpannableString sb1 = new SpannableString(str1);
        SpannableString sb2 = new SpannableString(str2);
        SpannableString sb3 = new SpannableString(str3);

        ForegroundColorSpan fcs1 = new ForegroundColorSpan(Color.GRAY);
        ForegroundColorSpan fcs2 = new ForegroundColorSpan(Color.BLACK);
        ForegroundColorSpan fcs3 = new ForegroundColorSpan(Color.GRAY);

        // Gray string
        sb1.setSpan(fcs1, 0, str1.length(), 0);
        builder.append(sb1);

        // Black string
        sb2.setSpan(new RelativeSizeSpan(2.2f), 0, str2.length(), 0);
        sb2.setSpan(fcs2, 0, str2.length(), 0);
        builder.append(sb2);

        // Gray string
        sb3.setSpan(fcs3, 0, str3.length(), 0);
        builder.append(sb3);

        // Set pie chart parameters
        pieChart.setCenterText(TextUtils.concat(builder));
        pieChart.setCenterTextSize(16f);

        pieChart.setDrawEntryLabels(false);
        pieChart.setDrawMarkers(false);

        pieChart.setTouchEnabled(false);

        pieChart.setHoleRadius(80f);
        pieChart.setRotationEnabled(false);

        Description desc = new Description();
        desc.setText("");
        pieChart.setDescription(desc);

        pieChart.getLegend().setEnabled(false);
    }

    // Initiates the pie chart (Sets values to 0)
    public static void initiatePieChart()
    {
        // Add an empty dataset to the pie chart
        List<PieEntry> empty = new ArrayList<>();
        empty.add(new PieEntry(1f, "Empty"));

        PieDataSet pieDataSet = new PieDataSet(empty, "empty");
        pieDataSet.setColor(ColorTemplate.rgb("F4F4F8"));

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);

        pieChart.getData().setDrawValues(false);
    }

    // Updates the pie chart with the corresponding User information
    public static void updateChart(int calsEaten, int totalCals, double protein, double carbs, double fat)
    {
        // If there are no calories, set pie chart values to 0
        if (calsEaten == 0){
            initiatePieChart();
            return;
        }

        // Set pie chart labels (protein, carbs and fats)
        List<PieEntry> macros = new ArrayList<>();
        macros.add(new PieEntry((float) protein, "Protein"));
        macros.add(new PieEntry((float) carbs, "Carbohydrates"));
        macros.add(new PieEntry((float) fat, "Fat"));

        // Add values to the dataset
        PieDataSet pieDataSet = new PieDataSet(macros, "Macro nutrients");
        pieDataSet.setSliceSpace(8f);

        pieDataSet.setColors(Arrays.asList(colors));

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);

        pieChart.getData().setDrawValues(false);
    }

    // Animate the pie chart
    public static void animatePieChart()
    {
        pieChart.animateXY(500, 500);
    }

    // Create a legend for the pie chart
    public static void createLegend()
    {
        TextView proteinCol = view.findViewById(R.id.protein);
        TextView carbsCol = view.findViewById(R.id.carbs);
        TextView fatCol = view.findViewById(R.id.fat);

        SpannableStringBuilder builder = new SpannableStringBuilder();

        double[] userMacros = User.getMacros();
        String[] userMacrosLabels = User.getMacrosLabels();

        String label;
        double value;

        // For each macro...
        for (int i = 0; i < userMacros.length; i++){
            label = userMacrosLabels[i];
            value = userMacros[i];

            // Creates a multi-coloured string with different font sizes
            SpannableString l = new SpannableString(label + "\n");
            SpannableString v = new SpannableString(String.valueOf(value) + "g");

            l.setSpan(new ForegroundColorSpan(Color.GRAY), 0, label.length(), 0);
            l.setSpan(new RelativeSizeSpan(0.8f), 0, label.length(), 0);
            builder.append(l);

            v.setSpan(new ForegroundColorSpan(Color.BLACK), 0, String.valueOf(value).length() + 1, 0);
            v.setSpan(new RelativeSizeSpan(1.2f), 0, String.valueOf(value).length() + 1, 0);
            builder.append(v);

            // Adds the correct label colour to the correct macro nutrient
            switch (label){
                case "Protein":
                    proteinCol.setText(TextUtils.concat(builder));
                    break;
                case "Carbohydrates":
                    carbsCol.setText(TextUtils.concat(builder));
                    break;
                case "Fat":
                    fatCol.setText(TextUtils.concat(builder));
                    break;
            }
            builder.clear();
        }
    }

    // Initialises the meal buttons
    public void setButtons()
    {
        // Breakfast meal
        CardView bButton = view.findViewById(R.id.breakfast);
        bButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPressed("Breakfast");
            }
        });

        // Lunch meal
        CardView lButton = view.findViewById(R.id.lunch);
        lButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPressed("Lunch");
            }
        });

        // Dinner meal
        CardView dButton = view.findViewById(R.id.dinner);
        dButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPressed("Dinner");
            }
        });

        // Snacks meal
        CardView sButton = view.findViewById(R.id.snacks);
        sButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPressed("Snacks");
            }
        });

        return;
    }

    // Determines which meal button is pressed and sets up the next activity accordingly
    public void buttonPressed(String selected)
    {
        // Starts the Meal User Information Acitivity
        Intent intent = new Intent(getActivity(), MealUserInformation.class);
        intent.putExtra("Meal", selected);
        startActivity(intent);

        // Turns off camera
        CameraFragment.turnOffCamera();
        return;
    }

    // Updates the breakfast menu item
    public static void updateBreakfast()
    {
        TextView cals = view.findViewById(R.id.breakfastcals);
        cals.setText(Breakfast.getCalories() + "cal");
    }

    // Updates the lunch menu item
    public static void updateLunch()
    {
        TextView cals = view.findViewById(R.id.lunchcals);
        cals.setText(Lunch.getCalories() + "cal");
    }

    // Updates the dinner menu item
    public static void updateDinner()
    {
        TextView cals = view.findViewById(R.id.dinnercals);
        cals.setText(Dinner.getCalories() + "cal");
    }

    // Updates the snacks menu item
    public static void updateSnacks()
    {
        TextView cals = view.findViewById(R.id.snackcals);
        cals.setText(Snacks.getCalories() + "cal");
    }

    // Gets the calories burned from the number of steps
    public void getCaloriesBurned()
    {
        double calories = MainActivity.getCounter() * 0.04;
        caloriesBurned.setText("" + ((int) calories));
    }
}
