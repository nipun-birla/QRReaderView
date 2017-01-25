QRReaderView
===

#### Modification of <a href="https://github.com/dlazaro66/QRCodeReaderView">QRCodeReaderView</a> by <a href="https://github.com/dlazaro66">dlazaro66</a> which is essentially a modification of  ZXING Barcode Scanner project for easy Android QR-Code detection and AR purposes. ####

This project implements an Android view which previews camera and notify when there's a QR code detected.

Some Classes of camera controls and autofocus are taken and slightly modified from QRCodeReaderView by dlazaro66.

You can also use this for Augmented Reality purposes, as you get QR control points coordinates when decoding.

Usage
-----
<!--
Add dependency in your build.gradle(app)

    dependencies {
        compile 'com.github.nipun-birla:Swipe3DRotateView:0.0.1'
    }
-->
- Add "QRReaderView" in XML.
- In your onCreate method, you can find the view as usual, using findViewById() function.
- Create an Activity which implements `onQRCodeReadListener`, and let implements required methods or set a `onQRCodeReadListener` to the QRCodeReaderView object
- Make sure you have camera permissions in order to use the library. (https://developer.android.com/training/permissions/requesting.html)

```xml

 <com.dlazaro66.qrcodereaderview.QRCodeReaderView
        android:id="@+id/qrdecoderview"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

```

- Start & Stop camera preview in onPause() and onResume() overriden methods.
- You can place widgets or views over QRDecoderView.

```java
	public class DecoderActivity extends Activity implements OnQRCodeReadListener {

    private TextView resultTextView;
	private QRCodeReaderView qrCodeReaderView;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decoder);

        mydecoderview = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        mydecoderview.setOnQRCodeReadListener(this);

    	// Use this function to enable/disable decoding
        mydecoderview.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        mydecoderview.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        mydecoderview.setTorchEnabled(true);

        // Use this function to set front camera preview
        mydecoderview.setFrontCamera();

        // Use this function to set back camera preview
        mydecoderview.setBackCamera();
    }

    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed in View
	@Override
	public void onQRCodeRead(String text, PointF[] points) {
		resultTextView.setText(text);
	}

	@Override
	protected void onResume() {
		super.onResume();
		qrCodeReaderView.startCamera();
	}

	@Override
	protected void onPause() {
		super.onPause();
		qrCodeReaderView.stopCamera();
	}
}
```


Add it to your project
----------------------


Add QRCodeReaderView dependency to your build.gradle


Libraries used in this project
------------------------------

* [ZXING] [1]

Screenshots
-----------

![Image](../master/readme_images/app-example.gif?raw=true)


Developed By
------------

* Nipun Birla

<a href="https://twitter.com/_dlazaro">
  <img alt="Follow me on Twitter" src="../master/readme_images/logo-twitter.png?raw=true" />
</a>
<a href="https://es.linkedin.com/pub/david-lázaro-esparcia/49/4b3/342">
  <img alt="Add me to Linkedin" src="../master/readme_images/logo-linkedin.png?raw=true" />
</a>

[1]: https://github.com/zxing/zxing/
[2]: https://www.swapcard.com/