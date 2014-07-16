package org.monsterlab.install;

import org.monster.timeline.R;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class TabDemo extends TabActivity{
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		TabHost tabHost=getTabHost();

		tabHost.addTab(tabHost.newTabSpec("TAB1")
				.setIndicator("  ",getResources().getDrawable(R.drawable.aa)).setContent(new Intent(this,InstallActivity.class)));

	}			
		
}
