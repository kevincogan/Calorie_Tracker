package com.example.phaze;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phaze.classes.Food;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyScannedFoodAdapter extends RecyclerView.Adapter<MyScannedFoodAdapter.MyViewHolder> implements AdapterView.OnItemSelectedListener
{

    List<Food> data = new ArrayList<>();
    Context context;

    boolean init = true;

    private RecyclerViewClickListener listener;

    // Custom adapter for displaying the selected food nutritional information
    public MyScannedFoodAdapter(Context ct, List<Food> food, RecyclerViewClickListener listener)
    {
        context = ct;
        data = food;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflates the layout from scanned_food_cardview.xml
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.scanned_food_cardview, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Binds the view holder to the data being passed
        holder.name.setText(data.get(position).getName());
//        if (data.get(position).getCaloriesInt() != -1.0) {
//            holder.calories.setText(data.get(position).getCaloriesInt() + "cal");
//        } else {
//            holder.calories.setText("Empty");
//            data.get(position).setCalories(0.0);
//            data.get(position).setCaloriesO(0.0);
//        }

        // Displays the protein (if protein present)
        if (data.get(position).getProtein() != -1.0) {
            holder.protein.setText(spanText("Protein", data.get(position).getProtein()));
        } else {
            holder.protein.setText("Empty");
            data.get(position).setProtein(0.0);
            data.get(position).setProteinO(0.0);
        }

        // Displays the carbs (if carbs present)
        if (data.get(position).getCarbs() != -1.0) {
            holder.carbs.setText(spanText("Carbs", data.get(position).getCarbs()));
        } else {
            holder.carbs.setText("Empty");
            data.get(position).setCarbs(0.0);
            data.get(position).setCarbsO(0.0);
        }

        // Displays the fat (if fat present)
        if (data.get(position).getFat() != -1.0) {
            holder.fat.setText(spanText("Fat", data.get(position).getFat()));
        } else {
            holder.fat.setText("Empty");
            data.get(position).setFat(0.0);
            data.get(position).setFatO(0.0);
        }

        // Creates a pie chart with the food information
        holder.setPieChart(data.get(position).getCaloriesInt(), data.get(position).getProtein(), data.get(position).getCarbs(), data.get(position).getFat());

        String[] measurements = data.get(position).getMeasurementsArray();

        if (init) {

            // Set up the drop down menu spinner for the serving sizes
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, measurements);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            holder.servingSize.setAdapter(spinnerArrayAdapter);
            holder.servingSize.setOnItemSelectedListener(this);
            init = false;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        // Allows you to select a item on the spinner and updates the nutritional information accordingly
        if (!text.split(" ")[0].equals(data.get(0).prevValue)) {
            data.get(0).updateValues(text);
        }
        if (data.get(0).updated) {
            notifyDataSetChanged();
            data.get(0).updated = false;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
    }

    // Builds the legend for the pie chart
    public CharSequence spanText(String macro, double value)
    {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        SpannableString l = new SpannableString(macro + "\n");
        SpannableString v = new SpannableString(value + "g");

        // Gray string
        l.setSpan(new ForegroundColorSpan(Color.GRAY), 0, macro.length(), 0);
        l.setSpan(new RelativeSizeSpan(0.8f), 0, macro.length(), 0);
        builder.append(l);

        // Black string
        v.setSpan(new ForegroundColorSpan(Color.BLACK), 0, String.valueOf(value).length() + 1, 0);
        v.setSpan(new RelativeSizeSpan(1.2f), 0, String.valueOf(value).length() + 1, 0);
        builder.append(v);

        return TextUtils.concat(builder);
    }

    // Holds all of the information for each food item
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView name;
//        TextView calories;
        TextView protein;
        TextView carbs;
        TextView fat;

        Spinner servingSize;

        ImageView arrow;

        PieChart pieChart;

        LinearLayout extras;

        // Constructor for the adapter view holder
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
//            calories = itemView.findViewById(R.id.calories);
            protein = itemView.findViewById(R.id.protein);
            carbs = itemView.findViewById(R.id.carbs);
            fat = itemView.findViewById(R.id.fat);
            arrow = itemView.findViewById(R.id.arrow);
            extras = itemView.findViewById(R.id.extras);
            pieChart = itemView.findViewById(R.id.piechart);
            servingSize = itemView.findViewById(R.id.servingSize);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View itemView) {
            listener.onClick(itemView, getAdapterPosition());

            // Hide and display extra information regarding the food (macros) on click
            if (extras.getVisibility() == View.GONE){
                extras.setVisibility(View.VISIBLE);
                arrow.setBackgroundResource(R.drawable.arrow_up);
                pieChart.animateXY(500, 500);
            }
            else {
                extras.setVisibility(View.GONE);
                arrow.setBackgroundResource(R.drawable.arrow_down);
            }
        }

        // Sets up pie chart for each food item
        public void setPieChart(int calories, double protein, double carbs, double fat)
        {

            // Sets pie chart parameters
            pieChart.setDrawEntryLabels(false);
            pieChart.setDrawMarkers(false);

            pieChart.setTouchEnabled(false);
            pieChart.setTransparentCircleAlpha(0);

            pieChart.setHoleRadius(75f);
            pieChart.setRotationEnabled(false);

            pieChart.setCenterText(calories + "cal");
            pieChart.setCenterTextSize(22f);

            Description desc = new Description();
            desc.setText("");
            pieChart.setDescription(desc);

            pieChart.getLegend().setEnabled(false);

            // Creates the labels
            if (calories > 0) {

                // If there is information on the food
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
            else {

                // If there is no information on the food
                List<PieEntry> empty = new ArrayList<>();
                empty.add(new PieEntry(1f, "Empty"));

                PieDataSet pieDataSet = new PieDataSet(empty, "empty");
                pieDataSet.setColor(ColorTemplate.rgb("F4F4F8"));

                PieData pieData = new PieData(pieDataSet);

                pieChart.setData(pieData);

                pieChart.getData().setDrawValues(false);
            }

            // Animates the pie chart
            pieChart.animateXY(500, 500);
        }
    }
}
