package org.sourceforge.kga.gui.actions.importData;

import javax.swing.table.AbstractTableModel;

import org.sourceforge.kga.Garden;


public class TaggedPlantsTableModel extends AbstractTableModel
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());
    CsvParser csv;
    String tag;

    public TaggedPlantsTableModel(CsvParser c)
    {
        csv = c;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount()
    {
        if (tag == null)
            return 0;
        log.info("get row count: " + Integer.toString(csv.tags.get(tag).size()));
        return csv.tags.get(tag).size();
    }

    @Override
    public int getColumnCount()
    {
        return 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (tag == null)
            return null;
        switch (columnIndex)
        {
        case 0:
            return csv.tags.get(tag).get(rowIndex);
        }
        return null;
    }


    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex)
    {
        // TODO: implement this
        this.fireTableDataChanged();
    }

    @Override
    public String getColumnName(int columnIndex)
    {
        switch (columnIndex)
        {
        case 0:
            return "plant";
        }
        return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        switch (columnIndex)
        {
        case 0:
            return String.class;
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return true;
    }
}
