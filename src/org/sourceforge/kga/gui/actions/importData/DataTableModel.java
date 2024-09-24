package org.sourceforge.kga.gui.actions.importData;

import javax.swing.table.AbstractTableModel;


public class DataTableModel extends AbstractTableModel
{
    CsvParser csv;

    public DataTableModel(CsvParser c)
    {
        csv = c;
    }

    @Override
    public int getRowCount()
    {
        return csv.values.size();
    }

    @Override
    public int getColumnCount()
    {
        return csv.columns.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        return csv.values.get(rowIndex).get(columnIndex);
    }


    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex)
    {
        csv.setValue(rowIndex, columnIndex, value.toString());
        this.fireTableDataChanged();
    }

    @Override
    public String getColumnName(int columnIndex)
    {
        return csv.columns.get(columnIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return true;
    }
}
