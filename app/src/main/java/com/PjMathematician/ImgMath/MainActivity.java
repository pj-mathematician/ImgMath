package com.PjMathematician.ImgMath;

import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.app.DownloadManager;
import android.widget.Toast;

import java.io.*;
import java.net.*;
import org.apache.commons.*;

import io.github.kexanie.library.MathView;
import android.net.*;
import android.content.*;
import android.database.*;
public class MainActivity extends Activity 
{ 
    MathView formula_one;
	DownloadManager.Request request;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


		final TextView textView = (TextView) findViewById(R.id.output_text);

        Button button = (Button) findViewById(R.id.convert_button);
		final Button button2 = (Button) findViewById(R.id.save_button);
        final EditText txt = (EditText)findViewById(R.id.main_input);
        button.setOnClickListener(new OnClickListener() {

				

				@Override
				public void onClick(View p1)
				{
					String tex=txt.getText().toString();
					String url = "https://latex.codecogs.com/png.latex?\\dpi{200}&space;\\bg_white&space;\\LARGE&space;"+tex.replace("$$","");
					final String urltx = url.replace(" ","&space;");
					textView.setText(urltx);
					formula_one = (MathView) findViewById(R.id.formula_one);
					formula_one.setText(tex);
					button2.setVisibility(1);
					
				}
			});
		button2.setOnClickListener(new OnClickListener() {
			    private BroadcastReceiver mDLCompleteReceiver;

				@Override
				public void onClick(View p1)
				{
					String tex=txt.getText().toString();
					String url = "https://latex.codecogs.com/png.latex?\\dpi{200}&space;\\bg_white&space;\\LARGE&space;"+tex.replace("$$","");
					final String urltx = url.replace(" ","&space;");
					try {
						request = new DownloadManager.Request(Uri.parse(urltx));
					} catch (IllegalArgumentException e) {
						textView.setText("Ugly Argument");

					}
					request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
					request.setTitle("DM Example");
					request.setDescription("Downloading file");
					request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
					String suffix="png";
					request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES +
															  File.separator + "ImgMath", "pic" + "." + suffix); //add variable
					request.allowScanningByMediaScanner();
					final DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
					/* this is our unique download id */
					final long DL_ID = dm.enqueue(request);
					mDLCompleteReceiver = new BroadcastReceiver() {

						@Override
						public void onReceive(Context context, Intent intent) {
							/* our download */
							if (DL_ID == intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)) {

								textView.clearAnimation();
								/* get the path of the downloaded file */
								DownloadManager.Query query = new DownloadManager.Query();
								query.setFilterById(DL_ID);
								Cursor cursor = dm.query(query);
								if (!cursor.moveToFirst()) {
									textView.setText("Download error: cursor is empty");
									return;
								}

								if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                                    != DownloadManager.STATUS_SUCCESSFUL) {
									textView.setText("Download failed: no success status");
									return;
								}

								String path = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
								textView.setText("File download complete. Location: \n" + path);
							}
						}
					};


					registerReceiver(mDLCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

					
				}

    });
}}

   
