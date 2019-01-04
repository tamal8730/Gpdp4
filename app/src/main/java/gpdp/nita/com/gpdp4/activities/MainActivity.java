package gpdp.nita.com.gpdp4.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import gpdp.nita.com.gpdp4.R;
import gpdp.nita.com.gpdp4.adapters.MenuAdapter;
import gpdp.nita.com.gpdp4.helpers.DatabaseHelper;
import gpdp.nita.com.gpdp4.helpers.InternetCheckAsync;
import gpdp.nita.com.gpdp4.helpers.MyJson;
import gpdp.nita.com.gpdp4.helpers.Upload;
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

    ArrayList<MainMenuModel> models;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Surveyor profile");

        recyclerView = findViewById(R.id.main_menu_recycler);
        models = new ArrayList<>();

        models.add(new MainMenuModel("Add beneficiary", R.drawable.ic_add_user));
        models.add(new MainMenuModel("View all beneficiaries", R.drawable.ic_list));
        models.add(new MainMenuModel("Sync status", R.drawable.ic_sync));
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

        subdivision = findViewById(R.id.subdivision);
        block = findViewById(R.id.block);
        gpvc = findViewById(R.id.gp_vc);
        district = findViewById(R.id.district);
        surveyorName = findViewById(R.id.surveyor_name);
        surveyorCode = findViewById(R.id.surveyor_code);
        surveyorImg = findViewById(R.id.surveyor_img);
        gpVcType = findViewById(R.id.gp_vc_type);

        String[] tokens = mSharedPrefLogin.getString(Constants.KEY_SERVER_RESPONSE, "")
                .split("#");

        mSharedPrefAuto.edit()
                .putString("district", tokens[3])
                .putString("subdivision", tokens[4])
                .putString("block_name", tokens[5])
                .putString("gp_vc_name", tokens[6])
                .putString("surveyor_id", tokens[2])
                .putString("gp_vc_type", tokens[7])
                .apply();

        new InternetCheckAsync(new InternetCheckAsync.Consumer() {
            @Override
            public void accept(Boolean isConnected) {
                if (isConnected) {
                    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                    queue.add(upload.requestJSONForUpdates(Constants.TABLES_TO_BE_DOWNLOADED_AFTER_LOGIN[0], true));
                } else {
                    if (!upload.allFilesExist(Constants.TABLES_TO_BE_DOWNLOADED_AFTER_LOGIN)) {
                        Toast.makeText(MainActivity.this, "Cannot proceed", Toast.LENGTH_SHORT).show();
                    } else {

                    }
                }
            }
        });


        String imgUrl = tokens[0].trim();

        Glide
                .with(this)
                .setDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_default_avatar)
                        .error(R.drawable.ic_default_avatar))
                .load(imgUrl)
                .into(surveyorImg);

        surveyorName.setText(tokens[1]);
        surveyorCode.setText(tokens[2]);
        district.setText(tokens[3]);
        subdivision.setText(tokens[4]);
        block.setText(tokens[5]);
        gpvc.setText(tokens[6]);
        gpVcType.setText(tokens[7]);


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
        });

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void inputBencode() {
        toScanner();
    }

    private void toScanner() {
        Intent toScanner = new Intent(this, ScannerActivity.class);
        startActivity(toScanner);
    }

    private void syncFromExternalStorage() {
        String surveyorId = mSharedPrefAuto.getString("surveyor_id", "unknown");
        String path = "gpdp/backups/" + surveyorId;
        File root = new File(Environment.getExternalStorageDirectory(), path);
        if (root.exists()) {
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
                JSONArray payload = new JSONArray();
                payload.put(text);
                upload.sendJSONArray(payload, DatabaseHelper.ben_code, surveyorId);
            }
        }

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
                if (tokens[0].equals("add")) {

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

