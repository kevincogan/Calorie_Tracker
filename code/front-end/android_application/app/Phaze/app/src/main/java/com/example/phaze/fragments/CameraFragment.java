package com.example.phaze.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.phaze.R;
import com.example.phaze.SearchFoodActivity;
import com.example.phaze.ShowCaptureActivity;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CameraFragment extends Fragment implements SurfaceHolder.Callback {

    static Camera camera;
    static Camera.PictureCallback jpegCallback;

    SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;

    final int CAMERA_REQUEST_CODE = 1;

    View view;

    ImageView flash;
    boolean flashOn = false;
    public static boolean clicked;

    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_camera, container, false);

        // Keeps track of clicked items so user cannot double click
        clicked = false;

        // Sets up surface view for camera object
        setUpSurfaceView();

        // Sets up the search bar on click listener
        EditText searchFood = view.findViewById(R.id.searchFood);
        searchFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!clicked) {
                    clicked = true;
                    // Turns off camera
                    turnOffCamera();

                    // Starts Search Food Activity
                    Intent intent = new Intent(getActivity(), SearchFoodActivity.class);
                    startActivity(intent);
                }
            }
        });

        // Toggles flash on and off
        flash = view.findViewById(R.id.flash);
        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Turns flash on
                if (flashOn){
                    flash.setBackgroundResource(R.drawable.flash_off);
                    flashOn = false;
                }

                // Turns flash off
                else {
                    flash.setBackgroundResource(R.drawable.flash_on);
                    flashOn = true;
                }

                // Updates camera parameters
                updateParameters();
            }
        });

        // Formats the image taken
        jpegCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                // Decodes and rotates bitmap image taken
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                Bitmap rotateBitmap = rotate(decodedBitmap);

                // Saves the bitmap locally
                String fileLocation = saveImageToStorage(rotateBitmap);
                if (fileLocation != null) {

                    // Begins Show Capture Activity and turns off camera
                    Intent intent = new Intent(getActivity(), ShowCaptureActivity.class);
                    startActivity(intent);
                    turnOffCamera();
                }
            }
        };

        return view;
    }

    // Turns off Camera
    public static void turnOffCamera()
    {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }
    }

    // Creates a surface view to display the camera view
    public void setUpSurfaceView()
    {
        mSurfaceView = view.findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();

        // Checks if user has given permission to use the camera
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            mSurfaceHolder.addCallback(this);
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    // Saves image to storage
    public String saveImageToStorage(Bitmap bitmap) {
        String fileName = "imageToSend";
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fileOutput = getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutput.write(bytes.toByteArray());
            fileOutput.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

    // Rotates the bitmap image by 90 degrees
    private Bitmap rotate(Bitmap decodedBitmap) {
        int width = decodedBitmap.getWidth();
        int height = decodedBitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.setRotate(90);

        return Bitmap.createBitmap(decodedBitmap, 0, 0, width, height, matrix, true);
    }

    // Takes a picture
    public static void captureImage() {
        try {
            camera.takePicture(null, null, jpegCallback);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    // Updates the camera parameters so that it fits on screen
    public void updateParameters()
    {
        Camera.Parameters parameters;
        parameters = camera.getParameters();

        camera.setDisplayOrientation(90);
        parameters.setPreviewFrameRate(30);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        if (flashOn){
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        }
        else{
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }

        // Gets best size fit for camera view
        Camera.Size bestSize = null;
        List<Camera.Size> sizeList = camera.getParameters().getSupportedPreviewSizes();
        bestSize = sizeList.get(0);

        for (int i = 1; i < sizeList.size(); i++) {
            if ((sizeList.get(i).width * sizeList.get(i).height) > (bestSize.width * bestSize.height)) {
                bestSize = sizeList.get(i);
            }
        }

        parameters.setPreviewSize(bestSize.width, bestSize.height);
        parameters.setPictureSize(bestSize.width, bestSize.height);

        camera.setParameters(parameters);
    }


    // Creates the surface for the camera view
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        camera = Camera.open();

        updateParameters();

        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    // Requests permission from the user to use the camera
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mSurfaceHolder.addCallback(this);
                    mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                }
                else{
                    Toast.makeText(getContext(), "Please allow permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }
}
