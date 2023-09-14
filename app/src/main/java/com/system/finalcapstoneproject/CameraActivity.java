package com.system.finalcapstoneproject;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private ImageButton captureButton;
    private SeekBar zoomSeekBar;
    // Define the camera ID (0 for back camera, 1 for front camera)
    int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    int currentZoom = 0;
    int maxZoom;
    float initialFingerSpacing = 0f;
    private int mFlashState = 0;
    private static final int FLASH_STATE_OFF = 0;
    private static final int FLASH_STATE_ON = 1;
    private static final int FLASH_STATE_AUTO = 2;
    private boolean mFlashSupported = true;
    private ImageButton mFlashIcon, backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        surfaceView = findViewById(R.id.surfaceView);
        captureButton = findViewById(R.id.captureButton);
        zoomSeekBar = findViewById(R.id.zoomSeekBar);
        mFlashIcon = findViewById(R.id.flash_toggle);
        backBtn = findViewById(R.id.back_toggle);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(CameraActivity.this, HomeActivity.class);
               startActivity(intent);
            }
        });
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Capture button clicked");
                camera.takePicture(null, null, pictureCallback);
            }
        });

        mFlashIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFlashState();
            }
        });

        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getPointerCount() == 2) {
                    float x1 = event.getX(0);
                    float y1 = event.getY(0);
                    float x2 = event.getX(1);
                    float y2 = event.getY(1);

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_POINTER_DOWN:
                            initialFingerSpacing = getFingerSpacing(event);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            float newFingerSpacing = getFingerSpacing(event);
                            if (newFingerSpacing > initialFingerSpacing) {
                                // Zoom in
                                if (currentZoom < maxZoom) {
                                    currentZoom++;
                                    setZoom(currentZoom);
                                }
                            } else if (newFingerSpacing < initialFingerSpacing) {
                                // Zoom out
                                if (currentZoom > 0) {
                                    currentZoom--;
                                    setZoom(currentZoom);
                                }
                            }
                            initialFingerSpacing = newFingerSpacing;
                            break;
                    }
                }
                return true;
            }
        });

        zoomSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setZoom(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
    private void toggleFlashState() {
        if (mFlashState == FLASH_STATE_OFF) {
            mFlashState = FLASH_STATE_ON;
        } else if (mFlashState == FLASH_STATE_ON) {
            mFlashState = FLASH_STATE_AUTO;
        } else if (mFlashState == FLASH_STATE_AUTO) {
            mFlashState = FLASH_STATE_OFF;
        }
        setFlashIcon();
    }

    private void setFlashIcon() {
        if (mFlashState == FLASH_STATE_OFF) {
            Glide.with(getApplicationContext())
                    .load(R.drawable.ic_flash_off)
                    .into(mFlashIcon);
        } else if (mFlashState == FLASH_STATE_ON) {
            Glide.with(getApplicationContext())
                    .load(R.drawable.ic_flash_on)
                    .into(mFlashIcon);
        } else if (mFlashState == FLASH_STATE_AUTO) {
            Glide.with(getApplicationContext())
                    .load(R.drawable.ic_flash_auto)
                    .into(mFlashIcon);
        }
        setAutoFlash(camera.getParameters());
    }

    private void setAutoFlash(Camera.Parameters parameters) {
            if (mFlashState == FLASH_STATE_OFF) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            } else if (mFlashState == FLASH_STATE_ON) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            } else if (mFlashState == FLASH_STATE_AUTO) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            }
            camera.setParameters(parameters);
    }




    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void setZoom(int zoomLevel) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setZoom(zoomLevel);
        camera.setParameters(parameters);
        currentZoom = zoomLevel;
        zoomSeekBar.setProgress(zoomLevel);
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions.");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                if (pictureFile != null) {
                    Log.d(TAG, "pictureFile not empty");
                    // Send the result back to the calling activity
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("image_path", pictureFile.getAbsolutePath());
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
                fos.close();
                Toast.makeText(CameraActivity.this, "Image saved", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
                Toast.makeText(CameraActivity.this, "Error saving image", Toast.LENGTH_SHORT).show();
            }
            // Restart the camera preview
            camera.startPreview();
        }
    };


    private File getOutputMediaFile() {
        // Check that the device has a mounted external storage
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        // Set the directory for the image file
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DOWNLOADS, "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Failed to create directory.");
                return null;
            }
        }

        // Get the timestamp for the image filename
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "IMG_" + timeStamp + ".jpg";

        // Create the image file
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return mediaFile;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open(cameraId);
            camera.setPreviewDisplay(holder);
            Camera.Parameters parameters = camera.getParameters();
            maxZoom = parameters.getMaxZoom();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            camera.setParameters(parameters);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (surfaceHolder.getSurface() == null) {
            return;
        }
        try {
            camera.stopPreview();
        } catch (Exception e) {
            Log.e(TAG, "Error stopping camera preview: " + e.getMessage());
        }
        try {
            Camera.Parameters parameters = camera.getParameters();

            // Get screen aspect ratio
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            float screenAspectRatio = (float) metrics.heightPixels / metrics.widthPixels;

            // Find the preview size with the closest aspect ratio to the screen
            Camera.Size bestSize = null;
            float bestAspectRatio = 0;
            for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
                float aspectRatio = (float) size.width / size.height;
                if (Math.abs(aspectRatio - screenAspectRatio) < Math.abs(bestAspectRatio - screenAspectRatio)) {
                    bestSize = size;
                    bestAspectRatio = aspectRatio;
                }
            }
            if (bestSize != null) {
                parameters.setPreviewSize(bestSize.width, bestSize.height);
            }

            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "portrait");
                camera.setDisplayOrientation(90);
                parameters.setRotation(90);
            } else {
                parameters.set("orientation", "landscape");
                camera.setDisplayOrientation(0);
                parameters.setRotation(0);
            }
            camera.setParameters(parameters);
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            maxZoom = parameters.getMaxZoom();
            zoomSeekBar.setMax(maxZoom);
        } catch (IOException e) {
            Log.e(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private void openCamera() {
        try {
            camera = Camera.open(cameraId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        openCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

}