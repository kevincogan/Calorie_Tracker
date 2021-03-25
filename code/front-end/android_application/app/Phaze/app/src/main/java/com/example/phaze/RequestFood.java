package com.example.phaze;

import android.accounts.NetworkErrorException;

import com.example.phaze.classes.Food;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.*;

public class RequestFood {

    private String food_appid = "f635a9ab";
    private String food_appkey = "fee3c57a8a37a553efef8356f775f5c3";

    public int n = 10;

    Food food;

    public List<Food> Request(String query) throws IOException, InterruptedException, JSONException, NetworkErrorException {
        //Keys to access the Edamam API
        String category = "generic-foods";

        //URL to be accessed to get information
        String url = "https://api.edamam.com/api/food-database/v2/parser?%s&nutrition-type=logging&app_id=%s&app_key=%s";

        //Adding the keys and what food we what to the URL request.
        String link = String.format(url, query, getFood_appid(), getFood_appkey());

        //Making a connection to the website.
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(link)
                .build();

        //Getting a response from the website.
        Response response = client.newCall(request).execute();
        String json_response = response.body().string();

        //Change string to json format
        JSONObject content = new JSONObject(json_response);

        List<Food> foodList = new ArrayList<>();
        JSONArray arr;
        try {
            arr = content.getJSONArray("hints");
        }
        catch (Exception e){
            e.printStackTrace();
            return foodList;
        }

        // Gets the first 10 foods from the array
        if (arr.length() < n){
            n = arr.length();
        }

        double cals;
        double protein;
        double fat;
        double carbs;

        // Retrieves the food information from the JSON object
        for (int i = 0; i < n; i++) {
            try {
                // Gets the food name
                String name = arr.getJSONObject(i).getJSONObject("food").getString("label");

                // Gets the food calories
                try {
                    cals = arr.getJSONObject(i).getJSONObject("food").getJSONObject("nutrients").getDouble("ENERC_KCAL");
                }
                catch (Exception e) {
                    cals = -1.0;
                }

                // Gets the food protein
                try {
                    protein = arr.getJSONObject(i).getJSONObject("food").getJSONObject("nutrients").getDouble("PROCNT");
                }
                catch (Exception e) {
                    protein = -1.0;
                }

                // Gets the food fat
                try {
                    fat = arr.getJSONObject(i).getJSONObject("food").getJSONObject("nutrients").getDouble("FAT");
                }
                catch (Exception e) {
                    fat = -1.0;
                }

                // Gets the food carbs
                try {
                    carbs = arr.getJSONObject(i).getJSONObject("food").getJSONObject("nutrients").getDouble("CHOCDF");
                }
                catch (Exception e) {
                    carbs = -1.0;
                }

                // Creates a hashmap for the food serving sizes
                Map<String, Integer> measures = new HashMap<String, Integer>();
                measures.put("Default", 100);
                JSONArray JSONmeasures = arr.getJSONObject(i).getJSONArray("measures");
                for (int j = 0; j < JSONmeasures.length(); j++){
                    String label = JSONmeasures.getJSONObject(j).getString("label");
                    try {
                        int weight = JSONmeasures.getJSONObject(j).getInt("weight");
                        measures.put(label, weight);
                    } catch (Exception e){
                        int weight = (int) Math.round(JSONmeasures.getJSONObject(j).getDouble("weight"));
                        measures.put(label, weight);
                        e.printStackTrace();
                    }
                }

                // Creates a new food object and adds it to the list
                food = new Food(name, cals, protein, fat, carbs, measures);
                foodList.add(food);
            }
            catch (Exception e){
//                e.printStackTrace();
            }
        }

        // Returns the food list
        return foodList;
    }

    // Gets the food app id
    public String getFood_appid() {
        return food_appid;
    }

    // Gets the food app key
    public String getFood_appkey() {
        return food_appkey;
    }
}
