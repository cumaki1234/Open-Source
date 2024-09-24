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

import org.sourceforge.kga.*;
import org.sourceforge.kga.gui.Gui;
import org.sourceforge.kga.gui.actions.importData.ImportCsv;
import org.sourceforge.kga.plant.Lifetime;
import org.sourceforge.kga.plant.NutritionalNeeds;
import org.sourceforge.kga.plant.RootDeepness;
import org.sourceforge.kga.plant.WeedControl;
import org.sourceforge.kga.prefs.Preferences;
import org.sourceforge.kga.translation.Iso639_1;
import org.sourceforge.kga.translation.Translation;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;

public class EditSpecies extends KgaAction implements TreeSelectionListener
{
    Translation t = Translation.getCurrent();
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());

    enum TreeNodeStatus { EXPANDED, COLLAPSED, LEAF }

    public class EditFamilyTableModel extends AbstractTableModel
    {
        ArrayList<Plant> displayed = new ArrayList<>();

        public EditFamilyTableModel()
        {
            //??? displayed.addAll(Plant.getKingdom().getChildren());
        }

        @Override
        public int getRowCount()
        {
            return displayed.size();
        }

        @Override
        public int getColumnCount()
        {
            return 7;
        }

        @Override
        public String getColumnName(int columnIndex)
        {
            switch (columnIndex)
            {
                case 0:
                    return t.name();
                case 1:
                    return t.nutritional_needs();
                case 2:
                    return "root_deepness";
                case 3:
                    return "weed_control";
                case 4:
                    return "lifetime";
                case 5:
                    return "repetition";
                case 6:
                    return "gap";
                default:
                    return null;
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex)
        {
            switch (columnIndex)
            {
                case 1:
                    return NutritionalNeeds.class;
                case 2:
                    return RootDeepness.class;
                case 3:
                    return WeedControl.class;
                case 4:
                    return Lifetime.Value.class;
                case 5:
                    return Integer.class;
                case 6:
                    return Integer.class;
            }
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            return columnIndex == 1;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex)
        {
            Plant plant = displayed.get(rowIndex);
            switch (columnIndex)
            {
                case 0:
                    return
                        plant.getChildren().size() == 0 ? TreeNodeStatus.LEAF :
                            rowIndex + 1 < model.displayed.size() &&
                            model.displayed.get(rowIndex) == model.displayed.get(rowIndex+ 1).getParent() ?
                                TreeNodeStatus.EXPANDED : TreeNodeStatus.COLLAPSED;
                case 1:
                    return plant.getNutritionalNeeds();
                case 2:
                    return plant.getRootDeepness();
                case 3:
                    return plant.getWeedControl();
                case 4:
                    return plant.lifetime.get();
                case 5:
                {
                    int repetition = plant.lifetime.getRepetitionYears();
                    if (repetition == Integer.MAX_VALUE)
                        return "∞";
                    return repetition;
                }
                case 6:
                    return plant.lifetime.getRepetitionGap();
                default:
                    return null;
            }
        }

        public String getName(int rowIndex)
        {
            TreeNodeStatus status = (TreeNodeStatus)getValueAt(rowIndex, 0);
            Plant plant = displayed.get(rowIndex);
            Plant parent = plant.getParent();
            String s = "";
            while (true)
            {
                parent = parent.getParent();
                if (parent == null)
                    break;
                s += "        ";
            }

            if (status == TreeNodeStatus.LEAF)
                s += "";
            else
            {
                s += status == TreeNodeStatus.COLLAPSED ? "+ " : "- ";
            }

            String scientific = plant.getName();
            String name = t.translate(plant);
            if (scientific.compareTo(name) == 0)
                s += scientific;
            else
                s += name + " ( " + scientific + " )";
            return s;
        }

        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex)
        {
            if (columnIndex == 0)
            {
                if (((TreeNodeStatus)getValueAt(rowIndex, 0)).equals(value))
                    return;
                TreeNodeStatus status = (TreeNodeStatus)value;
                Plant plant = displayed.get(rowIndex);
                if (status == TreeNodeStatus.EXPANDED)
                {
                    //???displayed.addAll(rowIndex + 1, plant.getChildren());
                }
                else
                {
                    while (rowIndex + 1 < displayed.size())
                    {
                        Plant next = displayed.get(rowIndex + 1);
                        Plant parent = next.getParent();
                        while (parent != null)
                        {
                            if (parent == plant)
                            {
                                displayed.remove(rowIndex + 1);
                                next = null;
                                break;
                            }
                            parent = parent.getParent();
                        }
                        if (next != null)
                            break;
                    }
                }
                fireTableDataChanged();
            }
            else if (columnIndex == 1)
            {
                displayed.get(rowIndex).setNutritionalNeeds((NutritionalNeeds)value);
                fireTableDataChanged();
            }
        }

        public void expand()
        {
        }
    }

    public EditSpecies(Gui gui)
    {
        super(gui, Translation.getCurrent().action_edit_species());
        buttonSave.addActionListener(this);
        buttonImportCsv.addActionListener(this);
    }


    JComboBox<Iso639_1.Language> comboLanguage = null;
    EditFamilyTableModel model = new EditFamilyTableModel();
    JTree tree;
    JDialog dialog = null;
    PlantPropertiesPane properties = new PlantPropertiesPane();

    JButton buttonImportCsv = new JButton(Translation.getCurrent().import_csv());
    JButton buttonSave = new JButton(Translation.getCurrent().action_save());

    private void buttonSaveAction()
    {
        String lastPath = Preferences.gui.importWindow.lastSpeciesFilePath.get();

        JFileChooser f = new JFileChooser(lastPath);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Species", "xml");
        f.setFileFilter(filter);
        if (f.showSaveDialog(this.getGui()) != JFileChooser.APPROVE_OPTION)
            return;

        File speciesFile = f.getSelectedFile();
        if (!speciesFile.getName().endsWith(".xml"))
            speciesFile = new File(
                speciesFile.getParent() + "/" +
                speciesFile.getName() + ".xml");

        Preferences.gui.importWindow.lastSpeciesFilePath.set(speciesFile.getParent());
        log.info("Set last path to  " + speciesFile.getParent());

        try
        {
            // TODO: PlantList.serializableSpecies.saveToFile(speciesFile);
        }
        catch (Exception ex)
        {
            Translation t = Translation.getCurrent();
            JOptionPane.showMessageDialog(getGui(),
                    ex.toString(),
                    t.error_saving_file(),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    abstract class PlantPropertiesRenderer implements TableCellRenderer
    {
        JLabel c = null;

        public abstract boolean hasProperty(Plant p);

        public abstract Object getProperty(Plant p);

        public String getPropertyAsString(Plant p)
        {
            Object o = getProperty(p);
            if (o == null)
                return "";
            return o.toString();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            Plant plant = model.displayed.get(row);
            if (c == null)
            {
                JLabel l = (JLabel)table.getDefaultRenderer(String.class).
                        getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c = new JLabel();
                c.setFont(l.getFont());
            }
            c.setOpaque(false);
            String v = getPropertyAsString(plant);
            if (hasProperty(plant))
            {
                c.setText(v);
                c.setForeground(Color.BLACK);
            }
            else if (!v.isEmpty())
            {
                c.setText(v);
                c.setForeground(Color.LIGHT_GRAY);
            }
            else
            {
                c.setText("");
                if (!checkChildren(plant))
                {
                    c.setOpaque(true);
                    c.setBackground(new Color(255, 235, 230));
                }
            }
            return c;
        }

        public boolean checkChildren(Plant p)
        {
            if (hasProperty(p))
                return true;
            if (p.getChildren().size() == 0)
                return false;
            for (Taxon child : p.getChildren())
                if (!checkChildren((Plant)child))
                    return false;
            return true;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == buttonSave)
        {
            buttonSaveAction();
            return;
        }
        if (e.getSource() == buttonImportCsv)
        {
            ImportCsv importCsv = new ImportCsv(getGui());
            importCsv.actionPerformed(null);
            return;
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(700, 350));

        JTable table = new JTable(model);
        JScrollPane scrollPane3 = new JScrollPane(table);
        panel.add(scrollPane3, BorderLayout.WEST);

        table.getColumnModel().getColumn(0).setCellRenderer(new TableCellRenderer()
            {
                @Override
                public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
                {
                    JLabel label = (JLabel)(table.getDefaultRenderer(String.class).getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column));
                    if (hasFocus)
                    {
                        TreeNodeStatus status = (TreeNodeStatus)model.getValueAt(row, 0);
                        model.setValueAt(
                            status == TreeNodeStatus.EXPANDED ?
                                TreeNodeStatus.COLLAPSED : TreeNodeStatus.EXPANDED, row, column);
                    }
                    label.setForeground(Color.BLACK);
                    label.setText(model.getName(row));
                    return label;
                }
            });
        table.getColumnModel().getColumn(1).setCellRenderer(new PlantPropertiesRenderer() {
            public boolean hasProperty(Plant p) { return p.hasNutritionalNeeds(); }

            public Object getProperty(Plant p) { return p.getNutritionalNeeds(); }
        });
        table.getColumnModel().getColumn(2).setCellRenderer(new PlantPropertiesRenderer() {
            public boolean hasProperty(Plant p) { return p.hasRootDeepness(); }

            public Object getProperty(Plant p) { return p.getRootDeepness(); }
        });
        table.getColumnModel().getColumn(3).setCellRenderer(new PlantPropertiesRenderer()
        {
            public boolean hasProperty(Plant p) { return p.hasWeedControl(); }

            public Object getProperty(Plant p) { return p.getWeedControl(); }
        });

        // table.getColumnModel().getColumn(0).setCellEditor(new ExpandCellEditor());
        table.getColumnModel().getColumn(0).sizeWidthToFit();
        table.getColumnModel().getColumn(1).setResizable(true);

        JScrollPane scrollPane2 = new JScrollPane();
        panel.add(scrollPane2, BorderLayout.CENTER);

        TableColumn nutritionalNeedsColumn = table.getColumnModel().getColumn(1);
        JComboBox<NutritionalNeeds.Type> comboBox = new JComboBox<>();
        comboBox.addItem(null);
        for (NutritionalNeeds.Type v : NutritionalNeeds.Type.values())
            comboBox.addItem(v);
        nutritionalNeedsColumn.setCellEditor(new DefaultCellEditor(comboBox));

        // openFile dialog
        dialog = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_OPTION, null,
                new JButton[] { buttonImportCsv, buttonSave }).createDialog(
                Translation.getCurrent().action_edit_species());
        Gui.makeWindowBoundsPersistent(dialog, "EditFamilies", false);
        dialog.setVisible(true);

        getGui().resetGui();
    }

    @Override
    public void valueChanged(TreeSelectionEvent event)
    {
        /*Plant node = (Plant)tree.getLastSelectedPathComponent();
        properties.loadPlant(node);
        model.setParent(node);*/
    }
}
