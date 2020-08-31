package com.PjMathematician.ImgMath;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.database.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import io.github.kexanie.library.*;
import java.io.*;
import android.util.*;
public class MainActivity extends Activity 
{ 
    MathView formula_one;


	//Resources res = getResources();
	DownloadManager.Request request;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final Resources res = getResources();
		final TextView textView = (TextView) findViewById(R.id.output_text);
        Button button = (Button) findViewById(R.id.convert_button);
		final Button button2 = (Button) findViewById(R.id.save_button);
        final EditText txt = (EditText)findViewById(R.id.main_input);
		final MathView formula_one = (MathView) findViewById(R.id.formula_one);
		txt.setSelection(2);
		txt.addTextChangedListener(new TextWatcher(){

                int blen=0;
				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					String tex=txt.getText().toString();
					textView.setText(tex);
					blen = tex.length();
				}

				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					String tex=txt.getText().toString();
					boolean bk=true;
					if (tex.length() < blen)
					{bk = false;}
					button2.setVisibility(-1);
			        formula_one.setText(tex);
					String s_=txt.getText().toString();
					String s=txt.getText().toString();
					String s1=txt.getText().toString();
					int k=tex.length() - 1;
					boolean insert=false;

					while (s_.lastIndexOf("_") != -1)
					{
						int m=s_.lastIndexOf("_");
						if (s_.substring(m + 1, m + 2).equals("{"))
						{
							s_ = s_.substring(0, m);
					    }
						else
						{
							insert = true;
							k = m;
							break;
						}
				    }
					while (s.lastIndexOf("^") != -1)
					{
						int m=s.lastIndexOf("^");
						if (s.substring(m + 1, m + 2).equals("{"))
						{
							s = s.substring(0, m);
					    }
						else
						{
							insert = true;
							k = m;
							break;
						}
				    }
					//ADD METHOD TO COMPLETE {
					if (insert && bk)
					{
						txt.getText().insert(txt.getSelectionStart(), "{");
						txt.getText().insert(txt.getSelectionStart(), "}");
						txt.setSelection(k + 2);
						textView.setText("done?");
					}
					

                }
				@Override
				public void afterTextChanged(Editable p1)
				{

					//nothing
				}


			}); 

        button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View p1)
				{

					String tex=txt.getText().toString(); 										
					String url = String.format(res.getString(R.string.ltx_url_main), res.getString(R.string.ltx_url_dpi), res.getString(R.string.ltx_url_bg), res.getString(R.string.ltx_url_size)) + tex.replace("$$", "");
					final String urltx = url.replace(" ", "&space;");

					textView.setText(urltx);
					button2.setVisibility(1);


				}
			});
		button2.setOnClickListener(new OnClickListener() {
			    private BroadcastReceiver mDLCompleteReceiver;

				@Override
				public void onClick(View p1)
				{
					String tex=txt.getText().toString();
					String url = "https://latex.codecogs.com/png.latex?\\dpi{200}&space;\\bg_white&space;\\LARGE&space;" + tex.replace("$$", "");
					final String urltx = url.replace(" ", "&space;");
					try
					{
						request = new DownloadManager.Request(Uri.parse(urltx));
					}
					catch (IllegalArgumentException e)
					{
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
						public void onReceive(Context context, Intent intent)
						{
							/* our download */
							if (DL_ID == intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L))
							{

								textView.clearAnimation();
								/* get the path of the downloaded file */
								DownloadManager.Query query = new DownloadManager.Query();
								query.setFilterById(DL_ID);
								Cursor cursor = dm.query(query);
								if (!cursor.moveToFirst())
								{
									textView.setText("Download error: cursor is empty");
									return;
								}

								if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                                    != DownloadManager.STATUS_SUCCESSFUL)
								{
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
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.setting:
				Intent i=new Intent(getApplicationContext(), SettingActivity.class);
				startActivity(i);
				return true;

			default:
			    return false;
		}
	}

}

