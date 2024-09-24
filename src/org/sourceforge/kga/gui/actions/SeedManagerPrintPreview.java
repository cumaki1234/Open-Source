package org.sourceforge.kga.gui.actions;

import javafx.embed.swing.SwingFXUtils;
import org.sourceforge.kga.SeedEntry;
import org.sourceforge.kga.SeedList;
import org.sourceforge.kga.gui.SeedFileWithChanges;
import org.sourceforge.kga.translation.Translation;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.prefs.Preferences;

/**
 * Created by Tiberius on 3/2/2016.
 */
public class SeedManagerPrintPreview extends AbstractAction implements AdjustmentListener
{
    JDialog parent;
    SeedFileWithChanges seedFile;
    LocalDate workingDate;
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
    final String prefPath = "gui/seedManager/printPreview";

    public SeedManagerPrintPreview(JDialog parent, SeedFileWithChanges seedFile, LocalDate workingDate)
    {
        this.parent = parent;
        this.seedFile = seedFile;
        this.workingDate = workingDate;
        createComponents();
    }

    JDialog dialog;
    JButton buttonSetup = new JButton();
    JCheckBox checkInventory = new JCheckBox();
    JCheckBox checkShoppingList = new JCheckBox();
    JCheckBox checkListOnDifferentPage = new JCheckBox();
    JCheckBox checkIcons = new JCheckBox();
    JCheckBox checkVariety = new JCheckBox();
    JCheckBox checkQuantity = new JCheckBox();
    JCheckBox checkComment = new JCheckBox();
    JCheckBox checkValidFrom = new JCheckBox();
    JCheckBox checkValidTo = new JCheckBox();
    JCheckBox checkGrid = new JCheckBox();
    JButton buttonPrint = new JButton();
    JButton buttonCancel = new JButton();
    JLabel labelImage = new JLabel();
    JPanel panel = new JPanel();
    JScrollBar scrollPage = new JScrollBar(JScrollBar.VERTICAL);
    final int BORDER_SIZE = 20;

    private void createComponents()
    {
        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.X_AXIS));
        panelButtons.add(buttonPrint);
        panelButtons.add(buttonCancel);
        panelButtons.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel panelChecks = new JPanel();
        panelChecks.setLayout(new BoxLayout(panelChecks, BoxLayout.Y_AXIS));
        panelChecks.add(buttonSetup);
        panelChecks.add(checkInventory);
        panelChecks.add(checkShoppingList);
        panelChecks.add(checkListOnDifferentPage);
        panelChecks.add(checkIcons);
        panelChecks.add(checkVariety);
        panelChecks.add(checkQuantity);
        panelChecks.add(checkComment);
        panelChecks.add(checkValidFrom);
        panelChecks.add(checkValidTo);
        panelChecks.add(checkGrid);
        panelChecks.add(panelButtons);

        buttonSetup.addActionListener(this);
        checkInventory.addActionListener(this);
        checkShoppingList.addActionListener(this);
        checkListOnDifferentPage.addActionListener(this);
        checkIcons.addActionListener(this);
        checkVariety.addActionListener(this);
        checkQuantity.addActionListener(this);
        checkComment.addActionListener(this);
        checkValidFrom.addActionListener(this);
        checkValidTo.addActionListener(this);
        checkGrid.addActionListener(this);
        buttonPrint.addActionListener(this);
        buttonCancel.addActionListener(this);

        labelImage.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));




        scrollPage.setMinimum(0);
        scrollPage.setVisibleAmount(1);
        scrollPage.setBlockIncrement(1);
        scrollPage.setUnitIncrement(1);
        scrollPage.addAdjustmentListener(this);

        loadPreferences();

        panel.setLayout(new BorderLayout());
        panel.add(panelChecks, BorderLayout.WEST);
        panel.add(labelImage, BorderLayout.CENTER);
        panel.add(scrollPage, BorderLayout.EAST);
        panel.setBorder(BorderFactory.createEmptyBorder(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));


        dialog = new JDialog(parent, ModalityType.APPLICATION_MODAL);
        dialog.setContentPane(panel);
        // TODO: Gui.makeWindowBoundsPersistent(dialog, prefPath, false);
    }

    void loadPreferences()
    {
        Preferences prefs = Preferences.userRoot().node(prefPath);
        checkInventory.setSelected(prefs.getBoolean("Inventory", true));
        checkShoppingList.setSelected(prefs.getBoolean("ShoppingList", true));
        checkListOnDifferentPage.setSelected(prefs.getBoolean("ListOnDifferentPage", false));
        checkIcons.setSelected(prefs.getBoolean("Icons", false));
        checkVariety.setSelected(prefs.getBoolean("Variety", true));
        checkQuantity.setSelected(prefs.getBoolean("Quantity", false));
        checkComment.setSelected(prefs.getBoolean("Comment", false));
        checkValidFrom.setSelected(prefs.getBoolean("ValidFrom", false));
        checkValidTo.setSelected(prefs.getBoolean("ValidTo", false));
        checkGrid.setSelected(prefs.getBoolean("Grid", false));
    }

    void savePreferences()
    {
        Preferences prefs = Preferences.userRoot().node(prefPath);
        prefs.putBoolean("Inventory", checkInventory.isSelected());
        prefs.putBoolean("ShoppingList", checkShoppingList.isSelected());
        prefs.putBoolean("ListOnDifferentPage", checkListOnDifferentPage.isSelected());
        prefs.putBoolean("Icons", checkIcons.isSelected());
        prefs.putBoolean("Variety", checkVariety.isSelected());
        prefs.putBoolean("Quantity", checkQuantity.isSelected());
        prefs.putBoolean("Comment", checkComment.isSelected());
        prefs.putBoolean("ValidFrom", checkValidFrom.isSelected());
        prefs.putBoolean("ValidTo", checkValidTo.isSelected());
        prefs.putBoolean("Grid", checkGrid.isSelected());
    }

    /*final int PREVIEW_MAX_SIZE = 400;
    void createImage()
    {
        PageFormat pf = PrintSetup.pageFormat;
        int width = (int)pf.getWidth(), height = (int)pf.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        try
        {
            print(g, pf, scrollPage.getValue());
        }
        catch (PrinterException e1)
        {
            e1.printStackTrace();
        }
        double factor = Math.min((double)PREVIEW_MAX_SIZE / width, (double)PREVIEW_MAX_SIZE / height);
        Image scaled  = image.getScaledInstance((int)(width * factor), (int)(height * factor), BufferedImage.SCALE_SMOOTH);
        labelImage.setIcon(new ImageIcon(scaled));
    }

    void createPreview()
    {
        maxWidths = null;
        PageFormat pf = PrintSetup.pageFormat;
        int pages = 0;
        try
        {
            pages = getNumberOfPages(pf);
        }
        catch (PrinterException e1)
        {
            e1.printStackTrace();
        }
        scrollPage.setMaximum(pages);
        scrollPage.setVisible(pages > 1);
        createImage();
        dialog.pack();
    }*/

    @Override
    public void actionPerformed(ActionEvent e)
    {
        /*Translation t = Translation.getCurrent();
        if (e.getSource() == buttonSetup)
        {
            PrintSetup.pageFormat = PrintSetup.printerJob.pageDialog(PrintSetup.pageFormat);
            createPreview();
        }
        else if (e.getSource().getClass() == JCheckBox.class)
        {
            if (e.getSource() == checkInventory && !checkInventory.isSelected())
                checkShoppingList.setSelected(true);
            else if (e.getSource() == checkShoppingList && !checkShoppingList.isSelected())
                checkInventory.setSelected(true);
            checkListOnDifferentPage.setEnabled(checkInventory.isSelected() && checkShoppingList.isSelected());
            createPreview();
        }
        else if (e.getSource() == buttonPrint)
        {
            dialog.setVisible(false);
            try
            {
                Book book = new Book();
                book.append(this, PrintSetup.pageFormat, getNumberOfPages(PrintSetup.pageFormat));
                PrintSetup.printerJob.setPageable(book);
                boolean doPrint = PrintSetup.printerJob.printDialog();
                if (doPrint)
                {
                    PrintSetup.printerJob.print();
                }
            }
            catch (PrinterException ex)
            {
                JOptionPane.showMessageDialog(parent,
                        ex.toString(),
                        t.error_print(),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (e.getSource() == buttonCancel)
        {
            dialog.setVisible(false);
        }
        else
        {
            dialog.setTitle(t.action_print());
            buttonSetup.setText(t.action_print_setup());
            checkInventory.setText(t.seed_manager_inventory());
            checkShoppingList.setText(t.seed_manager_shopping_list());
            checkListOnDifferentPage.setText(t.seed_manager_different_page());
            checkIcons.setText(t.image());
            checkVariety.setText(t.variety());
            checkQuantity.setText(t.quantity());
            checkComment.setText(t.comment());
            checkValidFrom.setText(t.valid_from());
            checkValidTo.setText(t.valid_to());
            checkGrid.setText(t.grid());
            buttonPrint.setText(t.action_print());
            buttonCancel.setText(t.cancel());

            createPreview();
            dialog.setVisible(true);
            savePreferences();
        }*/
    }

    //////////////////////////////////////////////////////////////////////////
    // Printable
    //
    private int getStringWidth(Font font, String text)
    {
        AffineTransform affine = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(affine, true, false);
        return (int)(font.getStringBounds(text, frc).getWidth());
    }

    private int getFontAscent(Font font)
    {
        AffineTransform affine = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(affine, true, true);
        return (int)(font.getLineMetrics("", 0, 0, frc).getAscent());
    }

    private int getFontHeight(Font font)
    {
        AffineTransform affine = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(affine, true, true);
        return (int)(font.getLineMetrics("", 0, 0, frc).getHeight());
    }

    private String quantityToString(SeedEntry.Quantity q)
    {
        if (q == null || q.quantity == 0)
            return "";
        String ret = Double.toString(q.quantity);
        if (!q.unit.isEmpty())
            ret += " " + q.unit;
        return ret;
    }

    private void printString(Graphics2D g, String text, java.awt.Point p, Font font)
    {
        int height = getFontHeight(font);
        if (currentPage == pageToPrint)
        {
            pagePrinted = true;
            int ascent = getFontAscent(font);
            g.setFont(font);
            g.drawString(text, p.x, p.y + ascent);
            /* g.setColor(Color.RED);
            g.drawRect(p.x, p.y, get StringWidth(font, text), height);
            g.setColor(Color.BLACK); */
        }
        p.y += height;
    }

    int entryPrintedFieldsCount = 1;
    int CELL_PADDING = 2;
    int COLUMN_SPACING = 30;
    private int[] getEntryWidth(SeedEntry entry)
    {
        int[] widths = new int[entryPrintedFieldsCount];
        widths[0] = getStringWidth(entryFont, entry.getPlantName(checkVariety.isSelected()));
        if (checkIcons.isSelected() && entry.getPlant().plant != null)
            widths[0] += getFontHeight(entryFont) - CELL_PADDING;
        int column = 1;
        if (checkQuantity.isSelected())
        {
            widths[column] = getStringWidth(entryFont, quantityToString(entry.getQuantity()));
            ++column;
        }
        if (checkComment.isSelected())
        {
            if (entry.getComment() != null)
                widths[column] = getStringWidth(entryFont, entry.getComment());
            ++column;
        }
        if (checkValidFrom.isSelected() || checkValidTo.isSelected())
        {
            widths[column] = getStringWidth(entryFont, getValidFromTo(entry));
            ++column;
        }
        return widths;
    }

    private void printGrid(Graphics2D g, java.awt.Point p, int height)
    {
        if (checkGrid.isSelected() && currentPage == pageToPrint)
        {
            Point pp = new Point(p);
            g.setColor(Color.LIGHT_GRAY);
            for (int i = 0; i < entryPrintedFieldsCount; ++i)
            {
                g.drawRect(pp.x, pp.y, maxWidths[i], height);
                pp.x += maxWidths[i];
            }
            g.setColor(Color.BLACK);
        }
    }

    private String getValidFromToHeader()
    {
        Translation t = Translation.getCurrent();
        String s = "";
        if (checkValidFrom.isSelected())
            s += t.valid_from();
        if (checkValidTo.isSelected())
        {
            if (checkValidFrom.isSelected())
                s += " - ";
            s += t.valid_to();
        }
        return s;
    }

    private void printHeader(Graphics2D g, java.awt.Point p)
    {
        if (entryPrintedFieldsCount == 1)
            return;
        int height = getFontHeight(headerFont);
        if (currentPage == pageToPrint)
        {
            printGrid(g, p, height);

            int ascent = getFontAscent(headerFont);
            Translation t = Translation.getCurrent();
            Point pp = new Point(p);
            if (checkGrid.isSelected())
                pp.x += CELL_PADDING;
            g.setFont(headerFont);
            g.drawString(t.species(), pp.x, pp.y + ascent);
            pp.x += maxWidths[0];

            int column = 1;
            if (checkQuantity.isSelected())
            {
                g.drawString(t.quantity(), pp.x, pp.y + ascent);
                pp.x += maxWidths[column];
                ++column;
            }
            if (checkComment.isSelected())
            {
                g.drawString(t.comment(), pp.x, pp.y + ascent);
                pp.x += maxWidths[column];
                ++column;
            }
            if (checkValidFrom.isSelected() || checkValidTo.isSelected())
            {
                g.drawString(getValidFromToHeader(), pp.x, pp.y + ascent);
                ++column;
            }
        }
        p.y += height;
    }

    private String getValidFromTo(SeedEntry entry)
    {
        String s = "";
        if (checkValidFrom.isSelected())
            s += entry.getValidFrom().format(dateFormatter);
        if (checkValidTo.isSelected() && entry.getValidTo() != null)
        {
            if (checkValidFrom.isSelected())
                s += " - ";
            s += entry.getValidTo().format(dateFormatter);
        }
        return s;
    }

    private void printEntry(Graphics2D g, SeedEntry entry, java.awt.Point p)
    {
        int height = getFontHeight(entryFont);
        if (currentPage == pageToPrint)
        {
            pagePrinted = true;
            int ascent = getFontAscent(entryFont);
            g.setFont(entryFont);

            printGrid(g, p, height);

            Point pp = new Point(p);
            if (checkGrid.isSelected())
                pp.x += CELL_PADDING;
            if (checkIcons.isSelected() && entry.getPlant().plant != null)
            {
                int imageSize = getFontHeight(entryFont) - 2 * CELL_PADDING;
                // g.drawRect(t.x, t.y + CELL_PADDING, imageSize, imageSize);
                g.drawImage(SwingFXUtils.fromFXImage(entry.getPlant().plant.getImage(), null), pp.x, pp.y, imageSize, imageSize, null);
                g.drawString(entry.getPlantName(checkVariety.isSelected()), pp.x + imageSize + CELL_PADDING, pp.y + ascent);
            }
            else
            {
                g.drawString(entry.getPlantName(checkVariety.isSelected()), pp.x, pp.y + ascent);
            }

            int column = 1;
            pp.x += maxWidths[0];
            if (checkQuantity.isSelected())
            {
                g.drawString(quantityToString(entry.getQuantity()), pp.x, pp.y + ascent);
                pp.x += maxWidths[column];
                ++column;
            }
            if (checkComment.isSelected())
            {
                if (entry.getComment() != null)
                {
                    g.drawString(entry.getComment(), pp.x, pp.y + ascent);
                }
                pp.x += maxWidths[column];
                ++column;
            }
            if (checkValidFrom.isSelected() || checkValidTo.isSelected())
            {
                g.drawString(getValidFromTo(entry), pp.x, pp.y + ascent);
                pp.x += maxWidths[column];
                ++column;
            }
        }
        p.y += height;
    }

    Font titleFont = new Font(Font.SANS_SERIF, Font.BOLD, 20);
    Font subtitleFont = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    Font headerFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
    Font entryFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

    int[] maxWidths = null;
    int columnWidth;
    int columnsCount = 0;
    void computeMaxWidth(Graphics2D g, PageFormat pf)
    {
        // compute maximum width of a column
        entryPrintedFieldsCount = 1 +
            (checkQuantity.isSelected() ? 1 : 0) +
            (checkComment.isSelected() ? 1 : 0) +
            (checkValidFrom.isSelected() || checkValidTo.isSelected() ? 1 : 0);
        maxWidths = new int[entryPrintedFieldsCount];
        if (entryPrintedFieldsCount != 0)
        {
            Translation t = Translation.getCurrent();
            maxWidths[0] = getStringWidth(headerFont, t.species());
            int column = 1;
            if (checkQuantity.isSelected())
            {
                maxWidths[column] = getStringWidth(headerFont, t.quantity());
                ++column;
            }
            if (checkComment.isSelected())
            {
                maxWidths[column] = getStringWidth(headerFont, t.comment());
                ++column;
            }
            if (checkValidFrom.isSelected() || checkValidTo.isSelected())
            {
                maxWidths[column] = getStringWidth(headerFont, getValidFromToHeader());
                ++column;
            }
        }
        else
        {
            maxWidths[0] = 0;
        }
        for (SeedList seedList : seedFile.getSeedCollection())
            for (SeedEntry entry : seedList.getSeedView())
            {
                int[] widths = getEntryWidth(entry);
                for (int i = 0; i < entryPrintedFieldsCount; ++i)
                    maxWidths[i] = Math.max(widths[i], maxWidths[i]);
            }
        if (checkGrid.isSelected())
            for (int i = 0; i < entryPrintedFieldsCount; ++i)
                maxWidths[i] += 2 * CELL_PADDING;
        else if (entryPrintedFieldsCount > 1)
            for (int i = 0; i < entryPrintedFieldsCount - 1; ++i)
                maxWidths[i] += CELL_PADDING;

        columnWidth = 0;
        for (int i = 0; i < entryPrintedFieldsCount; ++i)
            columnWidth += maxWidths[i];
        columnsCount = (int) ((pf.getImageableWidth() + COLUMN_SPACING) / (columnWidth + COLUMN_SPACING));
        if (columnsCount == 0)
            columnsCount = 1;
        if (columnsCount > 1)
            columnWidth += (int) ((pf.getImageableWidth() - columnsCount * columnWidth) / (columnsCount - 1));
    }

    int currentPage = 0;
    int pageToPrint = 0;
    boolean pagePrinted = false;

    //@Override
    public int print(Graphics graphics, PageFormat pf, int page) throws PrinterException
    {
        Translation t = Translation.getCurrent();
        Graphics2D g = (Graphics2D)graphics;

        //if (g != null)
        //    g.drawRect((int)pf.getImageableX(), (int)pf.getImageableY(), (int)pf.getImageableWidth() - 1, (int)pf.getImageableHeight() - 1);

        if (maxWidths == null)
            computeMaxWidth(g, pf);

        currentPage = 0;
        pageToPrint = page;
        pagePrinted = false;

        java.awt.Point p = new java.awt.Point((int)pf.getImageableX(), (int)pf.getImageableY());

        int firstRowY = 0, lastRowY = 0;

        ArrayList<SeedList> printedLists = new ArrayList<SeedList>();
        /* if (checkInventory.isSelected() && seedFile.getSeedInventory().size() != 0)
            printedLists.add(seedFile.getSeedInventory());
        if (checkShoppingList.isSelected() && seedFile.getSeedInventory().size() != 0)
            printedLists.add(seedFile.getSeedShoppingList()); */
        for (SeedList printList : printedLists)
        {
            p.x = (int) pf.getImageableX();
            if (checkListOnDifferentPage.isSelected() && printList != printedLists.get(0) && p.y != (int)pf.getImageableY())
            {
                ++currentPage;
                if (pagePrinted)
                    break;
                p.y = (int)pf.getImageableY();
            }
            if (checkListOnDifferentPage.isSelected() || printList == printedLists.get(0))
            {
                printString(g, workingDate.format(dateFormatter), p, titleFont);
            }

            int yAfterSubtitle = p.y + getFontHeight(subtitleFont) + 2 * getFontHeight(entryFont);
            if (p.y != (int)pf.getImageableY()) // no new line before subtitle when at the begin of page
                yAfterSubtitle += getFontHeight(subtitleFont);
            if (yAfterSubtitle >= pf.getImageableHeight() + pf.getImageableY())
            {
                ++currentPage;
                if (pagePrinted)
                    break;
                p.y = (int) pf.getImageableY();
            }
            if (p.y != (int)pf.getImageableY())
                printString(g, "", p, subtitleFont);
            printString(g, printList.getName(), p, subtitleFont);
            printString(g, "", p, entryFont);

            firstRowY = p.y;

            int columnIndex = 0;
            int elementsLeft = printList.getSeedView().size();
            int elementsPerColumn = elementsLeft / columnsCount + (elementsLeft % columnsCount == 0 ? 0 : 1);
            int elementsInColumn = 0;

            for (SeedEntry entry : printList.getSeedView())
            {
                if (firstRowY == p.y)
                    printHeader(g, p);
                printEntry(g, entry, p);
                --elementsLeft;
                ++elementsInColumn;

                // end of page
                if (p.y + getFontHeight(entryFont) >= pf.getImageableHeight() + pf.getImageableY())
                {
                    ++columnIndex;
                    lastRowY = p.y;
                    if (columnIndex >= columnsCount)
                    {
                        ++currentPage;
                        if (pagePrinted)
                            break;
                        p.x = (int) pf.getImageableX();
                        p.y = (int) pf.getImageableY();
                        lastRowY = p.y;
                        firstRowY = p.y;
                        columnIndex = 0;
                        elementsPerColumn = elementsLeft / columnsCount + (elementsLeft % columnsCount == 0 ? 0 : 1);
                    }
                    else
                    {
                        p.x += columnWidth;
                        p.y = firstRowY;
                    }
                    elementsInColumn = 0;
                }
                else if (elementsInColumn >= elementsPerColumn)
                {
                    ++columnIndex;
                    lastRowY = p.y;
                    p.x += columnWidth;
                    p.y = firstRowY;
                    elementsInColumn = 0;
                }
            }

            p.y = lastRowY;
            if (currentPage > page)
                break;
        }

        return pagePrinted ? Printable.PAGE_EXISTS : Printable.NO_SUCH_PAGE;
    }

    public int getNumberOfPages(PageFormat pf) throws PrinterException
    {
        print(null, pf, Integer.MAX_VALUE);
        return currentPage + 1;
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent arg0)
    {
        //createImage();
    }
}
