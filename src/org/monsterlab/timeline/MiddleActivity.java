package org.monsterlab.timeline;

import org.monster.timeline.R;
import org.monsterlab.photo_text.ImageEnlargedActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class MiddleActivity extends Activity{
	
	private ImageView image;
	private Bitmap bitmap;
	private TextView textView;
	private String imagePath;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.middle);
		image=(ImageView)findViewById(R.id.midImage);
		textView=(TextView)findViewById(R.id.midText);
		imagePath = getIntent().getStringExtra("image_path");
		String text = getIntent().getStringExtra("text");
		bitmap = BitmapFactory.decodeFile(imagePath);
		if(bitmap!=null){
			image.setImageBitmap(bitmap);
			image.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MiddleActivity.this,
							ImageEnlargedActivity.class);
					intent.putExtra("image_path", imagePath);
					startActivity(intent);
				}
			});
			}
		else{
			image.setImageResource(R.drawable.timeline1);
		}
		textView.setText(text);
	}

}
