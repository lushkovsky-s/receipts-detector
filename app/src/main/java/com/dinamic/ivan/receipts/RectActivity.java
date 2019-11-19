package com.dinamic.ivan.receipts;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import static java.lang.Math.abs;


public class RectActivity extends AppCompatActivity implements View.OnTouchListener {

    //TODO remove it immediately!!! (static)
    static Corner[] corners;
    Line lines[];
    int num = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);

         Corner tmp[] = {new Corner(this.getApplicationContext()),
            new Corner(this.getApplicationContext()),
            new Corner(this.getApplicationContext()),
            new Corner(this.getApplicationContext())};
        corners = tmp;
        Line tmp1[] = {new Line(this.getApplicationContext()),
                new Line(this.getApplicationContext()),
                new Line(this.getApplicationContext()),
                new Line(this.getApplicationContext())};

        lines = tmp1;

        setImg(getIntent().getStringExtra("fileName"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tip, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case 2131493008: // выяснено империческим путем
                Intent intent = new Intent();

                float points[] = new float[8];

                for (int i = 0; i < 4; i++) {
                    points[i*2] = corners[i].point.x;
                    points[i*2+1] = corners[i].point.y;
                }
                intent.putExtra("points", points);

                setResult(RESULT_OK, intent);
                finish();
        }

        return (super.onOptionsItemSelected(menuItem));
    }

    private void setImg(String fileName) {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int reqW = size.x;
        //TODO: change in auto mode
        int reqH = size.y - 1000; // хз, но работает

        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        options.inSampleSize = calculateInSampleSize(options, reqW, reqH);

        Bitmap myBitmap = decodeSampledBitmapFromResource(getResources(), fileName, reqW, reqH);

        Matrix m = new Matrix();
        if(!MainActivity.SeregeyMod)
            m.setRotate(90);
        Bitmap myBitmap2 = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), m, true);

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);
        ImageView im = new ImageView(this);
        im.setImageBitmap(myBitmap2);

        rl.addView(im);

        int h3 = size.y / 3;
        int w3 = size.x / 3;
        corners[0].setCoord(w3, h3);
        corners[1].setCoord(w3*2, h3);
        corners[2].setCoord(w3*2, h3*2);
        corners[3].setCoord(w3, h3*2);

        lines[0].setCoord(w3, h3, w3*2, h3);
        lines[1].setCoord(w3*2, h3, w3*2, h3*2);
        lines[2].setCoord(w3*2, h3*2, w3, h3*2);
        lines[3].setCoord(w3, h3*2, w3, h3);

        for (int i = 0; i < 4; i++) {
            corners[i].setOnTouchListener(this);
            rl.addView(corners[i]);
            rl.addView(lines[i]);
        }

        if(MainActivity.DEBUG){
            Intent intent = new Intent();

            float points[] = new float[8];

            for (int i = 0; i < 4; i++) {
                points[i*2] = corners[i].point.x;
                points[i*2+1] = corners[i].point.y;
            }

            Log.d("My", "finish");
            intent.putExtra("points", points);

            setResult(RESULT_OK, intent);
            finish();
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < 4; i++)
                    if(abs(corners[i].point.x - x) < 50 && abs(corners[i].point.y - y) < 50){
                        num = i;
                        break;
                    }

            break;
            case MotionEvent.ACTION_MOVE:
                if(num >= 0 && num < 4) {
                    corners[num].setCoord(x, y);

                    lines[num].setP1(x, y);
                    int tmp = num - 1;
                    if(tmp < 0)
                        tmp = 3;

                    lines[tmp].setP2(x, y);

                }

            break;
            case MotionEvent.ACTION_UP:
                num = -1;
            break;

        }
        return  true;
    }


    private static Bitmap decodeSampledBitmapFromResource(Resources res, String resId, int    reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
        return BitmapFactory.decodeFile(resId, options);
    }


    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
