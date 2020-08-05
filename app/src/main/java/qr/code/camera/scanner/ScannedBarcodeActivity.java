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
import com.google.android.gms.vision.text.Line;
import com.google.android.gms.vision.text.TextBlock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;

public class ScannedBarcodeActivity extends AppCompatActivity {


    private SurfaceView barcodeSurfaceView;
    private SurfaceView transparentView;
    private TextView txtBarcodeValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private Button btnShowBarcodeValue;
    String intentData = "";
    boolean isEmail, isPhone, isUrl;
    SurfaceHolder holderTransparent;
    Canvas canvas;
    Paint paint;
    private float mWidthScaleFactor;
    private float mHeightScaleFactor;

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
        txtBarcodeValue = findViewById(R.id.txt_BarcodeValue);
        barcodeSurfaceView = findViewById(R.id.barCode_surfaceView);
        transparentView = findViewById(R.id.transparent_SurfaceView);
        btnShowBarcodeValue = findViewById(R.id.btn_show_bar_code_value);


        holderTransparent = transparentView.getHolder();
        holderTransparent.setFormat(PixelFormat.TRANSPARENT);
        holderTransparent.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void initialiseDetectorsAndSources() {



        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        if (!barcodeDetector.isOperational()) {
            Toast.makeText(this, "Could not set up the detector!", Toast.LENGTH_LONG).show();
            return;
        }

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
                        Log.e("TAG", "surfaceCreated: started");


                    } else {
                        ActivityCompat.requestPermissions(ScannedBarcodeActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                        Log.e("TAG", "surfaceCreated: requestpemission");
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
                Toast.makeText(getApplicationContext(), "Barcode Scanner Released successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();


                if (barcodes.size() != 0) {

                    txtBarcodeValue.post(new Runnable() {

                        @Override
                        public void run() {


                            if (barcodes.valueAt(0).email != null) {
                                isEmail = true;
                            } else {
                                isEmail = false;
                            }

                            txtBarcodeValue.removeCallbacks(null);
                            btnShowBarcodeValue.setText("Show Bar code Value");
                            intentData = barcodes.valueAt(0).displayValue;
                            txtBarcodeValue.setText(intentData);


//                            final Rect recto = barcodes.valueAt(0).getBoundingBox();
//                            RectF rect = new RectF(recto);
//                            rect.left = translateX(rect.left);
//                            rect.top = translateY(rect.top);
//                            rect.right = translateX(rect.right);
//                            rect.bottom = translateY(rect.bottom);

                            Log.e("TAG", "run: former = " + mWidthScaleFactor + mHeightScaleFactor);


                            List<RectF> rarr = new ArrayList<>();
                            for (int i = 0; i < barcodes.size(); ++i) {
                                final Rect recto1 = barcodes.valueAt(i).getBoundingBox();
                                RectF rect1 = new RectF(recto1);
                                rect1.left = translateX(rect1.left);
                                rect1.top = translateY(rect1.top);
                                rect1.right = translateX(rect1.right);
                                rect1.bottom = translateY(rect1.bottom);
                                rarr.add(rect1);

                            }





                            canvas = holderTransparent.lockCanvas(); // get SurfaceView Canvass
                            drawRectangle(rarr);
                            holderTransparent.unlockCanvasAndPost(canvas);


                        }
                    });

                } else {

                    removeRectangle();

                }
            }
        });
    }

    private void removeRectangle() {
        canvas = holderTransparent.lockCanvas();
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        holderTransparent.unlockCanvasAndPost(canvas);
    }

    private void drawRectangle(List<RectF> rect) {
        getCameraScaleFactor();
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        //border's properties
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.rgb(100, 20, 50));
        paint.setStrokeWidth(3);

        for (int i=0; i<rect.size(); i++) {
            canvas.drawRect(rect.get(i).left, rect.get(i).top, rect.get(i).right, rect.get(i).bottom, paint);

        }

    }

    /**
     * Get
     * */
    private void getCameraScaleFactor() {
        Size size = cameraSource.getPreviewSize();
        int min = Math.min(size.getWidth(), size.getHeight());
        int max = Math.max(size.getWidth(), size.getHeight());
        mWidthScaleFactor = (float) canvas.getWidth() / (float) min;
        mHeightScaleFactor = (float) canvas.getHeight() / (float) max;
    }

    public float translateX(float x) {
        return scaleX(x);
    }

    /**
     * Adjusts the y coordinate from the preview's coordinate system to the view coordinate
     * system.
     */
    public float translateY(float y) {
        return scaleY(y);
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