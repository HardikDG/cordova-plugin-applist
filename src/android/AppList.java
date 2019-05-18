package org.apache.cordova;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaResourceApi.OpenForReadResult;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import android.util.Log;

public class AppList extends CordovaPlugin {

	private static final String LOG_TAG = "AppList";

	@Override
	public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {
		if ("applist".equals(action)) {
			this.cordova.getThreadPool().execute(new Runnable() { public void run() {
				applist(args, callbackContext);
			} });
			return true;
		} 

		return false;
	}

	private void applist(CordovaArgs args, CallbackContext callbackContext) {
		try {
			CordovaResourceApi resourceApi = webView.getResourceApi();
			
			callbackContext.success("una lista de apps");

		} catch (Exception e) {
			String errorMessage = "An error occurred while unzipping.";
			callbackContext.error(errorMessage);
			Log.e(LOG_TAG, errorMessage, e);
		} finally {
		}
	}

}
