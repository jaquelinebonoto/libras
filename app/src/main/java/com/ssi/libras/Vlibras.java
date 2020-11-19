package com.ssi.libras;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;

public class Vlibras {
    private RequestQueue requestQueue;
    String libras;
    Context context;

    public void getContext(Context main){
        context = main;
    };

    public String post(String data) {
        final String savedata = data;
        String url = "https://traducao2.vlibras.gov.br/translate";
        Log.d("String url", url);

        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);
                libras = response;
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR","error => "+error.toString());
                    }
                })
        {
            @Override
            public String getBodyContentType(){ return "application/json; charset=utf-8";}

            @Override
            public byte[] getBody() throws AuthFailureError {
                Log.d("Request", savedata);
                try{
                    return savedata==null ? null : savedata.getBytes("utf-8");
                }catch (UnsupportedEncodingException uee){
                    return null;
                }
            }
        };
        requestQueue.add(stringRequest);
        Log.d("Data", data.toString());
        Log.d("SaveData", savedata.toString());
        Log.d("POST REQUEST", stringRequest.toString());

        return libras;
    }

}
