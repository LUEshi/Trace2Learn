package edu.upenn.cis350.Trace2Learn;

import java.util.ArrayList;
import java.util.List;

import edu.upenn.cis350.Trace2Learn.CharacterCreationActivity.Mode;
import edu.upenn.cis350.Trace2Learn.Database.DbAdapter;
import edu.upenn.cis350.Trace2Learn.Database.LessonCharacter;
import edu.upenn.cis350.Trace2Learn.Database.LessonWord;
import edu.upenn.cis350.Trace2Learn.R.id;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

public class PhrasePracticeActivity extends Activity {
		
	private TextView _tagText;

	private DbAdapter _dbHelper;

	private Mode _currentMode = Mode.INVALID;

	private long id_to_pass = -1;

	private ArrayList<LessonCharacter> _characters;
	private ArrayList<Bitmap> _bitmaps;
	
	private ArrayList<SquareLayout> _displayLayouts;
	private ArrayList<SquareLayout> _traceLayouts;
	
	private ArrayList<CharacterDisplayPane> _displayPanes;
	private ArrayList<CharacterTracePane> _tracePanes;
	
	private ImageAdapter _imgAdapter;
	
	private Gallery _gallery;
	
	private ViewAnimator _animator;
	
	private enum Mode {
		CREATION, DISPLAY, ANIMATE, SAVE, INVALID, TRACE;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.practice_phrase);

		_animator = (ViewAnimator)this.findViewById(R.id.view_slot);
		
		_characters = new ArrayList<LessonCharacter>();
		_bitmaps = new ArrayList<Bitmap>();
		
		_displayLayouts = new ArrayList<SquareLayout>();
		_traceLayouts = new ArrayList<SquareLayout>();
		
		_displayPanes = new ArrayList<CharacterDisplayPane>();
		_tracePanes = new ArrayList<CharacterTracePane>();
		
		
		_imgAdapter = new ImageAdapter(this,_bitmaps);
        _gallery = (Gallery)findViewById(R.id.gallery);
        _gallery.setSpacing(0);
        
        _gallery.setAdapter(_imgAdapter);
		_gallery.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				setSelectedCharacter(position);
			}
			
		});

		_tagText = (TextView) this.findViewById(id.tag_list);

		_dbHelper = new DbAdapter(this);
		_dbHelper.open();

		initializeMode();

	}

	/**
	 * Initialize the display mode, if the activity was started with intent to
	 * display a character, that character should be displayed
	 */
	private void initializeMode() 
	{
		Bundle bun = getIntent().getExtras();
		if (bun != null && bun.containsKey("wordId")) 
		{
			setWord(_dbHelper.getWordById(bun.getLong("wordId")));
			setDisplayPane();
			id_to_pass = bun.getLong("wordId");
			updateTags();
		}
		else
		{
			// Yes this is bad form
			// Dont have time to figure out better error handling
			// Should reach here anyway basically just an assert
			throw new NullPointerException("Did not recieve wordId");
		}
	}

	private void setSelectedCharacter(int position) {
		_animator.setDisplayedChild(position);
		_tracePanes.get(position).clearPane();
		updateTags();
	}
	
	private void setWord(LessonWord word) {
		setCharacterList(word.getCharacterIds());
		setSelectedCharacter(0);
	}

	private void setCharacterList(List<Long> ids)
	{
		_characters.clear();
		_bitmaps.clear();
		_tracePanes.clear();
		_displayPanes.clear();
		_traceLayouts.clear();
		_displayLayouts.clear();
		for(long id : ids)
		{
			LessonCharacter ch = _dbHelper.getCharacterById(id);
			Bitmap bmp = BitmapFactory.buildBitmap(ch, 64, 64);
			this._characters.add(ch);
			this._bitmaps.add(bmp);
			SquareLayout disp = new SquareLayout(_animator.getContext());
			CharacterDisplayPane dispPane = new CharacterDisplayPane(disp.getContext());
			dispPane.setCharacter(ch);
			disp.addView(dispPane);
			
			this._displayLayouts.add(disp);
			this._displayPanes.add(dispPane);
			
			SquareLayout trace = new SquareLayout(_animator.getContext());
			CharacterTracePane tracePane = new CharacterTracePane(disp.getContext());
			tracePane.setTemplate(ch);
			trace.addView(tracePane);
			
			this._traceLayouts.add(trace);
			this._tracePanes.add(tracePane);
		}
		_imgAdapter.update(_bitmaps);
        _imgAdapter.notifyDataSetChanged();
	}
	
	/**
	 * Switches the display mode to display
	 */
	private synchronized void setDisplayPane()
	{
		if (_currentMode != Mode.DISPLAY) 
		{
			int curInd = _animator.getDisplayedChild();
			_animator.removeAllViews();
			for(SquareLayout disp : this._displayLayouts)
			{
				_animator.addView(disp);
			}
			_animator.setDisplayedChild(curInd);
			//setCharacter(this._characters.get(curInd));
			_currentMode = Mode.DISPLAY;
		}
	}

	/**
	 * Switches the display mode to display
	 */
	private synchronized void setCharacterTracePane()
	{
		if (_currentMode != Mode.TRACE) 
		{
			int curInd = _animator.getDisplayedChild();
			_animator.removeAllViews();
			for(SquareLayout trace : this._traceLayouts)
			{
				_animator.addView(trace);
			}
			_animator.setDisplayedChild(curInd);
			//setCharacter(this._characters.get(curInd));
			_currentMode = Mode.TRACE;
		}
	}
	
	public void setContentView(View view)
	{
		super.setContentView(view);
	}

//	private void setCharacter(LessonCharacter character)
//	{
//		_playbackPane.setCharacter(character);
//		_tracePane.setTemplate(character);
//	}

	private void updateTags()
	{
		if (_characters.size() > 0)
		{
			int ind = _animator.getDisplayedChild();
			List<String> tags = _dbHelper.getCharacterTags(_characters.get(ind).getId());
			this._tagText.setText(tagsToString(tags));
//			setCharacter(_dbHelper.getCharacterById(id_to_pass));
		}
	}

	public void onClearButtonClick(View view)
	{
		int child = _animator.getDisplayedChild();
		this._tracePanes.get(child).clearPane();
	}
	
	public void onTraceButtonClick(View view)
	{
		setCharacterTracePane();
	}
	
	@Override
	public void onRestart()
	{
		super.onRestart();
		updateTags();
	}

	private String tagsToString(List<String> tags)
	{
		StringBuffer buf = new StringBuffer();
		for (String str : tags)
		{
			buf.append(str + ", ");
		}

		return buf.toString();
	}

	public void onAnimateButtonClick(View view) 
	{
		Log.i("CLICK", "DISPLAY");
		setDisplayPane();
	}
	
	public void showToast(String msg){
		Context context = getApplicationContext();
		CharSequence text = msg;
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
}
