package edu.upenn.cis350.Trace2Learn;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainMenuActivity extends ListActivity {
	
	static final String[] APPS = new String[]
		{ 
			"Create Character", 
			"Create Word",
			"Search Characters",
			"Browse All Characters",
			"Browse All Words",
			"Browse All Lessons"
		};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setListAdapter(new ArrayAdapter<String>(this, R.layout.main_menu,APPS));

		ListView listView = getListView();
		listView.setTextFilterEnabled(true);
		final Context c=this;
		
		listView.setOnItemClickListener(
			new OnItemClickListener() 
			{
				public void onItemClick(
						AdapterView<?> parent,
						View view,
						int position,
						long id) 
				{
					CharSequence clicked = ((TextView) view).getText();
					if(clicked.equals(APPS[0]))
					{
						Intent i = new Intent().setClass(c, CharacterCreationActivity.class);
						startActivity(i);
					}
					else if(clicked.equals(APPS[1]))
					{
	
						Intent i = new Intent(c, CreateWordActivity.class);
						startActivity(i);
					
					}
					/*else if(clicked.equals(APPS[2]))
					{
	
						Intent i = new Intent(c, CreateLessonActivity.class);
						startActivity(i);
					
					}*/
					else if(clicked.equals(APPS[2]))
					{
						Intent i = new Intent(c, TestTagDbActivity.class);
						startActivity(i);
					
					}
					else if(clicked.equals(APPS[3])){
						Intent i = new Intent(c, BrowseCharactersActivity.class);
						startActivity(i);
					}
					else if(clicked.equals(APPS[4])){
						Intent i = new Intent(c, BrowseWordsActivity.class);
						startActivity(i);
					}
					else{
						Intent i = new Intent(c, BrowseLessonsActivity.class);
						startActivity(i);
					}
				}
			}
		);
	}
}
