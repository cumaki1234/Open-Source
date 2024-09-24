package org.sourceforge.kga.gui.actions.importData;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.*;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;

import org.sourceforge.kga.Garden;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.gui.Gui;
import org.sourceforge.kga.gui.actions.KgaAction;
import org.sourceforge.kga.prefs.Preferences;
import org.sourceforge.kga.translation.Translation;

public class ImportCsv extends KgaAction implements ListSelectionListener
{
    Translation t = Translation.getCurrent();
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());

    JDialog dialog = null;
    SearchAndReplace searchAndReplace;
    JTable tableSources      = new JTable();
    JTable tableTags         = new JTable();
    JTable tableTaggedPlants = new JTable();
    JTable tableData         = new JTable();

    File currentFile;
    CsvParser csv;

    public ImportCsv(Gui gui)
    {
        super(gui, Translation.getCurrent().import_csv());
        buttonSave.addActionListener(this);
        buttonImportCsv.addActionListener(this);
        buttonSearch.addActionListener(this);
        buttonOpen.addActionListener(this);
        buttonImportAll.addActionListener(this);
        searchAndReplace = new SearchAndReplace(tableData);
    }

    JButton buttonSearch = new JButton(Translation.getCurrent().search());
    JButton buttonOpen = new JButton(Translation.getCurrent().action_open());
    JButton buttonSave = new JButton(Translation.getCurrent().action_save());
    JButton buttonImportCsv = new JButton(Translation.getCurrent().import_csv());
    JButton buttonImportAll = new JButton("Import all");

    private void buttonOpenAction()
    {
        String lastPath = Preferences.gui.importWindow.lastPathImportCsv.get();
        log.info("lastPath=" + lastPath);

        JFileChooser f = new JFileChooser(lastPath);
        f.setFileFilter(new FileNameExtensionFilter("CSV", "csv"));
        f.setFileFilter(new FileNameExtensionFilter("TXT", "txt"));
        if (f.showOpenDialog(dialog) != JFileChooser.APPROVE_OPTION)
            return;

        currentFile = f.getSelectedFile();
        Preferences.gui.importWindow.lastPathImportCsv.set(currentFile.getParent());
        loadCsvFile(currentFile);
    }

    private void loadCsvFile(File newFile)
    {
        currentFile = newFile;

        csv = new CsvParser();
        try
        {
            csv.loadFile(currentFile);
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex.toString());
            return;
        }

        tableData.setModel(csv.getDataTableModel());
        tableData.setDefaultRenderer(String.class, new CsvRenderer(csv));

        tableSources.setModel(csv.getSourceTableModel());

        tableTags.setModel(csv.getTagTableModel());
        tableTags.getSelectionModel().addListSelectionListener(this);

        tableTaggedPlants.setModel(csv.getTaggedPlantsTableModel());
        // tableSources.setDefaultRenderer(String.class, new CsvRenderer(csv));
    }

    private void buttonSearchAction()
    {
        JDialog searchDialog = searchAndReplace.getDialog();
        Gui.makeWindowBoundsPersistent(searchDialog, t.import_csv(), false);
        searchDialog.setVisible(true);
    }

    private void buttonSaveAction()
    {
        JFileChooser f = new JFileChooser(currentFile);
        f.setFileFilter(new FileNameExtensionFilter("CSV", "csv"));
        f.setFileFilter(new FileNameExtensionFilter("TXT", "txt"));
        f.setSelectedFile(currentFile);
        if (f.showSaveDialog(dialog) != JFileChooser.APPROVE_OPTION)
            return;
        csv.save(f.getSelectedFile());
    }

    private void displayErrors(StringBuilder importErrors)
    {
        if (importErrors.length() == 0)
            return;

        Set<String> errors = new TreeSet<String>();
        for (String e : importErrors.toString().split("\n"))
            errors.add(e);
        StringBuilder out = new StringBuilder();
        for (String e : errors)
            out.append(e).append("\n");

        JTextArea text = new JTextArea(out.toString());
        //text.setEditable(false);
        JScrollPane pane = new JScrollPane(text);
        JDialog dialog = new JOptionPane(pane).createDialog(t.error_saving_file());
        dialog.setSize(400, 500);
        dialog.setVisible(true);
    }

    private void buttonImportAction()
    {
        StringBuilder importErrors = new StringBuilder();
        if (csv != null)
            csv.importData(importErrors);
        displayErrors(importErrors);
        buttonSaveSpeciesAction();
    }

    private void buttonImportAllAction()
    {
        StringBuilder importErrors = new StringBuilder();
        loadCsvFile(new File("d:\\tmp\\kga\\resources\\import\\companions1.txt"));
        csv.importData(importErrors);
        loadCsvFile(new File("d:\\tmp\\kga\\resources\\import\\companions2.txt"));
        csv.importData(importErrors);
        loadCsvFile(new File("d:\\tmp\\kga\\resources\\import\\companions3.txt"));
        csv.importData(importErrors);
        loadCsvFile(new File("d:\\tmp\\kga\\resources\\import\\companions4.txt"));
        csv.importData(importErrors);
        loadCsvFile(new File("d:\\tmp\\kga\\resources\\import\\companions5.txt"));
        csv.importData(importErrors);
        loadCsvFile(new File("d:\\tmp\\kga\\resources\\import\\companions6.txt"));
        csv.importData(importErrors);
        buttonSaveSpeciesAction();

        for (Plant plant : Resources.plantList().getPlants())
            plant.getCompanions().logConflicts(importErrors);

        displayErrors(importErrors);
    }

    private void buttonSaveSpeciesAction()
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

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object source = e == null ? null : e.getSource();
        if (source == buttonOpen)
        {
            buttonOpenAction();
            return;
        }
        else if (source == buttonSearch)
        {
            buttonSearchAction();
            return;
        }
        else if (source == buttonSave)
        {
            buttonSaveAction();
            return;
        }
        else if (source == buttonImportCsv)
        {
            buttonImportAction();
            return;
        }
        else if (source == buttonImportAll)
        {
            buttonImportAllAction();
            return;
        }


        JScrollPane scrollSources = new JScrollPane(tableSources);
        JScrollPane scrollTags = new JScrollPane(tableTags);
        JScrollPane scrollTaggedPlants = new JScrollPane(tableTaggedPlants);
        JScrollPane scrollData = new JScrollPane(tableData);

        scrollSources.setPreferredSize(new Dimension(0, 100));

        JPanel panelTags = new JPanel();
        panelTags.setLayout(new BoxLayout(panelTags, BoxLayout.LINE_AXIS));
        panelTags.setPreferredSize(new Dimension(0, 100));

        JPanel panelFull = new JPanel();
        panelFull.setLayout(new BoxLayout(panelFull, BoxLayout.PAGE_AXIS));
        panelFull.setPreferredSize(new Dimension(700, 550));

        panelFull.add(scrollSources);
        panelTags.add(scrollTags);
        panelTags.add(scrollTaggedPlants);
        panelFull.add(panelTags);
        panelFull.add(scrollData);
        /*
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

        JScrollPane scrollPane2 = new JScrollPane(properties);
        panel.add(scrollPane2, BorderLayout.CENTER);

        TableColumn nutritionalNeedsColumn = table.getColumnModel().getColumn(1);
        JComboBox<Plant.NutritionalNeeds> comboBox = new JComboBox<>();
        comboBox.addItem(null);
        for (Plant.NutritionalNeeds v : Plant.NutritionalNeeds.values())
            comboBox.addItem(v);
        nutritionalNeedsColumn.setCellEditor(new DefaultCellEditor(comboBox));
*/

        // openFile dialog
        dialog = new JOptionPane(panelFull, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_OPTION, null,
                new JButton[] { buttonImportAll, buttonImportCsv, buttonSave, buttonOpen, buttonSearch }).createDialog(
                t.import_csv());
        Gui.makeWindowBoundsPersistent(dialog, t.import_csv(), false);
        dialog.setVisible(true);

        getGui().resetGui();
    }

    @Override
    public void valueChanged(ListSelectionEvent e)
    {
        int row = 0, column = 0;
        if (tableTags.getSelectionModel() == null ||
            tableTags.getColumnModel().getSelectionModel() == null)
        {
            return;
        }
        row = tableTags.getSelectionModel().getLeadSelectionIndex();
        column = tableTags.getColumnModel().getSelectionModel().getLeadSelectionIndex();
        TableModel tagModel = csv.getTagTableModel();
        if (row < 0 || row > tagModel.getRowCount() ||
            column < 0 || column > tagModel.getColumnCount())
        {
            return;
        }

        String tag = (String) tagModel.getValueAt(row, column);
        log.info("Tag selected: " + tag);
        csv.getTaggedPlantsTableModel().setTag(tag);
    }
}
