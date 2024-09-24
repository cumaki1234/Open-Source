package org.sourceforge.kga.gui.actions.importData;

import javax.swing.table.AbstractTableModel;


public class TagTableModel extends AbstractTableModel
{
    CsvParser csv;

    public TagTableModel(CsvParser c)
    {
        csv = c;
    }

    @Override
    public int getRowCount()
    {
        return csv.tags.size();
    }

    @Override
    public int getColumnCount()
    {
        return 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        switch (columnIndex)
        {
        case 0:
            return csv.tags.keySet().toArray()[rowIndex]; // TODO; don't call toArray every time
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
            return "tag";
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
