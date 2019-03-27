package gpdp.nita.com.gpdp4.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import gpdp.nita.com.gpdp4.R;
import gpdp.nita.com.gpdp4.adapters.MenuAdapter;
import gpdp.nita.com.gpdp4.helpers.CustomAutoCompleteTextView;
import gpdp.nita.com.gpdp4.helpers.InternetCheckAsync;
import gpdp.nita.com.gpdp4.helpers.MyJson;
import gpdp.nita.com.gpdp4.helpers.Upload;
import gpdp.nita.com.gpdp4.interfaces.OnFormsEndListener;
import gpdp.nita.com.gpdp4.interfaces.OnJsonsDownloaded;
import gpdp.nita.com.gpdp4.interfaces.OnMenuItemSelected;
import gpdp.nita.com.gpdp4.models.MainMenuModel;
import gpdp.nita.com.gpdp4.repositories.Constants;

public class MainActivity extends AppCompatActivity {

    TextView subdivision, block, gpvc, district, surveyorName, surveyorCode, gpVcType;
    CircleImageView surveyorImg;

    SharedPreferences mSharedPrefLogin, mSharedPrefAuto;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    Upload upload;

    HashMap<String, String> benData;

    Dialog dialog;
    ProgressDialog progressDialog;

    ArrayList<MainMenuModel> models;

    RequestQueue requestQueue;

    int failedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Surveyor profile");

        dialog = new Dialog(this);
        progressDialog = new ProgressDialog(this);

        recyclerView = findViewById(R.id.main_menu_recycler);
        models = new ArrayList<>();

        models.add(new MainMenuModel("Add beneficiary", R.drawable.ic_add_user));
        models.add(new MainMenuModel("View all beneficiaries", R.drawable.ic_list));
        models.add(new MainMenuModel("Sync from backups", R.drawable.ic_sync));
        models.add(new MainMenuModel("Logout", R.drawable.ic_logout));


        mSharedPrefLogin = this.getSharedPreferences(Constants.REMEMBER_LOGIN, Context.MODE_PRIVATE);
        mSharedPrefAuto = this.getSharedPreferences(Constants.AUTO_VALUES, Context.MODE_PRIVATE);

        upload = new Upload(this);

        upload.setOnJsonDownloadedListener(new OnJsonsDownloaded() {
            @Override
            public void onSuccess() {
                Log.d("posxx", "success");
            }

            @Override
            public void onError(String errorMessage) {
                Log.d("posxx", errorMessage);
            }
        });

        upload.setOnFormsEndListener(new OnFormsEndListener() {
            @Override
            public void onSyncStarted() {

            }

            @Override
            public void onFormsEnd(boolean isSuccessful, String errorMessage) {
                if (!isSuccessful) onError();
            }
        });

        subdivision = findViewById(R.id.subdivision);
        block = findViewById(R.id.block);
        gpvc = findViewById(R.id.gp_vc);
        district = findViewById(R.id.district);
        surveyorName = findViewById(R.id.surveyor_name);
        surveyorCode = findViewById(R.id.surveyor_code);
        surveyorImg = findViewById(R.id.surveyor_img);
        gpVcType = findViewById(R.id.gp_vc_type);

        try {
            JSONObject response = new JSONObject(mSharedPrefLogin.getString(Constants.KEY_SERVER_RESPONSE, ""));

            mSharedPrefAuto.edit()
                    .putString("surveyor_name", response.getString("sv_name"))
                    .putString("district", response.getString("sv_district"))
                    .putString("subdivision", response.getString("sv_subdivision"))
                    .putString("block_name", response.getString("sv_block"))
                    .putString("gp_vc_name", response.getString("sv_gp_vc_name"))
                    .putString("surveyor_id", response.getString("sv_code"))
                    .putString("gp_vc_type", response.getString("sv_gp_vc_type"))
                    .putString("surveyor_img_url", response.getString("sv_image_link"))
                    .apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }


        new InternetCheckAsync(new InternetCheckAsync.Consumer() {
            @Override
            public void accept(Boolean isConnected) {
                if (isConnected) {
                    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                    queue.add(upload.requestOneJSONForUpdates(Constants.TABLES_TO_BE_DOWNLOADED_AFTER_LOGIN[0], true));
                    queue.add(upload.requestOneJSONForUpdates(Constants.TABLES_TO_BE_DOWNLOADED_AFTER_LOGIN[1], true));
                } else {
                    if (!upload.allFilesExist(Constants.TABLES_TO_BE_DOWNLOADED_AFTER_LOGIN)) {
                        Toast.makeText(MainActivity.this, "Cannot proceed", Toast.LENGTH_SHORT).show();
                    } else {

                    }
                }
            }
        });


        String imgUrl = mSharedPrefAuto.getString("surveyor_img_url", "").trim();

        Glide
                .with(this)
                .setDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_default_avatar)
                        .error(R.drawable.ic_default_avatar))
                .load(imgUrl)
                .into(surveyorImg);

        surveyorName.setText(mSharedPrefAuto.getString("surveyor_name", ""));
        surveyorCode.setText(mSharedPrefAuto.getString("surveyor_id", ""));
        district.setText(mSharedPrefAuto.getString("district", ""));
        subdivision.setText(mSharedPrefAuto.getString("subdivision", ""));
        block.setText(mSharedPrefAuto.getString("block_name", ""));
        gpvc.setText(mSharedPrefAuto.getString("gp_vc_name", ""));
        gpVcType.setText(mSharedPrefAuto.getString("gp_vc_type", ""));


        MenuAdapter adapter = new MenuAdapter(this, models, new OnMenuItemSelected() {
            @Override
            public void onLogout() {
                toLogin();
            }

            @Override
            public void onAddBen() {
                inputBencode();
            }

            @Override
            public void onSync() {
                syncFromExternalStorage();
            }

            @Override
            public void onViewAll() {
                toBenList();
            }
        });

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void toBenList() {
        Intent toBenList = new Intent(this, BenListActivity.class);
        startActivity(toBenList);
    }

    private void onError() {
        failedCount++;
    }

    private void inputBencode() {

        dialog.setContentView(R.layout.layout_ben_input);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        ImageView qr = dialog.findViewById(R.id.qr_ben_code);
        ImageButton go = dialog.findViewById(R.id.btn_go_ben);
        final CustomAutoCompleteTextView editText = dialog.findViewById(R.id.edt_ben_code);

        final ArrayList<String> suggestionsArray = new MyJson(this).getSuggestionsList("ben_list");

        ArrayAdapter<String> suggestions = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, suggestionsArray);


        editText.setThreshold(0);
        editText.setAdapter(suggestions);

        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toScanner();
                dialog.dismiss();
            }
        });

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String benCode = editText.getText().toString().trim();

                if (benCode.trim().equals("")) {
                    Toast.makeText(MainActivity.this, "Enter a beneficiary code first", Toast.LENGTH_SHORT).show();
                } else if (!suggestionsArray.contains(benCode)) {
                    Toast.makeText(MainActivity.this, "Invalid beneficiary code", Toast.LENGTH_SHORT).show();
                } else
                    toForms(benCode);
            }
        });
    }

    private void toForms(String benCode) {
        dialog.dismiss();
        Intent toForms = new Intent(this, FormsActivity.class);
        toForms.putExtra("ben_code", benCode);
        startActivity(toForms);
    }

    private void toScanner() {
        Intent toScanner = new Intent(this, ScannerActivity.class);
        startActivity(toScanner);
    }

    private void syncFromExternalStorage() {

        final int[] c = {0};
        failedCount = 0;

        progressDialog.setMessage("Please wait while we sync your data with our servers.");
        progressDialog.show();

        String surveyorId = mSharedPrefAuto.getString("surveyor_id", "unknown");
        String path = "gpdp/backups/" + surveyorId;


        uploadImages(surveyorId);


        final File root = new File(Environment.getExternalStorageDirectory(), path);
        if (root.exists()) {

            benData = new HashMap<>();

            final int numberOfFiles = root.list().length;
            if (numberOfFiles == 0) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "No backup files found", Toast.LENGTH_SHORT).show();
            }

            requestQueue = Volley.newRequestQueue(this);

            for (String fileName : root.list()) {
                StringBuilder text = new StringBuilder();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(root + "/" + fileName));
                    String line;
                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    br.close();

                } catch (IOException e) {
                    Toast.makeText(this, "Error reading files", Toast.LENGTH_SHORT).show();
                }
                JSONArray payload = null;
                try {

                    payload = new JSONArray(text.toString());
                    JSONObject jsonObj = payload.getJSONObject(0);
                    JSONArray jsonArray = jsonObj.getJSONArray("gpdp_basic_info_1");
                    JSONObject obj = jsonArray.getJSONObject(0);

                    String img_url = obj.getString("ben_image");

                    benData.put(fileName.replace(".json", ""), img_url);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                requestQueue.add(upload.sendJSONArray(payload, mSharedPrefAuto.getString("surveyor_id", "")));
            }
            requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<JSONArray>() {
                @Override
                public void onRequestFinished(Request<JSONArray> request) {
                    c[0]++;
                    if (c[0] == numberOfFiles) {
                        onSuccess(c[0]);
                    }
                }
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(MainActivity.this, "No backup files found", Toast.LENGTH_SHORT).show();
        }
    }


    private String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private String getStringFromFile(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure to close all streams.
        fin.close();
        return ret;
    }

    private ArrayList<String> getAllFileNames(String surveyorId) {

        File root = new File(Environment.getExternalStorageDirectory(), "gpdp/images/" + surveyorId);

        if (!root.exists()) return null;
        String[] files = root.list();
        if (files.length == 0) return null;

        return new ArrayList<>(Arrays.asList(files));
    }


    private boolean uploadImages(String surveyorId) {

        ArrayList<String> fileNames = getAllFileNames(surveyorId);
        if (fileNames == null) return false;
        String path = Environment.getExternalStorageDirectory() + "/gpdp/images/" + surveyorId;
        for (String fileName : fileNames) {
            try {
                upload.sendImage(getStringFromFile(path + "/" + fileName), fileName.replace(".txt", ""));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    private void onSuccess(final int total) {

        final Dialog dialog = new Dialog(this);
        Button button;

        if (failedCount == 0) {

            for (String ben : benData.keySet()) {

                this.getSharedPreferences(Constants.BEN_NAMES_SHARED_PREFS, Context.MODE_PRIVATE)
                        .edit()
                        .putString(ben, benData.get(ben))
                        .apply();
            }

            dialog.setContentView(R.layout.layout_success);
            button = dialog.findViewById(R.id.success_to_main);
            TextView message = dialog.findViewById(R.id.success_message);
            message.setText(R.string.sync_successful_multiple_files);
            button.setText(R.string.ok);
        } else if (failedCount == total) {
            dialog.setContentView(R.layout.layout_error);
            button = dialog.findViewById(R.id.error_tomain);
            TextView message = dialog.findViewById(R.id.error_message);
            message.setText(R.string.sync_failed_multiple_files);
            button.setText(R.string.try_again);
        } else {
            dialog.setContentView(R.layout.layout_sync_all_dialog);
            button = dialog.findViewById(R.id.btn_error_partial);
            button.setText(R.string.try_again);
            TextView message = dialog.findViewById(R.id.txt_message_error);
            message.setText((total - failedCount) + " entries synced successfully. " + failedCount + " failed. Try again");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (failedCount == 0) {
                } else {
                    syncFromExternalStorage();
                }
                dialog.dismiss();
            }
        });
        progressDialog.dismiss();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void toForm() {
        Intent toForms = new Intent(this, FormsActivity.class);
        startActivity(toForms);
    }

    private void toLogin() {
        Intent toLogin = new Intent(this, LoginActivity.class);
        startActivity(toLogin);
        finish();
    }


    @Override
    protected void onStart() {
        super.onStart();
        initJsons();
    }

    private void initJsons() {

        Constants.repeatedIndices.clear();
        Constants.formSequence.clear();
        Constants.subTitles.clear();

        MyJson myJson = new MyJson(this).initLists();
        myJson.setTableAndColumnList();
        ArrayList<String> loopList = myJson.getLoopList();
        Constants.looplist = loopList;

        for (int i = 0; i < loopList.size(); i++) {
            if (loopList.get(i) == null) {
                Constants.formSequence.add(i);
                Constants.repeatedIndices.add("");
                Constants.subTitles.add("");
            } else {
                String[] tokens = loopList.get(i).split(" ");
                if (tokens[0].equals("add") || tokens[0].equals("num_stu")) { //or num_stu

                } else {
                    ArrayList<Object> keys = MyJson.getSpinnerKeys(tokens[0], 0);
                    ArrayList<String> vals = MyJson.getSpinnerList(tokens[0]);
                    int l = keys.size() - 1;
                    for (int j = 0; j < l; j++) {
                        Constants.repeatedIndices.add((keys.get(1 + j)).toString());
                        Constants.formSequence.add(i);
                        Constants.subTitles.add(vals.get(1 + j));
                    }
                }
            }
        }
        for (int i = 0; i < Constants.formSequence.size(); i++) {
            Log.d("posxx", Constants.formSequence.get(i) + " " + Constants.repeatedIndices.get(i));
        }
    }
}

