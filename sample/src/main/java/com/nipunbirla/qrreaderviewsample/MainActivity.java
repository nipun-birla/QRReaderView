package com.nipunbirla.qrreaderviewsample;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.multi.qrcode.QRCodeMultiReader;
import com.nipunbirla.qrreaderview.QRCodeReaderView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews(){
        addFragment(new QRFragment(), R.id.activity_main, QRFragment.class.getSimpleName(), true);
    }

    public void addFragment(final Fragment fragment, final int resId, final String tag,final boolean replace) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if(replace)
            fragmentTransaction.replace(resId, fragment, tag);
        else
            fragmentTransaction.add(resId, fragment, tag);

        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            // it is possible that fragment might be null in FragmentManager
            for (Fragment fragment : fragments) {
                if (fragment != null) {
                    fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
        }
    }
}
