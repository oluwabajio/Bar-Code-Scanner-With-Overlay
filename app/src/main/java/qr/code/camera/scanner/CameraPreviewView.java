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
      //  canvas.drawRect(new Rect(10,10,200,200), rectanglePaint);
        Log.w(this.getClass().getName(), "On Draw Called");
    }
}

