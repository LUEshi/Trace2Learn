package edu.upenn.cis350.Trace2Learn.test;

import android.test.AndroidTestCase;
import android.test.ProviderTestCase2;
import android.util.Log;
import edu.upenn.cis350.Trace2Learn.BrowseCharactersActivity;
import edu.upenn.cis350.Trace2Learn.CharacterCreationActivity;
import edu.upenn.cis350.Trace2Learn.Database.DbAdapter;
import edu.upenn.cis350.Trace2Learn.Database.LessonCharacter;
import edu.upenn.cis350.Trace2Learn.Database.Stroke;
import junit.framework.TestCase;

public class LessonCharacterTest extends AndroidTestCase {
	
	Stroke s1, s2, s3;

	protected void dumpDBs()
	{
		for(String str : this.getContext().databaseList())
		{
			Log.i("DELETE", str);
			this.getContext().deleteDatabase(str);
		}
	}
	
	DbAdapter db;
	protected void setUp() throws Exception {
		super.setUp();
		dumpDBs();
		db = new DbAdapter(this.getContext());
		db.open();
		// TODO: Clear Database
		
		s1 = new Stroke(1,1);
		s1.addPoint(2, 2);
		s1.addPoint(3, 3);
		
		s2 = new Stroke(1,10);
		s2.addPoint(2, 20);
		s2.addPoint(3, 30);

		s3 = new Stroke(10,1);
		s3.addPoint(20, 2);
		s3.addPoint(30, 3);

	}

	protected void tearDown()
	{
		dumpDBs();
	}
	
	public void compareCharacters(LessonCharacter expected, LessonCharacter actual)
	{
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getNumStrokes(), actual.getNumStrokes());
		assertEquals(expected.getItemType(), actual.getItemType());
		assertEquals(expected.getTags(), actual.getTags());
	}
	
	public void testNoStrokes()
	{
		LessonCharacter c = new LessonCharacter();
		assertEquals(0,c.getNumStrokes());
		assertEquals(0,c.getStrokes().size());
		try{
			c.getStroke(0);
		}catch(IndexOutOfBoundsException e){}
		try{
			c.removeStroke(0);
		}catch(IndexOutOfBoundsException e){}
		
	}
	
	public void testNoStrokesSave()
	{
		LessonCharacter c = new LessonCharacter();
		db.addCharacter(c);
		compareCharacters(c, db.getCharacterById(c.getId()));
	}
	
	public void testOneStroke()
	{
		LessonCharacter c = new LessonCharacter();
		c.addStroke(s1);
		assertEquals(1,c.getNumStrokes());
		assertEquals(1,c.getStrokes().size());
		assertEquals(s1,c.getStroke(0));
		assertTrue(!c.removeStroke(s2));
		assertTrue(c.removeStroke(s1));
		assertTrue(!c.removeStroke(s1));
		
		c.addStroke(s2);
		assertEquals(s2,c.removeStroke(0));
		assertTrue(!c.removeStroke(s2));
		
	}
	
	public void testOneStrokeSave()
	{
		LessonCharacter c = new LessonCharacter();
		c.addStroke(s1);
		db.addCharacter(c);
		compareCharacters(c, db.getCharacterById(c.getId()));
	}
	
	public void testAddStrokeAfterSave()
	{
		LessonCharacter c = new LessonCharacter();
		c.addStroke(s1);
		db.addCharacter(c);
		compareCharacters(c, db.getCharacterById(c.getId()));
		c.addStroke(s2);
		compareCharacters(c, db.getCharacterById(c.getId()));
	}
	
	
	public void testRemovalByIndex()
	{
		LessonCharacter c = new LessonCharacter();
		c.addStroke(s1);
		c.addStroke(s2);
		c.addStroke(s2);
		c.addStroke(s3);
		assertEquals(4,c.getNumStrokes());
		
		assertEquals(s3,c.getStroke(3));
		assertEquals(s2,c.removeStroke(1));
		assertEquals(s3,c.removeStroke(2));
		assertEquals(s1,c.removeStroke(0));
		assertEquals(s2,c.removeStroke(0));
		assertEquals(0,c.getNumStrokes());
	}
	
	public void testRemovalByRefernece()
	{
		LessonCharacter c = new LessonCharacter();
		c.addStroke(s1);
		c.addStroke(s2);
		c.addStroke(s2);
		c.addStroke(s3);
		assertEquals(4,c.getNumStrokes());

		assertTrue(c.removeStroke(s3));
		assertTrue(c.removeStroke(s2));
		assertEquals(s2,c.removeStroke(1));
		assertTrue(c.removeStroke(s1));
		assertEquals(0,c.getNumStrokes());
		
	}
	
	public void testReorder()
	{
		LessonCharacter c = new LessonCharacter();
		c.addStroke(s1);
		c.addStroke(s2);
		c.addStroke(s2);
		c.addStroke(s3);
		assertEquals(4,c.getNumStrokes());
		
		//old>new
		assertEquals(s2,c.getStroke(2));
		c.reorderStroke(2, 0);
		assertEquals(s2,c.getStroke(0));
		assertEquals(s1,c.getStroke(1));
		assertEquals(s2,c.getStroke(2));
		assertEquals(s3,c.getStroke(3));
		
		//new>old
		c.reorderStroke(0, 3);
		assertEquals(s2,c.getStroke(3));
		assertEquals(s1,c.getStroke(0));
		assertEquals(s2,c.getStroke(1));
		assertEquals(s3,c.getStroke(2));
		
	}
	
	public void testSaveTags()
	{
		LessonCharacter c = new LessonCharacter();
		c.addTag("Tag1");
		db.addCharacter(c);
		LessonCharacter c1 = db.getCharacterById(c.getId());
		compareCharacters(c1, c);
	}
	
}
