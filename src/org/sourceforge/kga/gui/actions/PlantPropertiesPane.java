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

import javafx.beans.property.Property;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.sourceforge.kga.Animal;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Taxon;
import org.sourceforge.kga.gui.gardenplan.EditableGarden;
import org.sourceforge.kga.gui.gardenplan.GardenCanvas;
import org.sourceforge.kga.gui.gardenplan.plantSelection.PlantRelationshipPane;
import org.sourceforge.kga.gui.plants.PlantComponent;
import org.sourceforge.kga.gui.plants.PlantLabel;
import org.sourceforge.kga.plant.Companion;
import org.sourceforge.kga.plant.PropertySource;
import org.sourceforge.kga.plant.Reference;
import org.sourceforge.kga.plant.SourceList;
import org.sourceforge.kga.translation.Translation;

import java.util.ArrayList;
import java.util.TreeMap;


/**
 * Window that displays properties and rules for a plant
 */
public class PlantPropertiesPane extends VBox
{
    private static final long serialVersionUID = 1L;

    TextField textName = new TextField();
    TextField textFamily = new TextField();
    ImageView image = new ImageView();
    TextField textTaxon = new TextField();
    Button buttonParent = new Button();
    ComboBox<String> comboConnector = new ComboBox<>();

    public PlantPropertiesPane()
    {
        setPadding(new Insets(5));
        setSpacing(5);
        // setHgap(5);
        // setVgap(5);
        setPadding(new Insets(15, 15, 15, 15));
        //image.setFitHeight(1.5*GardenCanvas.PLANT_SIZE);
        image.setPreserveRatio(true);
    }

    void setupNodes(Plant plant, boolean edit)
    {
        getChildren().clear();

        Translation t = Translation.getCurrent();
        GridPane gridName = new GridPane();
        int row = 0;
        int col;
        int imageHeight=0;

        getChildren().add(gridName);

        col = 0;
        gridName.add(new Label(t.name()), col++, row);
        gridName.add(textName, col++, row);
        textName.setEditable(false);

        ++row;
        col = 0;
        gridName.add(new Label(t.family()), col++, row);
        gridName.add(textFamily, col++, row);
        textFamily.setEditable(false);

        if (plant.getType().ordinal() >= Taxon.Type.GENUS.ordinal())
        {
            ++row;
            col = 0;
            Label typeLabel = new Label(plant.getType() == Taxon.Type.GENUS ? t.genus() : t.species());
            imageHeight=(int)(6*typeLabel.getFont().getSize());
            gridName.add(typeLabel, col++, row);
            gridName.add(textTaxon, col++, row);
            textTaxon.setEditable(false);
        }

        image.setFitHeight(imageHeight);
        gridName.add(image, col++, 0, 1, 3);

    }

    public void loadPlant(Plant plant)
    {
        setupNodes(plant, false);

        Translation t = Translation.getCurrent();

        textFamily.setText(t.translate(plant.getFamily()));

        textTaxon.setText(plant.getName());

        textName.setText(t.translate(plant));
        image.setImage(plant.getImage());
        ScrollPane scrollCompanions = new ScrollPane(new PlantRelationshipPane(plant));
        getChildren().add(scrollCompanions);
    }

}

/*
    String getLifetimeText(Lifetime lifetime)
    {
        Translation t = Translation.getCurrent();
        StringBuilder text = new StringBuilder();
        text.append(lifetime.translate());
        if ((lifetime.get() != Lifetime.Value.PERENNIAL && lifetime.getRepetitionYears() != 1) ||
            (lifetime.get() == Lifetime.Value.PERENNIAL && lifetime.getRepetitionYears() != Integer.MAX_VALUE))
        {
            text.append(" ");
            text.append(t.allowed_repetitions());
            text.append("=");
            text.append(lifetime.getRepetitionYears());
            text.append(" ");
            text.append(lifetime.getRepetitionYears() == 1 ? t.year().toLowerCase() : t.years());
        }
        if (lifetime.getRepetitionYears() != Integer.MAX_VALUE)
        {
            text.append(" ");
            text.append(t.repetition_gap());
            text.append("=");
            text.append(lifetime.getRepetitionGap());
            text.append(" ");
            text.append(lifetime.getRepetitionGap() == 1 ? t.year().toLowerCase() : t.years());
        }

        // t.repetition_gap() + " " +
        //       Integer.toString(plant.lifetime.getRepetitionYears()) + "/" +
        //        Integer.toString(plant.lifetime.getRepetitionGap()) + " " + t.years());

        return text.toString();
    }

    public void loadPlant(Plant plant)
    {
        removeAll();
        if (plant == null)
        {
            return;
        }

        Translation t = Translation.getCurrent();
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);

        // create the controls that display plant properties ( name, family etc. )
        JLabel labelName           = new JLabel(t.translate(plant));
        JLabel labelScientificName = new JLabel(plant.getName());
        JLabel labelImage          = new JLabel();
        JLabel labelFamily         = new JLabel(
            t.family() + " " + t.translate(plant.getFamily()));
        JLabel labelLifetime       = new JLabel(getLifetimeText(plant.lifetime));

        Font f = getFont();
        labelName.          setFont(new Font(f.getName(), Font.BOLD, f.getSize() + 4));
        labelScientificName.setFont(new Font(f.getName(), Font.ITALIC, f.getSize()));
        labelFamily.        setFont(new Font(f.getName(), Font.PLAIN, f.getSize()));
        labelLifetime.      setFont(new Font(f.getName(), Font.PLAIN, f.getSize()));

        if (plant.getImage() != null)
            labelImage.     setIcon(new ImageIcon(SwingFXUtils.fromFXImage(plant.getImage(), null).getScaledInstance(72, 72, Image.SCALE_SMOOTH)));

        GroupLayout.ParallelGroup   hGroup  = layout.createParallelGroup();
        GroupLayout.SequentialGroup h2Group = layout.createSequentialGroup();
        GroupLayout.ParallelGroup   h3Group = layout.createParallelGroup();
        h3Group.addComponent(labelName);
        h3Group.addComponent(labelScientificName);
        h3Group.addComponent(labelFamily);
        h3Group.addComponent(labelLifetime);
        h2Group.addGroup(h3Group);
        h2Group.addComponent(labelImage);
        hGroup.addGroup(h2Group);

        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
        GroupLayout.ParallelGroup   v2Group = layout.createParallelGroup();
        GroupLayout.SequentialGroup v3Group = layout.createSequentialGroup();
        v3Group.addComponent(labelName);
        v3Group.addComponent(labelScientificName);
        v3Group.addComponent(labelFamily);
        v3Group.addComponent(labelLifetime);
        v2Group.addGroup(v3Group);
        v2Group.addComponent(labelImage);
        vGroup.addGroup(v2Group);

        // add companions
        for (Companion.Type companionType : Companion.Type.values())
        {
            JLabel labelRule = null;
            switch (companionType) {
                case GOOD:
                    labelRule = new JLabel(t.companion_good());
                    break;
                case IMPROVE:
                    labelRule = new JLabel(t.companion_improve());
                    break;
                case BAD:
                    labelRule = new JLabel(t.companion_bad());
                    break;
                case INHIBIT:
                    labelRule = new JLabel(t.companion_inhibit());
                    break;
                case ATTRACT_PEST:
                    labelRule = new JLabel(t.companion_attract_pest());
                    break;
                case REPEL_PEST:
                    labelRule = new JLabel(t.companion_repel_pest());
                    break;
                case ATTRACT_BENEFICIAL:
                    labelRule = new JLabel(t.companion_attract_beneficial());
                    break;
                case REPEL_BENEFICIAL:
                    labelRule = new JLabel(t.companion_repel_beneficial());
                    break;
                case TRAP_PEST:
                    labelRule = new JLabel(t.companion_trap_pest());
                    break;
            }

            ArrayList<Companion> selected = new ArrayList<>();
            for (Companion companion : plant.getCompanions().getInherited()) // TODO: display which companions are inherited
                if (companion.type == companionType)
                    selected.add(companion);
            if (selected.size() != 0)
            {
                hGroup.addComponent(labelRule);
                vGroup.addComponent(labelRule);

                JPanel panelRule = new JPanel(new GridLayout(0, 8));
                for (Companion companion : selected)
                {
                    panelRule.add(new PlantLabel(companion.plant));
                    // TODO: display additional and sources
                }
                hGroup.addComponent(panelRule);
                vGroup.addComponent(panelRule);
            }
        }

        // add good crop rotation rules
        StringBuilder goodCropRotation = null;;
        for (Rule rule : plant.getRules(GoodCropRotationRule.class))
        {
            if (goodCropRotation == null)
            {
                goodCropRotation = new StringBuilder();
                goodCropRotation.append("<html><b>");
                goodCropRotation.append(t.rotation_good());
                goodCropRotation.append(":</b> ");
            }
            GoodCropRotationRule goodCropRotationRule = (GoodCropRotationRule)rule;
            goodCropRotation.append(t.translate(goodCropRotationRule.getFamily()));
            goodCropRotation.append(" ");
        }
        if (goodCropRotation != null)
        {
            JLabel labelRotation = new JLabel(goodCropRotation.toString());
            labelRotation.setFont(new Font(f.getName(), Font.PLAIN, f.getSize()));
            hGroup.addComponent(labelRotation);
            vGroup.addComponent(labelRotation);
        }

        // add bad crop rotation rules
        StringBuilder badCropRotation = null;;
        for (Rule rule : plant.getRules(BadCropRotationRule.class))
        {
            if (badCropRotation == null)
            {
                badCropRotation = new StringBuilder();
                badCropRotation.append("<html><b>");
                badCropRotation.append(t.rotation_bad());
                badCropRotation.append(":</b> ");
            }
            BadCropRotationRule badCropRotationRule = (BadCropRotationRule)rule;
            badCropRotation.append(t.translate(badCropRotationRule.getFamily()));
            badCropRotation.append(" ");
        }
        if (badCropRotation != null)
        {
            JLabel labelRotation = new JLabel(badCropRotation.toString());
            labelRotation.setFont(new Font(f.getName(), Font.PLAIN, f.getSize()));
            hGroup.addComponent(labelRotation);
            vGroup.addComponent(labelRotation);
        }

        layout.setHorizontalGroup(hGroup);
        layout.setVerticalGroup(vGroup);
    }
*/
