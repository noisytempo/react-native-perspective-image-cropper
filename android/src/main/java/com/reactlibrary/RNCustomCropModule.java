
package com.reactlibrary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Base64;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import java.io.ByteArrayOutputStream;

public class RNCustomCropModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public RNCustomCropModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "CustomCropManager";
    }

    @ReactMethod
    public void crop(ReadableMap points, String base64Image, Callback successCallBack) {
        try {
            Toast.makeText(reactContext, "should crop now", Toast.LENGTH_LONG).show();
            WritableMap map = Arguments.createMap();

            map.putString("image", getCroppedImage(points, base64Image));
            successCallBack.invoke(null, map);
        } catch (Exception e) {
            Toast.makeText(reactContext, "Unable to crop the image" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String getCroppedImage(ReadableMap points, String base64Image) {

        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap srcBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);


        //target size
        int bitmapWidth = srcBitmap.getWidth();
        int bitmapHeight = (int)(srcBitmap.getWidth()/1.586);

        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        float[] src = new float[]{
                (float) points.getMap("topLeft").getDouble("x"), (float) points.getMap("topLeft").getDouble("y"),
                (float) points.getMap("topRight").getDouble("x"), (float) points.getMap("topRight").getDouble("y"),
                (float) points.getMap("bottomRight").getDouble("x"), (float) points.getMap("bottomRight").getDouble("y"),
                (float) points.getMap("bottomLeft").getDouble("x"), (float) points.getMap("bottomLeft").getDouble("y")
        };
        float[] dsc = new float[]{
                0, 0,
                bitmapWidth, 0,
                bitmapWidth, bitmapHeight,
                0, bitmapHeight
        };

        Matrix matrix = new Matrix();
        matrix.setPolyToPoly(src, 0, dsc, 0, 4);
        canvas.drawBitmap(srcBitmap, matrix, new Paint(Paint.ANTI_ALIAS_FLAG));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}