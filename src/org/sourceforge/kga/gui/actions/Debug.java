package org.sourceforge.kga.gui.actions;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.stage.Window;
import org.sourceforge.kga.Garden;
import org.sourceforge.kga.gui.PersistWindowBounds;
import org.sourceforge.kga.prefs.Preferences;
import org.sourceforge.kga.translation.Translation;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Created by tidu8815 on 26/09/2018.
 */
public class Debug extends Dialog<ButtonType>
{
    public static class Handler extends java.util.logging.Handler
    {
        public final Label label = new Label();

        void append(String text)
        {
            label.setText(text + "\n" + label.getText());
        }

        static Handler instance = null;
        public static void initialize()
        {
            instance = new Handler();
        }

        public static Handler getInstance()
        {
            return instance;
        }

        private Handler()
        {
            append(Garden.class.getName());
            Logger logger = Logger.getLogger(Garden.class.getName());
            while (logger.getParent() != null)
                logger = logger.getParent();
            logger.setLevel(Level.FINEST);
            logger.addHandler(this);
        }

        @Override
        public void publish(LogRecord record)
        {
            if (record.getSourceClassName().startsWith("org.sourceforge.kga"))
                append(record.getMessage());
        }

        @Override
        public void close() throws SecurityException
        {
        }

        @Override
        public void flush()
        {

        }
    }

    public void showDialogAndWait(Window parent)
    {
        setResizable(true);
        initOwner(parent);

        Translation t = Translation.getCurrent();

        setTitle(t.debug());

        ScrollPane pane = new ScrollPane(Handler.getInstance().label);
        // pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        getDialogPane().setContent(pane);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK);

        PersistWindowBounds.persistWindowBounds(
            getDialogPane(),
            Preferences.gui.debugWindow.windowBounds,
            true);
        showAndWait();
    }
}
