/**
 * Kitchen garden aid is a planning tool for kitchengardeners.
 * Copyright (C) 2010 Christian Nilsson
 *
 * This file is part of Kitchen garden aid.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 * Email contact: tiberius.duluman@gmail.com; christian1195@gmail.com
 */

package org.sourceforge.kga.gui.gardenplan;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.print.PageLayout;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Pair;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.sourceforge.kga.Garden;
import org.sourceforge.kga.GardenObserver;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Point;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.gui.Printable;
import org.sourceforge.kga.gui.actions.ExportableImage;
import org.sourceforge.kga.rules.Rule;


/**
 * @(#)GardenView.java
 *
 *
 * @author Tiberius Duluman
 * @version 1.00 2011/6/15
 */
// this class renders an EditableGarden to an offscreen image,
// and display the image in a panel
// this is the view in MVC model
public class GardenView
    extends BorderPane
    implements
        GardenObserver, EditableGardenObserver, ExportableImage, Printable
        // TODO: Printable, AdjustmentListener, ChangeListener
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(GardenView.class.getName());
    private static final long serialVersionUID = 1L;
    private int year;
    private EditableGarden garden = null;
    private GardenController controller;
    private GardenPanelHeaders headerLeft = new GardenPanelHeaders(GardenPanelHeaders.ROWS);
    private GardenPanelHeaders headerTop = new GardenPanelHeaders(GardenPanelHeaders.COLUMNS);
    private ScrollPane scrollCenter = new ScrollPane();
    private GridCanvas gridCanvas = new GridCanvas();
    StackPane stackCenter;

    // entire the garden is rendered in this image
    private GardenCanvas gardenCanvas = new GardenCanvas();
    private GardenBackgroundCanvas backgroundCanvas = new GardenBackgroundCanvas();

    public static final int PLANT_SIZE = 48; // one square has 48 pixels
    public static final int PADDING = Rule.IMAGE_SIZE;
    public static final int GRID_SIZE = PLANT_SIZE + 2 * PADDING; // one square has 48 pixels
    public static final int gridPrintSize = 72 / 2; // one square has 1/2 inch ( 1 inch has 72 divisions )


    /* TODO: static private Cursor cursorDelete;
    static private Cursor cursorPick;
    static
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        cursorDelete = tk.createCustomCursor(SwingFXUtils.fromFXImage(ImageLoader.fromResources("delete_cursor.png"), null), new Point(16, 14), "delete");
        cursorPick = tk.createCustomCursor(SwingFXUtils.fromFXImage(ImageLoader.fromResources("picker_cursor.png"), null), new Point(0, 31), "pick");
    } */


    public GardenView()
    {
        // initialize the rows ( 1, 2, 3, ... ) and column ( A, B, C, ... ) headers
        // TODO: rowHeader.setZoomFactor(getZoomFactor());
        // TODO: columnHeader.setZoomFactor(getZoomFactor());

        AnchorPane anchorTop = new AnchorPane();
        anchorTop.getChildren().add(headerTop);
        AnchorPane.setLeftAnchor(headerTop, headerLeft.getWidth());
        AnchorPane.setRightAnchor(headerTop, 0.);

        setLeft(headerLeft);
        setTop(anchorTop);

        stackCenter = new StackPane();
        stackCenter.getChildren().addAll(backgroundCanvas, gridCanvas, gardenCanvas);
        stackCenter.setAlignment(backgroundCanvas, Pos.TOP_LEFT);
        stackCenter.setAlignment(gardenCanvas, Pos.TOP_LEFT);

        scrollCenter.setFitToHeight(true);
        scrollCenter.setFitToWidth(true);
        scrollCenter.setStyle("-fx-background-insets: 0;-fx-padding: 0;");
        scrollCenter.setContent(stackCenter);
        setCenter(scrollCenter);

        scrollCenter.vvalueProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val)
            {
                headerLeft.setScrollY((int) getVisibleBounds(gardenCanvas).getMinY() + 1);
            }
        });
        scrollCenter.hvalueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val)
            {
                headerTop.setScrollX((int) getVisibleBounds(gardenCanvas).getMinX() + 1);
            }
        });
        scrollCenter.boundsInParentProperty().addListener(new ChangeListener<Bounds>()
        {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue)
            {
                Bounds bounds = getVisibleBounds(gardenCanvas);
                headerTop.setScrollX((int)bounds.getMinX() + 1);
                headerLeft.setScrollY((int)bounds.getMinY() + 1);
            }
        });

        // initialize scroll pane
        // TO REMOVE: Dimension2D imageSize = getImageSize();
        // TO REMOVE: getScrollCenter().setPrefSize(imageSize.getWidth(), imageSize.getHeight());

        /* TODO: scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); */
        /* scrollPane.getHorizontalScrollBar().getModel().addChangeListener(this);
        scrollPane.getVerticalScrollBar().getModel().addChangeListener(this);
        scrollPane.getHorizontalScrollBar().addAdjustmentListener(this);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(this); */

        // creates and register a controller to view events
        controller = new GardenController();
        controller.setView(gardenCanvas);

        // ??? some hack
        /* TODO: setToolTipText("123");

        // redraw the headers when panel is resized
        addComponentListener(new ComponentListener()
        {
            @Override
            public void componentHidden(ComponentEvent e) {}

            @Override
            public void componentMoved(ComponentEvent e) {}

            @Override
            public void componentResized(ComponentEvent e)
            {
                // System.err.println("resized " + gardenPanel.getSize().toString());
                rowHeader.setPreferredHeight(getHeight());
                columnHeader.setPreferredWidth(getWidth());
                rowHeader.invalidate();
                columnHeader.invalidate();
            }

            @Override
            public void componentShown(ComponentEvent e) {}
        }); */
        
        ChangeListener<Number> widthPropagator = new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				gardenCanvas.setMinImageSize(stackCenter.getWidth(),stackCenter.getHeight());
				
			}
        	
        };

        stackCenter.widthProperty().addListener(widthPropagator);
        stackCenter.heightProperty().addListener(widthPropagator);
    }

     public Node getNodeToExport()
    {
        return stackCenter;
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
        // observe changes in model
        if (garden != this.garden)
        {
            if (garden != null)
            {
                garden.removeObserver((GardenObserver) this);
                garden.removeObserver((EditableGardenObserver) this);
            }
            garden.addObserver((GardenObserver) this);
            garden.addObserver((EditableGardenObserver) this);

            this.garden = garden;
        }
        this.year = year;
        gardenCanvas.setGardenAndYear(garden, year);
        backgroundCanvas.setGardenAndYear(garden, year);
    }

    /* TODO: public ScrollPane getScrollPane()
    {
        return scrollPane;
    } */

    //////////////////////////////////////////////////////////////////////////
    // EditableGardenObserver interface
    //
    public void zoomFactorChanged(EditableGarden e)
    {
        headerTop.setZoomFactor(garden.getZoomFactor());
        headerLeft.setZoomFactor(garden.getZoomFactor());
        gridCanvas.setZoomFactor(garden.getZoomFactor());
        gardenCanvas.repaint();
        backgroundCanvas.repaint();
    }

    public void gardenChanged(EditableGarden e) {}

    public void previewSpeciesChanged(EditableGarden e, TaxonVariety<Plant> plant)
    {
        // repaint background only
        backgroundCanvas.repaint();
    }

    public void operationChanged(EditableGarden e)
    {
        /* TODO: switch (e.getOperation())
        {
            case AddPlant:
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                break;
            case DeletePlant:
                setCursor(cursorDelete);
                break;
            case PickPlant:
                setCursor(cursorPick);
                break;
        } */
    }

    Rectangle getGardenBounds()
    {
        return new Rectangle(
            garden.getBounds().x, garden.getBounds().y,
            garden.getBounds().width, garden.getBounds().height);
    }

    //////////////////////////////////////////////////////////////////////////
    // image draw methods
    //
    /*
    TO REMOVE:
    private Dimension2D getImageSize()
    {
        // compute the panel bounds based on garden bounds
        Rectangle bounds = getGardenBounds();
        return new Dimension2D((bounds.getWidth() + 2) * GRID_SIZE, (bounds.getHeight() + 2) * GRID_SIZE);
    }
    */

    /* TODO: void drawPreviewOutsideBounds(GraphicsContext g)
    {
        if (garden.getPreviewYear() != year)
            return;
        Rectangle bounds = getGardenBounds();
        bounds.grow(1, 1);
        if (bounds.contains(garden.getPreviewGrid().x, garden.getPreviewGrid().y))
            return;

        // translate origin to square origin
        Point p = GridToLocation(garden.getPreviewGrid());
        g.translate(p.x, p.y);
        g.scale(garden.getZoomFactor() / 100., garden.getZoomFactor() / 100.);

        // log.fine("Draw preview outside bounds " + garden.getPreviewGrid().toString());

        g.setGlobalAlpha(0.5);
        if (garden.getPreviewPlant().isItem())
            g.drawImage(garden.getPreviewPlant().getImage(), 0, 0, GRID_SIZE, GRID_SIZE);
        else
            g.drawImage(garden.getPreviewPlant().getImage(), PADDING, PADDING, PLANT_SIZE, PLANT_SIZE);
        g.setGlobalAlpha(1);

        g.scale(1., 1.);
        g.translate(-p.x, -p.y);
    } */



    /*

    protected void printGrid(Graphics g, org.sourceforge.kga.Point topLeft, org.sourceforge.kga.Point bottomRight)
    {
        g.setColor(new java.awt.Color(0, 0, 0, 25));

        // for print, don't extend grid over garden bounds
        for (int horizontal = 0; horizontal <= bottomRight.x - topLeft.x; ++horizontal)
        {
            int x = horizontal * getZoomFactor() * gridPrintSize / 100;
            g.drawLine(x, 0, x, (bottomRight.y - topLeft.y) * getZoomFactor() * gridPrintSize / 100);
        }
        for (int vertical = 0; vertical <= bottomRight.y - topLeft.y; ++vertical)
        {
            int y = vertical * getZoomFactor() * gridPrintSize / 100;
            g.drawLine(0, y, (bottomRight.x - topLeft.x) * getZoomFactor() * gridPrintSize / 100, y);
        }
    }

    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Rectangle r = g.getClipBounds();
        // log.finest("GardenView.paintComponent() " + r.toString());

        drawGrid(g);

        if (background == null)
            createBackgroundImage();
        if (image == null)
            createImage();
        WritableImage img1 = background.snapshot(null, null);
        BufferedImage bfr = SwingFXUtils.fromFXImage(img1, null);
        g.drawImage(
            bfr,
            r.x, r.y, r.x + r.width, r.y + r.height,
            r.x * 100 / getZoomFactor(), r.y * 100 / getZoomFactor(),
            (r.x + r.width) * 100 / getZoomFactor(),
            (r.y + r.height) * 100 / getZoomFactor(), this);
        image.snapshot(null, img1);
        g.drawImage(
                SwingFXUtils.fromFXImage(img1, bfr),
            r.x, r.y, r.x + r.width, r.y + r.height,
            r.x * 100 / getZoomFactor(), r.y * 100 / getZoomFactor(),
            (r.x + r.width) * 100 / getZoomFactor(),
            (r.y + r.height) * 100 / getZoomFactor(), this);

        // drawPreviewOutsideBounds((Graphics2D)g);
    }


    //////////////////////////////////////////////////////////////////////////
    //
    //
    public String getToolTipText(MouseEvent e)
    {
        // create the tooltip on mouse position
        org.sourceforge.kga.Point grid = LocationToGrid(e.getPoint());
        java.util.List<Plant> species = garden.getPlants(year, grid);
        if (species == null || species.isEmpty())
            return null;

        // add the plant lists
        StringBuilder tips = new StringBuilder();
        Translation t = Translation.getCurrent();
        tips.append("<html><b>");
        boolean empty = true;
        for (Plant s : species)
        {
            if (!empty)
                tips.append(",");
            empty = false;
            tips.append(t.translate(s));
        }
        tips.append("</b>");
        tips.append("<br>");

        // add the hints
        ArrayList<PropertySource> references = new ArrayList<>();
        HintList hints = Rule.getHints(garden, year, grid, true);
        if (hints != null)
        {
            tips.append(hints.getToolTipText());
        }

        // add the sources

        return tips.toString();
    } */

    //////////////////////////////////////////////////////////////////////////
    // Printable
    //
    
    public Node getPrintPageNodes(PageLayout pf, Rectangle2D viewport)
    {
        Label header = new Label(Integer.toString(year));
        SnapshotParameters params = new SnapshotParameters();
        params.setViewport(viewport);
        
    	Image exported = stackCenter.snapshot(params,  null);    	
        
    	BorderPane toReturn = new BorderPane();
    	toReturn.setTop(header);
    	toReturn.setBottom(new ImageView(exported));
    	
    	return toReturn;
    }
    
    public Collection<Rectangle2D> getPrintPageViewports(PageLayout pf){
    	int imageWidth = (int)pf.getPrintableWidth();
        int imageHeight = (int)pf.getPrintableHeight();

        //remove height of header
        imageHeight-=  new Label("").getFont().getSize();
        
        int columns =(int)Math.ceil(gardenCanvas.getWidth()/imageWidth);
     	int rows =(int)Math.ceil(gardenCanvas.getHeight()/imageWidth);
     	Collection<Rectangle2D> viewports = new LinkedList<Rectangle2D>();
    	for (int page=0;page<columns*rows;page++) {
    		int row = page / columns;
    		int column = page%columns;
    		Rectangle2D curr =new Rectangle2D(column*imageWidth,row*imageHeight,(column+1)*imageWidth,(row+1)*imageHeight);

    		Point topLeft = garden.imageToGrid(curr.getMinX(), curr.getMinY());
    		Point bottomRight = garden.imageToGrid(curr.getMaxX(), curr.getMaxY());
    		Point currPoint=topLeft;
    		boolean hasPlants=false;
    		while(!currPoint.equals(bottomRight)) {
    			List<TaxonVariety<Plant>> atPoint = garden.getPlants(year, currPoint);
    			hasPlants |= (atPoint!=null&&atPoint.size()>0);
    			if(hasPlants)
    				break;
    			if(currPoint.x==bottomRight.x) {
    				currPoint.y++;
    				currPoint.x=topLeft.x;
    			}else {
    				currPoint.x++;
    			}
    			
    		}
    		if(hasPlants) {
    			viewports.add(curr);
    		}
    	}
    	return viewports;
    }

	@Override
	public Collection<pageGenerator> getPrintTasks(PageLayout pf) {
		Collection<pageGenerator> tasks= new LinkedList<pageGenerator>();
		for (Rectangle2D curr : getPrintPageViewports(pf))
			tasks.add(()->{return getPrintPageNodes(pf,curr);});
		return tasks;
	}

    //////////////////////////////////////////////////////////////////////////
    // GardenObserver
    //
    @Override
    public void yearAdded(Garden garden, int year) {}

    @Override
    public void yearDeleted(Garden garden, int year) {}

    @Override
    public void hintsChanged(int year, org.sourceforge.kga.Point grid)
    {
    	//System.out.println("Hints changed");
        plantsChanged(year, grid);
    }

    @Override
    public void plantsChanged(int year, org.sourceforge.kga.Point grid)
    {
        if (year != this.year)
            return;
        //System.out.println("plants changed at: "+grid);
        gardenCanvas.paintSquare(grid);
    }

    @Override
    public void boundsChanged(org.sourceforge.kga.Rectangle bounds)
    {
        boolean verticalToEnd = scrollCenter.getVvalue() == scrollCenter.getVmax();
        boolean horizontalToEnd = scrollCenter.getHvalue() == scrollCenter.getHmax();
        log.info("boundsChanged v=" + Boolean.toString(verticalToEnd) + " h=" + Boolean.toString(horizontalToEnd));

        gardenCanvas.repaint();
        backgroundCanvas.repaint();

        scrollCenter.layout();
        if (verticalToEnd)
            scrollCenter.setVvalue(scrollCenter.getVmax());
        if (horizontalToEnd)
            scrollCenter.setHvalue(scrollCenter.getHmax());
    }

    public static Bounds getVisibleBounds(Node node)
    {
        // If node not visible, return empty bounds
        if (!node.isVisible())
            return new BoundingBox(0,0,-1,-1);

        // If node has clip, return clip bounds in node coords
        if (node.getClip() != null)
            return node.getClip().getBoundsInParent();

        // If node has parent, get parent visible bounds in node coords
        Bounds bounds = node.getParent() != null ? getVisibleBounds(node.getParent()) : null;
        if (bounds != null && !bounds.isEmpty())
            bounds = node.parentToLocal(bounds);
        return bounds;
    }
}
