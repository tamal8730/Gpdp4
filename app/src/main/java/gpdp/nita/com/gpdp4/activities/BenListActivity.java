package gpdp.nita.com.gpdp4.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import gpdp.nita.com.gpdp4.R;
import gpdp.nita.com.gpdp4.adapters.BenListAdapter;
import gpdp.nita.com.gpdp4.helpers.DatabaseHelper;
import gpdp.nita.com.gpdp4.models.BenModel;
import gpdp.nita.com.gpdp4.repositories.Constants;

public class BenListActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    SharedPreferences benList;
    ArrayList<BenModel> models;

    SharedPreferences mAutoValuesSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ben_list);
        setTitle("Beneficiaries");

        ConstraintLayout overlay = findViewById(R.id.overlay);

        mAutoValuesSharedPref = this.getSharedPreferences(Constants.AUTO_VALUES, Context.MODE_PRIVATE);

        String sCode = mAutoValuesSharedPref.getString("surveyor_id", "");

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        ArrayList<String> bens = dbHelper.getAllBeneficiaries(sCode);

        models = new ArrayList<>();

        for (int i = 0; i < bens.size(); i++) {
            models.add(new BenModel(bens.get(i), DatabaseHelper.getImageURL(bens.get(i))));
        }

        if (models.size() == 0) {
            overlay.setVisibility(View.VISIBLE);
        } else {
            overlay.setVisibility(View.GONE);
        }

//        benList = this.getSharedPreferences(Constants.BEN_NAMES_SHARED_PREFS, Context.MODE_PRIVATE);
//
//        Map<String, ?> all = benList.getAll();
//        for (String keys : all.keySet()) {
//            models.add(new BenModel(keys, benList.getString(keys, "")));
//        }

        recyclerView = findViewById(R.id.ben_recycler);
        BenListAdapter adapter = new BenListAdapter(models, this);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }
}
