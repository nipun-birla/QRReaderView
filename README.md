QRReaderView
===

#### Modification of <a href="https://github.com/dlazaro66/QRCodeReaderView">QRCodeReaderView</a> by <a href="https://github.com/dlazaro66">dlazaro66</a> which is essentially a modification of  ZXING Barcode Scanner project for easy Android QR-Code detection and AR purposes. ####

This project implements an Android view which previews camera and notify when there's a QR code detected.

Some Classes of camera controls and the view are taken and slightly modified from QRCodeReaderView by dlazaro66.

You can also use this for Augmented Reality purposes, as you get QR control points coordinates when decoding.

Usage
-----

![Image](https://cloud.githubusercontent.com/assets/7312366/22305793/df6acf62-e362-11e6-8102-46c42f64f1fc.gif)

- Make sure you have camera permissions in order to use the library. (https://developer.android.com/training/permissions/requesting.html)



Add dependency in your build.gradle(app)

    dependencies {
        compile 'com.github.nipun-birla:qrreaderview:0.0.1'
    }


Put QRReaderView in your layout as required :
```xml
    <com.nipunbirla.qrreaderview.QRCodeReaderView
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>
```

- Create an Activity/Fragment which implements `onQRCodeReadListener`, and let implements required methods or set a `onQRCodeReadListener` to the QRReaderView object
- Start & Stop camera preview in onStart() and onStop() overriden methods.
- You can place widgets or views over QRReaderView.

```java
	public class MyActivity extends Activity implements OnQRCodeReadListener {

	private QRReaderView qrReaderView;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decoder);

        qrReaderView = (QRReaderView) findViewById(R.id.qrdecoderview);
        qrReaderView.setOnQRCodeReadListener(this);

    	// Use this function to enable/disable decoding
        qrReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrReaderView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        qrReaderView.setTorchEnabled(true);

        // Use this function to set front camera preview
        qrReaderView.setFrontCamera();

        // Use this function to set back camera preview
        qrReaderView.setBackCamera();
    }

    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed in View
	@Override
	public void onQRCodeRead(String text, PointF[] points) {
		Toast.makeText(MyActivity.this, text, Toast.LENGTH_SHORT).show();
		qrReaderView.setQRDecodingEnabled(false);
	}

	@Override
	protected void onStart() {
		super.onResume();
		qrReaderView.startCamera();
	}

	@Override
	protected void onStop() {
		super.onPause();
		qrReaderView.stopCamera();
	}
}
```

Tips
------------------------------
- If you are using QRReaderView inside a view pager, it is recommended to turn camera preview off on selecting a different tab. Check qrpagersample project above to see how to control camera in that case.

- Similar case can occur if a fragment is added on top of QRReaderView and should be handled.

Libraries used in this project
------------------------------

* [ZXING] [1]


Developed By
------------

* <a href="https://in.linkedin.com/in/nipunbirla">Nipun Birla</a>

[1]: https://github.com/zxing/zxing/