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

package org.sourceforge.kga.gui;

import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;

import org.sourceforge.kga.Garden;
import org.sourceforge.kga.KitchenGardenAid;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.SeedList;
import org.sourceforge.kga.gui.actions.*;
import org.sourceforge.kga.gui.analyze.AnalysisPane;
import org.sourceforge.kga.gui.gardenplan.EditableGarden;
import org.sourceforge.kga.gui.gardenplan.GardenWindowProvider;
import org.sourceforge.kga.gui.gardenplan.OperationMediator;
import org.sourceforge.kga.gui.tableRecords.RecordTrackerPane;
import org.sourceforge.kga.plant.TagInGarden;
import org.sourceforge.kga.plant.TagInInventory;
import org.sourceforge.kga.translation.Iso639_1;
import org.sourceforge.kga.translation.Translation;


public class MainWindow extends CentralWindow implements FileWithChanges.Listener
{
    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(Garden.class.getName());
    private static final long serialVersionUID = 1L;
    public static final int DEFAULT_GRID_SIDE = 48;

    private OperationMediator operationMediator;
    
    private GardenWindowProvider gardenProvider;
    ProjectFileWithChanges project;
    

    public MainWindow(Stage primaryStage)
    {
    	this(primaryStage,new ProjectFileWithChanges(primaryStage));

    }
    
    public MainWindow(Stage primaryStage,ProjectFileWithChanges project)
    {
    	super(primaryStage,Arrays.asList(new CentralWindowProvider[] {
    			new GardenWindowProvider(primaryStage, project), new RecordTrackerPane(primaryStage, project), new AnalysisPane(primaryStage,project)
    	}), project);
    	this.project=project;
    	gardenProvider = (GardenWindowProvider)super.providerMap.get(Translation.Key.plan).getKey();
    	project.addListener(this);
        TagInInventory.getInstance().setSeedFile(projectFile);

        primaryStage.addEventHandler(
                WindowEvent.WINDOW_CLOSE_REQUEST,
                event -> {
                    // ask to save file on exit
                    boolean exit = projectFile.askToSave();

                    // ask user for feedback on exit
                    askForReview();

                    if (!exit)
                        event.consume();
                    else if (super.getCurrentProvider()!=null)
                    	super.getCurrentProvider().onHide();
                });

        /*if(project.project.getSeedCollection().isEmptyDefault()) {
        	SeedFileWithChanges loader = new SeedFileWithChanges(primaryStage);
        	loader.loadRecentFileList(project.project.getSeedCollection());
        	loader.openLast();
        }*/
        reinitialize();
    }

    private void reinitialize()
    {
        titleChanged();
        
        // openFile last project
        if (!projectFile.openLast())
            projectFile.createNew();
    }

    protected MenuBar createMenu()
    {
        Translation t = Translation.getCurrent();

        // menu bar
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu(t.file());
        Menu menuHelp = new Menu(t.action_help());
        menuBar.getMenus().addAll(menuFile, menuHelp);

        // File
        MenuItem fileNewGarden = new MenuItem(t.action_new_garden());
        MenuItem fileOpen = new MenuItem(t.action_open());
        MenuItem fileSave = new MenuItem(t.action_save());
        MenuItem fileSaveAs = new MenuItem(t.action_save_as());
        Menu     fileRecentFiles = new Menu(t.recent_files());
        projectFile.loadRecentFileList(fileRecentFiles);
        

        //ensure any seed varieties from the last opened seedfile are populated in the plantlist.
//      file.setMnemonic(KeyEvent.VK_F);
        /*
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(new Export(new Gui())));
        fileMenu.add(new JMenuItem(new Print(new Gui())));
        fileMenu.add(new JMenuItem(new PrintSetup(new Gui())));
        */
        MenuItem fileExit = new MenuItem(t.action_exit());

        fileNewGarden.setOnAction(event -> { projectFile.createNew(); });
        fileOpen.setOnAction(event -> { projectFile.open(); });
        fileSave.setOnAction(event -> { projectFile.save(false); });
        fileSaveAs.setOnAction(event -> { projectFile.save(true); });

        Menu fileImport = new Menu(t.action_import());
        MenuItem importSeedManager = new MenuItem(t.action_seed_manager());
        importSeedManager.setOnAction(event -> { 
        	SeedFileWithChanges seedFile = new SeedFileWithChanges(primaryStage);
        	seedFile.importTo(project.getProject().getSeedCollection(), true);
        	updateLanguage();
        });

        fileImport.getItems().add(importSeedManager);
	        
		Menu fileExport = new Menu(t.action_export());
		MenuItem exportSeedManager = new MenuItem(t.action_seed_manager());
        exportSeedManager.setOnAction(event -> {
        	SeedFileWithChanges seedFile = new SeedFileWithChanges(primaryStage);
        	project.getProject().getSeedCollection().importTo(seedFile.getSeedCollection(),true);
			seedFile.save(false);
			});
        fileExport.getItems().add(exportSeedManager);
		
		Menu print = new Menu(t.action_print());
		MenuItem     printPlan = new MenuItem(t.plan());
		MenuItem     printSetup = new MenuItem(t.action_print_setup());
		printPlan.setOnAction(event -> { new Print().actionPerformed(primaryStage, gardenProvider.getPrintTask()); });
        printSetup.setOnAction(event -> { new PrintSetup().actionPerformed(primaryStage); });
        print.getItems().addAll(printPlan,printSetup);

        // Tools
       // Menu     toolsAutogenerate = new Menu(t.seed_manager_autogenerate());
        Menu     toolsAutogenerate = new Menu(t.seed_manager_from_garden());
        //Menu toolsAutogenerateFromGarden = new Menu(t.seed_manager_from_garden());
    	//Menu toolsAutogenerateFromInventory = new Menu(t.seed_manager_from_inventory());
        //toolsAutogenerate.getItems().add(toolsAutogenerateFromGarden);
    	//toolsAutogenerate.getItems().add(toolsAutogenerateFromInventory);
        menuFile.setOnShowing(a->{
        	//Update menu items that change with project edits.

        	//update based of existing years:
        	Set<Integer> years = project.getGarden().getYears();
            if(years.size()>0) {
                toolsAutogenerate.setDisable(false);
                toolsAutogenerate.getItems().clear();
                for(Integer year:years) {
                    MenuItem yearTask = new MenuItem(t.year()+": " + year);
                    yearTask.setOnAction(w->{
                    	project.getProject().getSeedCollection().add(new SeedManagerAutogenerate(LocalDate.now()).fromGarden(project.getGarden(), year));
                    });
                    toolsAutogenerate.getItems().add(yearTask);
                }
            }
            else {
                toolsAutogenerate.setDisable(true);
            }

            //update based off seed collections:
            /*if(project.getProject().getSeedCollection().iterator().hasNext()) {
            	toolsAutogenerateFromInventory.setDisable(false);
            	toolsAutogenerateFromInventory.getItems().clear();
            	for (SeedList curr : project.getProject().getSeedCollection()) {
                    MenuItem fromList = new MenuItem(curr.getName());
                    fromList.setOnAction(w->{
                    	project.getProject().getSeedCollection().add(new SeedManagerAutogenerate(LocalDate.now()).fromInventory( curr));
            		});
                    toolsAutogenerateFromInventory.getItems().add(fromList);
            	}
            }
            else {
            	toolsAutogenerateFromInventory.setDisable(true);
            }*/
        });




        fileExit.setOnAction(event -> { if (projectFile.askToSave()) { System.exit(0); } });

        menuFile.getItems().addAll(fileNewGarden, fileOpen, fileSave, fileSaveAs,fileRecentFiles, new SeparatorMenuItem(), fileImport, fileExport, new SeparatorMenuItem(), print, printSetup, new SeparatorMenuItem(),toolsAutogenerate, new SeparatorMenuItem(), fileExit);

        // Garden
       
        // TODO: MenuItem gardenStatistics = new MenuItem(t.action_garden_statistics());

        // Plant
       
        /*
        SeedManagerSwing seedManager = new SeedManagerSwing(new Gui());
        species.add(new JMenuItem(new PlantEditor(new Gui())));
        species.add(new JMenuItem(seedManager));
        species.add(new JMenuItem(new ImportCsv(new Gui())));

        // Help
        JMenu helpMenu = new JMenu(t.action_help());
//      helpMenu.setMnemonic(KeyEvent.VK_H);
//      helpMenu.add(new JMenuItem(new TutorialAction()));
        helpMenu.add(new JMenuItem(new OpenHelp(new Gui()))); */
        Menu tutorials = new Menu(t.tutorial());
        MenuItem tutorial_first_garden = new MenuItem(t.tutorial_first_garden());
        tutorial_first_garden.setOnAction(event -> { KitchenGardenAid.getInstance().getHostServices().showDocument("https://youtu.be/sgkzMTtjIJ8"); });
        tutorials.getItems().add(tutorial_first_garden);
        
        MenuItem helpShowExample1 = new MenuItem(t.action_show_an_example());
        MenuItem helpShowExample2 = new MenuItem(t.action_show_another_example());
        MenuItem helpContact = new MenuItem(t.action_contact());
        MenuItem helpReview = new MenuItem(t.action_review());
        MenuItem helpTranslate = new MenuItem(t.action_edit_translation());
        MenuItem helpUpdates = new MenuItem(t.action_check_for_update());
        MenuItem helpDebug = new MenuItem(t.debug());
        MenuItem helpLanguage = new MenuItem(t.action_language());

        helpShowExample1.setOnAction(event -> { projectFile.open(null, Resources.openFile("example.kga")); });
        helpShowExample2.setOnAction(event -> { projectFile.open(null, Resources.openFile("tiberius.kga")); });
        helpContact.setOnAction(event -> { KitchenGardenAid.getInstance().getHostServices().showDocument("https://sourceforge.net/p/kitchengarden2/tickets/new/"); });
        helpReview.setOnAction(event -> { KitchenGardenAid.getInstance().getHostServices().showDocument("https://sourceforge.net/projects/kitchengarden2/reviews/new"); });
        helpTranslate.setOnAction(event->{new EditTranslation().showAndWait(primaryStage);});
        helpUpdates.setOnAction(event -> {new CheckForUpdate().manualCheck();});
        helpDebug.setOnAction(event -> { new Debug().showDialogAndWait(primaryStage); });
        helpLanguage.setOnAction(event -> {
            Language language = new Language();
            Iso639_1.Language selected = language.showAndWait(primaryStage);
            if (selected != null)
            {
                Translation.setCurrent(Resources.translations().get(selected.code));
                updateLanguage();
                reinitialize();
            }
        } );

        menuHelp.getItems().addAll(tutorials,helpShowExample1, helpShowExample2, helpDebug, new SeparatorMenuItem(),helpUpdates,helpContact,helpReview,helpTranslate, new SeparatorMenuItem(), helpLanguage);
        /*
        helpMenu.add(new JMenuItem(new ShowAnotherExample(new Gui())));
        helpMenu.add(new JMenuItem(new Contact(new Gui())));
        helpMenu.add(new JMenuItem(new About(new Gui())));
        helpMenu.addSeparator();
        helpMenu.add(new JMenuItem(new CheckForUpdate(new Gui())));
        helpMenu.add(new JMenuItem(new Review(new Gui())));
        helpMenu.add(new JMenuItem(new EditTranslation(new Gui())));
        helpMenu.add(new JMenuItem(new UploadToWebVersion(new Gui())));
        helpMenu.addSeparator();
        helpMenu.add(new JMenuItem(new Language(new Gui())));
        menuBar.add(helpMenu);
        setJMenuBar(menuBar); */

        return menuBar;
    }

    public void askForReview()
    {
        /*
        // ask user to give feedback
        Preferences preferences = Resources.getPrefences("gui");
        if (!preferences.getBoolean("askReview", true))
            return;

        Translation t = Translation.getCurrent();
        int result = JOptionPane.showConfirmDialog(
            null, t.ask_review(), t.action_review(),
            JOptionPane.YES_NO_CANCEL_OPTION);
        if (result == JOptionPane.CANCEL_OPTION)
            return;
        preferences.putBoolean("askReview", false);
        if (result == JOptionPane.YES_OPTION)
            Review.openPage();
            */
    }

/* TODO:    public GardenView getGardenView(int year)
    {
        for (int i = 0; i < tabbedPaneOfYears.getTabCount(); ++i)
        {
            JScrollPane pane = (JScrollPane)tabbedPaneOfYears.getComponentAt(i);
            GardenView gardenView = (GardenView)pane.getViewport().getView();
            if (year == 0 && i == tabbedPaneOfYears.getSelectedIndex() ||
                gardenView.getYear() == year)
                return gardenView;
        }
        return null;
    } */

    @Override
    public void objectChanged()
    {
        log.info("Gui.objectChanged()");

        titleChanged();

        EditableGarden garden = projectFile.getGarden();
        TagInGarden.getInstance().setGarden(garden);


        // announce other objects the new garden instance
        // TODO: TagInGarden.getInstance().setGarden(garden);
    }

    @Override
    public void unsavedChangesChanged()
    {
        titleChanged();
    }
    
    private void titleChanged()
    {
        String title;
        if (projectFile.getFile() != null)
            title = "Kitchen garden aid - " + projectFile.getFile().toString();
        else
            title = "Kitchen garden aid - " + Translation.getCurrent().action_new_garden();
        if (projectFile.hasUnsavedChanges())
            title += " *";
        primaryStage.setTitle(title);
    }
}
