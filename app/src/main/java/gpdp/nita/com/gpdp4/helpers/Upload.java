package gpdp.nita.com.gpdp4.helpers;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import gpdp.nita.com.gpdp4.interfaces.OnFormsEndListener;
import gpdp.nita.com.gpdp4.interfaces.OnJsonsDownloaded;
import gpdp.nita.com.gpdp4.repositories.Constants;

public class Upload {

    private int filesDownloaded;
    //    private ProgressDialog progressDialog;
    private OnJsonsDownloaded onJsonDownloadedListener;
    private OnFormsEndListener onFormsEndListener;
    private Context context;

    public Upload(Context context) {
        this.context = context;
        filesDownloaded = 0;
    }

    public void setOnJsonDownloadedListener(OnJsonsDownloaded onJsonDownloadedListener) {
        this.onJsonDownloadedListener = onJsonDownloadedListener;
    }

    public void setOnFormsEndListener(OnFormsEndListener onFormsEndListener) {
        this.onFormsEndListener = onFormsEndListener;
    }

    public void sendJSONArray(final JSONArray payload, final String ben_code, final String surveyorCode) {

//        progressDialog = new ProgressDialog(context);
//        progressDialog.setMessage("Please wait while we sync your data with our servers.");
//        progressDialog.show();
        onFormsEndListener.onSyncStarted();
        final boolean[] isSuccessful = {true};

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST,
                Constants.HTTP_URL + "?b=" + ben_code + "&s=" + surveyorCode,
                payload,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("resxxx", response.toString());
                        try {
                            String res = response.getString(1);
                            if (res.equalsIgnoreCase("NA")) {
                                isSuccessful[0] = false;
                                showError("You are not authorized for beneficiary " + ben_code);
                            } else {
                                sendSuccess();
                            }
//                            progressDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();

                            saveJsonOnExternalStorage(ben_code + ".json",
                                    payload.toString(),
                                    "backups/" + surveyorCode);

                            showError("");
                        }
//                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        progressDialog.dismiss();

                        saveJsonOnExternalStorage(ben_code + ".json",
                                payload.toString(),
                                "backups/" + surveyorCode);

                        showError("");

                    }
                });

        if (isSuccessful[0])
            Volley.newRequestQueue(context).add(jsonArrayRequest);
    }

    public JsonArrayRequest sendJSONArray(JSONArray payload, String surveyorId) {

        String benCode = "";

        try {
            JSONObject object1 = payload.getJSONObject(0);
            Iterator<String> iterator = object1.keys();
            if (iterator.hasNext()) {
                JSONArray array = object1.getJSONArray(iterator.next());
                benCode = array.getJSONObject(0).getString("ben_code");
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String finalBenCode = benCode;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST,
                Constants.HTTP_URL + "?b=" + benCode + "&s=" + surveyorId,
                payload,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d("resxxx1", response.toString());

                        try {
                            String res = response.getString(1);
                            if (res.equalsIgnoreCase("NA")) {
                                onFormsEndListener.onFormsEnd(false, "You are not authorized for beneficiary " + finalBenCode);
                            } else
                                onFormsEndListener.onFormsEnd(true, "");

                        } catch (JSONException e) {
                            onFormsEndListener.onFormsEnd(false, "");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("syncfailedxxx", error.getMessage());
                        onFormsEndListener.onFormsEnd(false, "");
                    }
                });
        return jsonArrayRequest;
    }

    private void showError(String errorMessage) {
        onFormsEndListener.onFormsEnd(false, errorMessage);
    }

    private void sendSuccess() {
        onFormsEndListener.onFormsEnd(true, "");
    }


    public void sendImage(final String imageString, final String ben_code) {

//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] imageBytes = baos.toByteArray();
//        final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);


        StringRequest request = new StringRequest(Request.Method.POST, Constants.UPLOAD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(context, "Error uploading ben image", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("image", imageString);
                parameters.put("img_name", ben_code);
                return parameters;
            }
        };

        Volley.newRequestQueue(context).add(request);
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
            Log.d("posxx", t.getLocalizedMessage());
            t.printStackTrace();
        }
        return returnVal.toString().toUpperCase();
    }

    private String getHashFromFileName(String filename) {
        InputStream is;
        try {
            String fileName = "gpdp/data/" + filename + ".json";
            String path = Environment.getExternalStorageDirectory() + "/" + fileName;
            File file = new File(path);
            if (!file.exists()) return " ";
            is = new FileInputStream(file);
            return getHashFromFileInputStream(is);

        } catch (IOException e) {
            Log.d("posxx", e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject encapsulateHashInJson(String filename) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", filename.split(" ")[0]);
            jsonObject.put("hash", getHashFromFileName(filename.split(" ")[0]));
            jsonObject.put("data", getExtraDataToSend(filename));
        } catch (JSONException e) {
            Log.d("posxx", e.getLocalizedMessage());
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONObject getExtraDataToSend(String filename) {
        JSONObject extras = new JSONObject();
        if (filename.equals(filename.split(" ")[0])) return extras;
        else {
            String[] tokens = filename.split(" ");
            String val = context.getSharedPreferences(Constants.AUTO_VALUES, Context.MODE_PRIVATE)
                    .getString(tokens[2], "");
            try {
                extras.put(tokens[1], val);
            } catch (JSONException e) {
                Log.d("posxx", e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        return extras;
    }

    public void requestMultipleJSONForUpdates() {
        String lastUpdated = getLastUpdateDate();

    }

    private String getLastUpdateDate() {

        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, "lastupdated.txt");

        if (!file.exists()) {
            return "";
        }

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            return "";
        }

        return text.toString();
    }

    private void setLastUpdateDate(String date) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "gpdp/");
            if (!root.exists()) {
                if (!root.mkdirs()) {
                    cannotCreateDirs();
                }
            }
            File file = new File(root, "lastupdated.txt");

            FileWriter writer = new FileWriter(file);
            writer.append(date);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JsonObjectRequest requestOneJSONForUpdates(final String filename, final boolean afterLogin) {

        Log.d("dexxx", encapsulateHashInJson(filename).toString());

        return new JsonObjectRequest(Request.Method.POST,
                Constants.UPDATE_TABLES,
                encapsulateHashInJson(filename),

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("dexxx", response.toString() + " hell");
                        try {
                            int error = response.getInt("error");
                            if (error == 1) {
                                String s = response.getString("data");
                                saveJsonOnExternalStorage(filename.split(" ")[0] + ".json", s, "data");

                                filesDownloaded++;
                            } else if (error == 2) {
                                filesDownloaded++;
                            } else if (error == 0) {
                                onError("404");
                            }
                            if (filesDownloaded == (!afterLogin ? Constants.master_tables.length : Constants.TABLES_TO_BE_DOWNLOADED_AFTER_LOGIN.length)) {
                                onSuccess();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            onError(e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.d("dexxx", error.toString() + " hell");
                        onError(error.getMessage());
                    }
                });
    }

    private void onSuccess() {
        onJsonDownloadedListener.onSuccess();
    }

    private void onError(String errorMessage) {
        onJsonDownloadedListener.onError(errorMessage);
    }

    public boolean allFilesExist(String[] fileNames) {
        File root = new File(Environment.getExternalStorageDirectory(), "gpdp/data/");

        if (!root.exists()) return false;
        String[] files = root.list();
        if (files.length == 0) return false;

        ArrayList<String> filesList = new ArrayList<>(Arrays.asList(files));
        for (String file : fileNames) {
            file = file.split(" ")[0];
            if (!filesList.contains(file + ".json")) {
                return false;
            }
        }
        return true;
    }

    private void saveJsonOnExternalStorage(String sFileName, String sBody, String subFolder) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "gpdp/" + subFolder + "/");
            if (!root.exists()) {
                if (!root.mkdirs()) {
                    cannotCreateDirs();
                }
            }
            File gpxfile = new File(root, sFileName);

            FileWriter writer = new FileWriter(gpxfile);

            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cannotCreateDirs() {

    }
}
