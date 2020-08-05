package qr.code.camera.scanner;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreviewView extends SurfaceView {

    protected final Paint rectanglePaint = new Paint();

    public CameraPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        rectanglePaint.setARGB(255, 200, 0, 0);
        rectanglePaint.setStyle(Paint.Style.FILL);
        rectanglePaint.setStrokeWidth(2);
    }

    @Override
    protected void onDraw(Canvas canvas){
        canvas.drawRect(new Rect(10,10,200,200), rectanglePaint);
        Log.w(this.getClass().getName(), "On Draw Called");
    }
}


//public class CameraPreview extends Activity implements SurfaceHolder.Callback{
//
//    private SurfaceHolder holder;
//    private Camera camera;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//
//        // We remove the status bar, title bar and make the application fullscreen
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
//        // We set the content view to be the layout we made
//        setContentView(R.layout.camera_preview);
//
//        // We register the activity to handle the callbacks of the SurfaceView
//        CameraPreviewView surfaceView = (CameraPreviewView) findViewById(R.id.camera_surface);
//        holder = surfaceView.getHolder();
//
//        holder.addCallback(this);
//        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//    }
//
//    public void surfaceChanged(SurfaceHolder holder, int format, int width,
//                               int height) {
//
//        Camera.Parameters params = camera.getParameters();
//
//        params.setPreviewSize(width, height);
//        camera.setParameters(params);
//
//        try {
//            camera.setPreviewDisplay(holder);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        camera.startPreview();
//
//    }
//
//    public void surfaceCreated(SurfaceHolder holder) {
//        camera = Camera.open();
//    }
//
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        camera.stopPreview();
//        camera.release();
//    }
//
//
//}
//android