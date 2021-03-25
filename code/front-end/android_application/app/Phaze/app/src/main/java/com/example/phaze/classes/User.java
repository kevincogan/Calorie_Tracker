package com.example.phaze.classes;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.phaze.meals.Breakfast;
import com.example.phaze.meals.Dinner;
import com.example.phaze.meals.Lunch;
import com.example.phaze.meals.Snacks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class User {

    public static String username;
    public static String password;

    public static Context context;

    private static int totalCalories = 2000;
    private static int caloriesEaten = 0;

    private static double totalProtein = 0.0;
    private static double totalCarbs = 0.0;
    private static double totalFat = 0.0;

    public static float userCounter = 0f;

    public static OkHttpClient client;
    public static boolean clientActive = false;

    // Resets all the values of the User
    public static void resetUser()
    {
        username = null;
        password = null;

        totalCalories = 2000;
        caloriesEaten = 0;

        totalProtein = 0.0;
        totalCarbs = 0.0;
        totalFat = 0.0;

        userCounter = 0f;

        // Re writes the local save
        User.putSaveLocal();
    }

    // Checks to see if user can auto login
    public static boolean autoLogin()
    {
        SharedPreferences localSave = context.getSharedPreferences("com.example.Phaze.LocalSaveKey", Context.MODE_PRIVATE);

        String savedName = localSave.getString("Username", null);
        String savedPassword = localSave.getString("Password", null);

        // If a username and password is saved locally, then can auto login
        if (savedName != null && savedPassword != null){
            User.username = savedName;
            User.password = savedPassword;

            return true;
        }
        // Cannot auto login
        else {
            return false;
        }
    }

    // Creates a local save
    public static void putSaveLocal()
    {
        if (context != null) {

            // Uses sharedPreferences to create a local save
            SharedPreferences localsave = context.getSharedPreferences("com.example.Phaze.LocalSaveKey", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = localsave.edit();

            // Saves the user information
            editor.putString("Username", username);
            editor.putString("Password", password);

            editor.putInt("totalCalories", totalCalories);
            editor.putInt("caloriesEaten", caloriesEaten);

            editor.putString("totalProtein", String.valueOf(totalProtein));
            editor.putString("totalCarbs", String.valueOf(totalCarbs));
            editor.putString("totalFat", String.valueOf(totalFat));

            editor.putFloat("userCounter", userCounter);

            // Saves the user's Breakfast meals
            if (Breakfast.getMeals().size() > 0) {
                String foods = "";
                for (Food food : Breakfast.getMeals()) {
                    foods += food.buildString();
                }
                editor.putString("Breakfast", foods);
            }

            // Saves the user's Lunch meals
            if (Lunch.getMeals().size() > 0) {
                String foods = "";
                for (Food food : Lunch.getMeals()) {
                    foods += food.buildString();
                }
                editor.putString("Lunch", foods);
            }

            // Saves the user's Dinner meals
            if (Dinner.getMeals().size() > 0) {
                String foods = "";
                for (Food food : Dinner.getMeals()) {
                    foods += food.buildString();
                }
                editor.putString("Dinner", foods);
            }

            // Saves the user's Snacks meals
            if (Snacks.getMeals().size() > 0) {
                String foods = "";
                for (Food food : Snacks.getMeals()) {
                    foods += food.buildString();
                }
                editor.putString("Snacks", foods);
            }

            editor.apply();
        }
    }

    // Retrieves the information saved locally
    public static void getSaveLocal()
    {
        if (context != null) {
            SharedPreferences localSave = context.getSharedPreferences("com.example.Phaze.LocalSaveKey", Context.MODE_PRIVATE);

            // Retrieves the user's macro nutrients
            totalCalories = localSave.getInt("totalCalories", totalCalories);
            caloriesEaten = localSave.getInt("caloriesEaten", caloriesEaten);

            totalProtein = Double.parseDouble(localSave.getString("totalProtein", String.valueOf(totalProtein)));
            totalCarbs = Double.parseDouble(localSave.getString("totalCarbs", String.valueOf(totalCarbs)));
            totalFat = Double.parseDouble(localSave.getString("totalFat", String.valueOf(totalFat)));

            // Retrieves the number of steps the user has taken
            userCounter = localSave.getFloat("userCounter", userCounter);

            List<Food> food_list = new ArrayList<>();

            // Gets the meals eaten for breakfast
            String savedMeal = localSave.getString("Breakfast", null);
            if (savedMeal != null) {
                String[] mealTokens = decodeString(savedMeal);
                for (String item : mealTokens) {
                    String[] token = item.split("%");
                    Food food = new Food(token[0], token[1], Double.parseDouble(token[2]), Double.parseDouble(token[3]), Double.parseDouble(token[4]), Double.parseDouble(token[5]));
                    food_list.add(food);
                    System.out.println(food.getName());
                }
                Breakfast.updateMeals(food_list);
            }

            food_list.clear();

            // Gets the meals eaten for Lunch
            savedMeal = localSave.getString("Lunch", null);
            if (savedMeal != null) {
                String[] mealTokens = decodeString(savedMeal);
                for (String item : mealTokens) {
                    String[] token = item.split("%");
                    Food food = new Food(token[0], token[1], Double.parseDouble(token[2]), Double.parseDouble(token[3]), Double.parseDouble(token[4]), Double.parseDouble(token[5]));
                    food_list.add(food);
                    System.out.println(food.getName());
                }
                Lunch.updateMeals(food_list);
            }

            food_list.clear();

            // Gets the meals eaten for Dinner
            savedMeal = localSave.getString("Dinner", null);
            if (savedMeal != null) {
                String[] mealTokens = decodeString(savedMeal);
                for (String item : mealTokens) {
                    String[] token = item.split("%");
                    Food food = new Food(token[0], token[1], Double.parseDouble(token[2]), Double.parseDouble(token[3]), Double.parseDouble(token[4]), Double.parseDouble(token[5]));
                    food_list.add(food);
                    System.out.println(food.getName());
                }
                Dinner.updateMeals(food_list);
            }

            food_list.clear();

            // Gets the meals eaten as Snacks
            savedMeal = localSave.getString("Snacks", null);
            if (savedMeal != null) {
                String[] mealTokens = decodeString(savedMeal);
                for (String item : mealTokens) {
                    String[] token = item.split("%");
                    Food food = new Food(token[0], token[1], Double.parseDouble(token[2]), Double.parseDouble(token[3]), Double.parseDouble(token[4]), Double.parseDouble(token[5]));
                    food_list.add(food);
                    System.out.println(food.getName());
                }
                Snacks.updateMeals(food_list);
            }
        }
    }

    // Start the HTTP client
    public static void startClient()
    {
        client = new OkHttpClient();
        clientActive = true;
    }

    // Get update from server
    public static void getServerInfo(){
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                totalCalories = 2000;

                // Array of information to request from server
                String[] types = {"calories", "protein", "carbs", "fat", "activity", "Breakfast", "Lunch", "Dinner", "Snacks"};

                // Loops through types
                for (String type : types) {

                    // Request info
                    RequestBody newFormBody = new FormBody.Builder()
                            .add("Username", username)
                            .add("Type", type)
                            .build();
                    Request getRequest = new Request.Builder()
                            .url("http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/retrieve_info")
                            .post(newFormBody)
                            .build();

                    try {
                        // Retrieve info
                        Response response2 = client.newCall(getRequest).execute();

                        String reply = response2.body().string();

                        String[] arr = decodeString(reply);
                        List<Food> food_list = new ArrayList<>();

                        // Addes the necessary information to the user
                        switch (type){
                            case "calories":
                                // Updates calories
                                if (reply != null && !reply.equals("None")){
                                    setCaloriesEaten((int) Double.parseDouble(reply));
                                }
                                else {
                                    setCaloriesEaten(0);
                                }
                                break;
                            case "protein":
                                // Updates protein
                                if (reply != null && !reply.equals("None")){
                                    setTotalProtein(Double.parseDouble(reply));
                                }
                                else {
                                    setTotalProtein(0.0);
                                }
                                break;
                            case "carbs":
                                // Updates carbs
                                if (reply != null && !reply.equals("None")){
                                    setTotalCarbs(Double.parseDouble(reply));
                                }
                                else {
                                    setTotalCarbs(0.0);
                                }
                                break;
                            case "fat":
                                // Updates fat
                                if (reply != null && !reply.equals("None")){
                                    setTotalFat(Double.parseDouble(reply));
                                }
                                else {
                                    setTotalFat(0.0);
                                }
                                break;
                            case "activity":
                                // Updates the number of steps
                                if (reply != null && !reply.equals("None")){
                                    userCounter = Float.parseFloat(reply);
                                }
                                break;
                            case "Breakfast":
                                // Updates Breakfast
                                if (arr != null && !arr.equals("None")) {
                                    for (String item : arr) {
                                        String[] token = item.split("%");
                                        Food food = new Food(token[0], token[1], Double.parseDouble(token[2]), Double.parseDouble(token[3]), Double.parseDouble(token[4]), Double.parseDouble(token[5]));
                                        food_list.add(food);
                                    }
                                    Breakfast.updateMeals(food_list);
                                }
                                break;
                            case "Lunch":
                                // Updates Lunch
                                food_list.clear();
                                if (arr != null && !arr.equals("None")) {
                                    for (String item : arr) {
                                        String[] token = item.split("%");
                                        Food food = new Food(token[0], token[1], Double.parseDouble(token[2]), Double.parseDouble(token[3]), Double.parseDouble(token[4]), Double.parseDouble(token[5]));
                                        food_list.add(food);
                                    }
                                    Lunch.updateMeals(food_list);
                                }
                                break;
                            case "Dinner":
                                // Updates Dinner
                                food_list.clear();
                                if (arr != null && !arr.equals("None")) {
                                    for (String item : arr) {
                                        String[] token = item.split("%");
                                        Food food = new Food(token[0], token[1], Double.parseDouble(token[2]), Double.parseDouble(token[3]), Double.parseDouble(token[4]), Double.parseDouble(token[5]));
                                        food_list.add(food);
                                    }
                                    Dinner.updateMeals(food_list);
                                }
                                break;
                            case "Snacks":
                                // Updates Snacks
                                food_list.clear();
                                if (arr != null && !arr.equals("None")) {
                                    for (String item : arr) {
                                        String[] token = item.split("%");
                                        Food food = new Food(token[0], token[1], Double.parseDouble(token[2]), Double.parseDouble(token[3]), Double.parseDouble(token[4]), Double.parseDouble(token[5]));
                                        food_list.add(food);
                                    }
                                    Snacks.updateMeals(food_list);
                                }
                                break;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }});

        if (clientActive) {
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Decodes the meals retrieved from the database
    public static String[] decodeString(String reply)
    {
        if (reply != null && reply.contains("|")) {
            String[] arr = reply.split(Pattern.quote("|"));
            return arr;
        }
        else{
            return null;
        }

    }

    // Send the user information to the server
    public static void updateServer(String action) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                String[] types = {"calories", "protein", "carbs", "fat"};

                for (String type : types) {

                    String value = "";

                    switch (type){
                        case "calories":
                            value = getCaloriesEaten() + "";
                            break;
                        case "protein":
                            value = getTotalProtein() + "";
                            break;
                        case "carbs":
                            value = getTotalCarbs() + "";
                            break;
                        case "fat":
                            value = getTotalFat() + "";
                            break;
                    }

                    // Update info
                    RequestBody formBody = new FormBody.Builder()
                            .add("Username", username)
                            .add("Value", value)
                            .add("Type", type)
                            .add("Action", action)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_micro")
                            .post(formBody)
                            .build();

                    try {
                        // Send info
                        Response response = client.newCall(request).execute();
                        System.out.println(type + ": " + response.code());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }});

        if (clientActive) {
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Sends the meals eaten by the user to the server
    public static void updateServerMeals(String action){
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                String[] meals = {"Breakfast", "Lunch", "Dinner", "Snacks"};

                for (String type : meals){

                    String mealInformation = "";

                    switch (type){
                        case "Breakfast":
                            // Builds string from meals in breakfast
                            if (Breakfast.getMeals().size() > 0) {
                                for (Food food : Breakfast.getMeals()) {
                                    mealInformation += food.buildString();
                                }
                            }
                            break;
                        case "Lunch":
                            // Builds string from meals in lunch
                            if (Lunch.getMeals().size() > 0) {
                                for (Food food : Lunch.getMeals()) {
                                    mealInformation += food.buildString();
                                }
                            }
                            break;
                        case "Dinner":
                            // Builds string from meals in dinner
                            if (Dinner.getMeals().size() > 0) {
                                for (Food food : Dinner.getMeals()) {
                                    mealInformation += food.buildString();
                                }
                            }
                            break;
                        case "Snacks":
                            // Builds string from meals in snacks
                            if (Snacks.getMeals().size() > 0) {
                                for (Food food : Snacks.getMeals()) {
                                    mealInformation += food.buildString();
                                }
                            }
                            break;
                    }
                        // Update info
                        RequestBody formBody = new FormBody.Builder()
                                .add("Username", username)
                                .add("Food", mealInformation)
                                .add("Type", type)
                                .add("Action", action)
                                .build();
                        Request request = new Request.Builder()
                                .url("http://3ypapi-env-1.eba-6ggcn643.eu-west-1.elasticbeanstalk.com/update_food")
                                .post(formBody)
                                .build();

                        try {
                            // Send info
                            Response response = client.newCall(request).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                }
            }});

        if (clientActive) {
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Updates the user information locally
    public static void update()
    {
        caloriesEaten = 0;
        caloriesEaten += Breakfast.getCalories() + Lunch.getCalories() + Dinner.getCalories() + Snacks.getCalories();

        totalProtein = 0.0;
        totalCarbs = 0.0;
        totalFat = 0.0;

        // Calculates the total nutritional value from breakfast
        List<Food> meals = Breakfast.getMeals();
        for (int i = 0; i < meals.size(); i++){
            totalProtein += meals.get(i).getProtein();
            totalCarbs += meals.get(i).getCarbs();
            totalFat += meals.get(i).getFat();
        }

        // Calculates the total nutritional value from lunch
        meals = Lunch.getMeals();
        for (int i = 0; i < meals.size(); i++){
            totalProtein += meals.get(i).getProtein();
            totalCarbs += meals.get(i).getCarbs();
            totalFat += meals.get(i).getFat();
        }

        // Calculates the total nutritional value from dinner
        meals = Dinner.getMeals();
        for (int i = 0; i < meals.size(); i++){
            totalProtein += meals.get(i).getProtein();
            totalCarbs += meals.get(i).getCarbs();
            totalFat += meals.get(i).getFat();
        }

        // Calculates the total nutritional value from snacks
        meals = Snacks.getMeals();
        for (int i = 0; i < meals.size(); i++){
            totalProtein += meals.get(i).getProtein();
            totalCarbs += meals.get(i).getCarbs();
            totalFat += meals.get(i).getFat();
        }

    }

    // Update stats with information passed
    public static void updateStats(int cals, double protein, double carbs, double fat)
    {
        User.caloriesEaten += cals;
        User.totalProtein += protein;
        User.totalCarbs += carbs;
        User.totalFat += fat;
    }

    // Setters and Getters
    public static double[] getMacros()
    {
        return new double[]{Math.round(totalProtein * 10.0) / 10.0, Math.round(totalCarbs * 10.0) / 10.0, Math.round(totalFat * 10.0) / 10.0};
    }

    public static String[] getMacrosLabels()
    {
        return new String[]{"Protein", "Carbohydrates", "Fat"};
    }

    public static int getTotalCalories() {
        return totalCalories;
    }

    public static void setTotalCalories(int totalCalories) {
        User.totalCalories = totalCalories;
    }

    public static int getCaloriesEaten() {
        return caloriesEaten;
    }

    public static void setCaloriesEaten(int caloriesEaten) {
        User.caloriesEaten = caloriesEaten;
    }

    public static double getTotalProtein() {
        return Math.round(totalProtein * 10.0) / 10.0;
    }

    public static void setTotalProtein(double totalProtein) {
        User.totalProtein = totalProtein;
    }

    public static double getTotalCarbs() {
        return Math.round(totalCarbs * 10.0) / 10.0;
    }

    public static void setTotalCarbs(double totalCarbs) {
        User.totalCarbs = totalCarbs;
    }

    public static double getTotalFat() {
        return Math.round(totalFat * 10.0) / 10.0;
    }

    public static void setTotalFat(double totalFat) {
        User.totalFat = totalFat;
    }
}
