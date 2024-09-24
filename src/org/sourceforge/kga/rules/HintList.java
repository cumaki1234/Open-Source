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

package org.sourceforge.kga.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.sourceforge.kga.Point;
import org.sourceforge.kga.Garden;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.plant.*;
import org.sourceforge.kga.plant.Companion;
import org.sourceforge.kga.translation.Translation;

public class HintList implements Iterable<Hint>
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());

	ArrayList<Hint> hints = new ArrayList<Hint>(); 
	
	private Garden garden;
	private int year;
	private Point grid;
	private boolean detailed;
	public HintList(Garden garden, int year, Point grid, boolean detailed)
	{
		this.garden = garden;
		this.year   = year;
		this.grid   = grid;
		this.detailed = detailed;
	}
	
	public Garden getGarden()
	{
	    return garden;
	}
    
    public int getYear()
    {
        return year;
    }
    
    public Point getGrid()
    {
        return grid;
    }
	
	int[] values = new int[Hint.Value.values().length]; 
    public int getValue(Hint.Value type)
    {
        return values[type.ordinal()];
    }

	public boolean addRotationHint(Hint.Value type, Plant current, Plant previous, int previousYear, Rule rule)
	{
	    for (Hint hint : rotationHints)
	    {
	        if (hint.getValue() == type && hint.getCurrentPlant() == current &&
	            hint.getNeighborPlant() == previous && hint.getNeighborYear() == previousYear)
	        {
	            if (hint.getRule().getClass() == RotationRepetition.class)
	            {
	                // hint already added as species/family repetition, ignore other repetition hints
	                return false;
	            }
	            if (detailed)
	                hint.addDetail(new Hint(current, previous, previousYear, rule, type));
	            return true;
	        }
	    }
	    
        ++values[type.ordinal()];
        Hint hint = new Hint(current, previous, previousYear, rule, type);
        rotationHints.add(hint);
        hints.add(hint);
        return true;
	}
	
	private boolean duplicatedCompanion(Hint hint, Companion companion)
	{
        return
            hint.getCompanion().type == companion.type &&
            hint.getCompanion().animals.equals(companion.animals) &&
            hint.getCompanion().improve.equals(companion.improve);
	}
	
    public boolean addCompanionHint(Companion companion, Plant plant, Plant neighbor, Point grid, Rule rule)
    {
        Hint.Value value = companion.type.isBeneficial() ? Hint.Value.GOOD : Hint.Value.BAD; 
        for (Hint hint : companionHints)
        {
            // there is a hint with a plant that is child for this companion.plant - ignore all other childem
            // ex: corn surrounded by two species of cucurbits
            if (companion.plant.isParentOf(hint.getNeighborPlant()) && hint.getNeighborPlant() != neighbor)
                return true;
            // a species generates only one hint, regardless of how many places it ocupies
            // ex: corn surrounded by cucumbers
            if (hint.getNeighborPlant() == neighbor && !hint.getNeighborGrid().equals(grid))
                return true;
            if (hint.getValue() == value && hint.getCurrentPlant() == plant &&
                hint.getNeighborPlant() == neighbor && hint.getNeighborGrid().equals(grid))
            {
                if (duplicatedCompanion(hint, companion))
                    return true;
                for (Hint detail : hint.getDetails())
                    if (duplicatedCompanion(detail, companion))
                        return true;
                if (detailed)
                    hint.addDetail(new Hint(plant, neighbor, grid, rule, companion));
                return true;
            }
        }

        ++values[value.ordinal()];
        Hint newHint = new Hint(plant, neighbor, grid, rule, companion);
        companionHints.add(newHint);
        hints.add(newHint);
        return true;
    }

	ArrayList<Hint> companionHints = new ArrayList<>();
    ArrayList<Hint> rotationHints = new ArrayList<>();
	
	@Override
	public Iterator<Hint> iterator()
	{
		return hints.iterator();
	}

	public boolean isEmpty()
	{
		return hints.isEmpty();
	}
	
	private static void addHintDescription(StringBuilder toolTip, String text)
	{
	    if (text.isEmpty())
	        return;
        toolTip.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        toolTip.append(text);
	}
	
    private static void addHintReferences(List<PropertySource> sources, TreeSet<Integer> sourceIndexes, ReferenceList references)
    {
        for (Reference ref : references)
        {
            sourceIndexes.add(SourceList.add(sources, ref.source));
        }
    }

	public String getToolTipText()
	{
		return getToolTipText(hints);
	}

	public static String getToolTipText(Collection<Hint> hints)
	{
        Translation t = Translation.getCurrent();
	    StringBuilder toolTip = new StringBuilder();
	    ArrayList<PropertySource> sources = new ArrayList<>();
	    
	    for (Hint hint : hints)
	    {
	        if (hint.getValue() == Hint.Value.TIP)
	            continue; // TODO: not yet supported
	                
	        toolTip.append("<span style='color: ");
	        toolTip.append(hint.getValue() == Hint.Value.GOOD ? "green" : "red");
            toolTip.append("'>");
	        
	        if (hint.isRotation())
	        {
	            toolTip.append(t.translate(hint.getCurrentPlant()));
                if (hint.getCurrentPlant() != hint.getNeighborPlant())
                {
                    toolTip.append(" ").append(t.rotation_after()).append(" ").append(t.translate(hint.getNeighborPlant()));
                }
	        }
	        else
	        {
	            toolTip.append(t.translate(hint.getCurrentPlant())).append(" ");
	            toolTip.append(hint.getCompanion().type.isBeneficial() ? t.companion_helped_by()  : t.companion_dislike());
                //if (hint.getCompanion().plant != hint.getNeighborPlant())
	            //    toolTip.append(" ").append(hint.getCompanion().plant.getName()).append('(');
                toolTip.append(" ").append(t.translate(hint.getNeighborPlant()));
	        }
            int sourcesInsertPlace = toolTip.length();

	        addHintDescription(toolTip, hint.getDescription());
	        TreeSet<Integer> sourceIndexes = new TreeSet<>();
	        addHintReferences(sources, sourceIndexes, hint.getReferences());
	        for (Hint detail : hint.getDetails())
	        {
	            addHintDescription(toolTip, detail.getDescription());
	            addHintReferences(sources, sourceIndexes, detail.getReferences());
	        }

	        toolTip.append("</span><br/>");
	        
	        StringBuilder indexes = new StringBuilder();
	        for (Integer index : sourceIndexes)
	        {
	            if (indexes.length() != 0)
	                indexes.append(",");
	            indexes.append(" [");
	            indexes.append(index + 1);
	            indexes.append("]");
	        }
	        toolTip.insert(sourcesInsertPlace, indexes);
	    }
	    
        if (!sources.isEmpty())
            toolTip.append("<hr>");
        for (int i = 0; i < sources.size(); ++i)
        {
            PropertySource source = sources.get(i);
            toolTip.append("[");
            toolTip.append(i + 1);
            toolTip.append("] ");
            toolTip.append(source.name);
            if (source.url != null && !source.url.isEmpty())
            {
                toolTip.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;<i>");
                
                toolTip.append("<a href=\""+source.url+"\">");
                
                if (source.url.length() > 50)
                {
                    toolTip.append(source.url.substring(0, 50));
                    toolTip.append("...");
                }
                else
                {
                    toolTip.append(source.url);
                }
                
                toolTip.append("</a>");
                toolTip.append("</i>");

            }
            toolTip.append("<br/>");
        } 
        return toolTip.toString();
	}
	        
	        
}
