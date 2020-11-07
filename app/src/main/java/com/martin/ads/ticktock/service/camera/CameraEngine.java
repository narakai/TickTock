package com.martin.ads.ticktock.service.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;

import com.martin.ads.ticktock.ui.DigitalClockActivity;
import com.martin.ads.ticktock.utils.DateData;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class CameraEngine {
    private static final String TAG = "CameraEngine";

    public static void takePicture(final String outputPath){
        Log.d(TAG, "take pic");
        Camera camera = DigitalClockActivity.camera;
        try {
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] bytes, Camera camera) {
                    BufferedOutputStream bos;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    try {
                        bos = new BufferedOutputStream(new FileOutputStream(outputPath));
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, bos);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    bitmap.recycle();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Camera getFrontCameraImpl() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    Camera camera = Camera.open(i);
                    if(camera == null){
                        camera = Camera.open();
                    }
                    return camera;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return Camera.open();
    }
    public static Camera getFrontCamera(){
        Camera camera = getFrontCameraImpl();
        try {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(1280, 720);
            parameters.setPictureSize(1280, 720);
            camera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return camera;
    }
}
