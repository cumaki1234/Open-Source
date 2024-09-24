package org.sourceforge.kga.gui.actions;

import org.sourceforge.kga.*;
import org.sourceforge.kga.SeedEntry.PlantOrUnregistered;
import org.sourceforge.kga.translation.Translation;

import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

/**
 * Created by Tiberius on 3/6/2016.
 */
public class SeedManagerAutogenerate
{
    LocalDate workingDate;
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());

    public SeedManagerAutogenerate( LocalDate workingDate)
    {
        this.workingDate = workingDate;
    }

    ArrayList<PlantOrUnregistered> toBuy = new ArrayList<>();
    TreeSet<PlantOrUnregistered> toSkip;

    /*private void enableSkipButtons()
    {
        boolean skip = false, dontSkip = false;
        for (int i : tableSpecies.getSelectedRows())
        {
            if (toSkip.contains(toBuy.get(i)))
                dontSkip = true;
            else
                skip = true;
            if (skip && dontSkip)
                break;
        }
        buttonSkip.setEnabled(skip);
        buttonDontSkip.setEnabled(dontSkip);
    }

    private void createComponents()
    {
        buttonSkip.addActionListener(this);
        buttonDontSkip.addActionListener(this);
        buttonOk.addActionListener(this);
        buttonCancel.addActionListener(this);
        enableSkipButtons();

        tableSpecies.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                enableSkipButtons();
            }
        });

        JScrollPane scroll = new JScrollPane(tableSpecies);

        for (String id : Preferences.gui.seedManager.autogenerate.skip.get().split(":"))
        {
            if (id.isEmpty())
                continue;
            if (!id.startsWith("'"))
            {
                Plant plant = Resources.plantList().getPlant(Integer.parseInt(id));
                toSkip.add(new PlantOrUnregistered(plant));
            }
            else if (id.endsWith("'"))
                toSkip.add(new PlantOrUnregistered(id.substring(1, id.length() - 1)));
        }

        dialog = new JOptionPane(scroll, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_OPTION, null,
                new JButton[] { buttonSkip, buttonDontSkip, buttonOk, buttonCancel }).createDialog(
                Translation.getCurrent().action_seed_manager());
        Gui.makeWindowBoundsPersistent(dialog, Preferences.gui.seedManager.autogenerate.windowBounds, false);
    }*/

    public SeedList fromGarden(Garden garden, int year)
    {
        Translation t = Translation.getCurrent();

        HashMap<Point, java.util.List<TaxonVariety<Plant>>> squares = garden.getAllSquares().get(year);

    	Set<TaxonVariety<Plant>> toAdd = new HashSet<TaxonVariety<Plant>>();
        SeedList generated = new SeedList(t.seed_manager_autogenerate()+" - "+LocalDate.now().toString());
        generated.setDate(workingDate);
        for (java.util.List<TaxonVariety<Plant>> plants : squares.values())
        {
            if (plants == null)
                continue;
            toAdd.addAll(plants);
        }
        
        //the intermediate set is sued to deduplicate.
        
        for (TaxonVariety<Plant> plant : toAdd)
            if (!plant.isItem()) {
            	PlantOrUnregistered plantToAdd = new PlantOrUnregistered(plant.getTaxon());
            	generated.add(plantToAdd, plant.getVariety(), null, "", workingDate, null);
            }
        return generated;
    }

    public SeedList fromInventory(SeedList baseOff)
    {
        Translation t = Translation.getCurrent();
    	Set<TaxonVariety<Plant>> toAdd = new HashSet<TaxonVariety<Plant>>();
        for (SeedEntry entry : baseOff.getAllEntries())
        	toAdd.add(Resources.plantList().getVariety(entry.getPlant().plant,entry.getVariety()));
        //The intermediate set is used to dedupe plants.

    	SeedList toBuy = new SeedList(t.seed_manager_autogenerate()+" - "+LocalDate.now().toString());
        for (TaxonVariety<Plant> curr: toAdd)        
            toBuy.add(new PlantOrUnregistered(curr.getTaxon()),curr.getVariety(), null, null, workingDate, null);
        return toBuy;
    }

    /* TODO: public class PlantColumnRenderer extends PlantComponent implements TableCellRenderer
    {
        private static final long serialVersionUID = 1L;

        public PlantColumnRenderer()
        {
            setOpaque(true); //MUST do this for background to show up.
        }

        public Component getTableCellRendererComponent(
                JTable table, Object object, boolean isSelected, boolean hasFocus, int row, int column)
        {
            PlantOrUnregistered p = (PlantOrUnregistered)object;
            if (p.plant != null)
            {
                setPlant(p.plant);
            }
            else
            {
                setUnregisteredPlant(p.unregisteredPlant);
                setToolTipText(null);
            }
            setSelected(isSelected);
            if (toSkip.contains(p))
                strikeText();
            return this;
        }
    } */
}
