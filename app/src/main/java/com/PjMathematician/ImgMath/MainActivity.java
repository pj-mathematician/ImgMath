package com.PjMathematician.ImgMath;

import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
//import com.;
import io.github.kexanie.library.MathView;
public class MainActivity extends Activity 
{ 
    MathView formula_one;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
       /*if (! Python.isStarted()) {
            Python.start(AndroidPlatform(this));
        }*/
		final TextView textView = (TextView) findViewById(R.id.output_text);
        Button button = (Button) findViewById(R.id.convert_button);
        final EditText txt = (EditText)findViewById(R.id.main_input);
        button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View p1)
				{
					String tex=txt.getText().toString();
					textView.setText(tex);
					formula_one = (MathView) findViewById(R.id.formula_one);
					formula_one.setText(tex);
				}
			});
    }
}

