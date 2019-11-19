package com.dinamic.ivan.receipts;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Ivan on 05.11.2016.
 */

class Line extends View {

    Paint paint = new Paint();
    MyPoint point1 = new MyPoint();
    MyPoint point2 = new MyPoint();

    public void setCoord(float x1, float y1, float x2, float y2){
        point1.x = x1;
        point1.y = y1;
        point2.x = x2;
        point2.y = y2;
        invalidate();
    }

    public void setP1(float x, float y){
        point1.x = x;
        point1.y = y;
        invalidate();
    }

    public void setP2(float x, float y){
        point2.x = x;
        point2.y = y;
        invalidate();
    }

    public Line(Context context) {
        super(context);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(15);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(point1.x, point1.y, point2.x, point2.y, paint);

    }

}

class Corner extends View {

    Paint paint = new Paint();
    MyPoint point = new MyPoint();

    public void setCoord(float x, float y){
        point.x = x;
        point.y = y;
        invalidate();
    }

    public Corner(Context context) {
        super(context);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(30);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(point.x, point.y, 15, paint);
    }

}
class MyPoint {
    float x, y;
}

