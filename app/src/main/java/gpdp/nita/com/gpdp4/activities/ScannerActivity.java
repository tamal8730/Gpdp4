package gpdp.nita.com.gpdp4.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.zxing.Result;

import gpdp.nita.com.gpdp4.helpers.Utility;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setTitle("Scan");
        setContentView(mScannerView);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Utility.MY_PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(ScannerActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean result = Utility.checkPermission(ScannerActivity.this, 1, "Permission required"
                , "GPDP needs camera permission");
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        String ben_code = result.getText();
        if (formatCorrect()) {
            Intent toForms = new Intent(this, FormsActivity.class);
            toForms.putExtra("ben_code", ben_code);
            startActivity(toForms);
            finish();
        } else {
            Toast.makeText(this, "Invalid barcode", Toast.LENGTH_SHORT).show();
        }
        mScannerView.resumeCameraPreview(this);
    }

    private boolean formatCorrect() {
        return true;
    }
}
