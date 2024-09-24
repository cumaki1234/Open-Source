package org.sourceforge.kga.gui.actions.importData;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.sourceforge.kga.translation.Translation;

public class SearchAndReplace implements DocumentListener, ActionListener, ListSelectionListener
{
    Translation t = Translation.getCurrent();
    DataTableModel csvModel;
    JButton buttonSearchNext = new JButton(t.search());
    JButton buttonReplace = new JButton(t.replace());
    JButton buttonReplaceAll = new JButton(t.replace_all());
    JCheckBox checkSearchWholeText = new JCheckBox(t.search_whole_text());
    JCheckBox checkSearchCaseInsensitive = new JCheckBox(t.search_case_insensitive());
    JCheckBox checkSearchReverse = new JCheckBox(t.search_direction_reverse());
    JCheckBox checkSearchByColumn = new JCheckBox(t.search_direction_by_column());
    JTextField textSearch = new JTextField();
    JTextField textReplace = new JTextField();
    JTable table;
    JDialog searchDialog;

    public SearchAndReplace(JTable table)
    {
        this.table = table;
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel labelSearch = new JLabel(t.search());
        JLabel labelReplace = new JLabel(t.replace());

        buttonSearchNext.addActionListener(this);
        buttonReplace.addActionListener(this);
        buttonReplaceAll.addActionListener(this);
        buttonReplace.setEnabled(false);

        textSearch.getDocument().addDocumentListener(this);
        checkSearchWholeText.addActionListener(this);
        checkSearchCaseInsensitive.addActionListener(this);
        table.getSelectionModel().addListSelectionListener(this);

        GridBagConstraints c = new GridBagConstraints();
        panel.add(labelSearch, c);
        c.gridx = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        panel.add(textSearch, c);
        c.gridy = 1;
        panel.add(textReplace, c);
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.weightx = 0;
        panel.add(labelReplace, c);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridwidth = 2;
        c.gridy = 2;
        panel.add(checkSearchWholeText, c);
        c.gridy = 3;
        panel.add(checkSearchCaseInsensitive, c);
        c.gridy = 4;
        panel.add(checkSearchReverse, c);
        c.gridy = 5;
        panel.add(checkSearchByColumn, c);
        searchDialog = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_OPTION, null,
                new JButton[] { buttonSearchNext, buttonReplace, buttonReplaceAll }).createDialog(
                t.search());
    }

    public JDialog getDialog()
    {
        this.csvModel = (DataTableModel)table.getModel();
        return searchDialog;
    }

    private void checkReplaceButton()
    {
        if (csvModel == null || textSearch.getText().isEmpty())
        {
            buttonReplace.setEnabled(false);
            return;
        }

        int row = 0, column = 0;
        if (table.getSelectionModel() != null &&
            table.getColumnModel().getSelectionModel() != null)
        {
            row = table.getSelectionModel().getLeadSelectionIndex();
            column = table.getColumnModel().getSelectionModel().getLeadSelectionIndex();
            if (row < 0 || row > csvModel.getRowCount() ||
                    column < 0 || column > csvModel.getColumnCount())
            {
                buttonReplace.setEnabled(false);
                return;
            }
            buttonReplace.setEnabled(
                checkValue((String)csvModel.getValueAt(row, column), textSearch.getText()) != -1);
        }
    }

    int checkValue(String value, String criteria)
    {
        if (checkSearchCaseInsensitive.isSelected())
        {
            value = value.toLowerCase();
            criteria = criteria.toLowerCase();
        }
        if (checkSearchWholeText.isSelected())
            return value.compareTo(criteria) == 0 ? 0 : -1;
        return value.indexOf(criteria);
    }

    boolean searchNextCell()
    {
        if (csvModel == null)
            return false;

        //getGui().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        int row = 0, column = 0;
        if (table.getSelectionModel() != null &&
            table.getColumnModel().getSelectionModel() != null)
        {
            row = table.getSelectionModel().getLeadSelectionIndex();
            column = table.getColumnModel().getSelectionModel().getLeadSelectionIndex();
            if (row < 0 || row > csvModel.getRowCount())
                row = 0;
            if (column < 0 || column > csvModel.getColumnCount())
                column = 0;
        }
        int i = row, j = column;
        do
        {
            if (checkSearchReverse.isSelected())
            {
                if (checkSearchByColumn.isSelected())
                {
                    // on column
                    --i;
                    if (i < 0)
                    {
                        i = csvModel.getRowCount() - 1;
                        --j;
                        if (j < 0)
                            j = csvModel.getColumnCount() - 1;
                    }
                }
                else
                {
                    // on row
                    --j;
                    if (j < 0)
                    {
                        j = csvModel.getColumnCount() - 1;
                        --i;
                        if (i < 0)
                            i = csvModel.getRowCount() - 1;
                    }
                }
            }
            else
            {
                if (checkSearchByColumn.isSelected())
                {
                    // on column
                    ++i;
                    if (i >= csvModel.getRowCount())
                    {
                        i = 0;
                        ++j;
                        if (j >= csvModel.getColumnCount())
                            j = 0;
                    }
                }
                else
                {
                    // on row
                    ++j;
                    if (j >= csvModel.getColumnCount())
                    {
                        j = 0;
                        ++i;
                        if (i >= csvModel.getRowCount())
                            i = 0;
                    }
                }
            }
            // log.info("Searching in " + Integer.toString(i) + " " + Integer.toString(j));
            if (checkValue((String)csvModel.getValueAt(i, j), textSearch.getText()) != -1)
            {
                table.changeSelection(i, j, false, false);
                //getGui().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return true;
            }
        } while (!(i == row && j == column));
        //getGui().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        return false;
    }

    private void replaceValueAt(int row, int column)
    {
        String value = (String)csvModel.getValueAt(row, column);
        int found = checkValue(value, textSearch.getText());
        if (found != -1)
        {
            String replace = value.substring(0, found) + textReplace.getText() +
                value.substring(found + textSearch.getText().length());
            csvModel.setValueAt(replace, row, column);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object source = e == null ? null : e.getSource();
        if (source == buttonSearchNext)
        {
            if (!searchNextCell())
                JOptionPane.showMessageDialog(null, Translation.getCurrent().nothing_found());
        }
        else if (source == buttonReplace)
        {
            int row = table.getSelectionModel().getLeadSelectionIndex();
            int column = table.getColumnModel().getSelectionModel().getLeadSelectionIndex();
            replaceValueAt(row, column);
            if (!searchNextCell())
                JOptionPane.showMessageDialog(null, Translation.getCurrent().nothing_found());
        }
        else if (source == buttonReplaceAll)
        {
            for (int i = 0; i < csvModel.getRowCount(); ++i)
                for (int j = 0; j < csvModel.getColumnCount(); ++j)
                    replaceValueAt(i, j);
        }
        else if (source == textSearch || source == checkSearchWholeText ||
                source == checkSearchCaseInsensitive)
        {
            valueChanged(null);
        }
    }


    @Override
    public void valueChanged(ListSelectionEvent e)
    {
        checkReplaceButton();
    }

    @Override
    public void insertUpdate(DocumentEvent e)
    {
        checkReplaceButton();
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
        checkReplaceButton();
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
        checkReplaceButton();
    }
}
