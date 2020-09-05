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
import android.support.v7.widget.*;
public class MainActivity extends Activity 
{ 
    MathView formula_one;
    private SharedPreferences mPreferences;
	private String sharedPrefFile = "com.PjMathematician.ImgMath";
	DownloadManager.Request request;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
		SharedPreferences.Editor preferencesEditor = mPreferences.edit();
		preferencesEditor.putString("test", "Test");
		if (!mPreferences.contains("ltx_url_main"))
		{
			preferencesEditor.putString("ltx_url_main", "https://latex.codecogs.com/png.latex?\\dpi{%1$s}&amp;space;\\bg_%2$s&amp;space;\\%3$s&amp;space;");
			preferencesEditor.putString("ltx_url_bg", "white");
			preferencesEditor.putString("ltx_url_size", "LARGE");
			preferencesEditor.putString("ltx_url_dpi", "200");
			preferencesEditor.putBoolean("dynam", true);
			preferencesEditor.commit();
		}	
        final Resources res = getResources();
		final TextView textView = findViewById(R.id.output_text);
        Button button = findViewById(R.id.convert_button);
		final Button button2 = findViewById(R.id.save_button);
        final EditText txt = findViewById(R.id.main_input);
		final MathView formula_one = findViewById(R.id.formula_one);
		txt.setSelection(2);
		txt.setFocusableInTouchMode(true);
		txt.addTextChangedListener(new TextWatcher(){

                int blen=0;
				//SharedPreferences.Editor preferencesEditor = mPreferences.edit();
				boolean inp=true;
				String lcd="";		
				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					String tex=txt.getText().toString();
					//textView.setText(ind);
					blen = tex.length();
					//if(txt.hasSelection()){
					//textView.setText(("fool"));
					//}

				}

				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					String tex=txt.getText().toString();
					int inm=txt.getSelectionStart();
					
					//textView.setText(inm);
					//String tmp=inm+"";
					//inm=Integer.parseInt(tmp);
					//textView.setText(tex.substring(inm-1,inm));
					String lch="";
					if (tex.length() > 4)
					{
						lch = tex.substring(inm - 1, inm);
					}
					else
					{
						lch = "$";
					}
					boolean bk=true;
					if (tex.length() < blen)
					{bk = false;}
					button2.setVisibility(-1);
			        formula_one.setText(tex);
					String s_=txt.getText().toString();
					String s=txt.getText().toString();
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
                    //textView.setText(ind);

					if (insert && bk)
					{
						txt.getText().insert(txt.getSelectionStart(), "{");
						//txt.getText().insert(txt.getSelectionStart(), "}");
						txt.setSelection(k + 2);
						//inp=false;
						textView.setText("done?");
					}
					//textView.setText("ok");
					if (((lch.equals("{")) && bk))
					{
						txt.getText().insert(inm, "}");
						txt.setSelection(inm);
					}




                }
				@Override
				public void afterTextChanged(Editable p1)
				{

					//txt.setSelected(true);
				}


			}); 

        button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View p1)
				{

					String tex=txt.getText().toString(); 										
					String url = String.format(mPreferences.getString("ltx_url_main", ""), mPreferences.getString("ltx_url_dpi", ""), mPreferences.getString("ltx_url_bg", ""), mPreferences.getString("ltx_url_size", "")) + tex.replace("$$", "");
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
					final ProgressBar pb=findViewById(R.id.load);
					pb.setVisibility(1);
					//String url = "https://latex.codecogs.com/png.latex?\\dpi{200}&space;\\bg_white&space;\\LARGE&space;" + tex.replace("$$", "");
					String url = String.format(mPreferences.getString("ltx_url_main", ""), mPreferences.getString("ltx_url_dpi", ""), mPreferences.getString("ltx_url_bg", ""), mPreferences.getString("ltx_url_size", "")) + tex.replace("$$", "");
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
					request.setTitle("ImgLatex");
					request.setDescription("Downloading Image");
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
								pb.setVisibility(-1);
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
								button2.setVisibility(-1);
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
			case R.id.option:
				SharedPreferences.Editor preferencesEditor = mPreferences.edit();
				Dialog dialog = new Dialog(this);
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				lp.copyFrom(dialog.getWindow().getAttributes());
				lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
				lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.dialog);
				Switch s=dialog.findViewById(R.id.dynaswitch);
				s.setChecked(mPreferences.getBoolean("dynam", true));
				Spinner spdpi = dialog.findViewById(R.id.dpil);
				if (mPreferences.getString("ltx_url_dpi", "").equals("100"))
				{
					spdpi.setSelection(0);
				}
				else if (mPreferences.getString("ltx_url_dpi", "").equals("200"))
				{
					spdpi.setSelection(1);
				}
				else if (mPreferences.getString("ltx_url_dpi", "").equals("300"))
				{
					spdpi.setSelection(2);
				}
				else if (mPreferences.getString("ltx_url_dpi", "").equals("500"))
				{
					spdpi.setSelection(3);
				}
				Spinner sbg = dialog.findViewById(R.id.bgl);
				if (mPreferences.getString("ltx_url_bg", "").equals("Transparent"))
				{
					sbg.setSelection(0);
				}
				else if (mPreferences.getString("ltx_url_bg", "").equals("white"))
				{
					sbg.setSelection(1);
				}
				else if (mPreferences.getString("ltx_url_bg", "").equals("black"))
				{
					sbg.setSelection(2);
				}
				else if (mPreferences.getString("ltx_url_bg", "").equals("blue"))
				{
					sbg.setSelection(3);
				}
				else if (mPreferences.getString("ltx_url_bg", "").equals("green"))
				{
					sbg.setSelection(4);
				}
				else if (mPreferences.getString("ltx_url_bg", "").equals("yellow"))
				{
					sbg.setSelection(5);
				}
				else if (mPreferences.getString("ltx_url_bg", "").equals("red"))
				{
					sbg.setSelection(6);
				}


				dialog.show();
				dialog.getWindow().setAttributes(lp);
			case R.id.about:
				//
			default:
			    return false;
		}
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		EditText txt = findViewById(R.id.main_input);
		txt.clearFocus();
	}

}
