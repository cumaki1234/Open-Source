package org.sourceforge.kga.gui.gardenplan;


import javafx.application.Platform;
import javafx.geometry.Dimension2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.gui.ClickableTooltip;
import org.sourceforge.kga.gui.gardenplan.EditableGarden.Operation;
import org.sourceforge.kga.gui.rules.HintListDisplay;
import org.sourceforge.kga.plant.PropertySource;
import org.sourceforge.kga.rules.Companion;
import org.sourceforge.kga.rules.Hint;
import org.sourceforge.kga.rules.HintList;
import org.sourceforge.kga.rules.Rule;
import org.sourceforge.kga.translation.Translation;
import org.sourceforge.kga.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GardenCanvas extends Canvas
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(GardenCanvas.class.getName());

    public static final int PLANT_SIZE = 48;
    public static final int PADDING = Rule.IMAGE_SIZE;
    public static final int GRID_SIZE = PLANT_SIZE + 2 * PADDING;
    EditableGarden garden = null;
    int year;
    org.sourceforge.kga.Point lastPoint;
    ClickableTooltip tooltip;
    
    double minWidth;
    double minHeight;

    private Set<Point> repaintQueue;
    
    public void setMinImageSize(double x, double y) {
    	minWidth=x;
    	minHeight=y;
    	repaint();
    }

    public GardenCanvas()
    {
        this.garden = garden;
        this.year = year;
        //final WebView htmlTooltip = new WebView();
        Canvas tooltipParent = this;
       // final Tooltip canvasTip = new ClickableTooltip(htmlTooltip,this);
       // final WebEngine tooltipEngine = htmlTooltip.getEngine();
        //canvasTip.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
       // canvasTip.setGraphic(htmlTooltip);
        //HintListDisplay display = new HintListDisplay(null);
        super.setOnMouseMoved(e ->{
            org.sourceforge.kga.Point grid = garden.imageToGrid(e.getX(), e.getY());
            if(!grid.equals(lastPoint)) {
            	lastPoint=grid;
            	HintList hints = getHintList(e);
            	if(hints==null || !hints.iterator().hasNext()) {
            		if(tooltip!=null) {
            			tooltip.uninstall();
            			tooltip=null;
            		}
            	}else {
            		if (tooltip!=null){
            			tooltip.uninstall();  		
            		}
            		tooltip=new ClickableTooltip(new HintListDisplay(hints),this);      
            	}
            }
        	//tooltipEngine.loadContent(getToolTipText(e));
        	
        });
        repaintQueue = new HashSet<>();
    }

    private Dimension2D getImageSize()
    {
    	Point minSize = garden.imageToGrid(minWidth, minHeight);
        return new Dimension2D(Math.max(garden.getBounds().width +2,minSize.x) * GRID_SIZE, Math.max(garden.getBounds().height + 2,minSize.y) * GRID_SIZE);
    }

    public void repaint()
    {
        if (garden == null)
            return;

        // TODO: getTopLevelAncestor().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        Dimension2D imageSize = getImageSize();
        setWidth(imageSize.getWidth() * garden.getZoomFactor() / 100.);
        setHeight(imageSize.getHeight() * garden.getZoomFactor() / 100.);

        // create image object
        GraphicsContext g = getGraphicsContext2D();
        log.info("Draw garden " + Integer.toString(year));

        g.clearRect(0, 0, getWidth(), getHeight());

        // draw each square in selected year
        Map<Point, List<TaxonVariety<Plant>>> yearMap = garden.getAllSquares().get(year);
        if (yearMap != null) {
        	long start = System.currentTimeMillis();
        	log.info("full Repaint started");
            for (Map.Entry<org.sourceforge.kga.Point, java.util.List<TaxonVariety<Plant>>> s : yearMap.entrySet())
            {
                paintSquare(s.getKey());//, s.getValue(), g, false);
            }
        	log.info("full Repaint completed in: "+(System.currentTimeMillis()-start)+"ms");
        }

        // TODO: getTopLevelAncestor().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public synchronized void paintSquare(Point grid)
    {
    	
    	//In many situations (hints!!!!) the UI may request repainting the same point multiple times for the same frame, which is unnecesary and slow. This prevents that.
    	if(repaintQueue.size()==0){
    		Platform.runLater(()->{
        		runEnqueuedPaint();    			
    		});
    	}
    	repaintQueue.add(grid);
        //paintSquare(grid, garden.getPlants(year, grid), getGraphicsContext2D(), true);
    }
    
    public synchronized void runEnqueuedPaint() {
    	System.out.println("Starting draw for: "+repaintQueue.size()+" points");
    	long start = System.currentTimeMillis();
    	Set<Point> skip = new HashSet<Point>();
    	Set<Point> toRepaint = new HashSet<Point>();
    	for(Point curr : repaintQueue) {
    		toRepaint.addAll(garden.deps.GetRootParents(curr));
    	}
    	repaintQueue.clear();
    	GraphicsContext g = getGraphicsContext2D();
    	System.out.println("Points reduced to:"+toRepaint.size()+" and ordered in: "+(System.currentTimeMillis()-start)+"ms");
    	for(Map.Entry<Point,Set<Point>> currSize : garden.deps.groupBySizeFromPoints(toRepaint, new CanvasGridDrawer(g)).entrySet()) {

    		for(Point grid : currSize.getValue()) {
    			//TODO: MUST add logic to run in the correct order.
    			boolean clear = garden.deps.GetDependenciesAffectingPoint(grid).size()==0;//If a parent square added me as a dependancy since I checked, then don't clear me!
    			paintSquare(grid, garden.getPlants(year, grid), g, clear,false, skip);
    			skip.add(grid);
    		}
    	}
    	System.out.println("Completed draw in "+(System.currentTimeMillis()-start)+"ms");
    }
    
    
    private class CanvasGridDrawer implements GridDrawer{
    	 GraphicsContext g;
    	
    	public CanvasGridDrawer( GraphicsContext g) {
    		this.g=g;
    	}

		@Override
		public void clearRectangle(Point grid, Point size) {
	        Affine affineBackup = g.getTransform();
	        g.setTransform(garden.gridToImage(grid));
    		g.clearRect(1, 1, size.x*GRID_SIZE, size.y*GRID_SIZE);
            g.setTransform(affineBackup);
		}

		@Override
		public void draw(Point grid, int size, List<TaxonVariety<Plant>> toDraw) {
	        Affine affineBackup = g.getTransform();
	        g.setTransform(garden.gridToImage(grid));
			paintPreparedSquare(grid,toDraw,g,size);
            g.setTransform(affineBackup);
		}

		@Override
		public List<TaxonVariety<Plant>> getPlantsAtPoint(Point toDraw) {
			if(garden.previewPlant!=null&&toDraw.equals(garden.previewGrid) && garden.previewYear==year) {
				List<TaxonVariety<Plant>> toRet = new LinkedList<>(garden.getPlants(year, toDraw));
				if(!toRet.contains(garden.previewPlant))
					toRet.add(garden.previewPlant);
				return toRet;
			}else {
				return garden.getPlants(year, toDraw);
			}
    		//return garden.getAllSquares().get(year).get(toDraw);
		}
    	
    }

    private void paintSquare(Point grid, java.util.List<TaxonVariety<Plant>> plantList, GraphicsContext g, boolean clear, boolean paintParentSquares, Set<Point> skip)
    {
    	GridDrawer drawer = new CanvasGridDrawer(g);
    		
        // translate origin to square origin
        
        garden.deps.draw(grid, plantList, clear, drawer, paintParentSquares, skip);


    }
    
    private void paintPreparedSquare(Point grid, java.util.List<TaxonVariety<Plant>> plantList, GraphicsContext g, int gridsToSpan)//Note: Only supports square sizes.
        {

        // when drawing in a clear garden, clear is not needed, as the image is blank
        // when drawing only a square, clear previous square
        

        // log.fine("Draw square " + grid.toString());
        // draw paths
        boolean isPath = false;
        for(TaxonVariety<Plant> curr : plantList) {
        	if(curr.getTaxon().getId()==Plant.ID_PATH) {
        		isPath=true;
        		
        		break;
        	}
        }
        if(isPath) {
        	drawSquareWithPath(grid, g);
        }// draw plant
        else
        {
        	int divisions = (int)Math.sqrt(plantList.size());
        	if (divisions * divisions < plantList.size())
        		++divisions;
        	int divSize = (PLANT_SIZE + GRID_SIZE*(gridsToSpan-1) )/ divisions;
        	int k = 0;
        	for (int i = 0; i < divisions; ++i)
        		for (int j = 0; j < divisions && k < plantList.size(); ++j, ++k)
        		{
        			TaxonVariety<Plant>variety=plantList.get(k);
        			Plant plant = variety.getTaxon();
        			if (plant == null)
        			{
        				System.err.println("species is null");
        				continue;
        			}
        			boolean tmp = garden.isPreview(year, grid, plant);
        			if (tmp)
        				g.setGlobalAlpha(0.5);
        			int x = divSize * j, y = divSize * i, size = divSize;
        			if (plantList.size() == 2)
        			{
        				size = PLANT_SIZE * 2 / 3+(gridsToSpan-1)*GRID_SIZE;
        				if (k == 1)
        				{
        					x = PLANT_SIZE / 3;
        					y = PLANT_SIZE / 3;
        				}
        			}
        			if (plant.isItem())
        				g.drawImage(variety.getImage(GRID_SIZE*gridsToSpan), x, y, GRID_SIZE*gridsToSpan, GRID_SIZE*gridsToSpan);
        			else {
        				Image image = variety.getImage(size);
        				g.drawImage(image, PADDING + x, PADDING + y, size, size);
        			}
        			if (tmp)
        				g.setGlobalAlpha(1);
        		}
        }

        List<TaxonVariety<Plant>> multiGridPlantsHere = new LinkedList<TaxonVariety<Plant>>();
        for (MultiPointDependancy curr : garden.deps.GetDependenciesAffectingPoint(grid)){
        	List<TaxonVariety<Plant>> plants = garden.getPlants(year, curr.grid);
        	if(plants!=null) {
	        	for (TaxonVariety<Plant> plant : garden.getPlants(year, curr.grid)) {
	        		if (grid.instersectsAtSize(curr.grid, 1, plant.getSize().x)) {
	        			multiGridPlantsHere.add(plant);
	        		}
	        	}
        	}
        }
        //gridsToSpan
        for(int dx=0;dx<gridsToSpan;dx++) {
            for(int dy=0;dy<gridsToSpan;dy++) {
            	Point currPos = new Point(grid.x+dx,grid.y+dy);
		        Affine affineBackup = g.getTransform();
		        g.setTransform(garden.gridToImage(currPos));
                drawHints(currPos,g,plantList);       
                g.setTransform(affineBackup);
            }
        }

        }
    
    private void drawHints(Point grid, GraphicsContext g, List<TaxonVariety<Plant>> multiGridPlantsHere) {
        // draw hints
        // log.fine("Draw hints");
        HintList hints = Rule.getHints(garden, year, grid, false,multiGridPlantsHere);
        int hintsCount = GRID_SIZE / Rule.IMAGE_SIZE;
        boolean[][] fill = new boolean[hintsCount][hintsCount];

        int good = 0;
        int bad = 0;
        for (Hint hint : hints)
        {
            // just count +/- hints, draw them in free positions after arrows have been drawn
            Point targetGrid = hint.getNeighborGrid();
            if (hint.isRotation() || targetGrid.equals(grid))
            {
                if (hint.getValue() == Hint.Value.GOOD)
                    ++good;
                if (hint.getValue() == Hint.Value.BAD)
                    ++bad;
                continue;
            }

            // draw an arrow hint
            int rdx = targetGrid.x - grid.x;
            int rdy = targetGrid.y - grid.y;
            int dx = rdx < 0 ? -1 : rdx > 0 ? 1 : 0;
            int dy = rdy < 0 ? -1 : rdy > 0 ? 1 : 0;
            int x = 0, y = 0;
            boolean found = false;
            if (dx == 0 || dy == 0)
            {
                // find position for an horizontal/horizontal arrow
                for (int i = 0; i < hintsCount && !found; ++i)
                    for (int j = 0; j <= hintsCount / 2 && !found; ++j)
                        for (int k = -1; k <= 1 && !found; k += 2)
                        {
                            if (dx == 0 && dy == -1)
                            {
                                x = hintsCount / 2 + k * j;
                                y = i;
                            }
                            else if (dx == 0 && dy == 1)
                            {
                                x = hintsCount / 2 + k * j;
                                y = hintsCount - i - 1;
                            }
                            else if (dx == -1 && dy == 0)
                            {
                                x = i;
                                y = hintsCount / 2 + k * j;
                            }
                            else if (dx == 1 && dy == 0)
                            {
                                x = hintsCount - i - 1;
                                y = hintsCount / 2 + k * j;
                            }

                            if (x >= 0 && x < hintsCount && y >= 0 && y < hintsCount && !fill[x][y])
                            {
                                found = true;
                                fill[x][y] = true;
                            }
                        }
            }
            else
            {
                // find position for an arrow in a corner
                for (int i = 0; i < hintsCount * 2 - 1 && !found; ++i)
                {
                    int c = 1 + (i < hintsCount ? i : hintsCount * 2 - 2 - i);
                    for (int j = 0; j <= c / 2 && !found; ++j)
                        for (int k = 1; k >= -1 && !found; k -= 2)
                        {
                            if (dx == -1 && dy == -1)
                            {
                                x = i / 2;
                                y = (i + 1) / 2;
                                x += k * j;
                                y -= k * j;
                            }
                            else if (dx == 1 && dy == -1)
                            {
                                x = hintsCount - 1 - i / 2;
                                y = (i + 1) / 2;
                                x -= k * j;
                                y -= k * j;
                            }
                            else if (dx == -1 && dy == 1)
                            {
                                x = i / 2;
                                y = hintsCount - 1 - (i + 1) / 2;
                                x += k * j;
                                y += k * j;
                            }
                            else
                            {
                                x = hintsCount - 1 - i / 2;
                                y = hintsCount - 1 - (i + 1) / 2;
                                x -= k * j;
                                y += k * j;
                            }
                            if (x >= 0 && x < hintsCount && y >= 0 && y < hintsCount && !fill[x][y])
                            {
                                found = true;
                                fill[x][y] = true;
                            }
                        }
                }
            }

            x *=Rule.IMAGE_SIZE;
            y *= Rule.IMAGE_SIZE;

            // draw the arrow
            if (found)
            {
                ++dx;
                ++dy;
                if (hint.getValue() == Hint.Value.GOOD)
                    g.drawImage(Rule.GOOD_ARROWS[dy][dx], (x) , (y) , Rule.IMAGE_SIZE, Rule.IMAGE_SIZE);
                if (hint.getValue() == Hint.Value.BAD)
                    g.drawImage(Rule.BAD_ARROWS[dy][dx], (x) , (y) , Rule.IMAGE_SIZE, Rule.IMAGE_SIZE);
            }
        }

        // draw +/- hints
        int equal = Math.min(good, bad);
        good -= equal;
        bad  -= equal;

        while (equal > 0 || good > 0 || bad > 0)
        {
            int x = 0, y = 0;
            boolean found = false;
            for (y = 0; y < hintsCount; ++y)
            {
                for (x = 0; x < hintsCount; ++x)
                    if (!fill[x][y])
                    {
                        fill[x][y] = true;
                        found = true;
                        break;
                    }
                if (found)
                    break;
            }
            if (!found)
                break;
            if (equal > 0)
            {
                g.drawImage(Rule.EQUAL, x * Rule.IMAGE_SIZE, y * Rule.IMAGE_SIZE, Rule.IMAGE_SIZE, Rule.IMAGE_SIZE);
                --equal;
            }
            else if (good > 0)
            {
                g.drawImage(Rule.GOOD, x * Rule.IMAGE_SIZE, y * Rule.IMAGE_SIZE, Rule.IMAGE_SIZE, Rule.IMAGE_SIZE);
                --good;
            }
            else if (bad > 0)
            {
                g.drawImage(Rule.BAD, x * Rule.IMAGE_SIZE, y * Rule.IMAGE_SIZE, Rule.IMAGE_SIZE, Rule.IMAGE_SIZE);
                --bad;
            }
        }

    }

    void drawSquareWithPath(Point grid, GraphicsContext g)
    {
    	javafx.scene.image.Image imagePath = Resources.plantList().getPlant(116).getImage();
    	Point p = new Point(grid);
    	// draw middle of the path ( which is common for all directions )
    	g.drawImage(imagePath, 0, 0, GRID_SIZE, GRID_SIZE, 0, 0, GRID_SIZE, GRID_SIZE);
    	for (int i = 0; i < 4; ++i)
    	{
    		switch (i)
    		{
    		case 0:
    			p.translate(0, -1);
    			break;
    		case 1:
    			p.translate(1, 1);
    			break;
    		case 2:
    			p.translate(-1, 1);
    			break;
    		case 3:
    			p.translate(-1, -1);
    			break;
    		}

    		// a path is near
    		Collection<Plant> neighbor = garden.getPlantTaxons(year, p);
    		if (neighbor == null || !neighbor.contains(Resources.plantList().getPlant(116)))
    			g.drawImage(imagePath,
    					0, (i + 5) * GRID_SIZE, GRID_SIZE, GRID_SIZE,
    					0, 0, GRID_SIZE, GRID_SIZE);
    		else
    			g.drawImage(imagePath,
    					0, (i + 1) * GRID_SIZE, GRID_SIZE, GRID_SIZE,
    					0, 0, GRID_SIZE, GRID_SIZE);
    	}
    }
    
    private void runForEachPoint() {
    	
    }

    public EditableGarden getGarden()
    {
        return garden;
    }

    public int getYear()
    {
        return year;
    }

    public void setGardenAndYear(EditableGarden garden, int year)
    {
        this.garden = garden;
        this.year = year;
        repaint();
    }


    public HintList getHintList(MouseEvent e)
    {
        // create the tooltip on mouse position
        org.sourceforge.kga.Point grid = garden.imageToGrid(e.getX(), e.getY());//LocationToGrid(e.getPoint());
        
        HintList hints = Rule.getHints(garden, year, grid, true,true);
        
        //IF we are in add mode, add companion hints for the plant we are considering adding.
        /*if(garden.getOperation()==Operation.AddPlant || garden.getSelectedPlant()!=null) {
        	new Companion().getHints(hints, garden.getSelectedPlant());
        }*/
        return hints;
    }

    public String getToolTipText(MouseEvent e)
    {
        // create the tooltip on mouse position
        StringBuilder tips = new StringBuilder();
        tips.append("<html><body>");
        // add the hints
        ArrayList<PropertySource> references = new ArrayList<>();
        HintList hints = getHintList(e);
        if (hints != null)
        {
            tips.append(hints.getToolTipText());
        }
        else {
        	return null;
        }

        return tips.toString();
    } 

}
