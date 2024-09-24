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

import org.sourceforge.kga.Point;
import org.sourceforge.kga.plant.Companion;
import org.sourceforge.kga.plant.ReferenceList;
import org.sourceforge.kga.Plant;

/**
 * If you break a warning type rule or follow a benefit type rule you will get a hint.
 * The hint tells the user if she is doing right or wrong.
 * @author Christian Nilsson
 *
 */
public class Hint
{
    public enum Value
    {
        GOOD, BAD, TIP
    }

    private Plant currentPlant;
    private Plant neighborPlant;
    private Point neighborGrid;
    private int   neighborYear;
    private Rule  rule;
    private Companion  companion;
    private Hint.Value value;
    private ArrayList<Hint> details = new ArrayList<Hint>();

    public Hint(
        Plant currentPlant,
        Plant neighborPlant, Point neighborGrid,
        Rule rule, Companion companion)
    {
        this.currentPlant  = currentPlant;
        this.neighborPlant = neighborPlant;
        this.neighborGrid  = neighborGrid;
        this.neighborYear  = 0;
        this.rule          = rule;
        this.companion     = companion;
        this.value         = companion.type.isBeneficial() ? Hint.Value.GOOD : Hint.Value.BAD;
    }

    public Hint(
        Plant currentPlant,
        Plant neighborPlant, int neighborYear,
        Rule rule, Hint.Value value)
    {
        this.currentPlant  = currentPlant;
        this.neighborPlant = neighborPlant;
        this.neighborGrid  = null;
        this.neighborYear  = neighborYear;
        this.rule          = rule;
        this.companion     = null;
        this.value         = value;
    }
    
    public Plant getCurrentPlant()  { return currentPlant; }
    public Plant getNeighborPlant() { return neighborPlant; }
    public Point getNeighborGrid()  { return neighborGrid; }
    public int   getNeighborYear()  { return neighborYear; }
    public Rule  getRule()          { return rule; }
    public String getDescription()  { return rule.getDescription(this); }
    public ReferenceList getReferences() { return rule.getReferences(this); }
    public Companion getCompanion() { return companion; }
    public Hint.Value getValue()    { return value; }
    public boolean isRotation()     { return companion == null; }
    
    public void addDetail(Hint detail) { details.add(detail); }
    public ArrayList<Hint> getDetails() { return details; }
}
