package com.nipunbirla.qrpagersample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private MyPagerAdapter mAdapterViewPager;
    private static int CAM_REQ_CODE = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        mAdapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(mAdapterViewPager);
        vpPager.setOffscreenPageLimit(3);
        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                switch (position){
                    case 0:
                        if(mAdapterViewPager.getInstantiatedFragment(position) instanceof QRReaderOperations){

                            //Post delayed for smooth scroll in view pager

                            Util.postDelayedOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((QRReaderOperations) mAdapterViewPager.getInstantiatedFragment(position)).initializeCam();
                                }
                            }, 300);
                        }
                        break;
                    default:
                        if(mAdapterViewPager.getInstantiatedFragment(0) instanceof QRReaderOperations){
                            Util.postDelayedOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((QRReaderOperations) mAdapterViewPager.getInstantiatedFragment(0)).closeCam();
                                }
                            }, 300);
                        }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void requestCamPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAM_REQ_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAM_REQ_CODE){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ((QRReaderOperations) mAdapterViewPager.getInstantiatedFragment(0)).initializeCam();
            } else {
                ((QRReaderOperations) mAdapterViewPager.getInstantiatedFragment(0)).closeCam();
                //TODO - provide UI for request permission
            }
            return;
        }
    }
}
