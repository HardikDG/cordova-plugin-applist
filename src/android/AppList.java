package org.apache.cordova;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Bitmap;
import android.util.Base64;
import android.content.Intent;
import android.content.Context;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

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

	private Context context = null;
	private PackageManager Pm = null;
	private List<ApplicationInfo> apps = null;

	@Override
	public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {
		if ("applist".equals(action)) {
			this.cordova.getThreadPool().execute(new Runnable() { public void run() {
					applist(args, callbackContext);
					} });
			return true;
		} 
		else if ("appicon".equals(action)) {
			this.cordova.getThreadPool().execute(new Runnable() { public void run() {
					appicon_str(args, callbackContext);
					} });
			return true;
		} 
		else if ("appstart".equals(action)) {
			this.cordova.getThreadPool().execute(new Runnable() { public void run() {
					appstart(args, callbackContext);
					} });
			return true;
		} 		
		return false;
	}

	/**
	 * @credit http://developer.android.com/reference/android/content/AsyncTaskLoader.html
	 */

	//U: carga lista de apps para esta instancia si no estaba	
	private void applist_load(boolean force) {
		if (apps!=null && !force) { return; }
		//A: solo si no estaban cargadas
	
		context= this.cordova.getActivity().getApplicationContext(); 
		Pm= context.getPackageManager();
		apps = Pm.getInstalledApplications(0);
		if (apps == null) { apps = new ArrayList<ApplicationInfo>(); }
	}

	private void applist(CordovaArgs args, CallbackContext callbackContext) {
		try {
			applist_load(true);

			JSONObject r = new JSONObject();
			JSONObject pkgs = new JSONObject();
			for (int i = 0; i < apps.size(); i++) {
				ApplicationInfo app= apps.get(i);
				String pkg = app.packageName;
				if (Pm.getLaunchIntentForPackage(pkg) != null) {
					//A: only apps which are launchable
					String label= pkg;
					File mApkFile = new File(app.sourceDir);
					if (mApkFile.exists()) { //A: is mounted
						CharSequence labelcs = app.loadLabel(Pm);
						label = labelcs != null ? labelcs.toString() : pkg;
					}
					else {
						Log.w(LOG_TAG, "app not found "+pkg+" "+app.sourceDir);
					}
					JSONObject appInfo = new JSONObject();
                    appInfo.put("name", label);
                    appInfo.put("icon", getIcon(app));
                    pkgs.put(pkg, appInfo);
				}
			}
			r.put("apps", pkgs);
			r.put("totalApp", apps.size());
			callbackContext.success(r);
		} catch (Exception e) {
			String errorMessage = "Can't retrieve app list";
			callbackContext.error(errorMessage);
			Log.e(LOG_TAG, errorMessage, e);
		} finally {
		}
	}

	private void appicon_str(CordovaArgs args, CallbackContext callbackContext) {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  

			Bitmap bmp= appicon(args.getString(0));
			bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
			byte[] byteArray = byteArrayOutputStream .toByteArray();
			String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
			callbackContext.success(encoded);

		} catch (Exception e) {
			String errorMessage = "Can't retrieve app icon";
			callbackContext.error(errorMessage);
			Log.e(LOG_TAG, errorMessage, e);
		} finally {
		}
	}

	private void appstart(CordovaArgs args, CallbackContext callbackContext) {
		try {
			String pkg= args.getString(0);
			Intent intent = Pm.getLaunchIntentForPackage(pkg);
			if (intent != null) {
				this.cordova.getActivity().startActivity(intent);
				callbackContext.success("OK");
			}
			else {
				callbackContext.error("Can't launch app, no intent");
			}
		} catch (Exception e) {
			String errorMessage = "Can't launch app";
			callbackContext.error(errorMessage);
			Log.e(LOG_TAG, errorMessage, e);
		} finally {
		}
	}

	public Bitmap appicon(String pkg) {
		if (apps==null) { applist_load(false); }
		//A: cargamos lista de apps

		ApplicationInfo app= null;
		for (int i = 0; app==null && i<apps.size(); i++) {
			ApplicationInfo x= apps.get(i);
			if (pkg.equals( x.packageName )) { app= x; }
		}
		if (app==null) { 
			Log.e(LOG_TAG,"pkg not found: "+pkg);
			return null; 
		}
		//A: encontramos app

		File mApkFile = new File(app.sourceDir);
		if (mApkFile.exists()) {
			//A: esta montada la app (SD, etc.)
			Drawable mIcon = app.loadIcon(Pm);
			if (mIcon instanceof BitmapDrawable) {
				BitmapDrawable bitmapDrawable = (BitmapDrawable) mIcon;
				if(bitmapDrawable.getBitmap() != null) {
					return bitmapDrawable.getBitmap();
				}
			}
		}
		else {
			Log.e(LOG_TAG,"pkg file not found: "+pkg+" "+app.sourceDir);
		}
		//A: si estaba disponible (ej. en la SD) devolvimos el icono

		return null;
	}

	private String getIcon(ApplicationInfo app) {
        File mApkFile = new File(app.sourceDir);
        if (mApkFile.exists()) {
            Drawable mIcon = app.loadIcon(Pm);
            if (mIcon instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) mIcon;
                if(bitmapDrawable.getBitmap() != null) {
                    Bitmap bmp = bitmapDrawable.getBitmap();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream .toByteArray();
                    String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    return encoded;
                }
            }
        }
        else {
            Log.e(LOG_TAG,"pkg file not found: "+app.packageName);
        }
        //A: si estaba disponible (ej. en la SD) devolvimos el icono

        return "";
    }

}
