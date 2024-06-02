package com.example.myjavaapplication.controllers;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myjavaapplication.model.User;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class VolleyRequest {

    private Context context;
    private RequestQueue queue;

    public VolleyRequest(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }

    public void getRequest(String url, final VolleyResponseListener<JSONObject> onSuccess, final VolleyErrorListener onError) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> onSuccess.onSuccess(response),
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    onError.onError(error != null ? error.getMessage() : "Unknown error occurred");
                }
            });

        queue.add(jsonObjectRequest);
    }

    public void postRequest(String url, JSONObject requestBody, VolleyCallback callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> callback.onSuccess(response),
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }


    public interface VolleyResponseListener<T> {
        void onSuccess(T response);
    }

    public interface VolleyErrorListener {
        void onError(String errorMessage);
    }

    public interface VolleyCallback {
        void onSuccess(JSONObject response);
        void onError(String error);

    }
}
