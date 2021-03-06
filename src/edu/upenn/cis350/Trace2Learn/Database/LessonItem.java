package edu.upenn.cis350.Trace2Learn.Database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;


public abstract class LessonItem {
	
	/** The tag cache*/
	protected List<String> _tags;
	
	/** The id of the item */
	protected long _id;
	
	/** The private tag cache of the item */
	protected String private_tag;
	
	/** Reference to the database in which the item is stored */
	protected DbAdapter _db;
	
	/** The last time the item was synched with the database */
	protected Date _lastUpdate;
	
	/** Identifier for type of character **/
	protected ItemType _type;
	
	public enum ItemType
	{
		CHARACTER,
		WORD,
		LESSON
	}
	
	protected LessonItem()
	{
		_id = -1;
		_tags = new ArrayList<String>();
		
		_lastUpdate = new Date(0);
	}
	
	/**
	 * Synchs the LessonItem to match the database
	 * TODO: Uses a timestamp to check for any new updates that need to be
	 * pulled from the database
	 * @return True - if the item was updated
	 * 		   False - otherwise
	 */
	protected boolean update()
	{
		if(_db == null || _id < 0) return false;
		if(updateTypeData())
		{
			_lastUpdate = new Date();
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Synchs the type specific data from the database
	 * TODO: Uses a timestamp to check for any new updates that need to be
	 * pulled from the database
	 * @return True - if the item was updated
	 * 		   False - otherwise
	 */
	protected abstract boolean updateTypeData();
	
	public ItemType getItemType()
	{
		return _type;
	}
	
	public void setId(long id)
	{
		_id = id;
	}
	
	public void setDatabase(DbAdapter db)
	{
		_db = db;
	}
	
	public long getId()
	{
		return _id;
	}
	
	public void setPrivateTag(String tag){
		private_tag = tag;
	}
	
	public String getPrivateTag(){
		return private_tag;
	}
	
	public void setTagList(List<String> tags){
		_tags.addAll(tags);
	}
	
	/**
	 * Updates the LessonItem's contents to match those in the database
	 */
	public synchronized void update(DbAdapter db)
	{
		updateTags(db);
	}
	
	/**
	 * Updates the LessonItem's tags to represent those in the database
	 */
	protected synchronized void updateTags(DbAdapter db)
	{
		switch(_type)
		{
		case CHARACTER:
			_tags = db.getCharacterTags(_id);
			break;
		case WORD:
			_tags = db.getWordTags(_id);
			break;
		case LESSON:
			// TODO uncomment when there is lesson support
			//_tags = db.getLessonTags(_id);
			break;
		}
	}
	
	public synchronized boolean hasTag(String tag)
	{
		return _tags.contains(tag);
	}
	
	public synchronized void addTag(String tag)
	{
		_tags.add(tag);
	}
	
	public synchronized List<String> getTags()
	{
		return new ArrayList<String>(_tags);
	}
	
	/** 
	 * The ratio for determining how large a stroke should be given the size
	 * of the canvas
	 */
	private static final float _heightToStroke = 8F/400F;
	
	/**
	 * Configures the paint options given the size of the canvas
	 * @param height - the height of the canvas on which the paint options will
	 * 				   be used
	 * @return The configured paint options
	 */
	private Paint buildPaint(float height) {
		
		Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(0xFFFF0000);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(height*_heightToStroke);
        
        return paint;
	}	
	
	/**
	 * Draws the item in the canvas provided
	 * @param canvas
	 */
	public void draw(Canvas canvas)
	{
		Paint paint = buildPaint(canvas.getHeight());
        
        draw(canvas, paint);
	}
	
	/**
	 * Draws the item in the canvas provided in a animation percentage
	 * The time is a normalized step from 0 to 1, 0 being not shown at all
	 * and 1 being completely drawn.
	 * @param canvas - the canvas to draw on
	 * @param time - the time in the animation from 0 to 1
	 */
	public void draw(Canvas canvas, float time)
	{
		Paint paint = buildPaint(canvas.getHeight());
        
        draw(canvas, paint, time);
	}
	
	/**
	 * Draws the item in the canvas provided, using the provided paint brush
	 * @param canvas - the canvas to draw on
	 * @param paint - the drawing settings for the item
	 */
	public void draw(Canvas canvas, Paint paint)
	{
		Rect bounds = canvas.getClipBounds();
		draw(canvas, paint, bounds.left, bounds.top, bounds.width(), bounds.height());
	}
	
	/**
	 * Draws the item in the canvas provided in a animation percentage, using the provided paint brush
	 * The time is a normalized step from 0 to 1, 0 being not shown at all
	 * and 1 being completely drawn.
	 * @param canvas - the canvas to draw on
	 * @param paint - the drawing settings for the item
	 * @param time - the time in the animation from 0 to 1
	 */
	public void draw(Canvas canvas, Paint paint, float time)
	{
		Rect bounds = canvas.getClipBounds();
		draw(canvas, paint, bounds.left, bounds.top, bounds.width(), bounds.height(), time);
	}
	
	/**
	 * Draws the item in the canvas provided within the provided bounding box
	 * The time is a normalized step from 0 to 1, 0 being not shown at all
	 * and 1 being completely drawn.
	 * @param canvas - the canvas to draw on
	 * @param left - the left bound in which the item should be drawn
	 * @param top - the top bound in which the item should be drawn
	 * @param width - the width of the bounding box in which the item should be drawn
	 * @param height - the height of the bounding box in which the item should be drawn
	 * @param time - the time in the animation from 0 to 1
	 */
	public void draw(Canvas canvas, float left, float top, float width, float height, float time)
	{
		Paint paint = buildPaint(height);
        
        draw(canvas, paint, left, top, width, height, 1);
	}
	
	/**
	 * Draws the item in the canvas provided within the provided bounding box
	 * @param canvas - the canvas to draw on
	 * @param left - the left bound in which the item should be drawn
	 * @param top - the top bound in which the item should be drawn
	 * @param width - the width of the bounding box in which the item should be drawn
	 * @param height - the height of the bounding box in which the item should be drawn
	 */
	public void draw(Canvas canvas, float left, float top, float width, float height)
	{
		Paint paint = buildPaint(height);
        
        draw(canvas, paint, left, top, width, height);
	}
	
	/**
	 * Draws the item in the canvas provided, using the provided paint brush
	 * within the provided bounding box
	 * @param canvas - the canvas to draw on
	 * @param paint - the drawing settings for the item
	 * @param left - the left bound in which the item should be drawn
	 * @param top - the top bound in which the item should be drawn
	 * @param width - the width of the bounding box in which the item should be drawn
	 * @param height - the height of the bounding box in which the item should be drawn
	 */
	public void draw(Canvas canvas, Paint paint, float left, float top, float width, float height)
	{
		draw(canvas, paint, left, top, width, height, 1F);
	}
	
	/**
	 * Draws the item in the canvas provided, using the provided paint brush
	 * within the provided bounding box
	 * The time is a normalized step from 0 to 1, 0 being not shown at all
	 * and 1 being completely drawn.
	 * @param canvas - the canvas to draw on
	 * @param paint - the drawing settings for the item
	 * @param left - the left bound in which the item should be drawn
	 * @param top - the top bound in which the item should be drawn
	 * @param width - the width of the bounding box in which the item should be drawn
	 * @param height - the height of the bounding box in which the item should be drawn
	 * @param time - the time in the animation from 0 to 1
	 */
	public abstract void draw(Canvas canvas, Paint paint, float left, float top, float width, float height, float time);
}
