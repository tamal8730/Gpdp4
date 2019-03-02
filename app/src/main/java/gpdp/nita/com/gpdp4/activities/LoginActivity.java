package gpdp.nita.com.gpdp4.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;

import gpdp.nita.com.gpdp4.R;
import gpdp.nita.com.gpdp4.repositories.Constants;

public class LoginActivity extends AppCompatActivity {

    ImageView logo;
    Button btnLogin;
    EditText surveyorId, password;
    SharedPreferences mSharedPrefLogin;
    CheckBox checkBox;
    ConstraintLayout root;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSharedPrefLogin = this.getSharedPreferences(Constants.REMEMBER_LOGIN, Context.MODE_PRIVATE);
        if (mSharedPrefLogin.getBoolean(Constants.KEY_LOGGED_IN, false)) {
            toMain();
        }

        logo = findViewById(R.id.logo);
        btnLogin = findViewById(R.id.btn_login);
        surveyorId = findViewById(R.id.edt_surveyor_code);
        password = findViewById(R.id.edt_password);
        checkBox = findViewById(R.id.remember_password);
        root = findViewById(R.id.root);

        setTitle("Login");

        Glide
                .with(this)
                .load(R.drawable.gpdp_logo)
                .into(logo);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(surveyorId.getText().toString(), password.getText().toString());
            }
        });
    }

    private void toMain() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void login(final String id, final String password) {

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(R.layout.layout_progress)
                .create();
        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LOGIN_VALIDATOR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        dialog.dismiss();

                        Log.d("loginxxx", ServerResponse.trim());

                        switch ((ServerResponse.trim())) {
                            case "invalid":

                                Snackbar
                                        .make(root, "Invalid credentials", Snackbar.LENGTH_LONG).show();

                                //Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                                break;
                            case "false":

                                Snackbar
                                        .make(root, "Invalid credentials", Snackbar.LENGTH_LONG).show();

//                                Toast.makeText(LoginActivity.this,
//                                        "Invalid credentials",
//                                        Toast.LENGTH_LONG).show();
                                break;
                            default:
                                if (checkBox.isChecked()) {
                                    mSharedPrefLogin.edit()
                                            .putBoolean(Constants.KEY_LOGGED_IN, true)
                                            .apply();
                                }
                                mSharedPrefLogin.edit()
                                        .putString(Constants.KEY_SERVER_RESPONSE, ServerResponse.trim())
                                        .apply();
                                toMain();
                                break;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        dialog.dismiss();
                        String errorMessage = volleyError.toString();
                        if (errorMessage.contains("Unable to resolve host")) {
                            Snackbar
                                    .make(root, "No internet connection", Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar
                                    .make(root, "Unknown error", Snackbar.LENGTH_LONG).show();
                        }

                        //Toast.makeText(LoginActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("code", id);
                params.put("password", password);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(stringRequest);
    }
}