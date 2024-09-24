/**
 * @(#)EditableGarden.java
 *
 *
 * @author
 * @version 1.00 2011/9/16
 */
package org.sourceforge.kga.gui.gardenplan;

import javafx.scene.transform.Affine;
import org.sourceforge.kga.*;
import org.sourceforge.kga.rules.Hint;
import org.sourceforge.kga.rules.HintList;
import org.sourceforge.kga.rules.Rule;

import java.util.*;


public class EditableGarden extends Garden
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());
    static public int minZoomFactor = 50, maxZoomFactor = 200;
    int zoomFactor = 100;
    int previewYear = 0;
    Point previewGrid = null;
    TaxonVariety<Plant> previewPlant = null;
    TaxonVariety<Plant> selectedPlant = null;

    java.util.List<EditableGardenObserver> observers = new ArrayList<EditableGardenObserver>();

    public EditableGarden()
    {
        log.setLevel(java.util.logging.Level.FINEST);
    }

    public void addObserver(EditableGardenObserver observer)
    {
        observers.add(observer);
    }

    public void removeObserver(EditableGardenObserver observer)
    {
        observers.remove(observer);
    }
    
    public void removeAllIfObserving(Collection<?> toRemove) {
    	observers.removeAll(toRemove);
    }

    public int getZoomFactor()
    {
        return zoomFactor;
    }

    public void setZoomFactor(int zoomFactor)
    {
        if (zoomFactor < minZoomFactor)
            zoomFactor = minZoomFactor;
        if (zoomFactor > maxZoomFactor)
            zoomFactor = maxZoomFactor;
        if (zoomFactor == this.zoomFactor)
            return;
        this.zoomFactor = zoomFactor;
        for (EditableGardenObserver o : observers)
            o.zoomFactorChanged(this);
    }

    // TODO: preview not available outside gardenbounds.
    // TODO: Why not initialize with bigger garden bounds?
    //Then preview becomes available for bigger area at startup.
    //We could replace int year and Point grid with Square square.
    /**
     * Checks if a plant is a preview. Preview is a function to add a plant to
     * the garden temporary to see which hints are given.
     * @param year the year of the square which the preview is located at
     * @param grid the location of the square which the preview is located at
     * @param plant the plant which is the preview
     * @return true if plant is the preview
     */
    public boolean isPreview(int year, Point grid, Plant plant)
    {
        if (previewYear == 0 || previewGrid == null || previewPlant == null)
            return false;
        // log.fine("params> year" + year + "grid" + grid.x + ":" + grid.y + " species" + plant.getId() +
        //         " previews> year" + previewYear + "grid" + previewGrid.x + ":" + previewGrid.y + " species" + previewPlant.getId());
        return previewYear == year && previewPlant.equals(plant) &&
            previewGrid.x == grid.x && previewGrid.y == grid.y;
    }
    
    public int getPreviewYear() { return previewYear; }
    public Point getPreviewGrid() { return previewGrid; }
    public TaxonVariety<Plant> getPreviewPlant() { return previewPlant; }

    /**
     * Adds a preview. Preview is a feature to add a plant to the garden temporary
     * to see which hints are given. This method does not effect gardenbounds.
     * @param year the year of the square which the preview will be at
     * @param grid the location of the square which the preview will be located
     * @param plant the plant which is the preview
     */
    public void addPreview(int year, Point grid, TaxonVariety<Plant> plant)
    {
        removePreview();

        List<TaxonVariety<Plant>> plantList = getPlants(year, grid);
        if (plantList != null && plantList.contains(plant))
            return;

        previewYear = year;
        previewGrid = grid;
        previewPlant = plant;

        // log.fine("Adding preview square with " + plant + " at grid " + grid);
        squareChanged(year, grid);
    }

    /**
     * Removes the preview.
     */
    public void removePreview()
    {
        if (previewYear == 0 || previewGrid == null || previewPlant == null)
        {
            // log.fine("Remove preview species - nothing");
            return;
        }

        // log.fine("Remove preview species " + previewPlant + " at grid " + previewGrid);
        int tmpYear = previewYear;
        Point tmpGrid = new Point(previewGrid);

        previewYear = 0;
        previewGrid = null;
        Point oldSize = previewPlant.getSize();
        previewPlant = null;

        squareChanged(tmpYear, tmpGrid);
        for(int i=0;i<oldSize.x;i++) {
        	for(int j=0;j<oldSize.y;j++) {
        		if(i==j && i==0) {
        			continue;
        		}
                squareChanged(tmpYear, new Point(tmpGrid.x+i,tmpGrid.y+j));        		
        	}
        }
    }
    
    public List<Plant> getPlantTaxons(int year, Point grid){
    	List<Plant> plantList = new LinkedList<Plant>();
    	List<TaxonVariety<Plant>> varietyList = getPlants(year,grid);
    	if(varietyList==null) {
    		return null;
    	}
    	for (TaxonVariety<Plant> curr : varietyList){
    		plantList.add(curr.getTaxon());
    	}
    	return plantList;
    }

    @Override
    public List<TaxonVariety<Plant>> getPlants(int year, Point grid)
    {
        List<TaxonVariety<Plant>> plantList = super.getPlants(year, grid);
        if (previewYear == year && previewGrid != null && grid.equals(previewGrid))
        {
            // performs a copy of the list ( or clear entries in list if preview is an item ) and add the preview plant
            if (plantList == null ||
                previewPlant.isItem() ||
                plantList.get(0).getTaxon().isItem())
            {
                plantList = new ArrayList<TaxonVariety<Plant>>();
            }
            else
            {
                plantList = new ArrayList<TaxonVariety<Plant>>(plantList);
            }
            plantList.add(previewPlant);
        }
        return plantList;
    }

    public List<TaxonVariety<Plant>> getSpeciesNoPreview(int year, Point grid)
    {
        return super.getPlants(year, grid);
    }

    public TaxonVariety<Plant> getSelectedPlant()
    {
        return selectedPlant;
    }

    public void setSelectedPlant(TaxonVariety<Plant> plant)
    {
        selectedPlant = plant;
        for (EditableGardenObserver o : observers)
            o.previewSpeciesChanged(this, selectedPlant);
    }

    public Map.Entry<Integer, Integer> getPreviewHints(int year, Point grid)
    {
        if (selectedPlant == null || selectedPlant.isItem())
            return null;

        int tmpYear = previewYear;
        Point tmpGrid = previewGrid;
        TaxonVariety<Plant> tmpPlant = previewPlant;

        int good = 0, bad = 0;
        {
            previewYear = 0;
            previewGrid = null;
            previewPlant = null;
            List<TaxonVariety<Plant>> plantList = getPlants(year, grid);
            if (plantList == null || !plantList.contains(selectedPlant))
            {
                // count hints before adding plant
                for (int dx = -1; dx <= 1; ++dx)
                {
                    for (int dy = -1; dy <= 1; ++dy)
                    {
                        HintList hints = Rule.getHints(this, year, new Point(grid.x + dx, grid.y + dy), false);
                        good -= hints.getValue(Hint.Value.GOOD);
                        bad -= hints.getValue(Hint.Value.BAD);
                    }
                }

                // count hints after adding plant
                previewYear = year;
                previewGrid = grid;
                previewPlant = selectedPlant;
                for (int dx = -1; dx <= 1; ++dx)
                {
                    for (int dy = -1; dy <= 1; ++dy)
                    {
                        HintList hints = Rule.getHints(this, year, new Point(grid.x + dx, grid.y + dy), false);
                        good += hints.getValue(Hint.Value.GOOD);
                        bad += hints.getValue(Hint.Value.BAD);
                    }
                }
            }
        }


        previewYear = tmpYear;
        previewGrid = tmpGrid;
        previewPlant = tmpPlant;
        return new AbstractMap.SimpleEntry<>(good, bad);
    }

    private void callGardenChanged()
    {
        for (EditableGardenObserver o : observers)
            o.gardenChanged(this);
    }

    public boolean addPlant(int year, Point grid, TaxonVariety<Plant> plant)
    {
        boolean b = super.addPlant(year, grid, plant);
        if (b)
            callGardenChanged();
        return b;
    }

    public boolean addYear(int newYear)
    {
        boolean b = super.addYear(newYear);
        if (b)
            callGardenChanged();
        return b;
    }

    public void deleteYear(int year)
    {
        super.deleteYear(year);
        callGardenChanged();
    }

    public void removePlant(int year, Point grid, TaxonVariety<Plant> plant)
    {
        super.removePlant(year, grid, plant);
        callGardenChanged();
    }

    public enum Operation
    {
        AddPlant,
        DeletePlant,
        PickPlant
    }
    Operation operation = Operation.AddPlant;

    public Operation getOperation()
    {
        return this.operation;
    }

    public void setOperation(Operation operation)
    {
        this.operation = operation;
        for (EditableGardenObserver o : observers)
            o.operationChanged(this);
    }

    /**
     * Converts a position from the grid of the garden to a position in the image
     */
    /*
    private Point gridToImageLocation(Point grid)
    {
        Point offset = garden.getBounds().getLocation();

        Point location = new Point(grid.x, grid.y);
        location.translate(-offset.x + 1, -offset.y + 1);
        location.x *= GRID_SIZE;
        location.y *= GRID_SIZE;
        return location;
    }
    */

    public Point imageToGrid(double x, double y)
    {
        int px = (int)(x * 100 / zoomFactor / GardenView.GRID_SIZE);
        int py = (int)(y * 100 / zoomFactor / GardenView.GRID_SIZE);
        return new Point(getBounds().x + px - 1, getBounds().y + py - 1);
    }

    Affine gridToImage(Point p)
    {
        Affine affine = new Affine();
        org.sourceforge.kga.Rectangle bounds = getBounds();
        affine.appendScale(zoomFactor / 100., zoomFactor / 100.);
        affine.appendTranslation((p.x - bounds.x + 1) * GardenView.GRID_SIZE, (p.y - bounds.y + 1) * GardenView.GRID_SIZE);
        return affine;
    }
}
