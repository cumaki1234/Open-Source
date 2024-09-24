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

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import org.sourceforge.kga.*;
import org.sourceforge.kga.translation.Iso639_1;
import org.sourceforge.kga.translation.Translation;

import java.util.Optional;

public class Language extends Dialog<ButtonType>
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());
    private static final long serialVersionUID = 1L;

    Iso639_1.Language selected;

    public Language()
    {
    }

    public Iso639_1.Language showAndWait(Window owner)
    {
        /* setResizable(false);
        initStyle(StageStyle.UTILITY);
        initModality(Modality.WINDOW_MODAL); */

        VBox pane = new VBox();
        ScrollPane scroll = new ScrollPane();
        ListView<Iso639_1.Language> combo = new ListView<>();
        Label labelAuthor = new Label();
        scroll.setContent(combo);

        pane.getChildren().addAll(scroll, labelAuthor);
        getDialogPane().getButtonTypes().add(ButtonType.OK);
        if (owner != null)
        {
            initOwner(owner);
            getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        }
        getDialogPane().setContent(pane);

        combo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Translation t = Resources.translations().get(newValue.code);
            labelAuthor.setText(t == null ? "" : t.getAuthor());
        });

        // load available languages
        for (Iso639_1.Language language : Resources.translations().getLanguageItems())
        {
            combo.getItems().add(language);
            if (language.code.equals(Translation.getCurrent().getLanguage()))
                combo.getSelectionModel().select(language);
        }

        Iso639_1.Language selected = null;
        Optional<ButtonType> result = showAndWait();
        if (owner == null || result.isPresent() && result.get() == ButtonType.OK)
        {
            selected = combo.getSelectionModel().getSelectedItem();
        }
        return selected;
    }
}
