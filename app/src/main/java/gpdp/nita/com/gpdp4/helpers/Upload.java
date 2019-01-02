package gpdp.nita.com.gpdp4.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import gpdp.nita.com.gpdp4.interfaces.OnJsonsDownloaded;
import gpdp.nita.com.gpdp4.repositories.Constants;

public class Upload {

    private static final Upload instance = new Upload();
    public static int filesDownloaded = 0;
    private ProgressDialog progressDialog;
    private OnJsonsDownloaded onJsonDownloadedListener;

    private Upload() {
    }

    public static Upload getInstance() {
        return instance;
    }

    public void setOnJsonDownloadedListener(OnJsonsDownloaded onJsonDownloadedListener) {
        this.onJsonDownloadedListener = onJsonDownloadedListener;
    }


    public void sendJSONArray(JSONArray payload, final String ben_code, final Context context, String surveyorCode) {

        saveJsonOnExternalStorage(context, ben_code + ".json", payload.toString(), "backups/" + surveyorCode);

//        progressDialog = new ProgressDialog(context);
//        progressDialog.setMessage("Please Wait, We are sending your data on Server");
//        progressDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, Constants.HTTP_URL, payload,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            String res = response.getString(0);
                            Toast.makeText(context, ben_code + " synced", Toast.LENGTH_SHORT).show();
//                            progressDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, ben_code + " not synced", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        progressDialog.dismiss();
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonArrayRequest);
    }


    public void sendImage(Bitmap bitmap, final Context context, final String ben_code) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        StringRequest request = new StringRequest(Request.Method.POST, Constants.UPLOAD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error uploading ben image", Toast.LENGTH_LONG).show();
            }
        })


        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("image", imageString);
                parameters.put("img_name", ben_code);
                return parameters;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }


    private String getHashFromFileInputStream(InputStream fileStream) {
        StringBuilder returnVal = new StringBuilder();
        try {
            byte[] buffer = new byte[1024];
            MessageDigest md5Hash = MessageDigest.getInstance("MD5");
            int numRead = 0;
            while (numRead != -1) {
                numRead = fileStream.read(buffer);
                if (numRead > 0) {
                    md5Hash.update(buffer, 0, numRead);
                }
            }
            fileStream.close();

            byte[] md5Bytes = md5Hash.digest();
            for (byte md5Byte : md5Bytes) {
                returnVal.append(Integer.toString((md5Byte & 0xff) + 0x100, 16).substring(1));
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return returnVal.toString().toUpperCase();
    }

    private String getHashFromFileName(String filename, boolean afterLogin) {
        InputStream is;
        try {
            String fileName = "gpdp/data/" + (afterLogin ? filename.split(" ")[0] : filename) + ".json";
            String path = Environment.getExternalStorageDirectory() + "/" + fileName;
            File file = new File(path);
            if (!file.exists()) return " ";
            is = new FileInputStream(file);
            return getHashFromFileInputStream(is);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject encapsulateHashInJson(String filename, boolean afterLogin, Context context) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", afterLogin ? filename.split(" ")[0] : filename);
            jsonObject.put("hash", getHashFromFileName(filename, afterLogin));
            jsonObject.put("data", getExtraDataToSend(filename, afterLogin, context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONObject getExtraDataToSend(String filename, boolean afterLogin, Context context) {
        JSONObject extras = new JSONObject();
        if (!afterLogin) return extras;
        else {
            String[] tokens = filename.split(" ");
            String val = context.getSharedPreferences(Constants.AUTO_VALUES, Context.MODE_PRIVATE)
                    .getString(tokens[2], "");
            try {
                extras.put(tokens[1], val);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return extras;
    }

    public void requestJSONForUpdates(final String filename, final Context context, final boolean afterLogin) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                Constants.UPDATE_TABLES,
                encapsulateHashInJson(filename, afterLogin, context),

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int error = response.getInt("error");
                            if (error == 1) {
                                String s = response.getString("data");
                                saveJsonOnExternalStorage(context,
                                        afterLogin ? filename.split(" ")[0] : filename
                                                + ".json", s, "data");

                                filesDownloaded++;
                            } else if (error == 2) {
                                filesDownloaded++;
                            } else if (error == 0) {
                                onError();
                            }
                            if (filesDownloaded == Constants.master_tables.length) {
                                onSuccess();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            onError();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        onError();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    private void onSuccess() {
        onJsonDownloadedListener.onSuccess();
    }

    private void onError() {
        onJsonDownloadedListener.onError();
    }

    public boolean allFilesExist(String[] fileNames) {
        File root = new File(Environment.getExternalStorageDirectory(), "gpdp/data/");

        if (!root.exists()) return false;
        String[] files = root.list();
        if (files.length == 0) return false;

        ArrayList<String> filesList = new ArrayList<>(Arrays.asList(files));
        for (String file : fileNames) {
            if (!filesList.contains(file + ".json")) {
                Log.d("posxx", file);
                return false;
            }
        }
        return true;
    }

    private void saveJsonOnExternalStorage(Context context, String sFileName, String sBody, String subFolder) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "gpdp/" + subFolder + "/");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
