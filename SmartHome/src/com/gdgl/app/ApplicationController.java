package com.gdgl.app;

import android.app.Application;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.videogo.constant.Config;
import com.videogo.openapi.EzvizAPI;
import com.videogo.constant.Config;
import com.videogo.openapi.EzvizAPI;
/***
 * initial volley RequestQueue while app start
 * @author justek
 *
 */
public class ApplicationController extends Application {
	
public static String APP_KEY = "4333c1fb24a948e5a036c6fb3c055cf8";
    
    public static String API_URL = "https://open.ys7.com";
    public static String WEB_URL = "https://auth.ys7.com";
	
	private boolean isDragSlidMenu = true;

    /**
     * Log or request TAG
     */
    public static final String TAG = "VolleyPatterns";

    /**
     * Global request queue for Volley
     */
    private RequestQueue mRequestQueue;

    /**
     * A singleton instance of the application class for easy access in other places
     */
    private static ApplicationController sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        Config.LOGGING = true;
        EzvizAPI.init(this, APP_KEY); 
        EzvizAPI.getInstance().setServerUrl(API_URL, WEB_URL);     
        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this));
        sInstance = this;
        CrashHandler crashHandler = CrashHandler.getInstance();  
        crashHandler.init(this);
    }

    /**
     * @return ApplicationController singleton instance
     */
    public static synchronized ApplicationController getInstance() {
        return sInstance;
    }

    /**
     * @return The Volley Request queue, the queue will be created if it is null
     */
    public RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    /**
     * Adds the specified request to the global queue, if tag is specified
     * then it is used else Default TAG is used.
     * 
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue().add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     * 
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);

        getRequestQueue().add(req);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     * 
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
    /***
     * check whether the operation is on the main thread!
     * @return
     */
    public static Boolean isOnMainThread()
    {
    	if (Looper.myLooper()!=Looper.getMainLooper()) {
			Log.i("Thread test", "not the Main thread!");
			return false;
		}else {
			Log.i("Thread test", "is the Main thread!");
			return true;
		}
    }
    
    public void setIsDragSlidMenu(boolean b){
    	isDragSlidMenu = b;
    }
    
    public boolean getIsDragSlidMenu(){
    	return isDragSlidMenu;
    }
}
