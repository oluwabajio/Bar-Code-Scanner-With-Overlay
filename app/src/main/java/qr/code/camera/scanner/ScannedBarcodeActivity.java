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


    SurfaceView surfaceView;
    SurfaceView transparentView;
    TextView txtBarcodeValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    Button btnAction;
    String intentData = "";
    boolean isEmail = false;
    SurfaceHolder holderTransparent;
    Canvas canvas;
    Paint paint;
    private float  mWidthScaleFactor;
    private float mHeightScaleFactor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_barcode);

        initViews();
    }

    private void initViews() {
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
        transparentView = findViewById(R.id.TransparentView);

        btnAction = findViewById(R.id.btnAction);


        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (intentData.length() > 0) {
                    Toast.makeText(ScannedBarcodeActivity.this, "Scanned Text is "+ intentData, Toast.LENGTH_LONG).show();
                }


            }
        });


        holderTransparent = transparentView.getHolder();
        holderTransparent.setFormat(PixelFormat.TRANSPARENT);
        holderTransparent.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void initialiseDetectorsAndSources() {

        Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1920,1080)
                .setAutoFocusEnabled(true)
                .setRequestedFps(2.0f)
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

               surfaceView.setWillNotDraw(false);

                try {
                    if (ActivityCompat.checkSelfPermission(ScannedBarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                        Log.e("TAG", "surfaceCreated: started" );










                    } else {
                        ActivityCompat.requestPermissions(ScannedBarcodeActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                        Log.e("TAG", "surfaceCreated: requestpemission" );
                    }



                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("TAG", "surfaceCreated: "+ e.getMessage() );
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
                Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

             //   Log.e("TAG", "receiving: " );
                if (barcodes.size() != 0) {


                    txtBarcodeValue.post(new Runnable() {

                        @Override
                        public void run() {


                            if (barcodes.valueAt(0).email != null) {
                                txtBarcodeValue.removeCallbacks(null);
                                intentData = barcodes.valueAt(0).email.address;
                                txtBarcodeValue.setText(intentData);
                                isEmail = true;
                                btnAction.setText("ADD CONTENT TO THE MAIL");
                                Point[] corners = barcodes.valueAt(0).cornerPoints;
                                Log.e("TAG", "run: Size is " +corners.length);
                                for (Point element: corners) {
                                    Log.e("TAG", "run: "+ element.x + " y = "+ element.y);
                                }


                            } else {
                                isEmail = false;
                                btnAction.setText("LAUNCH URL");
                                intentData = barcodes.valueAt(0).displayValue;
                                txtBarcodeValue.setText(intentData);

                                Point[] corners = barcodes.valueAt(0).cornerPoints;
                                Log.e("TAG", "run: Size is " +corners.length);

                                final Rect recto = barcodes.valueAt(0).getBoundingBox();
                                RectF rect = new RectF(recto);
                                rect.left = translateX(rect.left);
                                rect.top = translateY(rect.top);
                                rect.right = translateX(rect.right);
                                rect.bottom = translateY(rect.bottom);
//                                canvas.drawRect(rect, mRectPaint);

                                Log.e("TAG", "run: former = "+mWidthScaleFactor +mHeightScaleFactor );
//
//                                // Draws a label at the bottom of the barcode indicate the barcode value that was detected.
//                                canvas.drawText(barcode.rawValue, rect.left, rect.bottom, mTextPaint)
//
//
//                                Canvas canvas = surfaceView.getHolder().lockCanvas(null);
//                                Paint myPaint = new Paint();
//                                myPaint.setStyle(Paint.Style.STROKE);
//                                myPaint.setColor(Color.rgb(0, 0, 0));
//                                myPaint.setStrokeWidth(10);
//
//                                for (Point element: corners) {
//                                    Log.e("TAG", "run: "+ element.x + " y = "+ element.y);
//                                    canvas.drawCircle(element.x, element.y, 1, myPaint);
//                                }
                               // canvas.drawRect(corners[0], corners[1],corners[2], corners[3], myPaint);


//                                Canvas canvas = surfaceView.getHolder().lockCanvas();
//                                if (canvas == null) {
//                                    Log.e("TAG", "Cannot draw onto the canvas as it's null");
//                                } else {
//                                    Log.e("TAG", "doing it");
//                                    Paint myPaint = new Paint();
//                                    myPaint.setColor(Color.rgb(100, 20, 50));
//                                    myPaint.setStrokeWidth(10);
//                                    myPaint.setStyle(Paint.Style.STROKE);
//                                    canvas.drawRect(100, 100, 200, 200, myPaint);
//
//                                    surfaceView.getHolder().unlockCanvasAndPost(canvas);
//                                }



                                canvas = holderTransparent.lockCanvas();
                                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                                //border's properties
                                paint = new Paint();
                                paint.setStyle(Paint.Style.STROKE);
                                paint.setColor(Color.rgb(100, 20, 50));
                                paint.setStrokeWidth(3);
                                canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom, paint);

                                Size size = cameraSource.getPreviewSize();
                                int min = Math.min(size.getWidth(), size.getHeight());
                                int max = Math.max(size.getWidth(), size.getHeight());
                                mWidthScaleFactor = (float) canvas.getWidth() / (float) min;
                                mHeightScaleFactor = (float) canvas.getHeight() / (float) max;

                                Log.e("TAG", "run:lkl = "+mWidthScaleFactor +mHeightScaleFactor );
//


                                holderTransparent.unlockCanvasAndPost(canvas);
                            }


                        }
                    });

                }
            }
        });
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