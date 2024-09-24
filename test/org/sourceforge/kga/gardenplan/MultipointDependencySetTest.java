package org.sourceforge.kga.gardenplan;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sourceforge.kga.KGATest;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Point;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.gui.gardenplan.MultiPointDependencySet;

public class MultipointDependencySetTest extends KGATest {
	MultiPointDependencySet dependency;
	testGridDrawer drawer;

	@BeforeEach
	public void setupTest() {
		dependency = new MultiPointDependencySet();
		drawer = new testGridDrawer(20,20, dependency);
	}

	@Test
	public void testw2vAdjacentToPlacedAt0x0() {		
		drawer.addPlant(new Point(0,0), getListOf(2, ID_FENNEL));
		dependency.draw(new Point(0,1), getListOf(2, ID_CARROT), true, drawer);
		//drawer.assertRectEquals(new Point(2,0), drawer.getLowerLeft(), null);
		//drawer.assertRectEquals(new Point(0,3), drawer.getLowerLeft(), null);
		drawer.assertEmptyExcept(new Point(0,0), new Point(2,3));
		drawer.assertRectEquals(new Point(0,0), new Point(1,0), getListOf(2, ID_FENNEL));
		drawer.assertRectEquals(new Point(0,2), new Point(1,2), getListOf(2, ID_CARROT));
		drawer.assertRectEquals(new Point(0,1), new Point(1,1), getListOf(2, ID_FENNEL,ID_CARROT));
	}

	@Test
	public void testAdjacentToPlacedAt0x0() {	
		for(int count =2;count<=3;count++) {
			System.out.println("Trying with count: "+count);
			horizontallyAdjacentPattern(0,1, count);
		}
	}



	@Test
	/**
	 * Note: this sequence of calls is taken from exact UI calls at a point where the UI was buggy.
	 */
	public void testw3MouseDownThenUp() {	
		dependency.draw(new Point(0,0), getListOf(3, ID_CARROT), true, drawer);
		dependency.draw(new Point(0,0), getListOf(3, ID_CARROT), true, drawer);
		dependency.draw(new Point(0,0), null, true, drawer);
		dependency.draw(new Point(0,0), null, true, drawer);
		drawer.assertRectEquals(new Point(0,0), drawer.getLowerLeft(), null);
		dependency.draw(new Point(0,1), getListOf(3, ID_CARROT), true, drawer);
		dependency.draw(new Point(0,1), getListOf(3, ID_CARROT), true, drawer);
		dependency.draw(new Point(0,1), null, true, drawer);
		dependency.draw(new Point(0,1), null, true, drawer);
		drawer.assertRectEquals(new Point(0,0), drawer.getLowerLeft(), null);
		dependency.draw(new Point(0,0), getListOf(3, ID_CARROT), true, drawer);
		dependency.draw(new Point(0,0), getListOf(3, ID_CARROT), true, drawer);
		dependency.draw(new Point(0,0), null, true, drawer);
		dependency.draw(new Point(0,0), null, true, drawer);
		drawer.assertRectEquals(new Point(0,0), drawer.getLowerLeft(), null);
	}

	public void horizontallyAdjacentPattern(int fenel_x,int carrot_x, int size) {	
		int y=0;
		drawer.addPlant(new Point(fenel_x,y), getListOf(size, ID_FENNEL));
		dependency.draw(new Point(fenel_x,y), getListOf(size, ID_CARROT), true, drawer);
		dependency.draw(new Point(fenel_x,y), null, true, drawer);
		dependency.draw(new Point(fenel_x,y), getListOf(size, ID_FENNEL), true, drawer);
		dependency.draw(new Point(carrot_x,y), getListOf(size, ID_CARROT), true, drawer);
		//drawer.assertRectEquals(new Point(fenel_x,y+size), drawer.getLowerLeft(), null);
		//drawer.assertRectEquals(new Point(carrot_x+size,0), drawer.getLowerLeft(), null);
		drawer.assertEmptyExcept(new Point(fenel_x,y), new Point(carrot_x+size,y+size));
		drawer.assertRectEquals(new Point(fenel_x,y), new Point(carrot_x,size), getListOf(2, ID_FENNEL));
		drawer.assertRectEquals(new Point(fenel_x+size,y), new Point(fenel_x+size,size-1), getListOf(2, ID_CARROT));
		drawer.assertRectEquals(new Point(carrot_x,y), new Point(size-1,size-1), getListOf(2, ID_FENNEL,ID_CARROT));
	}

	@Test
	/**
	 * Note: this sequence of calls is taken from exact UI calls at a point where the UI was buggy.
	 */
	public void testMouseDown_add_mouseUp_at_mid_w3() {
		Point addPoint = new Point(2,2);
		int width = 3;
		Point br = new Point(addPoint.x+width,addPoint.y+width);
		dependency.draw(new Point(2,0), getListOf(width, ID_CARROT), true, drawer);
		dependency.draw(new Point(2,0), null, true, drawer);
		drawer.assertRectEquals(new Point(0,0), drawer.getLowerLeft(), null);
		dependency.draw(new Point(2,1), getListOf(width, ID_CARROT), true, drawer);
		dependency.draw(new Point(2,1), null, true, drawer);
		drawer.assertRectEquals(new Point(0,0), drawer.getLowerLeft(), null);
		dependency.draw(new Point(2,2), getListOf(width, ID_CARROT), true, drawer);
		dependency.draw(new Point(2,2), null, true, drawer);
		drawer.assertRectEquals(new Point(0,0), drawer.getLowerLeft(), null);

		drawer.addPlant(addPoint, getListOf(width, ID_CARROT));
		drawer.assertEmptyExcept(addPoint, br);
		drawer.assertRectEquals(addPoint, br, getListOf(width, ID_CARROT));

		dependency.draw(new Point(2,1), getListOf(width, ID_FENNEL), true, drawer);
		drawer.assertEmptyExcept(new Point(2,1), br);
		drawer.assertRectEquals(new Point(2,1), new Point (2,4), getListOf(width, ID_FENNEL));
		drawer.assertRectEquals(addPoint, new Point (br.x,br.y-1), getListOf(width, ID_CARROT, ID_FENNEL));
		drawer.assertRectEquals(new Point(addPoint.x,br.y-1), new Point (br.x,br.y), getListOf(width, ID_CARROT));


		dependency.draw(new Point(2,1), null, true, drawer);
		drawer.assertEmptyExcept(addPoint, br);
		drawer.assertRectEquals(addPoint, br, getListOf(width, ID_CARROT));	

		dependency.draw(new Point(2,0), getListOf(3, ID_FENNEL), true, drawer);
		dependency.draw(new Point(2,0), null, true, drawer);
		drawer.assertEmptyExcept(addPoint, br);
		drawer.assertRectEquals(addPoint, br, getListOf(width, ID_CARROT));	
	}


	@Test
	public void testadd_mouseDown_at_mid_w2() {
		Point addPoint = new Point(2,2);
		int width = 2;
		Point br = new Point(addPoint.x+width,addPoint.y+width);

		drawer.addPlant(addPoint, getListOf(width, ID_CARROT));
		drawer.assertEmptyExcept(addPoint, br);
		drawer.assertRectEquals(addPoint, br, getListOf(width, ID_CARROT));

		dependency.draw(new Point(2,3), getListOf(width, ID_FENNEL), true, drawer);
		drawer.assertEmptyExcept(addPoint, new Point(2+width,3+width));
		drawer.assertRectEquals(addPoint, new Point (addPoint.x,addPoint.y+1), getListOf(width, ID_CARROT));
		drawer.assertRectEquals(new Point(2,3), new Point (2,3+width), getListOf(width, ID_CARROT, ID_FENNEL));
		drawer.assertRectEquals(new Point (2,3+width),new Point(2+width,3+width), getListOf(width, ID_FENNEL));

		dependency.draw(new Point(2,3), null, true, drawer);
		drawer.assertEmptyExcept(addPoint, br);
		drawer.assertRectEquals(addPoint, br, getListOf(width, ID_CARROT));	

		dependency.draw(new Point(2,0), getListOf(3, ID_FENNEL), true, drawer);
		dependency.draw(new Point(2,0), null, true, drawer);
		drawer.assertEmptyExcept(addPoint, br);
		drawer.assertRectEquals(addPoint, br, getListOf(width, ID_CARROT));	
	}


	@Test
	public void testadd_mouseRight_back_left() {
		Point addPoint = new Point(2,2);
		int width = 2;
		Point br = new Point(addPoint.x+width,addPoint.y+width);

		drawer.addPlant(addPoint, getListOf(width, ID_CARROT));
		drawer.assertEmptyExcept(addPoint, br);
		drawer.assertRectEquals(addPoint, br, getListOf(width, ID_CARROT));

		dependency.draw(new Point(3,2), getListOf(width, ID_FENNEL), true, drawer);
		//drawer.assertEmptyExcept(addPoint, new Point(2+width,3+width));
		//drawer.assertRectEquals(addPoint, new Point (addPoint.x,addPoint.y+1), getListOf(width, ID_CARROT));
		//drawer.assertRectEquals(new Point(2,3), new Point (2,3+width), getListOf(width, ID_CARROT, ID_FENNEL));
		//drawer.assertRectEquals(new Point (2,3+width),new Point(2+width,3+width), getListOf(width, ID_FENNEL));

		dependency.draw(new Point(3,2), null, true, drawer);
		drawer.assertEmptyExcept(addPoint, br);
		drawer.assertRectEquals(addPoint, br, getListOf(width, ID_CARROT));	
		System.out.println(drawer);

		//dependency.draw(new Point(2,0), getListOf(3, ID_FENNEL), true, drawer);
		//dependency.draw(new Point(2,0), null, true, drawer);
		//drawer.assertEmptyExcept(addPoint, br);
		//drawer.assertRectEquals(addPoint, br, getListOf(width, ID_CARROT));	
	}

	@Test
	/**
	 * Note: this sequence of calls is taken from exact UI calls at a point where the UI was buggy.
	 */
	public void testMouseleft_right_past_square() {
		Point addPoint = new Point(1,1);
		int width = 2;
		Point br = new Point(addPoint.x+width,addPoint.y+width);
		

		dependency.draw(new Point(0,1), getListOf(width, ID_CARROT), true, drawer);
		dependency.draw(new Point(0,1), null, true, drawer);
		drawer.assertRectEquals(new Point(0,0), drawer.getLowerLeft(), null);

		dependency.draw(addPoint, getListOf(width, ID_CARROT), true, drawer);
		dependency.draw(addPoint, null, true, drawer);
		drawer.assertRectEquals(new Point(0,0), drawer.getLowerLeft(), null);
		
		drawer.addPlant(addPoint, getListOf(width, ID_CARROT));
		drawer.assertEmptyExceptRect(addPoint, br, getListOf(width, ID_CARROT));
		

		dependency.draw(addPoint, null, true, drawer);
		drawer.assertRectEquals(new Point(0,0), drawer.getLowerLeft(), null);
		dependency.draw(addPoint, getListOf(width, ID_CARROT), true, drawer);
		drawer.assertEmptyExceptRect(addPoint, br, getListOf(width, ID_CARROT));
		
		dependency.draw(new Point(2,1), getListOf(width, ID_FENNEL), true, drawer);
		dependency.draw(addPoint, getListOf(width, ID_CARROT), true, drawer);		
		dependency.draw(new Point(2,1), null, true, drawer);
		drawer.assertEmptyExceptRect(addPoint, br, getListOf(width, ID_CARROT));
		
		dependency.draw(addPoint, getListOf(width, ID_CARROT), true, drawer);
		drawer.assertEmptyExceptRect(addPoint, br, getListOf(width, ID_CARROT));
		
		dependency.draw(new Point(2,0), getListOf(width, ID_FENNEL), true, drawer);	
		drawer.assertEmptyExcept(new Point(1,1), new Point(2+width,1+width));
		//drawer.assertRectEquals(addPoint, new Point(1,3), getListOf(width, ID_CARROT));
		
		dependency.draw(addPoint, getListOf(width, ID_CARROT), true, drawer);

		dependency.draw(new Point(2,0),null, true, drawer);	
		drawer.assertEmptyExceptRect(addPoint, br, getListOf(width, ID_CARROT));
		
		dependency.draw(addPoint, getListOf(width, ID_CARROT), true, drawer);
		drawer.assertEmptyExceptRect(addPoint, br, getListOf(width, ID_CARROT));
	}
	
	
	
	@Test
	public void test_rotate_around_added() {
		Point addPoint = new Point(5,5);
		for (int width=2;width<4;width++) {
			for(int circleWidth=1;circleWidth<=width;circleWidth++) {

				drawer.addPlant(addPoint, getListOf(width, ID_CARROT));
				//dependency.draw(addPoint, getListOf(width, ID_CARROT), true, drawer);
				drawer.assertEmptyExceptRect(addPoint, new Point(addPoint.x+width,addPoint.y+width), getListOf(width, ID_CARROT));
				PointRotater rotater = new PointRotater(addPoint,getListOf(width, ID_CARROT),getListOf(width, ID_FENNEL),width);
				System.out.println("Starting circle test width: "+width+" circleWidth: "+circleWidth);
				rotater.encircleTest_clockwise(circleWidth);
				dependency = new MultiPointDependencySet();
				drawer = new testGridDrawer(20,20, dependency);
			}
		}
	}
	
	private class PointRotater{
		Point center;
		List<TaxonVariety<Plant>> atCenter;
		int width;
		List<TaxonVariety<Plant>> toDraw;

		int dx;
		int dy;
		public PointRotater(Point center, List<TaxonVariety<Plant>> atCenter, List<TaxonVariety<Plant>> toDraw, int width) {
			this.center=center;
			this.atCenter=atCenter;this.width=width;
			this.toDraw=toDraw;
		}
		
		private void drawAndMove(int dxPer, int dyPer, int iterations, boolean clearFirst) {
			for(int count=0;count<iterations;count++) {
				if(clearFirst) {
					dependency.draw(new Point(center.x+dx,center.y+dy), null, true, drawer);
					drawer.assertEmptyExceptRect(center, new Point(center.x+width,center.y+width), atCenter);
				}
				dx+=dxPer;
				dy+=dyPer;
				dependency.draw(new Point(center.x+dx,center.y+dy), toDraw, true, drawer);
				//System.out.println("drawer");
				clearFirst=true;			
			}
		}
		
		/**
		 * draws a circle around the center plant.
		 * @params width what width to draw the circle around. If matching the center plant width, it will do the outside, if smaller, it will do a circle while overlapping.
		 */
		public void encircleTest_clockwise(int width) {
			dx=0;
			dy=-1;
			drawer.assertEmptyExceptRect(center, new Point(center.x+this.width,center.y+this.width), atCenter);
			drawAndMove(0,0,1,false);//draw the first figure.
			drawAndMove(1,0,width+1,true);//draw the top row (except the leftmost corner).
			drawAndMove(0,1,width+2,true);//draw the right vertical.
			drawAndMove(1,0,width+2,true);//draw the bottom row.
			drawAndMove(0,-1,width+2,true);//draw the left row.
			drawAndMove(1,0,1,true);//move back to the starting position.
			dependency.draw(new Point(center.x+dx,center.y+dy), null, true, drawer);
			drawer.assertEmptyExceptRect(center, new Point(center.x+this.width,center.y+this.width), atCenter);			
		}
	}

}
