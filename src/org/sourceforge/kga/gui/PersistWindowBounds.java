package org.sourceforge.kga.gui;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.sourceforge.kga.prefs.EntryDouble;
import org.sourceforge.kga.prefs.EntryWindowBounds;

/**
 * Created by Tiberius on 8/16/2017.
 */
public class PersistWindowBounds
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(PersistWindowBounds.class.getName());

    private static boolean noWindowSystem()
    {
        /* log.fine("Stage.class.isInstance(window)=" + Boolean.toString(Stage.class.isInstance(window)));
        if (Stage.class.isInstance(window))
        {
            log.fine("Style=" + ((Stage) window).getStyle().toString());
        }
        return Stage.class.isInstance(window) && ((Stage)window).getStyle() == StageStyle.UNDECORATED; */
        return !Platform.isSupported(ConditionalFeature.UNIFIED_WINDOW);
    }

    public static void moveToCenter(Region node, double ratio)
    {
        Window window = node.getScene().getWindow();
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        window.setX(visualBounds.getWidth() * (1 - ratio) / 2);
        window.setY(visualBounds.getHeight()  * (1 - ratio) / 2);
        node.setPrefWidth(visualBounds.getWidth() * ratio);
        node.setPrefHeight(visualBounds.getHeight() * ratio);
    }

    public static void persistWindowBounds(Region node, final EntryWindowBounds bounds, final boolean persistSize)
    {
        if (noWindowSystem())
        {
            moveToCenter(node, 1);
        }
        else
        {
            loadWindowBounds(node, bounds, persistSize);
            node.getScene().getWindow().addEventHandler(
                    WindowEvent.WINDOW_HIDING,
                    event -> { saveWindowBounds(node, bounds, persistSize); });
        }
    }

    private static void loadWindowBounds(Region node, final EntryWindowBounds bounds, final boolean persistSize)
    {
        log.info("Restoring bounds for " + bounds.x.node().toString());

        double x = bounds.x.get();
        double y = bounds.y.get();
        if (!Double.isNaN(x) && !Double.isNaN(y))
        {
            log.info("Restore location to " + Double.toString(x) + " " + Double.toString(y));
            Window window = node.getScene().getWindow();
            window.setX(Math.max(0., x));
            window.setY(Math.max(0., y));
        }

        if (persistSize)
        {
            double w = bounds.w.get();
            double h = bounds.h.get();
            if (!Double.isNaN(w) && !Double.isNaN(h))
            {
                log.info("Restore size to " + Double.toString(w) + " " + Double.toString(h));
                node.setPrefWidth(Math.max(0., w));
                node.setPrefHeight(Math.max(0., h));
            }
        }
    }

    private static void saveWindowBounds(Region node, EntryWindowBounds bounds, boolean persistSize)
    {
        Window window = node.getScene().getWindow();
        log.info(
            "Saving window position: " + bounds.x.node().toString() +
            " x=" + Double.toString(window.getX()) +
            " y=" + Double.toString(window.getY()));
        bounds.x.set(window.getX());
        bounds.y.set(window.getY());
        if (persistSize)
        {
            log.info("Saving window size: " + bounds.x.node().toString() + " w=" + Double.toString(node.getWidth()) + " h=" + Double.toString(node.getHeight()));
            bounds.w.set(node.getWidth());
            bounds.h.set(node.getHeight());
        }
    }

    public static void persistDividerPosition(SplitPane splitPane, EntryDouble entry)
    {
        double divPosition = entry.get();
        if (divPosition > 0.)
            splitPane.setDividerPositions(divPosition);
        splitPane.getDividers().get(0).positionProperty().addListener((observable, oldValue, newValue) -> {
            entry.set(newValue.doubleValue());
        });
    }
}
