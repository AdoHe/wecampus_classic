package com.westudio.wecampus.net;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

/**
 * Created by nankonami on 13-9-10.
 */
public class GsonRequest<T> extends Request<T> {
    private Class<T> clazz;
    private Gson mGson;
    private Response.Listener listener;

    public GsonRequest(int method, String url, Class<T> clazz,
                       Response.Listener successListener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);

        this.clazz = clazz;
        this.mGson = new Gson();
        this.listener = successListener;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String data = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(mGson.fromJson(data, clazz), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }

    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        return super.parseNetworkError(volleyError);
    }

    @Override
    protected void deliverResponse(T t) {
        listener.onResponse(t);
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
    }
}