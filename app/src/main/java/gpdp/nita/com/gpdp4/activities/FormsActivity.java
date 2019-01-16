package gpdp.nita.com.gpdp4.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import gpdp.nita.com.gpdp4.R;
import gpdp.nita.com.gpdp4.adapters.FormsAdapter;
import gpdp.nita.com.gpdp4.helpers.DatabaseHelper;
import gpdp.nita.com.gpdp4.helpers.Upload;
import gpdp.nita.com.gpdp4.helpers.Utility;
import gpdp.nita.com.gpdp4.interfaces.OnDependentSpinnerItemSelected;
import gpdp.nita.com.gpdp4.interfaces.OnFormsEndListener;
import gpdp.nita.com.gpdp4.interfaces.OnValuesEnteredListener;
import gpdp.nita.com.gpdp4.interfaces.OnViewModifiedListener;
import gpdp.nita.com.gpdp4.models.FormsModel;
import gpdp.nita.com.gpdp4.repositories.Constants;
import gpdp.nita.com.gpdp4.viewmodel.FormsViewModel;
import gpdp.nita.com.gpdp4.viewmodel.MyCustomViewModelFactory;

public class FormsActivity extends AppCompatActivity implements OnValuesEnteredListener {


    private static final int GALLERY_REQUEST_CODE = 130;
    private static final int CAMERA_REQUEST_CODE = 140;
    int cameraOrGallery = 0;  //0-cam


    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    FormsAdapter adapter;
    FormsViewModel formsViewModel;
    Button mNext, mPrev;
    List<FormsModel> mFormsModels;
    TextView subtitle;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    CircleImageView mCircleImageView;
    String benCode;

    CircleImageView drawerDp;
    TextView drawerSurveyorCode, drawerName;

    SharedPreferences mAutoValuesSharedPref, benList;
    ProgressDialog progressDialog;

    Object[] answersList;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forms);

        benList = this.getSharedPreferences(Constants.BEN_NAMES_SHARED_PREFS, Context.MODE_PRIVATE);

        dialog = new Dialog(FormsActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mAutoValuesSharedPref = this.getSharedPreferences(Constants.AUTO_VALUES, Context.MODE_PRIVATE);

        if (getIntent().getExtras() != null) {
            benCode = getIntent().getExtras().getString("ben_code");
            DatabaseHelper.ben_code = benCode;
        } else {
            benCodeError();
        }
        FormsViewModel.formNumber = 0;

        recyclerView = findViewById(R.id.recycler);
        mNext = findViewById(R.id.btn_next);
        mPrev = findViewById(R.id.btn_prev);
        toolbar = findViewById(R.id.toolbar);
        subtitle = findViewById(R.id.txt_subtitle);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        drawerDp = navigationView.getHeaderView(0).findViewById(R.id.drawer_dp);
        drawerName = navigationView.getHeaderView(0).findViewById(R.id.drawer_name);
        drawerSurveyorCode = navigationView.getHeaderView(0).findViewById(R.id.drawer_surveyor_code);

        setDrawerHeader();

        navigationView.setCheckedItem(R.id.form0);
        setTitle(navigationView.getCheckedItem().getTitle());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.form0)
                    return selectNavItem(0, menuItem);
                else if (id == R.id.form1)
                    return selectNavItem(1, menuItem);
                else if (id == R.id.form2)
                    return selectNavItem(2, menuItem);
                else if (id == R.id.form3)
                    return selectNavItem(3, menuItem);
                else if (id == R.id.form4)
                    return selectNavItem(4, menuItem);
                else if (id == R.id.form5)
                    return selectNavItem(5, menuItem);
                else if (id == R.id.form6)
                    return selectNavItem(6, menuItem);
                else if (id == R.id.form7)
                    return selectNavItem(7, menuItem);
                else if (id == R.id.form8)
                    return selectNavItem(8, menuItem);
                else if (id == R.id.form9)
                    return selectNavItem(9, menuItem);
                else if (id == R.id.form10)
                    return selectNavItem(10, menuItem);
                else if (id == R.id.form11)
                    return selectNavItem(11, menuItem);
                else if (id == R.id.form12)
                    return selectNavItem(12, menuItem);
                else if (id == R.id.form13)
                    return selectNavItem(13, menuItem);
                else if (id == R.id.form14)
                    return selectNavItem(14, menuItem);
                else if (id == R.id.form15)
                    return selectNavItem(15, menuItem);
                else if (id == R.id.form16)
                    return selectNavItem(16, menuItem);
                else if (id == R.id.form17)
                    return selectNavItem(17, menuItem);
                else if (id == R.id.form18)
                    return selectNavItem(18, menuItem);
                else if (id == R.id.form19)
                    return selectNavItem(19, menuItem);
                else if (id == R.id.form20)
                    return selectNavItem(20, menuItem);
                else if (id == R.id.form21)
                    return selectNavItem(21, menuItem);
                else if (id == R.id.form22)
                    return selectNavItem(22, menuItem);
                else if (id == R.id.form23)
                    return selectNavItem(23, menuItem);
                else if (id == R.id.form24)
                    return selectNavItem(24, menuItem);
                else if (id == R.id.form25)
                    return selectNavItem(25, menuItem);

                return false;
            }
        });


        formsViewModel = ViewModelProviders.of(this, new MyCustomViewModelFactory(this.getApplication(), benCode))
                .get(FormsViewModel.class);

        Constants.initFormList();

        formsViewModel.getFormsModel().observe(this, new Observer<List<FormsModel>>() {
            @Override
            public void onChanged(@Nullable List<FormsModel> formsModels) {

                mFormsModels = formsModels;
                adapter=null;
                linearLayoutManager=null;
                adapter=new FormsAdapter(FormsActivity.this, mFormsModels, new OnValuesEnteredListener() {
                    @Override
                    public void onDateSet(String date, int position) {
                        answersList = formsViewModel.onDateSet(date, position);
                    }

                    @Override
                    public void onViewRemoved(int position, int category) {
                        answersList = formsViewModel.onViewRemoved(position, category);
                    }

                    @Override
                    public void onTyping(String text, int position) {
                        answersList = formsViewModel.onTyping(text, position);
                    }

                    @Override
                    public void onRadioButtonChecked(int checkId, int position) {
                        answersList = formsViewModel.onRadioButtonSelected(checkId, position);
                    }

                    @Override
                    public void onSpinnerItemSelected(Object key, int position) {
                        answersList = formsViewModel.onSpinnerItemSelected(key, position);
                    }

                    @Override
                    public void onProfilePicTapped(CircleImageView circleImageView, int position) {
                        selectImage();
                        mCircleImageView = circleImageView;
                        answersList = formsViewModel.onProfilePictureTapped(position);
                    }
                });

                linearLayoutManager=new LinearLayoutManager(FormsActivity.this);

                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(linearLayoutManager);

                runLayoutAnimation(recyclerView);

                linearLayoutManager.scrollToPosition(0);

                String subT = formsViewModel.getSubTitle();
                if (subT.trim().equals(""))
                    subtitle.setVisibility(View.GONE);
                else {
                    subtitle.setVisibility(View.VISIBLE);
                    subtitle.setText(subT);
                }
            }
        });

        formsViewModel.setOnViewModifiedListener(new OnViewModifiedListener() {

            @Override
            public void onViewModified(int anchorPosition, int id, String[] tokens) {
                adapter.onRadioButtonSelected(anchorPosition, id, tokens);
            }
        });

        formsViewModel.setOnDependentSpinnerItemSelected(new OnDependentSpinnerItemSelected() {
            @Override
            public void onDependentSpinnerItemSelected(int anchorPosition, int selectionPosition, String[] tokens) {
                adapter.onDependentSpinnerItemChosen(anchorPosition, selectionPosition, tokens);
            }
        });

        formsViewModel.getOneRow().observe(this, new Observer<ArrayList<Object>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Object> list) {
                if (list != null && list.size() > 0) {
                    formsViewModel.modify(list);
                }
            }
        });

        formsViewModel.setOnFormsEndListener(new OnFormsEndListener() {
            @Override
            public void onSyncStarted() {
                progressDialog = new ProgressDialog(FormsActivity.this);
                progressDialog.setMessage("Please wait while we sync your data with our servers.");
                progressDialog.show();
            }

            @Override
            public void onFormsEnd(boolean isSuccessful) {
                if (isSuccessful) {
                    benList.edit()
                            .putString(DatabaseHelper.ben_code, DatabaseHelper.getImageURL(DatabaseHelper.ben_code))
                            .apply();
                    dialog.setContentView(R.layout.layout_success);
                    dialog.findViewById(R.id.success_to_main).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toMain();
                        }
                    });
                } else {
                    dialog.setContentView(R.layout.layout_error);
                    dialog.findViewById(R.id.error_tomain).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toMain();
                        }
                    });
                }
                if (!dialog.isShowing()) {
                    dialog.show();
                }

                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });

        initRecyclerView();


        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formsViewModel.insert(answersList);
                int i = formsViewModel.loadNext();
                if (i != -1)
                    navigationView.setCheckedItem(getId(i));
                setTitle(navigationView.getCheckedItem().getTitle());

                adapter.clearCache();
            }
        });


        mPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = formsViewModel.loadPrev();
                if (i != -1)
                    navigationView.setCheckedItem(getId(i));
                setTitle(navigationView.getCheckedItem().getTitle());

                adapter.clearCache();
            }
        });

    }

    private void toMain() {
        Intent toMain = new Intent(this, MainActivity.class);
        toMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(toMain);
        finish();
    }

    private void setDrawerHeader() {
        drawerSurveyorCode.setText(mAutoValuesSharedPref.getString("surveyor_id", ""));
        drawerName.setText(mAutoValuesSharedPref.getString("surveyor_name", ""));
        Glide
                .with(this)
                .setDefaultRequestOptions(new RequestOptions()
                        .error(R.drawable.ic_default_avatar)
                        .placeholder(R.drawable.ic_default_avatar))
                .load(mAutoValuesSharedPref.getString("surveyor_img_url", ""))
                .into(drawerDp);
    }

    private void benCodeError() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.opt_profile) {
            toMain();
            return true;
        } else if (id == R.id.opt_sync) {
            sync();
            return true;
        } else return false;
    }

    private void sync() {
        mNext.callOnClick();
        formsViewModel.onFormsEnd();
    }

    private int getId(int pos) {
        switch (pos) {
            case 0:
                return R.id.form0;
            case 1:
                return R.id.form1;

            case 2:
                return R.id.form2;
            case 3:
                return R.id.form3;

            case 4:
                return R.id.form4;
            case 5:
                return R.id.form5;

            case 6:
                return R.id.form6;
            case 7:
                return R.id.form7;

            case 8:
                return R.id.form8;
            case 9:
                return R.id.form9;

            case 10:
                return R.id.form10;
            case 11:
                return R.id.form11;

            case 12:
                return R.id.form12;
            case 13:
                return R.id.form13;

            case 14:
                return R.id.form14;
            case 15:
                return R.id.form15;

            case 16:
                return R.id.form16;
            case 17:
                return R.id.form17;

            case 18:
                return R.id.form18;
            case 19:
                return R.id.form19;

            case 20:
                return R.id.form20;

            case 21:
                return R.id.form21;
            case 22:
                return R.id.form22;

            case 23:
                return R.id.form23;
            case 24:
                return R.id.form24;

            case 25:
                return R.id.form25;

            default:
                return -1;
        }
    }

    private boolean selectNavItem(int index, MenuItem menuItem) {
        boolean formPresent = formsViewModel.loadFormNumber(index);
        if (formPresent) {

            adapter.clearCache();

            drawerLayout.closeDrawer(GravityCompat.START);
            setTitle(menuItem.getTitle());
            return true;
        } else return false;
    }

    private void initRecyclerView() {
        adapter = new FormsAdapter(this, formsViewModel.getFormsModel().getValue(), this);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemViewCacheSize(30);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDateSet(String date, int position) {
        answersList = formsViewModel.onDateSet(date, position);
    }

    @Override
    public void onViewRemoved(int position, int category) {
        answersList = formsViewModel.onViewRemoved(position, category);
    }

    @Override
    public void onTyping(String text, int position) {
        answersList = formsViewModel.onTyping(text, position);
    }

    @Override
    public void onRadioButtonChecked(int checkId, int position) {
        answersList = formsViewModel.onRadioButtonSelected(checkId, position);
    }

    @Override
    public void onSpinnerItemSelected(Object key, int position) {
        answersList = formsViewModel.onSpinnerItemSelected(key, position);
    }

    @Override
    public void onProfilePicTapped(CircleImageView circleImageView, int position) {
        selectImage();
        this.mCircleImageView = circleImageView;
        answersList = formsViewModel.onProfilePictureTapped(position);
    }


    private void selectImage() {

        final CharSequence[] items = {"Take Photo", "Choose from gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(FormsActivity.this);
        builder.setTitle("Upload profile picture");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (item == 0) {

                    boolean result = Utility.checkPermission(FormsActivity.this, 1, "Permission required"
                            , "GPDP needs camera permission to take photos");
                    cameraOrGallery = 0;
                    if (result) cameraIntent();

                } else if (item == 1) {

                    boolean result = Utility.checkPermission(FormsActivity.this, 0, "Permission required"
                            , "GPDP needs read/write permission to external storage for storing user data");
                    cameraOrGallery = 1;
                    if (result) galleryIntent();

                }
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Photo"), GALLERY_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (cameraOrGallery == 1)
                        galleryIntent();
                } else {
                    Toast.makeText(FormsActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }

            case Utility.MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (cameraOrGallery == 0)
                        cameraIntent();
                } else {
                    Toast.makeText(FormsActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE)
                onSelectFromGalleryResult(data);
            else if (requestCode == CAMERA_REQUEST_CODE)
                onCaptureImageResult(data);
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());

                sendBitmap(bm);

                Glide
                        .with(FormsActivity.this)
                        .setDefaultRequestOptions(new RequestOptions()
                                .placeholder(R.drawable.ic_default_avatar)
                                .error(R.drawable.ic_default_avatar)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true))
                        .load(bm)
                        .into(mCircleImageView);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendBitmap(Bitmap bm) {
        new Upload(this).sendImage(bm, DatabaseHelper.ben_code);
    }

    private void onCaptureImageResult(Intent data) {

        Bitmap thumbnail = null;
        if (data.getExtras() != null) {
            thumbnail = (Bitmap) data.getExtras().get("data");
        }

        sendBitmap(thumbnail);

        Glide
                .with(FormsActivity.this)
                .setDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_default_avatar)
                        .error(R.drawable.ic_default_avatar)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                .load(thumbnail)
                .into(mCircleImageView);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_show_up);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
    }
}