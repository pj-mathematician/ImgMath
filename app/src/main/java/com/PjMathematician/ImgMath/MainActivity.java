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
//import com.rarepebble.colorpicker.ColorPickerView;;
public class MainActivity extends Activity 
{ 
    //@BindView(R.id.formula_one)
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
		try
		{
			if (txt.getText().toString().length() >= 2)
			{
				txt.setSelection(2);
			}
			//txt.setFocusableInTouchMode(true);

			txt.addTextChangedListener(new TextWatcher(){

					int blen=0;
					//SharedPreferences.Editor preferencesEditor = mPreferences.edit();
					//boolean inp=true;
					//String lcd="";
					int before_length=4;
					@Override
					public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
					{
						if (txt.getText().toString().length() >= 2)
						{
							String tex=txt.getText().toString();
							before_length = txt.length();
						}            
					}

					@Override
					public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
					{
						try
						{
							String tex=txt.getText().toString();
							Boolean insert=false;
							Boolean backspace = false;
							if (tex.length() < before_length)
							{
								backspace = true;
							}
							textView.setText(tex.substring(txt.getSelectionStart() - 1, txt.getSelectionStart()));
							String last_char = tex.substring(txt.getSelectionStart() - 1, txt.getSelectionStart());
							if ((last_char.equals("_") || last_char.equals("^")) && (!backspace))
							{
								insert = true;
								txt.getText().insert(txt.getSelectionStart(), "{");
								//txt.getText().insert(txt.getSelectionStart(),"}");
								txt.setSelection(txt.getSelectionStart());
							}
							if ((last_char.equals("{") && !insert) && !backspace)
							{
								txt.getText().insert(txt.getSelectionStart(), "}");
								txt.setSelection(txt.getSelectionStart() - 1);
							}
							formula_one.setText(txt.getText().toString());
						}
						catch (Exception e)
						{
							txt.setSelection(txt.getText().toString().length() - 2);
						}
					}
					@Override
					public void afterTextChanged(Editable p1)
					{

						//txt.setSelected(true);
					}


				}); 
		}
		catch (Exception e)
		{}

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
				final SharedPreferences.Editor preferencesEditor = mPreferences.edit();
				final Dialog dialog = new Dialog(this);
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				lp.copyFrom(dialog.getWindow().getAttributes());
				lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
				lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.dialog);
				final Switch s=dialog.findViewById(R.id.dynaswitch);
				s.setChecked(mPreferences.getBoolean("dynam", true));
				final Spinner spdpi = dialog.findViewById(R.id.dpil);
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
				else if (mPreferences.getString("ltx_url_dpi", "").equals("1000"))
				{
					spdpi.setSelection(4);
				}
//				final Spinner sbg = dialog.findViewById(R.id.bgl);
//				if (mPreferences.getString("ltx_url_bg", "").equals("Transparent"))
//				{
//					sbg.setSelection(0);
//				}
//				else if (mPreferences.getString("ltx_url_bg", "").equals("white"))
//				{
//					sbg.setSelection(1);
//				}
//				else if (mPreferences.getString("ltx_url_bg", "").equals("black"))
//				{
//					sbg.setSelection(2);
//				}
//				else if (mPreferences.getString("ltx_url_bg", "").equals("blue"))
//				{
//					sbg.setSelection(3);
//				}
//				else if (mPreferences.getString("ltx_url_bg", "").equals("green"))
//				{
//					sbg.setSelection(4);
//				}
//				else if (mPreferences.getString("ltx_url_bg", "").equals("yellow"))
//				{
//					sbg.setSelection(5);
//				}
//				else if (mPreferences.getString("ltx_url_bg", "").equals("red"))
//				{
//					sbg.setSelection(6);
//				}
//
//
				dialog.show();
				dialog.getWindow().setAttributes(lp);
				Button saver = dialog.findViewById(R.id.saveit);
				Button canceler = dialog.findViewById(R.id.canc);
				saver.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View p1)
						{
							int s1=spdpi.getSelectedItemPosition();
							//int s2=sbg.getSelectedItemPosition();
							preferencesEditor.putBoolean("dynam",s.isChecked());
							switch (s1)
							{
								case 0:
									preferencesEditor.putString("ltx_url_dpi", "100");
									break;
								case 1:
									preferencesEditor.putString("ltx_url_dpi", "200");
									break;
								case 2:
									preferencesEditor.putString("ltx_url_dpi", "300");
									break;
								case 3:
									preferencesEditor.putString("ltx_url_dpi", "500");
									break;
								case 4:
									preferencesEditor.putString("ltx_url_dpi", "1000");

									break;
								default:
								    preferencesEditor.putString("ltx_url_dpi","200");
									break;                                        
							}
//							switch(s2){
//							    case 0:
//							 	    preferencesEditor.putString("ltx_url_bg", "Transparent");
//									break;
//								case 1:
//							      	preferencesEditor.putString("ltx_url_bg", "white");
//									break;
//								case 2:
//								    preferencesEditor.putString("ltx_url_bg", "black");
//									break;									
//								case 3:
//								    preferencesEditor.putString("ltx_url_bg", "blue");
//									break;
//								case 4:
//								    preferencesEditor.putString("ltx_url_bg", "green");
//									break;
//								case 5:
//								    preferencesEditor.putString("ltx_url_bg", "yellow");
//									break;
//								case 6:
//								preferencesEditor.putString("ltx_url_bg", "red");
//									break;
//							}
							preferencesEditor.commit();
							dialog.cancel();
						}
						});
					canceler.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View p1)
						{
						dialog.cancel();
							}
						});
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
