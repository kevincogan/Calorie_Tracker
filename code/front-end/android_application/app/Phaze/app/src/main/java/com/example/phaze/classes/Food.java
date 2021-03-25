package com.example.phaze.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Food implements Serializable {

    private String name;

    private double calories;
    private double caloriesO;

    private double protein;
    private double proteinO;

    private double fat;
    private double fatO;

    private double carbs;
    private double carbsO;

    private List<String> foodInfo;
    private Map<String, Integer> measurements;

    public String prevValue = "Default";
    public String currentServingSize = "Default 100g";
    public boolean updated = false;

    // Food class constructor
    public Food(String n, String measure, double c, double p, double cb, double f)
    {
        setName(n);

        setCalories(c);
        setCaloriesO(c);

        setProtein(p);
        setProteinO(p);

        setFat(f);
        setFatO(f);

        setCarbs(cb);
        setCarbsO(cb);

        currentServingSize = measure;

        setFoodInfo();
    }

    // Food class constructor
    public Food(String n, double c, double p, double f, double cb, Map<String, Integer> measures)
    {
        // Capitalises all words in the name
        if (n.contains(" ")) {
            String[] tmp = n.split(" ");
            String newString = "";
            if (tmp.length > 1) {
                for (int i = 0; i < tmp.length; i++) {
                    newString += tmp[i].substring(0, 1).toUpperCase() + tmp[i].substring(1) + " ";
                }
                n = newString.substring(0, newString.length() - 1);
            }
            setName(n);
        } else {
            setName(n.substring(0, 1).toUpperCase() + n.substring(1));
        }

        // Sets the current calories and original calories
        setCalories(c);
        setCaloriesO(c);

        // Sets the current protein and original protein
        setProtein(p);
        setProteinO(p);

        // Sets the current fat and original fat
        setFat(f);
        setFatO(f);

        // Sets the current carbs and original carbs
        setCarbs(cb);
        setCarbsO(cb);

        // Sets the food measurements (serving sizes)
        setMeasurements(measures);
        setFoodInfo();
    }

    // Adds the food infor to a List
    public void setFoodInfo()
    {
        foodInfo = new ArrayList<>();

        foodInfo.add(name);
        foodInfo.add("" + calories);
        foodInfo.add("" + protein);
        foodInfo.add("" + fat);
        foodInfo.add("" + carbs);
    }

    // Setters and Getters
    public List<String> getFoodInfo()
    {
        return foodInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCalories() {
        return calories;
    }

    public double getOriginalCalories() {
        return calories;
    }

    public int getCaloriesInt()
    {
        return (int) Math.round(calories);
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = Math.round(protein * 10) / 10.0;;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = Math.round(fat * 10) / 10.0;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = Math.round(carbs * 10) / 10.0;
    }

    public Map<String, Integer> getMeasurements() {
        return measurements;
    }

    // Returns the Food measurements in the form of an array of Strings
    public String[] getMeasurementsArray() {
        String[] servingSize = new String[getMeasurements().size()];
        servingSize[0] = "Default (100g)";
        int i = 1;
        for (String key : getMeasurements().keySet()) {
            if (!key.equals("Default")) {
                int val = getMeasurements().get(key);
                servingSize[i] = key + " (" + val + ")";
                i++;
            }
        }
        return servingSize;
    }

    // Creates a hashmap for the serving sizes
    public void setMeasurements(Map<String, Integer> measurements) {
        this.measurements = measurements;
    }

    // Updates the current calories based on the serving size chosen by the user
    public void updateValues(String value)
    {
        try {
            String key = value.split(" ")[0];
            double multiplier = (getMeasurements().get(key) / 100.0);

            setCalories(getCaloriesO() * multiplier);
            setProtein(getProteinO() * multiplier);
            setCarbs(getCarbsO() * multiplier);
            setFat(getFatO() * multiplier);

            if (!value.equals(prevValue)) {
                updated = true;
                prevValue = value;
                currentServingSize = value;
            }

            System.out.println(getCaloriesInt());
        }
        catch (Exception e){
            System.out.println("Error here");
        }
    }

    // More Setters and Getters
    public double getCaloriesO() {
        return caloriesO;
    }

    public void setCaloriesO(double caloriesO) {
        this.caloriesO = caloriesO;
    }

    public double getProteinO() {
        return proteinO;
    }

    public void setProteinO(double proteinO) {
        this.proteinO = proteinO;
    }

    public double getFatO() {
        return fatO;
    }

    public void setFatO(double fatO) {
        this.fatO = fatO;
    }

    public double getCarbsO() {
        return carbsO;
    }

    public void setCarbsO(double carbsO) {
        this.carbsO = carbsO;
    }

    // Builds a string of necessary information to send out to the database
    public String buildString()
    {
        String foodFormat = this.getName() + "%" + this.currentServingSize + "%" + this.getCaloriesInt() + "%" + this.getProtein() + "%" + this.getCarbs() + "%" + this.getFat() + "|";
        return foodFormat;
    }
}
