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


package org.sourceforge.kga.gui.actions;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;

import java.util.*;

import javafx.embed.swing.SwingFXUtils;
import org.sourceforge.kga.*;
import org.sourceforge.kga.gui.*;
import org.sourceforge.kga.gui.plants.PlantLabel;
import org.sourceforge.kga.rules.*;
import org.sourceforge.kga.translation.*;

public class GardenStatistics extends KgaAction
{
    private static final long serialVersionUID = 1L;

    public GardenStatistics(Gui gui)
    {
        super(gui, Translation.getCurrent().action_garden_statistics());
    }

    Map<Integer, Integer> hintsGood;
    Map<Integer, Integer> hintsBad;
    Map<Integer, Map<TaxonVariety<Plant>, Integer>> frequency;
    Set<Integer> yearsSet;
    Set<TaxonVariety<Plant>> plantSet;
    Map<TaxonVariety<Plant>, Integer> totalYears;
    Map<TaxonVariety<Plant>, Integer> totalSquares;

    JToggleButton currentSortButton;
    Map<JToggleButton, Comparator<TaxonVariety<Plant>>> sortButtons;
    JScrollPane scrollPane;
    Dimension statisticsCellSize;

    static private Icon iconAscending = UIManager.getIcon("Table.ascendingSortIcon");
    static private Icon iconDescending = UIManager.getIcon("Table.descendingSortIcon");

    @Override
    public void actionPerformed(ActionEvent e)
    {
        // sort button has been pressed
        if (e.getSource() instanceof JToggleButton)
        {
            // set toggle buttons
            JToggleButton button = (JToggleButton)e.getSource();
            if (currentSortButton == button)
            {
                if (currentSortButton.getIcon() == iconAscending)
                    currentSortButton.setIcon(iconDescending);
                else
                    currentSortButton.setIcon(iconAscending);
                currentSortButton.setSelected(true);
            }
            else
            {
                currentSortButton.setIcon(iconAscending);
                currentSortButton.setSelected(false);
                currentSortButton = button;
                currentSortButton.setSelected(true);
            }

            // check for reverse order
            Comparator<TaxonVariety<Plant>> comparator = sortButtons.get(currentSortButton);
            if (comparator instanceof SpeciesComparatorByFrequency)
            {
                ((SpeciesComparatorByFrequency)comparator).setAscending(
                    currentSortButton.getIcon() == iconAscending);
            }
            else
            {
                if (currentSortButton.getIcon() == iconDescending)
                    comparator = Collections.reverseOrder(comparator);
            }

            // resort map
            ArrayList<TaxonVariety<Plant>> tmp = new ArrayList<>();
            tmp.addAll(plantSet);
            plantSet = new TreeSet<TaxonVariety<Plant>>(comparator);
            plantSet.addAll(tmp);

            // redraw statistics table
            fillStatisticsTable();
            return;
        }

        // initialize structures
        hintsGood = new TreeMap<Integer, Integer>();
        hintsBad  = new TreeMap<Integer, Integer>();
        frequency = new TreeMap<Integer, Map<TaxonVariety<Plant>, Integer>>();
        plantSet = new TreeSet<TaxonVariety<Plant>>(new TaxonVarietyComparatorByName());
        totalYears = new HashMap<TaxonVariety<Plant>, Integer>();
        totalSquares = new HashMap<TaxonVariety<Plant>, Integer>();
        sortButtons = new HashMap<JToggleButton, Comparator<TaxonVariety<Plant>>>();

        computeStatistics();
        displayStatistics();
    }

    private void computeStatistics()
    {
        // compute statistics
        for (Map.Entry<Integer, HashMap<org.sourceforge.kga.Point, java.util.List<TaxonVariety<Plant>>>> yearMap : getGarden().getAllSquares().entrySet())
        {
            int year = yearMap.getKey();
            hintsGood.put(year, 0);
            hintsBad.put(year, 0);
            Map<TaxonVariety<Plant>, Integer> yearFrequency = new HashMap<TaxonVariety<Plant>, Integer>();
            frequency.put(year, yearFrequency);
            for (Map.Entry<org.sourceforge.kga.Point, java.util.List<TaxonVariety<Plant>>> s : yearMap.getValue().entrySet())
            {
                // compute hints
                HintList hints = Rule.getHints(getGarden(), year, s.getKey(), false);
                hintsGood.put(year, hintsGood.get(year) + hints.getValue(Hint.Value.GOOD));
                hintsBad.put(year, hintsBad.get(year) + hints.getValue(Hint.Value.BAD));

                // compute planting frequency
                for (TaxonVariety<Plant> plant : s.getValue())
                {
                    if (plant.isItem())
                        continue;
                    Integer count = yearFrequency.get(plant);
                    if (count == null)
                        count = 0;
                    yearFrequency.put(plant, count + 1);
                }
            }
        }

        // compute a set with all plant in the garden
        for (Map.Entry<Integer, Map<TaxonVariety<Plant>, Integer>> yearFrequency : frequency.entrySet())
            plantSet.addAll(yearFrequency.getValue().keySet());
        yearsSet = frequency.keySet();

        // for each plant in the garden
        for (TaxonVariety<Plant> plant : plantSet)
        {
            // compute how many years and how many squares it was planted
            for (Map.Entry<Integer, Map<TaxonVariety<Plant>, Integer>> yearFrequency : frequency.entrySet())
            {
                Integer squares = yearFrequency.getValue().get(plant);
                if (squares != null)
                {
                    Integer tmp = totalYears.get(plant);
                    if (tmp == null)
                        tmp = 0;
                    totalYears.put(plant, ++tmp);

                    tmp = totalSquares.get(plant);
                    if (tmp == null)
                        tmp = 0;
                    totalSquares.put(plant, tmp + squares);
                }
            }
        }
    }

    private JToggleButton createSortButton(Comparator<TaxonVariety<Plant>> c)
    {
        JToggleButton buttonSort = new JToggleButton();
        buttonSort.setIcon(iconAscending);
        buttonSort.setPreferredSize(new Dimension(iconAscending.getIconWidth() + 20, iconAscending.getIconHeight()));
        buttonSort.addActionListener(this);
        sortButtons.put(buttonSort, c);
        return buttonSort;
    }


    private void displayStatistics()
    {
        Translation t = Translation.getCurrent();

        // column panel
        GridBagLayout layoutYears = new GridBagLayout();
        JPanel panelYears = new JPanel(layoutYears);
        GridBagConstraints c = new GridBagConstraints();
        JLabel label;

        c.gridy = 0;
        c.ipadx = 5;
        c.ipady = 2;
        c.fill = GridBagConstraints.BOTH;
        for (Integer year : yearsSet)
        {
            c.gridx = 0;
            for (Integer value : new Integer[]
                {
                    year,
                    hintsGood.get(year),
                    hintsBad.get(year),
                    hintsGood.get(year) - hintsBad.get(year)
                })
            {
                label = new JLabel(value.toString());
                label.setHorizontalAlignment(SwingConstants.TRAILING);
                label.setBorder(PlantLabel.defaultBorder);
                panelYears.add(label, c);
                ++c.gridx;
            }
            panelYears.add(createSortButton(new SpeciesComparatorByFrequency(frequency.get(year))), c);
            ++c.gridy;
        }

        // adding years planted and squares planted hint
        label = new JLabel(t.total_years());
        label.setBorder(PlantLabel.defaultBorder);
        c.gridx = 0;
        c.gridwidth = 4;
        panelYears.add(label, c);

        c.gridx = 4;
        c.gridwidth = 1;
        panelYears.add(createSortButton(new SpeciesComparatorByFrequency(totalYears)), c);

        label = new JLabel(t.total_squares());
        label.setBorder(PlantLabel.defaultBorder);
        ++c.gridy;
        c.gridx = 0;
        c.gridwidth = 4;
        panelYears.add(label, c);

        c.gridx = 4;
        c.gridwidth = 1;
        panelYears.add(createSortButton(new SpeciesComparatorByFrequency(totalSquares)), c);

        // determine size of the cell with statistics
        JPanel panelSpecies = createSpeciesPanel();
        statisticsCellSize = new Dimension(
            panelSpecies.getLayout().minimumLayoutSize(panelSpecies).width / plantSet.size(),
            layoutYears.minimumLayoutSize(panelYears).height / (yearsSet.size() + 2));

        // corner panel
        GridBagLayout layoutCorner = new GridBagLayout();
        JPanel panelCorner = new JPanel(layoutCorner);

        label = new JLabel(" ");
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 5;
        panelCorner.add(label, c);

        label = new JLabel(t.year());
        label.setBorder(PlantLabel.defaultBorder);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        panelCorner.add(label, c);

        label = new JLabel();
        label.setIcon(new ImageIcon(SwingFXUtils.fromFXImage(Rule.GOOD, null)));
        label.setBorder(PlantLabel.defaultBorder);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridx = 1;
        panelCorner.add(label, c);

        label = new JLabel();
        label.setIcon(new ImageIcon(SwingFXUtils.fromFXImage(Rule.BAD, null)));
        label.setBorder(PlantLabel.defaultBorder);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridx = 2;
        panelCorner.add(label, c);

        label = new JLabel("=");
        label.setBorder(PlantLabel.defaultBorder);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridx = 3;
        panelCorner.add(label, c);

        c.gridx = 4;
        currentSortButton = createSortButton(new TaxonVarietyComparatorByName());
        currentSortButton.setSelected(true);
        panelCorner.add(currentSortButton, c);

        // add table into a scroll pane
        scrollPane = new JScrollPane();
        scrollPane.setRowHeaderView(panelYears);
        scrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, panelCorner);

        Dimension panelSpeciesSize = panelSpecies.getLayout().minimumLayoutSize(panelSpecies);
        Dimension panelYearsSize = layoutYears.minimumLayoutSize(panelYears);
        Insets border = scrollPane.getBorder().getBorderInsets(scrollPane);
        scrollPane.setPreferredSize(new Dimension(
            Math.min(800,
                panelSpeciesSize.width + panelYearsSize.width +
                scrollPane.getVerticalScrollBar().getMaximumSize().width +
                border.left + border.right),
            Math.min(600,
                panelSpeciesSize.height + panelYearsSize.height +
                scrollPane.getHorizontalScrollBar().getMaximumSize().height) +
                border.top + border.bottom));

        fillStatisticsTable();
        scrollPane.getHorizontalScrollBar().setUnitIncrement(statisticsCellSize.width / 3);

        // display dialog
        JDialog properties = new JOptionPane(scrollPane).createDialog(
            t.action_garden_statistics());
        Gui.makeWindowBoundsPersistent(properties, "GardenStatistics", false);
        properties.setVisible(true);
    }

    JPanel createSpeciesPanel()
    {
        GridLayout layoutSpecies = new GridLayout(1, plantSet.size());
        JPanel panelSpecies = new JPanel(layoutSpecies);
        // TODO: for (Taxon plant : plantSet)
        //      panelSpecies.add(new PlantLabel(plant));
        return panelSpecies;
    }

    void fillStatisticsTable()
    {
        // row panel
        JPanel panelSpecies = createSpeciesPanel();

        // fill table with squares count
        JLabel label;
        JPanel panelStatistics = new JPanel(
            new GridLayout(yearsSet.size() + 2, plantSet.size()));
        for (Map.Entry<Integer, Map<TaxonVariety<Plant>, Integer>> yearFrequency : frequency.entrySet())
        {
            for (TaxonVariety<Plant> plant : plantSet)
            {
                Integer squares = yearFrequency.getValue().get(plant);
                label = new JLabel(squares == null ? "" : squares.toString());
                label.setPreferredSize(statisticsCellSize);
                label.setHorizontalAlignment(SwingConstants.TRAILING);
                label.setBorder(PlantLabel.defaultBorder);
                panelStatistics.add(label);
            }
        }

        for (TaxonVariety<Plant> plant : plantSet)
        {
            label = new JLabel(totalYears.get(plant).toString());
            label.setPreferredSize(statisticsCellSize);
            label.setHorizontalAlignment(SwingConstants.TRAILING);
            label.setBorder(PlantLabel.defaultBorder);
            panelStatistics.add(label);
        }

        for (TaxonVariety<Plant> plant : plantSet)
        {
            label = new JLabel(totalSquares.get(plant).toString());
            label.setPreferredSize(statisticsCellSize);
            label.setHorizontalAlignment(SwingConstants.TRAILING);
            label.setBorder(PlantLabel.defaultBorder);
            panelStatistics.add(label);
        }

        scrollPane.setViewportView(panelStatistics);
        scrollPane.setColumnHeaderView(panelSpecies);
    }
}
