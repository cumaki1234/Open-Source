package org.sourceforge.kga.gui.actions.importData;

import java.awt.Color;
import java.awt.Component;
import java.util.TreeMap;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.sourceforge.kga.Garden;
import org.sourceforge.kga.gui.actions.importData.CsvParser;

public class CsvRenderer extends DefaultTableCellRenderer
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());
    CsvParser csv;

    public CsvRenderer(CsvParser c)
    {
        this.csv = c;
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        TreeMap<Integer, String> e = csv.errors.get(row);
        String error = null;
        if (e != null)
        {
            error = e.get(column);
            super.setBackground(error != null ? Color.RED : null);
        }
        else
        {
            super.setBackground(null);
        }

        JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setToolTipText(error);
        return label;
    }
}
