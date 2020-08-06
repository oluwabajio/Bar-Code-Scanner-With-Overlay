package qr.code.camera.scanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import android.os.Bundle;

public class ScannedBarcodeActivity extends AppCompatActivity {


    private SurfaceView barcodeSurfaceView;
    private SurfaceView transparentSurfaceView;
    private TextView barcodeValue_TextView, barcodeValue_TextView2;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private Button btnShowBarcodeValue;
    String intentData = "";
    boolean isEmail, isPhone, isUrl;
    SurfaceHolder holderTransparent;
    Canvas canvas;
    Paint paint;
    private float mWidthScaleFactor;
    private float mHeightScaleFactor;
    private static final int REQUEST_CAMERA_PERMISSION = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_barcode);

        initViews(); //initialize all views
        initListeners();
    }

    private void initListeners() {
        btnShowBarcodeValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intentData.length() > 0) {
                    Toast.makeText(ScannedBarcodeActivity.this, "Scanned Text is " + intentData, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void initViews() {
        barcodeValue_TextView = findViewById(R.id.txt_BarcodeValue);
        barcodeValue_TextView2 = findViewById(R.id.txt_BarcodeValue2);
        barcodeSurfaceView = findViewById(R.id.barCode_surfaceView);
        transparentSurfaceView = findViewById(R.id.transparent_SurfaceView);
        btnShowBarcodeValue = findViewById(R.id.btn_show_bar_code_value);


        holderTransparent = transparentSurfaceView.getHolder();
        holderTransparent.setFormat(PixelFormat.TRANSPARENT);
        holderTransparent.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void initialiseDetectorsAndSources() {

        //initialise the Detector
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        if (!barcodeDetector.isOperational()) {
            Toast.makeText(this, "Could not set up the detector!", Toast.LENGTH_LONG).show();
            return;
        }

        if (cameraSource != null) {
            cameraSource = null;

        }

            //Initialise the camera
            cameraSource = new CameraSource.Builder(this, barcodeDetector)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1920, 1080)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();


        barcodeSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                barcodeSurfaceView.setWillNotDraw(false);
                try {
                    if (ActivityCompat.checkSelfPermission(ScannedBarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(barcodeSurfaceView.getHolder());
                        Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

                    } else {
                        ActivityCompat.requestPermissions(ScannedBarcodeActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                        Toast.makeText(getApplicationContext(), "Camera Permission is Needed", Toast.LENGTH_LONG).show();
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("TAG", "surfaceCreated: " + e.getMessage());
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                 cameraSource.stop();
            }


        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
               // Toast.makeText(getApplicationContext(), "Barcode scanner has been released", Toast.LENGTH_SHORT).show();
                Log.e("TAG", "receiveDetections: Barcode scanner has been released " );
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                Log.e("TAG", "receiveDetections: Bar code has started " );

                if (barcodes.size() != 0) {

                    barcodeValue_TextView.post(new Runnable() {

                        @Override
                        public void run() {


                            if (barcodes.valueAt(0).email != null) { //You can check if bar code value is email, url, phone number, etc
                                barcodeValue_TextView.removeCallbacks(null);
                                isEmail = true;
                            } else {
                                isEmail = false;
                            }

                            barcodeValue_TextView.removeCallbacks(null);
                            btnShowBarcodeValue.setText("Show Bar code Value");
                            intentData = barcodes.valueAt(0).displayValue;
                            barcodeValue_TextView.setText(intentData);
                            barcodeValue_TextView2.setText("  "+intentData+"  ");


                            final Rect rect = barcodes.valueAt(0).getBoundingBox(); //Get Barcode rectangle dimensions
                            RectF rectf = new RectF(rect);  // Convert the rectangle dimension values from int type to float type
                            rectf.left = scaleX(rectf.left);  //scale the dimensions with our scaling factor
                            rectf.top = scaleY(rectf.top);
                            rectf.right = scaleX(rectf.right);
                            rectf.bottom = scaleY(rectf.bottom);

                            canvas = holderTransparent.lockCanvas(); // get SurfaceView Canvas so we can be able to draw our rectagle
                            drawFillTypeRectangle(rectf); //Draw rectangles
                            holderTransparent.unlockCanvasAndPost(canvas); //release surfaceview


                        }
                    });

                } else {

                    barcodeValue_TextView2.post(new Runnable() {

                        @Override
                        public void run() {
                            removeRectangle();
                        }
                    });





                }
            }
        });
    }

    /*
     * Remove all Rectangles on the screen
     * */
    private void removeRectangle() {
        canvas = holderTransparent.lockCanvas();
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        holderTransparent.unlockCanvasAndPost(canvas);
        barcodeValue_TextView2.setText("");
        barcodeValue_TextView2.setVisibility(View.GONE);
    }


    /*
     * Draws a Fill Type Rectangle on the screen
     * */
    private void drawFillTypeRectangle(RectF rect) {
        calculateCameraScaleFactor();
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        /// Paint Object for the Barcode Rectangle
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#8cff0000"));

        /// Paint object for the top rectangle cotaining barcode value
        Paint paint2 = new Paint();
        paint2.setStyle(Paint.Style.FILL);
        paint2.setColor(Color.parseColor("#8c000000"));

        canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom, paint); //Draws the bar code rectagle
     //   canvas.drawRect(rect.left - 20, rect.top - (rect.bottom - rect.top),
       //         rect.right + 20, rect.bottom - (rect.bottom - rect.top + 50), paint2); //draws the rectagle containing bar code value
        barcodeValue_TextView2.setVisibility(View.VISIBLE);
        barcodeValue_TextView2.setX(rect.left);
        barcodeValue_TextView2.setY(rect.top - (rect.bottom - rect.top));

        Log.e("TAG", "drawFillTypeRectangle: left = " + rect.left + " top = " + rect.top + " right = " + rect.right + " bottom = " + rect.bottom);
    }

    /*
     * Draws a border type rectangle on the screen
     * */
    private void drawStrokeTypeRectangle(RectF rect) {
        calculateCameraScaleFactor();
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.rgb(100, 20, 50));
        paint.setStrokeWidth(3);
        canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom, paint);


    }

    /*
    * Calculate Camera to phone screen scale
    * */
    private void calculateCameraScaleFactor() {
        Size size = cameraSource.getPreviewSize();
        int min = Math.min(size.getWidth(), size.getHeight());
        int max = Math.max(size.getWidth(), size.getHeight());
        mWidthScaleFactor = (float) canvas.getWidth() / (float) min;
        mHeightScaleFactor = (float) canvas.getHeight() / (float) max;
    }



    /**
     * Adjusts a horizontal value of the supplied value from the preview scale to the view
     * scale.
     */
    public float scaleX(float horizontal) {
        return horizontal * mWidthScaleFactor;
    }

    /**
     * Adjusts a vertical value of the supplied value from the preview scale to the view scale.
     */
    public float scaleY(float vertical) {
        return vertical * mHeightScaleFactor;
    }


    @Override
    protected void onPause() {
        super.onPause();

        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initialiseDetectorsAndSources();


    }

}