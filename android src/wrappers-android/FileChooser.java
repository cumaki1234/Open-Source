package org.sourceforge.kga.wrappers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.gui.PersistWindowBounds;
import org.sourceforge.kga.translation.Translation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

/**
 * Created by Tiberius on 8/17/2017.
 */
public class FileChooser extends Stage
{
    public enum FileType
    {
        GARDEN,
        SEEDS
    }

    private static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(FileChooser.class.getName());
    FileType fileType;
    String initialDirectory;
    boolean isSaveDialog;
    File selectedFile = null;

    public FileChooser(String initialDirectory, FileType fileType)
    {
        if (initialDirectory.isEmpty())
            initialDirectory = "/";
        this.initialDirectory = initialDirectory;
        this.fileType = fileType;
    }

    boolean loadFiles(File directory)
    {
        ArrayList<File> files = new ArrayList<>();
        try
        {
            if (directory != null && directory.exists())
            {
                files.add(new File(directory.getPath() + File.separator + ".."));
                Collections.addAll(files, directory.listFiles());
                textDirectory.setText(directory.getPath());
            }
            else
            {
                textDirectory.setText("");
                Collections.addAll(files, File.listRoots());
            }
        }
        catch (Throwable ex)
        {
            log.info(ex.getMessage());
            return false;
        }

        Collections.sort(files,
                (o1, o2) -> {
                    if (o1.getName().compareTo("..") == 0)
                        return -1;
                    if (o2.getName().compareTo("..") == 0)
                        return 1;
                    return o1.getName().compareTo(o2.getName());
                });

        boxDirectories.getChildren().clear();
        paneFiles.getChildren().clear();
        for (File file : files)
        {
            if (toggleFilter.isSelected() && file.isFile() && !file.getName().endsWith(getExtension()))
                continue;
            Button open = new Button();
            String text = file.getName().isEmpty() ? file.getPath() : file.getName();
            open.setText(text);
            open.setMaxWidth(Double.MAX_VALUE);
            open.setAlignment(Pos.CENTER_LEFT);

            open.setGraphic(new ImageView(Resources.folder()));
            open.setOnAction(event -> {
                if (file.isDirectory())
                {
                    if (file.getName().compareTo("..") == 0)
                        loadFiles(file.getParentFile().getParentFile());
                    else
                        loadFiles(file);
                }
                else
                {
                    if (!isSaveDialog)
                    {
                        selectedFile = new File(textDirectory.getText(), file.getName());
                        close();
                    }
                    else
                    {
                        textSaveName.setText(file.getName());
                    }
                }
            });

            if (file.isDirectory())
            {
                boxDirectories.getChildren().add(open);
            }
            else
            {
                if (file.getName().endsWith(getExtension()))
                    open.setGraphic(createFileTypeImage());
                else
                    open.setGraphic(new ImageView(Resources.file()));
                paneFiles.getChildren().add(open);
            }
        }
        return true;
    }

    private String getExtension()
    {
        if (fileType == FileType.GARDEN)
            return ".kga";
        return ".seed";
    }

    TextField textDirectory = new TextField();
    TextField textSaveName = new TextField();
    BorderPane paneContent = new BorderPane();
    TilePane paneFiles = new TilePane();
    VBox boxDirectories = new VBox();
    ToggleButton toggleFilter = null;
    ToggleButton toggleAll = null;

    private ImageView createFileTypeImage()
    {
        ImageView imageView = new ImageView(Resources.applicationIcon());
        imageView.setFitWidth(Resources.file().getWidth());
        imageView.setFitHeight(Resources.file().getHeight());
        return imageView;
    }

    private void showDialog(boolean isSaveDialog)
    {
        this.isSaveDialog = isSaveDialog;

        textDirectory.setEditable(false);

        HBox boxFileType = new HBox();
        toggleFilter = new ToggleButton(Translation.getCurrent().garden(), createFileTypeImage());
        toggleAll = new ToggleButton(Translation.getCurrent().all(), new ImageView(Resources.file()));
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleFilter.setToggleGroup(toggleGroup);
        toggleAll.setToggleGroup(toggleGroup);
        toggleFilter.setSelected(true);
        boxFileType.getChildren().addAll(toggleFilter, toggleAll);
        toggleFilter.setOnAction(event -> { loadFiles(new File(textDirectory.getText())); });
        toggleAll.setOnAction(event -> { loadFiles(new File(textDirectory.getText())); });
        boxFileType.setPadding(new Insets(0, 0, 0, 5));

        HBox boxTop = new HBox();
        boxTop.getChildren().addAll(textDirectory, boxFileType);
        boxTop.setHgrow(textDirectory, Priority.ALWAYS);
        boxTop.setPadding(new Insets(3));
        paneContent.setTop(boxTop);

        ScrollPane scrollDirectories = new ScrollPane();
        scrollDirectories.setContent(boxDirectories);
        scrollDirectories.setPrefWidth(150);
        scrollDirectories.setFitToHeight(true);
        scrollDirectories.setFitToWidth(true);
        paneContent.setLeft(scrollDirectories);

        paneFiles.setHgap(1);
        paneFiles.setVgap(1);
        paneFiles.setMaxWidth(Double.MAX_VALUE);
        paneFiles.setMaxHeight(Double.MAX_VALUE);
        ScrollPane scrollFiles = new ScrollPane();
        scrollFiles.setContent(paneFiles);
        scrollFiles.setMaxWidth(Double.MAX_VALUE);
        scrollFiles.setMaxHeight(Double.MAX_VALUE);
        scrollFiles.setFitToHeight(true);
        scrollFiles.setFitToWidth(true);
        paneContent.setCenter(scrollFiles);

        VBox boxBottom = new VBox();
        if (isSaveDialog)
        {
            Label label = new Label(Translation.getCurrent().file());
        /* String cssLayout = "-fx-border-color: red;\n" +
                "-fx-border-insets: 0;\n" +
                "-fx-border-width: 1;\n" +
                "-fx-border-style: dashed;\n";
        label.setStyle(cssLayout); */
            label.setMaxHeight(Double.MAX_VALUE);
            label.setAlignment(Pos.CENTER);
            HBox boxFile = new HBox();
            boxFile.getChildren().addAll(label, textSaveName);
            boxFile.setPadding(new Insets(3));
            boxFile.setHgrow(textSaveName, Priority.ALWAYS);
            boxBottom.getChildren().add(boxFile);
        }

        ButtonBar buttonBar = new ButtonBar();
        if (isSaveDialog)
        {
            Button saveButton = new Button(Translation.getCurrent().action_save());
            ButtonBar.setButtonData(saveButton, ButtonBar.ButtonData.OK_DONE);
            saveButton.setOnAction(event ->
            {
                String text = textSaveName.getText().trim();
                if (text.isEmpty())
                    return;
                if (!text.endsWith(getExtension()))
                    text += getExtension();
                File file = new File(textDirectory.getText(), text);
                if (file.exists())
                {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, Translation.getCurrent().overwrite_file(), ButtonType.YES, ButtonType.NO );
                    Optional<ButtonType> response = alert.showAndWait();
                    if (response.isPresent() && response.get() != ButtonType.YES)
                        return;
                }
                selectedFile = file;
                close();
            });

            buttonBar.getButtons().add(saveButton);
        }
        Button cancelButton = new Button(Translation.getCurrent().cancel());
        cancelButton.setOnAction(event -> { close(); });
        ButtonBar.setButtonData(cancelButton, ButtonBar.ButtonData.CANCEL_CLOSE);
        buttonBar.getButtons().add(cancelButton);
        buttonBar.setPadding(new Insets(10));
        boxBottom.getChildren().add(buttonBar);

        paneContent.setBottom(boxBottom);


        loadFiles(new File(initialDirectory));

        setScene(new Scene(paneContent));
        Screen screen = Screen.getPrimary();
        PersistWindowBounds.persistWindowBounds(this, "FileChooser", false);

        showAndWait();
    }

    public File showOpenDialog(Window owner)
    {
        showDialog(false);
        return selectedFile;
    }

    public File showSaveDialog(Window window)
    {
        showDialog(true);
        return selectedFile;
    }
}
