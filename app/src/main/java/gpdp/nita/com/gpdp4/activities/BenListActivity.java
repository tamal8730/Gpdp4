package gpdp.nita.com.gpdp4.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

import gpdp.nita.com.gpdp4.R;
import gpdp.nita.com.gpdp4.adapters.BenListAdapter;
import gpdp.nita.com.gpdp4.models.BenModel;
import gpdp.nita.com.gpdp4.repositories.Constants;

public class BenListActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    SharedPreferences benList;
    ArrayList<BenModel> models;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ben_list);
        setTitle("Beneficiaries");

        models = new ArrayList<>();

        benList = this.getSharedPreferences(Constants.BEN_NAMES_SHARED_PREFS, Context.MODE_PRIVATE);

        Map<String, ?> all = benList.getAll();
        for (String keys : all.keySet()) {
            models.add(new BenModel(keys, benList.getString(keys, "")));
        }

        recyclerView = findViewById(R.id.ben_recycler);
        BenListAdapter adapter = new BenListAdapter(models, this);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }
}
