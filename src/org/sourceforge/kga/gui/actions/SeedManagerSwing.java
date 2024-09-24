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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.prefs.Preferences;

import org.sourceforge.kga.*;
import org.sourceforge.kga.SeedEntry;
import org.sourceforge.kga.SeedEntry.PlantOrUnregistered;
import org.sourceforge.kga.SeedEntry.Quantity;
import org.sourceforge.kga.gui.*;
import org.sourceforge.kga.plant.TagInInventory;
import org.sourceforge.kga.translation.*;

public class SeedManagerSwing // extends KgaAction implements FileWithChanges.Listener
{
    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(Garden.class.getName());
    private static final long serialVersionUID = 1L;

    JDialog dialog;
    JTextField textDate = new JTextField();
    LocalDate workingDate;
    SeedFileWithChanges seedFile = null;//new SeedFileWithChanges();
    JToggleButton buttonDateList = new JToggleButton();
    JButton buttonNow = new JButton();
    JButton buttonDelete = new JButton();
    JButton buttonMove = new JButton();
    JButton buttonCopy = new JButton();
    JTable tableInventory = new JTable();
    JTable tableShoppingList = new JTable();
    JLabel labelInventory = new JLabel();
    JLabel labelShoppingList = new JLabel();
    JMenu fileMenu = new JMenu();
    JMenuItem menuNew = new JMenuItem();
    JMenuItem menuOpen = new JMenuItem();
    JMenuItem menuSave = new JMenuItem();
    JMenuItem menuSaveAs = new JMenuItem();
    JMenuItem menuPrint = new JMenuItem();
    JMenu toolsMenu = new JMenu();
    JMenuItem menuOptions = new JMenuItem();
    JMenu menuAutogenerate = new JMenu();
    JMenuItem menuAutogenerateFromGarden = new JMenuItem();
    JMenuItem menuAutogenerateFromInventory = new JMenuItem();
    JComboBox<LocalDate> comboValidFromDates = new JComboBox<>();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
    boolean inventorySelected = false;

    public class SeedListTableModel extends AbstractTableModel implements SeedList.Listener
    {
        SeedList seedList;

        public SeedListTableModel(SeedList seedList)
        {
            this.seedList = seedList;
            seedList.addListener(this);
        }

        @Override
        public void viewChanged()
        {
            fireTableDataChanged();
        }

        @Override
        public void listChanged()
        {
            fireTableDataChanged();
            // loadValidFromDates();
        }

        @Override
        public int getRowCount()
        {
            return seedList.size() + 1;
        }

        @Override
        public int getColumnCount()
        {
            int k = 0;
            /*for (int i = 0; i < viewColumns.length; ++i)
                if (viewColumns[i])
                    ++k; */
            return k;
        }

        public int convertColumn(int j)
        {
            int i = 0;/*
            while (j != 0)
            {
                ++i;
                if (i >= viewColumns.length)
                    break;
                if (viewColumns[i])
                    --j;
            }*/
            return i;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex)
        {
            if (rowIndex == seedList.size())
            {
                if (columnIndex == 0)
                    return new PlantOrUnregistered("");
                else
                    return null;
            }
            SeedEntry entry = seedList.get(rowIndex);
            switch (convertColumn(columnIndex))
            {
                case 0:
                    return entry.getPlant();
                case 1:
                    return entry.getVariety();
                case 2:
                    return entry.getQuantity();
                case 3:
                    return entry.getComment();
                case 4:
                    return entry.getValidFrom();
                case 5:
                    return entry.getValidTo();
            }
            return null;
        }


        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex)
        {
            /*
            SeedEntry entry;
            switch (convertColumn(columnIndex))
            {
                case 0:
                    SeedEntry.PlantOrUnregistered p = (SeedEntry.PlantOrUnregistered)value;
                    if (p.plant != null)
                        seedList.add(p.plant, workingDate);
                    else if (!p.unregisteredPlant.isEmpty())
                        seedList.add(p.unregisteredPlant, workingDate);
                    break;
                case 1:
                    entry = seedList.get(rowIndex);
                    seedList.setVariety(entry, value.toString(), workingDate);
                    break;
                case 2:
                    entry = seedList.get(rowIndex);
                    seedList.setQuantity(entry, (Quantity)value, workingDate);
                    break;
                case 3:
                    entry = seedList.get(rowIndex);
                    seedList.setComment(entry, value.toString(), workingDate);
                    break;
            }
            this.fireTableDataChanged();*/
        }

        @Override
        public String getColumnName(int columnIndex)
        {
            Translation t = Translation.getCurrent();
            switch (convertColumn(columnIndex))
            {
                case 0:
                    return t.name();
                case 1:
                    return t.variety();
                case 2:
                    return t.quantity();
                case 3:
                    return t.comment();
                case 4:
                    return t.valid_from();
                case 5:
                    return t.valid_to();
            }
            return null;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex)
        {
            switch (convertColumn(columnIndex))
            {
                case 0:
                    return SeedEntry.PlantOrUnregistered.class;
                case 1:
                case 3:
                    return String.class;
                case 2:
                    return SeedEntry.Quantity.class;
                case 4:
                case 5:
                    return LocalDate.class;
            }
            return null;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            if (rowIndex < seedList.size())
            {
                columnIndex = convertColumn(columnIndex);
                SeedEntry entry = seedList.get(rowIndex);
                if (columnIndex == 1 && entry.getValidFrom().equals(workingDate))
                    return true;
                if (columnIndex == 2)
                    return true;
                if (columnIndex == 3 && entry.getValidFrom().equals(workingDate))
                    return true;
            }
            else if (rowIndex == seedList.size() && columnIndex == 0)
                return true;
            return false;
        }
    }

    /*
    public SeedManagerSwing(Gui gui)
    {
        super(gui, Translation.getCurrent().action_seed_manager());
        TagInInventory.getInstance().setSeedFile(seedFile);
        seedFile.addListener(this);
        createComponents();
        workingDate = LocalDate.now();
        textDate.setText(workingDate.format(dateFormatter));
        seedFile.setDate(workingDate);
        if (seedFile.getSeedInventory() == null && !seedFile.openLast())
            seedFile.createNew();
    }
    */

    /* TODO: public class PlantColumnRenderer extends PlantComponent implements TableCellRenderer
    {
        private static final long serialVersionUID = 1L;

        public PlantColumnRenderer()
        {
            // TODO: setOpaque(true); //MUST do this for background to show up.
        }

        public Component getTableCellRendererComponent(
            JTable table, Object color, boolean isSelected, boolean hasFocus, int row, int column)
        {
            SeedListTableModel seedList = (SeedListTableModel)table.getModel();
            if (row == seedList.getRowCount() - 1)
            {
                setUnregisteredPlant("");
            }
            else
            {
                SeedEntry.PlantOrUnregistered p = (SeedEntry.PlantOrUnregistered)seedList.getValueAt(row,  column);
                if (p.plant != null)
                {
                    setPlant(p.plant);
                }
                else
                {
                    setUnregisteredPlant(p.unregisteredPlant);
                }
            }
            return this;
        }
    } */

    public class QuantityColumnRenderer extends DefaultTableCellRenderer
    {
        public QuantityColumnRenderer()
        {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        protected void setValue(Object value)
        {
            Quantity quantity = (Quantity)value;
            if (quantity == null)
                super.setValue(null);
            else
                super.setValue(Double.toString(quantity.quantity) + " " + quantity.unit);
        }
    }

    public class QuantityTextField extends AbstractCellEditor  implements TableCellEditor
    {
        JPanel panel = new JPanel(new GridLayout());
        JTextField textQuantity = new JTextField();
        JComboBox<String> comboUnit = new JComboBox<String>();

        public QuantityTextField()
        {
            for (String unit : SeedList.getUnits())
                comboUnit.addItem(unit);
            comboUnit.setEditable(true);
            comboUnit.getEditor().getEditorComponent().setPreferredSize(new Dimension(1, 1));
            panel.add(textQuantity);
            panel.add(comboUnit);
        }


        @Override public boolean stopCellEditing()
        {
            try
            {
                if (!(textQuantity.getText().isEmpty() || textQuantity.getText().equals("0")))
                    Double.parseDouble(textQuantity.getText());
            }
            catch (NumberFormatException ex)
            {
                JOptionPane.showMessageDialog(null, "Invalid quantity " + textQuantity.getText());
                return false;
            }
            return super.stopCellEditing();
        }

        @Override
        public Object getCellEditorValue()
        {
            Quantity q = null;
            try
            {

                if (!(textQuantity.getText().isEmpty() || textQuantity.getText().equals("0")))
                {
                    q = new Quantity();
                    q.quantity = Double.parseDouble(textQuantity.getText());
                    q.unit = comboUnit.getEditor().getItem() == null ? "" : comboUnit.getEditor().getItem().toString();
                }
            }
            catch (NumberFormatException ex)
            {
                q = null;
            }
            return q;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table,
                Object value, boolean isSelected, int row, int column) {
            Quantity q = (Quantity)value;
            if (q != null)
            {
                textQuantity.setText(Double.toString(q.quantity));
                if (q.unit != null)
                    comboUnit.setSelectedItem(q.unit);
            }
            else
            {
                textQuantity.setText("");
                comboUnit.setSelectedItem("");
            }
            return panel;
        }
    }
    /*
    private void dateChangedFromComboBox()
    {
        workingDate = (LocalDate)comboValidFromDates.getSelectedItem();
        seedFile.setDate(workingDate);
        textDate.removeActionListener(this);
        textDate.setText(workingDate.format(dateFormatter));
        textDate.addActionListener(this);
    }

    private void dateChangedFromTextBox()
    {
        try
        {
            workingDate = LocalDate.parse(textDate.getText(), dateFormatter);
        }
        catch (DateTimeParseException ex)
        {
            JOptionPane.showMessageDialog(null, "Invalid date " + textDate.getText());
        }
        seedFile.setDate(workingDate);

        textDate.removeActionListener(this);
        textDate.setText(workingDate.format(dateFormatter));
        textDate.addActionListener(this);

        LocalDate dateForCombo = validFromDates.floor(workingDate);
        if (dateForCombo == null && validFromDates.size() > 0)
            dateForCombo = validFromDates.first();
        comboValidFromDates.removeActionListener(this);
        comboValidFromDates.setSelectedItem(dateForCombo);
        comboValidFromDates.addActionListener(this);
    }


    ArrayList<SeedEntry> getSelectedEntries()
    {
        JTable table = inventorySelected ? tableInventory : tableShoppingList;
        SeedList seedList = inventorySelected ? seedFile.getSeedInventory() : seedFile.getSeedShoppingList();
        ArrayList<SeedEntry> entries = new ArrayList<>();
        for (int i : table.getSelectedRows())
        {
            if (i < seedList.size())
                entries.add(seedList.get(i));
        }
        return entries;
    }

    private void stopEditing()
    {
        if (tableInventory.isEditing())
            tableInventory.getCellEditor().cancelCellEditing();
        if (tableShoppingList.isEditing())
            tableShoppingList.getCellEditor().cancelCellEditing();
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        Translation t = Translation.getCurrent();
        if (e.getSource() == menuNew)
        {
            seedFile.createNew();
        }
        else if (e.getSource() == menuOpen)
        {
            seedFile.open();
        }
        else if (e.getSource() == menuSave)
        {
            seedFile.save(false);
        }
        else if (e.getSource() == menuSaveAs)
        {
            seedFile.save(true);
        }
        else if (e.getSource() == menuPrint)
        {
            SeedManagerPrintPreview printPreview = new SeedManagerPrintPreview(dialog, seedFile, workingDate);
            printPreview.actionPerformed(e);
        }
        else if (e.getSource() == menuOptions)
        {
            showOptions();
        }
        else if (e.getSource() == menuAutogenerateFromGarden)
        {
            SeedManagerAutogenerate autogenerate = new SeedManagerAutogenerate(dialog, seedFile, workingDate);
            if (autogenerate.fromGarden(getGarden()) == JOptionPane.OK_OPTION)
                autogenerate.actionPerformed(e);
        }
        else if (e.getSource() == menuAutogenerateFromInventory)
        {
            SeedManagerAutogenerate autogenerate = new SeedManagerAutogenerate(dialog, seedFile, workingDate);
            if (autogenerate.fromInventory() == JOptionPane.OK_OPTION)
                autogenerate.actionPerformed(e);
        }
        else if (e.getSource() == textDate)
        {
            dateChangedFromTextBox();
        }
        else if (e.getSource() == comboValidFromDates)
        {
            dateChangedFromComboBox();
        }
        else if (e.getSource() == buttonDelete)
        {
            stopEditing();
            ArrayList<SeedEntry> entries = getSelectedEntries();
            SeedList seedList = inventorySelected ? seedFile.getSeedInventory() : seedFile.getSeedShoppingList();
            for (SeedEntry entry : entries)
                seedList.remove(entry, workingDate);
        }
        else if (e.getSource() == buttonMove)
        {
            stopEditing();
            ArrayList<SeedEntry> entries = getSelectedEntries();
            SeedList seedList1 = inventorySelected ? seedFile.getSeedInventory() : seedFile.getSeedShoppingList();
            SeedList seedList2 = !inventorySelected ? seedFile.getSeedInventory() : seedFile.getSeedShoppingList();
            for (SeedEntry entry : entries)
            {
                seedList2.add(entry.getPlant(), entry.getVariety(), entry.getQuantity(), entry.getComment(), workingDate, null);
                seedList1.remove(entry, workingDate);
            }
        }
        else if (e.getSource() == buttonCopy)
        {
            ArrayList<SeedEntry> entries = getSelectedEntries();
            SeedList seedList2 = !inventorySelected ? seedFile.getSeedInventory() : seedFile.getSeedShoppingList();
            for (SeedEntry entry : entries)
            {
                seedList2.add(entry.getPlant(), entry.getVariety(), entry.getQuantity(), entry.getComment(), workingDate, null);
            }
        }
        else
        {
            workingDate = LocalDate.now();
            textDate.setText(workingDate.format(dateFormatter));
            seedFile.setDate(workingDate);

            buttonNow.setText(t.now());
            buttonDateList.setText(buttonDateList.isSelected() ? t.changes_date() : t.working_date());
            buttonDelete.setText(t.delete());
            buttonMove.setText(t.move());
            buttonCopy.setText(t.copy());
            fileMenu.setText(t.file());
            menuNew.setText(t.action_new_seed_list());
            menuOpen.setText(t.action_open());
            menuSave.setText(t.action_save());
            menuSaveAs.setText(t.action_save_as());
            menuPrint.setText(t.action_print());
            toolsMenu.setText(t.seed_manager_tools());
            menuOptions.setText(t.seed_manager_options());
            menuAutogenerate.setText(t.seed_manager_autogenerate());
            menuAutogenerateFromGarden.setText(t.seed_manager_from_garden());
            menuAutogenerateFromInventory.setText(t.seed_manager_from_inventory());
            labelInventory.setText(t.seed_manager_inventory());
            labelShoppingList.setText(t.seed_manager_shopping_list());

            // TODO JMenu recentFilesMenu = seedFile.getRecentFilesMenu();
            // recentFilesMenu.setText(t.recent_files());
            // if (recentFilesMenu.getParent() == null && recentFilesMenu.getItemCount() != 0)
            //    fileMenu.insert(recentFilesMenu, 4);

            dialog.setVisible(true);
        }
    }

    private void createComponents()
    {
        Preferences prefs = Resources.getPreferences("gui/seedManager");
        viewColumns[1] = prefs.getBoolean("viewVariety", true);
        viewColumns[2] = prefs.getBoolean("viewQuantity", true);
        viewColumns[3] = prefs.getBoolean("viewComment", true);
        viewColumns[4] = prefs.getBoolean("viewValidFrom", false);
        viewColumns[5] = prefs.getBoolean("viewValidTo", false);

        JPanel panelDate = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        textDate.addActionListener(this);
        textDate.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent arg0) { dateChangedFromTextBox(); }

            @Override
            public void focusGained(FocusEvent arg0) {}
        });
        buttonDateList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                comboValidFromDates.setVisible(buttonDateList.isSelected());
                textDate.setVisible(!buttonDateList.isSelected());
                if (buttonDateList.isSelected())
                    dateChangedFromComboBox();
                else
                    dateChangedFromTextBox();
                Translation t = Translation.getCurrent();
                buttonDateList.setText(buttonDateList.isSelected() ? t.changes_date() : t.working_date());
            }
        });
        comboValidFromDates.setVisible(false);
        comboValidFromDates.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(
                    JList list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus)
            {
                LocalDate date = (LocalDate)value;
                String strDate = date == null ? null : date.format(dateFormatter);
                return super.getListCellRendererComponent(list, strDate, index, isSelected, cellHasFocus);
            }
        });
        buttonNow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                buttonDateList.setSelected(false);
                comboValidFromDates.setVisible(buttonDateList.isSelected());
                textDate.setVisible(!buttonDateList.isSelected());
                textDate.setText(LocalDate.now().format(dateFormatter));
                dateChangedFromTextBox();
            }
        });
        comboValidFromDates.addActionListener(this);
        panelDate.add(buttonDateList);
        panelDate.add(comboValidFromDates);
        panelDate.add(textDate);
        panelDate.add(buttonNow);

        JScrollPane scrollInventory = new JScrollPane(tableInventory);
        JScrollPane scrollShoppingList = new JScrollPane(tableShoppingList);

        JPanel panelLeft = new JPanel(new BorderLayout());
        panelLeft.add(labelInventory, BorderLayout.NORTH);
        panelLeft.add(scrollInventory, BorderLayout.CENTER);

        JPanel panelRight = new JPanel(new BorderLayout());
        panelRight.add(labelShoppingList, BorderLayout.NORTH);
        panelRight.add(scrollShoppingList, BorderLayout.CENTER);

        JPanel panel1 = new JPanel(new GridLayout());
        panel1.add(panelLeft);
        panel1.add(panelRight);

        JPanel panel2 = new JPanel(new BorderLayout());
        panel2.add(panelDate, BorderLayout.NORTH);
        panel2.add(panel1, BorderLayout.CENTER);

        buttonDelete.addActionListener(this);
        buttonMove.addActionListener(this);
        buttonCopy.addActionListener(this);

        tableInventory.addFocusListener(new FocusListener()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                inventorySelected = true;
            }

            @Override
            public void focusLost(FocusEvent e) {}
        });
        tableShoppingList.addFocusListener(new FocusListener()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                inventorySelected = false;
            }

            @Override
            public void focusLost(FocusEvent e) {}
        });

        // File
        fileMenu.add(menuNew);
        fileMenu.add(menuOpen);
        fileMenu.add(menuSave);
        fileMenu.add(menuSaveAs);
        fileMenu.add(menuPrint);
        menuNew.addActionListener(this);
        menuOpen.addActionListener(this);
        menuSave.addActionListener(this);
        menuSaveAs.addActionListener(this);
        menuPrint.addActionListener(this);

        toolsMenu.add(menuOptions);
        toolsMenu.add(menuAutogenerate);
        menuAutogenerate.add(menuAutogenerateFromGarden);
        menuAutogenerate.add(menuAutogenerateFromInventory);
        menuOptions.addActionListener(this);
        menuAutogenerateFromGarden.addActionListener(this);
        menuAutogenerateFromInventory.addActionListener(this);

        // display dialog
        dialog = new JOptionPane(panel2, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_OPTION, null,
                new JButton[] { buttonDelete, buttonMove, buttonCopy }).createDialog(
                Translation.getCurrent().action_seed_manager());
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        dialog.setJMenuBar(menuBar);
        dialog.setResizable(true);
        dialog.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                dialog.setDefaultCloseOperation(seedFile.askToSave() ? JDialog.HIDE_ON_CLOSE : JDialog.DO_NOTHING_ON_CLOSE);
            }
        });
        Gui.makeWindowBoundsPersistent(dialog, "SeedManagerSwing", true);
    }

    TreeSet<LocalDate> validFromDates = new TreeSet<LocalDate>();
    void loadValidFromDates()
    {
        TreeSet<LocalDate> tmp = new TreeSet<LocalDate>();
        tmp.addAll(seedFile.getSeedInventory().getValidFromDates());
        tmp.addAll(seedFile.getSeedShoppingList().getValidFromDates());
        if (!tmp.equals(validFromDates))
        {
            validFromDates = tmp;
            comboValidFromDates.setModel(
                new DefaultComboBoxModel<LocalDate>(validFromDates.toArray(new LocalDate[0])));
            if (!comboValidFromDates.isVisible())
                dateChangedFromTextBox();
        }
    }

    class LocalDateRenderer extends DefaultTableCellRenderer
    {
        public LocalDateRenderer() { super(); }

        public void setValue(Object value)
        {
            setText((value == null) ? "" : ((LocalDate)value).format(dateFormatter));
        }
    }

    @Override
    public void objectChanged()
    {
        // TODO: JMenu recentFilesMenu = seedFile.getRecentFilesMenu();
        // if (recentFilesMenu.getParent() == null && recentFilesMenu.getItemCount() != 0)
        //    fileMenu.insert(recentFilesMenu, 4);
        titleChanged();

        tableInventory.setModel(new SeedListTableModel(seedFile.getSeedInventory()));
        tableShoppingList.setModel(new SeedListTableModel(seedFile.getSeedShoppingList()));
        loadValidFromDates();

        // TODO: tableInventory.setDefaultRenderer(SeedEntry.PlantOrUnregistered.class, new PlantColumnRenderer());
        tableInventory.setDefaultRenderer(SeedEntry.Quantity.class, new QuantityColumnRenderer());
        tableInventory.setDefaultRenderer(LocalDate.class, new LocalDateRenderer());
        tableInventory.setDefaultEditor(SeedEntry.PlantOrUnregistered.class, new DefaultCellEditor(new PlantComboBox()));
        tableInventory.setDefaultEditor(SeedEntry.Quantity.class, new QuantityTextField());
        tableInventory.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        // TODO: tableShoppingList.setDefaultRenderer(SeedEntry.PlantOrUnregistered.class, new PlantColumnRenderer());
        tableShoppingList.setDefaultRenderer(SeedEntry.Quantity.class, new QuantityColumnRenderer());
        tableShoppingList.setDefaultRenderer(LocalDate.class, new LocalDateRenderer());
        tableShoppingList.setDefaultEditor(SeedEntry.PlantOrUnregistered.class, new DefaultCellEditor(new PlantComboBox()));
        tableShoppingList.setDefaultEditor(SeedEntry.Quantity.class, new QuantityTextField());
        tableShoppingList.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    }


    @Override
    public void unsavedChangesChanged()
    {
        titleChanged();
    }

    private void titleChanged()
    {
        Translation t = Translation.getCurrent();
        String title;
        if (seedFile.getFile() != null)
            title = t.action_seed_manager() + " - " + seedFile.getFile().toString();
        else
            title = t.action_seed_manager() + " - new file"; // TODO: change new file
        if (seedFile.hasUnsavedChanges())
            title += " *";
        dialog.setTitle(title);
    }

    boolean viewColumns[] = new boolean[] { true, true, true, true, false, false };
    private void showOptions()
    {
        Translation t = Translation.getCurrent();
        JCheckBox checkVariety = new JCheckBox(t.seed_manager_view_variety(), viewColumns[1]);
        JCheckBox checkQuantity = new JCheckBox(t.seed_manager_view_quantity(), viewColumns[2]);
        JCheckBox checkComment = new JCheckBox(t.seed_manager_view_comment(), viewColumns[3]);
        JCheckBox checkValidFrom = new JCheckBox(t.seed_manager_view_valid_from(), viewColumns[4]);
        JCheckBox checkValidTo = new JCheckBox(t.seed_manager_view_valid_to(), viewColumns[5]);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(checkVariety);
        panel.add(checkQuantity);
        panel.add(checkComment);
        panel.add(checkValidFrom);
        panel.add(checkValidTo);
        int answer = JOptionPane.showConfirmDialog(
            dialog, panel, t.seed_manager_options(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (answer == JOptionPane.OK_OPTION)
        {
            viewColumns[1] = checkVariety.isSelected();
            viewColumns[2] = checkQuantity.isSelected();
            viewColumns[3] = checkComment.isSelected();
            viewColumns[4] = checkValidFrom.isSelected();
            viewColumns[5] = checkValidTo.isSelected();

            Preferences prefs = Resources.getPreferences("gui/seedManager");
            prefs.putBoolean("viewVariety", viewColumns[1]);
            prefs.putBoolean("viewQuantity", viewColumns[2]);
            prefs.putBoolean("viewComment", viewColumns[3]);
            prefs.putBoolean("viewValidFrom", viewColumns[4]);
            prefs.putBoolean("viewValidTo", viewColumns[5]);

            objectChanged();
        }
    }
        */
}