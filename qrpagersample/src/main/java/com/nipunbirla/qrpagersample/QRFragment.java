package com.nipunbirla.qrpagersample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nipunbirla.qrreaderview.QRCodeReaderView;

/**
 * Created by Nipun on 1/24/2017.
 */

public class QRFragment extends Fragment implements QRCodeReaderView.OnQRCodeReadListener, QRReaderOperations {

    QRCodeReaderView mQRCodeReaderView;
    TextView mScanButton, mFlashButton;

    // newInstance constructor for creating fragment with arguments
    public static QRFragment newInstance() {
        QRFragment qrFragment = new QRFragment();
        return qrFragment;
    }

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

    @Override
    public void onStart() {
        super.onStart();
        checkAndAskCameraPermission();
    }

    @Override
    public void onStop() {
        super.onStop();
        closeCam();
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        mQRCodeReaderView.setQRDecodingEnabled(false);
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
        mScanButton.setVisibility(View.VISIBLE);
    }

    private void checkAndAskCameraPermission(){
        if( ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            initCamera();
        } else {
            //ask for permission
            if(getActivity() instanceof MainActivity){
                ((MainActivity) getActivity()).requestCamPermission();
            }
        }
    }

    private void initCamera(){
        if(mQRCodeReaderView != null) {
            mQRCodeReaderView.startCamera();
            mQRCodeReaderView.setOnQRCodeReadListener(this);
            mQRCodeReaderView.setQRDecodingEnabled(true);
        }
    }

    @Override
    public void initializeCam() {
        checkAndAskCameraPermission();
    }

    @Override
    public void closeCam() {
        if(mQRCodeReaderView != null) {
            mQRCodeReaderView.stopCamera();
            mQRCodeReaderView.setOnQRCodeReadListener(null);
        }
    }
}
