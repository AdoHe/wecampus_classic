package com.westudio.wecampus.net;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;

/**
 * Created by nankonami on 13-9-10.
 */
public class GsonRequest<T> extends Request<T> {

    private static final String TAG = "GSONREQUEST";

    protected Class<T> clazz;
    protected Gson mGson;
    protected Response.Listener listener;
    protected Response.ErrorListener errorListener;

    /**
     * Constructor used for GET/DELETE/PATCH
     * @param method
     * @param url
     * @param clazz
     * @param successListener
     * @param errorListener
     */
    public GsonRequest(int method, String url, Class<T> clazz,
                       Response.Listener successListener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);

        this.clazz = clazz;
        this.mGson = new Gson();
        this.listener = successListener;
        this.errorListener = errorListener;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            if(response.statusCode != 200) {
                return Response.error(new VolleyError(response));
            } else {
                String data = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                JSONObject json = new JSONObject();
                Object object = new JSONTokener(data).nextValue();
                if (object instanceof JSONObject) {
                    return Response.success(mGson.fromJson(data, clazz), HttpHeaderParser.parseCacheHeaders(response));
                } else {
                    JSONArray array = new JSONArray(data);
                    json.put("objects", array);
                    return Response.success(mGson.fromJson(json.toString(), clazz), HttpHeaderParser.parseCacheHeaders(response));
                }

            }
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return null;
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
