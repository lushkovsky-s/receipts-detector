package com.dinamic.ivan.receipts;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.provider.MediaStore;
import java.io.File;
import java.io.UnsupportedEncodingException;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class MainActivity extends AppCompatActivity {

    final String TAG = "MyLogs";
    final static boolean DEBUG = false;
    final static boolean SeregeyMod = false;


    final int TYPE_PHOTO = 1;

    ListView lvMain;

    final int REQUEST_CODE_PHOTO = 1;
    final int REQUEST_CODE_POINTS = 2;

    //    final String TAG = "myLogs";

    AutoCompleteTextView edText;
    String fileName;

    Intent pointsIntent;

//    Handler handler;

    final String URL = "http://a-c-b.tech:5000/recognize";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pointsIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                pointsIntent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_PHOTO));
                startActivityForResult(pointsIntent, REQUEST_CODE_PHOTO);
            }
        });

        lvMain = (ListView) findViewById(R.id.lvMain);

        if (DEBUG) {
            fileName = "/storage/emulated/0/photo_1478296234532.jpg";
            pointsIntent = new Intent(this, RectActivity.class);
            pointsIntent.putExtra("fileName", fileName);
            startActivityForResult(pointsIntent, REQUEST_CODE_POINTS);
        }
//        edText = (AutoCompleteTextView) findViewById(R.id.editText);

                // ATTENTION: This was auto-generated to implement the App Indexing API.
                // See https://g.co/AppIndexing/AndroidStudio for more information.
                client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CODE_PHOTO) {

                File imgFile = new File(fileName);

                if (imgFile.exists()) {
                    Log.i(TAG, "photo saved as " + fileName);
                    Intent points = new Intent(this, RectActivity.class);
                    points.putExtra("fileName", imgFile.getAbsolutePath());
                    startActivityForResult(points, REQUEST_CODE_POINTS);
                }
            } else if (requestCode == REQUEST_CODE_POINTS) {
//                float[] arr = new float[1];

                Log.d("My", "in MainActivity");
                        try {
                            makePostRequest();
                        } catch (Exception ex) {
                            Log.v("My", " url exeption! ");
                        }
            }

        } else if (resultCode == RESULT_CANCELED) {
            Log.d(TAG, "Taking a picture was canceled");
        }
    }


    private Uri generateFileUri(int type) {
        File file = null;
        fileName = Environment.getExternalStorageDirectory() + "/" + "photo_" + System.currentTimeMillis() + ".jpg";
        file = new File(fileName);
        return Uri.fromFile(file);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void makePostRequest() throws UnsupportedEncodingException {
        Corner points [] = RectActivity.corners;
        String  crop = "";

        for (int i = 0; i < 3; i++) {
            crop += String.valueOf(points[i].point.x) + "x" + String.valueOf(points[i].point.y) + ";";
        }
        crop += String.valueOf(points[3].point.x) + "x" + String.valueOf(points[3].point.y);

        Ion.with(getApplicationContext())
                .load(URL)
                .setMultipartParameter("crop", crop)
                .setMultipartFile("image", "image/jpeg", new File(fileName))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.d(TAG, result.toString());

                        String status = result.get("status").getAsString();
                        if (status.equals("Error")) {
                            Log.w(TAG, "Error while getting answer from server");
                            return;
                        }

                        JsonObject receipt_data = result.getAsJsonObject("receipt_data");

                        JsonArray items = receipt_data.getAsJsonArray("items");

                        String[] itemsString = new String[items.size()];
                        for (int i = 0; i < items.size(); i++) {
                            JsonObject item = items.get(i).getAsJsonObject();
                            itemsString[i] = item.get("name").getAsString() + "; ";
                            itemsString[i] += item.get("count").getAsString();
                            switch (item.get("unit").getAsString()) {
                                case "pieces":
                                    itemsString[i] += "шт";
                                    break;
                                case "grams":
                                    itemsString[i] += "гр";
                                    break;
                                case "milliliter":
                                    itemsString[i] += "мл";
                                    break;
                            }
                            itemsString[i] += "   =" + item.get("price").getAsString() + "руб.";


                        }
                        setList(itemsString);
                    }

                });
    }
    private void setList(String[] listItems){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);

        // присваиваем адаптер списку
        lvMain.setAdapter(adapter);
    }



    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
