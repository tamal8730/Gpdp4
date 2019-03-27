package gpdp.nita.com.gpdp4.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gpdp.nita.com.gpdp4.R;
import gpdp.nita.com.gpdp4.adapters.BenListAdapter;
import gpdp.nita.com.gpdp4.helpers.DatabaseHelper;
import gpdp.nita.com.gpdp4.helpers.MyJson;
import gpdp.nita.com.gpdp4.interfaces.OnBenListItemSelected;
import gpdp.nita.com.gpdp4.models.BenModel;
import gpdp.nita.com.gpdp4.repositories.Constants;

public class BenListActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    SharedPreferences benList;
    ArrayList<BenModel> models;

    SharedPreferences mAutoValuesSharedPref;
    BenListAdapter adapter;

    SearchView searchView;

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

        MyJson myJson = new MyJson(this);

        models = new ArrayList<>();

        for (int i = 0; i < bens.size(); i++) {
            JSONObject benStatus = myJson.getStatusAndCompleteCount(bens.get(i));
            int status = 0;
            int count = 26;
            try {
                status = Integer.parseInt(benStatus.getString("status"));
                count = benStatus.getInt("incomplete");
            } catch (JSONException e) {

            }

            models.add(
                    new BenModel(
                            bens.get(i),
                            DatabaseHelper.getImageURL(bens.get(i)),
                            dbHelper.getFamilyHeadName(bens.get(i)),
                            count,
                            status
                    )
            );
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
        adapter = new BenListAdapter(models, this, new OnBenListItemSelected() {
            @Override
            public void onBenListItemSelected(String benCode) {
                toForms(benCode);
            }
        });

        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ben_list_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        return true;
    }

    private void toForms(String benCode) {
        Intent toForms = new Intent(this, FormsActivity.class);
        toForms.putExtra("ben_code", benCode);
        startActivity(toForms);
    }
}
