package edu.upenn.cis350.Trace2Learn.Database;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

public class LessonCharacter extends LessonItem {

	private List<Stroke> _strokes;
	
	public LessonCharacter()
	{
		_type = ItemType.CHARACTER;
		_strokes = new ArrayList<Stroke>();
		_id = -1;
	}
	
	public LessonCharacter(long id)
	{
		this();
		_id = id;
	}
	
	protected boolean updateTypeData()
	{
		// TODO: check against db timestamp
		
		
		return true;
	}
	
	public synchronized void addStroke(Stroke stroke)
	{
		_strokes.add(stroke);
	}
	
	public synchronized List<Stroke> getStrokes()
	{
		List<Stroke> l = new ArrayList<Stroke>();
		l.addAll(_strokes);
		return l;
	}
	
	public synchronized Stroke getStroke(int i)
	{
		return _strokes.get(i);
	}
	
	@Override
	public synchronized void update(DbAdapter db)
	{
		super.update(db);
		updateStrokes(db);
	}
	
	/**
	 * Update the strokes to match the ones in the database
	 */
	protected synchronized void updateStrokes(DbAdapter db)
	{
		// TODO add a get stroke method.
		// Do we need this?
	}
	
	/**
	 * removes the known stroke for the character
	 * @param stroke - The stroke to be removed from the character
	 * @return - true if the stroke was removed, false otherwise
	 */
	public synchronized boolean removeStroke(Stroke stroke)
	{
		return _strokes.remove(stroke);
	}
	
	/**
	 * removes the ith stroke of the character
	 * @param i - the index of the stroke to be removed
	 * @return - the stroke that was removed from the character
	 */
	public synchronized Stroke removeStroke(int i)
	{
		return _strokes.remove(i);
	}
	
	/**
	 * removes all of the strokes from the character
	 */
	public synchronized void clearStrokes()
	{
		_strokes.clear();
	}

	/**
	 * Moves the stroke to a new position in the stroke order.
	 * The other strokes will be shifted as appropriate 
	 * 
	 * @param oldIndex - the original ordering of the stroke
	 * @param newIndex - the new position of the stroke in the order
	 */
	public synchronized void reorderStroke(int oldIndex, int newIndex)
	{
		if(oldIndex < 0 || oldIndex >= _strokes.size())
		{
			throw new IndexOutOfBoundsException("Index: " + oldIndex + " is not within the bounds of a " + _strokes.size() + " length list");
		}
		if(newIndex < 0 || newIndex >= _strokes.size())
		{
			throw new IndexOutOfBoundsException("Index: " + newIndex + " is not within the bounds of a " + _strokes.size() + " length list");
		}
		
		Stroke movedStroke = _strokes.get(oldIndex);
		
		if(oldIndex < newIndex)
		{
			for(int i = oldIndex; i + 1 <= newIndex; i++)
			{
				_strokes.set(i, _strokes.get(i+1));
			}
			_strokes.set(newIndex, movedStroke);
		}
		else if(oldIndex > newIndex)
		{
			for(int i = oldIndex; i - 1 >= newIndex; i--)
			{
				_strokes.set(i, _strokes.get(i-1));
			}
			_strokes.set(newIndex, movedStroke);
		}
		
	}

	public synchronized int getNumStrokes() {
		return _strokes.size();
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
	@Override
	public void draw(Canvas canvas, Paint paint, float left, float top, float width, float height)
	{
		Matrix matrix = new Matrix();
		Log.i("DRAW", "Scale: " + width + " " + height);
		Log.i("DRAW", "Strokes: " + _strokes.size());
		matrix.postScale(width, height);
		matrix.postTranslate(left,  top);
		List<Stroke> strokes = getStrokes();
		for(Stroke stroke : strokes)
		{
			Path path = stroke.toPath(matrix);
			canvas.drawPath(path, paint);
		}
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
	@Override
	public void draw(Canvas canvas, Paint paint, float left, float top, float width, float height, float time)
	{
		Matrix matrix = new Matrix();
		Log.i("DRAW", "Scale: " + width + " " + height);
		Log.i("DRAW", "Strokes: " + _strokes.size());
		Log.i("DRAW", "Time: " + time);
		matrix.postScale(width, height);
		matrix.postTranslate(left,  top);
		
		List<Stroke> strokes = getStrokes();
		
		float strokeTime = 1F/strokes.size();
		float coveredTime = 0;
		for(Stroke stroke : strokes)
		{
			if(coveredTime > time) break;
			float sTime = time - coveredTime;
			if(sTime > strokeTime) sTime = strokeTime;
			Path path = stroke.toPath(matrix, sTime/strokeTime);
			canvas.drawPath(path, paint);
			coveredTime += strokeTime;
		}
	}
	
}
