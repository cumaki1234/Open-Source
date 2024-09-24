package org.sourceforge.kga.gui.actions.importData;

import javax.swing.table.AbstractTableModel;


public class SourceTableModel extends AbstractTableModel
{
    CsvParser csv;

    public SourceTableModel(CsvParser c)
    {
        csv = c;
    }

    @Override
    public int getRowCount()
    {
        return csv.sources.size();
    }

    @Override
    public int getColumnCount()
    {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        switch (columnIndex)
        {
        case 0:
            return csv.sources.get(rowIndex).id;
        case 1:
            return csv.sources.get(rowIndex).name;
        case 2:
            return csv.sources.get(rowIndex).url;
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
            return "id";
        case 1:
            return "name";
        case 2:
            return "url";
        }
        return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        switch (columnIndex)
        {
        case 0:
            return Integer.class;
        case 1:
        case 2:
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
