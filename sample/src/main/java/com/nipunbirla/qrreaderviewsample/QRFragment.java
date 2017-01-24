package com.nipunbirla.qrreaderviewsample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nipunbirla.qrreaderview.QRCodeReaderView;

/**
 * Created by Nipun on 1/17/2017.
 */

public class QRFragment extends Fragment implements QRCodeReaderView.OnQRCodeReadListener {

    QRCodeReaderView mQRCodeReaderView;
    TextView mScanButton, mFlashButton;
    private static int CAM_REQ_CODE = 12;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mQRCodeReaderView = (QRCodeReaderView)view.findViewById(R.id.qr);
        mScanButton = (TextView) view.findViewById(R.id.tv_scan);

        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScanButton.setVisibility(View.GONE);
                mQRCodeReaderView.setQRDecodingEnabled(true);
            }
        });

        mFlashButton = (TextView) view.findViewById(R.id.tv_flash);

        if(getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
            mFlashButton.setVisibility(View.VISIBLE);

            mFlashButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFlashButton.setSelected(!mFlashButton.isSelected());
                    mQRCodeReaderView.setTorchEnabled(mFlashButton.isSelected());
                }
            });

        } else {
            mFlashButton.setVisibility(View.GONE);
        }
    }

    private void checkAndAskCameraPermission(){
        if( ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            initCamera();
        } else {
            //ask for permission
            if(getActivity() instanceof MainActivity){
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAM_REQ_CODE);
            }
        }
    }

    private void initCamera(){
        mQRCodeReaderView.startCamera();
        mQRCodeReaderView.setOnQRCodeReadListener(this);
        mQRCodeReaderView.setQRDecodingEnabled(true);
    }

    private void stopCamera(){
        mQRCodeReaderView.stopCamera();
        mQRCodeReaderView.setOnQRCodeReadListener(null);
    }

    @Override
    public void onStart() {
        super.onStart();
        checkAndAskCameraPermission();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopCamera();
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        mQRCodeReaderView.setQRDecodingEnabled(false);
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
        mScanButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAM_REQ_CODE){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initCamera();
            } else {
                stopCamera();
                //TODO - provide UI for request permission
            }
            return;
        }
    }

}
