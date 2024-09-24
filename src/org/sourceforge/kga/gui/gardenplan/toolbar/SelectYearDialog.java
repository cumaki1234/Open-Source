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

package org.sourceforge.kga.gui.gardenplan.toolbar;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import org.sourceforge.kga.Garden;
import org.sourceforge.kga.gui.gardenplan.GardenTabPane;
import org.sourceforge.kga.translation.Translation;

import java.util.Calendar;
import java.util.Collections;
import java.util.Optional;


public class SelectYearDialog extends Dialog<ButtonType>
{
    private static final long serialVersionUID = 1L;

    public SelectYearDialog()
    {
    }

    private int findYear(Garden garden, boolean yearInGarden, Integer oldValue, Integer newValue)
    {
        if (garden.getYears().contains(newValue) == yearInGarden)
            return newValue;

        int direction = newValue != null && oldValue != null && newValue - oldValue < 0 ? -1 : 1;
        while (garden.getYears().contains(newValue))
        {
            newValue += direction;
        }
        return newValue;
    }

    private Spinner<Integer> createSpinner(Garden garden, boolean yearInGarden, int selectedYear)
    {
        if (yearInGarden)
        {
            int min = Collections.min(garden.getYears());
            int max = Collections.max(garden.getYears());;
            return new Spinner<>(min, max, selectedYear);
        }
        else
        {
            return new Spinner<>(1900, 2100, findYear(garden, yearInGarden, 0, selectedYear)) ;
        }
    }

    public Optional<Integer> showAndWait(Garden garden, boolean yearInGarden, int selectedYear, String title, String text)
    {
        setTitle(title);//t.action_new_year());
        Spinner<Integer> spinner = createSpinner(garden, yearInGarden, selectedYear);
        spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            spinner.getValueFactory().setValue(findYear(garden, yearInGarden, oldValue, newValue));
        });
        spinner.getEditor().setPrefColumnCount(5);

        HBox box = new HBox();
        box.getChildren().addAll(new Label(text),spinner);//t.choose_year_to_add()), spinner);
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);

        getDialogPane().setContent(box);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = showAndWait();
        Optional<Integer> yearToAdd = Optional.empty();
        if (result.isPresent() && result.get() == ButtonType.OK)
        {
            yearToAdd = Optional.of(spinner.getValue());
        }
        return yearToAdd;
    }

    public static void newYear(Garden garden, GardenTabPane gardenTabPane)
    {
    	Translation t = Translation.getCurrent();
        Optional<Integer> yearToAdd = new SelectYearDialog().showAndWait(
                garden, false, Calendar.getInstance().get(Calendar.YEAR),t.action_new_year(),t.choose_year_to_add());
        if (yearToAdd.isPresent())
        {
            garden.addYear(yearToAdd.get());
            gardenTabPane.selectYear(yearToAdd.get());
        }
    }

    public static void deleteYear(Garden garden, GardenTabPane gardenTabPane)
    {
    	Translation t = Translation.getCurrent();
        Optional<Integer> yearToDelete = new SelectYearDialog().showAndWait(
                garden, true, gardenTabPane.selectedYear(),t.action_year_delete(),t.choose_year_to_delete());
        if (yearToDelete.isPresent())
        {
            garden.deleteYear(yearToDelete.get());
            if (garden.getYears().size() == 0)
            {
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                garden.addYear(currentYear);
                gardenTabPane.selectYear(currentYear);
            }
        }
    }
}
