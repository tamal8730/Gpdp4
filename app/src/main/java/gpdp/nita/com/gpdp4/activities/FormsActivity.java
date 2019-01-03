package gpdp.nita.com.gpdp4.activities;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
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
import gpdp.nita.com.gpdp4.interfaces.OnValuesEnteredListener;
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

    CircleImageView circleImageView;


    String benCode;

    Object[] answersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forms);

        if (getIntent().getExtras() != null)
            benCode = getIntent().getExtras().getString("ben_code");
        else {
            benCodeError();
        }
        FormsViewModel.formNumber = 0;

        recyclerView = findViewById(R.id.recycler);
        mNext = findViewById(R.id.btn_next);
        mPrev = findViewById(R.id.btn_prev);

        formsViewModel = ViewModelProviders.of(this, new MyCustomViewModelFactory(this.getApplication(), benCode))
                .get(FormsViewModel.class);

        Constants.initFormList();

        formsViewModel.getFormsModel().observe(this, new Observer<List<FormsModel>>() {
            @Override
            public void onChanged(@Nullable List<FormsModel> formsModels) {
                mFormsModels = formsModels;
                adapter.notifyDataSetChanged();
                linearLayoutManager.scrollToPosition(0);
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

        initRecyclerView();


        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formsViewModel.insert(answersList);
                formsViewModel.loadNext();
            }
        });

        mPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formsViewModel.loadPrev();
            }
        });

    }

    private void benCodeError() {

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
    public void onViewRemoved(int start, int batchSize) {
        //formsViewModel.deleteViewAtRange(start,batchSize);
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
        this.circleImageView = circleImageView;
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
                        .into(circleImageView);

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
                .into(circleImageView);
    }
}
