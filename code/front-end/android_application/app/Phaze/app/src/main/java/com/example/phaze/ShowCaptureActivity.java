package com.example.phaze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShowCaptureActivity extends AppCompatActivity {

    Bitmap bitmap;
//    ImageClassifier imageClassifier;
    BarcodeDetector barcodeDetector;

    Classifier classifier;
    List<Classifier.Recognition> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_capture);

        // Retrieves the bitmap saved in the Camera Fragment
        try {
            bitmap = BitmapFactory.decodeStream(getApplication().openFileInput("imageToSend"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            finish();
            return;
        }

        // Displays the image
        ImageView mImage = (ImageView) findViewById(R.id.imageCapture);
        mImage.setImageBitmap(bitmap);

        // sets up the image classifier and activates the CNN model
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    classifier = new Classifier(getAssets(),"converted_model.tflite", "labels.txt", 224);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Gets the prediction
                result = classifier.recogniseImage(bitmap);
            }});

        // Barcode detector
        String code = "";
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        barcodeDetector = new BarcodeDetector.Builder(getApplicationContext()).build();

        SparseArray<Barcode> barcodes = barcodeDetector.detect(frame);

        // If detects barcode....
        if (barcodes.size() != 0) {
            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
            code = barcodes.valueAt(0).displayValue;

            NutritionalInfoSheet infoSheet = new NutritionalInfoSheet();
            infoSheet.setName("upc=" + code);
            infoSheet.show(getSupportFragmentManager(), "TAG");
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Continues using Tensorflow
        else {
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // If it got a result
            if (result.size() > 0) {
                // Format food name so that it can query the food database
                String food_name = result.get(0).title.toLowerCase();

                if (food_name.contains(" ")) {
                    String[] construct = food_name.split(" ");
                    food_name = "";
                    for (int i = 0; i < construct.length; i++) {
                        food_name += construct[i] + "%20";
                    }
                    food_name = food_name.substring(0, food_name.length() - 3);
                }

                // Display the info sheet
                NutritionalInfoSheet infoSheet = new NutritionalInfoSheet();
                infoSheet.setName("ingr=" + food_name);
                infoSheet.show(getSupportFragmentManager(), "TAG");
            }
        }
    }
}