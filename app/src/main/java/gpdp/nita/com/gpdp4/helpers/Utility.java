package gpdp.nita.com.gpdp4.helpers;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

public class Utility {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 321;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context, int code, String title, String message) {
        int currentAPIVersion = Build.VERSION.SDK_INT;

        final String ctxcompat = code == 0 ? Manifest.permission.READ_EXTERNAL_STORAGE
                : Manifest.permission.CAMERA;

        final int permission_code = code == 0 ? MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                : MY_PERMISSIONS_REQUEST_CAMERA;

        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, ctxcompat) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, ctxcompat)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle(title);
                    alertBuilder.setMessage(message);
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{ctxcompat}, permission_code);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{ctxcompat}, permission_code);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}