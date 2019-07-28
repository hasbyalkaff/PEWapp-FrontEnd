package unsri.final_project.pewapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * Created by Hasby on 10-Mar-19.
 */

public class Requester {
    private String TAG = "Requester Class";
    private String url;
    private boolean isProcessing;
    private RequestQueue mRequestQueue;

    public Requester(Context context){
        this.url = "http://172.20.10.2:5000/testing";
        this.isProcessing = false;
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        this.mRequestQueue = new RequestQueue(cache, network);
        this.mRequestQueue.start();
    }

    public void jsonPOST(final Bitmap image, final ImageView imageView, final ImageView expressionView, final TextView textView){
        if(!this.isProcessing) {
            this.isProcessing = true;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encode = Base64.encodeToString(byteArray, Base64.DEFAULT);

            this.setImage(imageView, image);
            JSONObject data = new JSONObject();
            try {
                data.put("image", encode);
                if(textView == null) data.put("step", 0);
                else data.put("step", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, this.url, data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, response.toString());
                    String expression;
                    try {
                        expression = response.getString("expression");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        expression = "Gagal Koneksi";
                    }
                    if(expressionView != null) {
                        setImage(expressionView, image);
                        setExpression(textView, expression);
                    }
                    isProcessing = false;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, error.toString());
                    error.printStackTrace();
                    isProcessing = false;
                }
            });

            this.mRequestQueue.add(jsonObjectRequest);
        }
    }

    private void setImage(final ImageView imageView, final Bitmap bitmap){
        imageView.post(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bitmap);
            }
        });
    }
    private void setExpression(final TextView textView, final String string){
        textView.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(string);
            }
        });
    }

}
