package org.sourceforge.kga.gui.tableRecords.SeedManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.sourceforge.kga.KGATest;
import org.sourceforge.kga.Resources;
import org.sourceforge.kga.SeedCollection;
import org.sourceforge.kga.SeedEntry;
import org.sourceforge.kga.SeedList;
import org.sourceforge.kga.gui.tableRecords.tableRecordTest;
import org.sourceforge.kga.gui.tableRecords.seedlistmanager.SeedTable;
import org.sourceforge.kga.SeedEntry.PlantOrUnregistered;
import org.sourceforge.kga.SeedEntry.Quantity;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;

public class TestSeedTable extends tableRecordTest<SeedEntry> {
	//@Rule public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();
	
	private static final int COLUMN_PLANT=0;
	private static final int COLUMN_VARIETY=1;
	private static final int COLUMN_QUANTITY=2;
	private static final int COLUMN_COMMENT=3;
	
	private TableColumn<SeedEntry,PlantOrUnregistered> ui_getPlantCol(SeedTable table) {
		return (TableColumn<SeedEntry,PlantOrUnregistered>)ui_getCol(table,COLUMN_PLANT);
	}
	
	private TableColumn<SeedEntry,String> ui_getVarietyCol(SeedTable table) {
		return ui_getStringCol(table,COLUMN_VARIETY);
	}
	
	private TableColumn<SeedEntry,Quantity> ui_getQuantityCol(SeedTable table) {
		return (TableColumn<SeedEntry,Quantity>)ui_getCol(table,COLUMN_QUANTITY);
	}
	
	private TableColumn<SeedEntry,String> ui_getQuantityUnitCol(SeedTable table) {
		return (TableColumn<SeedEntry,String>)ui_getQuantityCol(table).getColumns().get(1);
	}
	
	private TableColumn<SeedEntry,Double> ui_getQuantityValCol(SeedTable table) {
		return (TableColumn<SeedEntry,Double>)ui_getQuantityCol(table).getColumns().get(0);
	}
	
	private TableColumn<SeedEntry,String> ui_getCommentCol(SeedTable table) {
		return ui_getStringCol(table,COLUMN_COMMENT);
	}
	
	private void ui_editPlantCol(SeedTable table, PlantOrUnregistered val, int row) {
		ui_editCol(table,val,row,COLUMN_PLANT);
	}
	
	private void ui_editVarietyCol(SeedTable table, String val, int row) {
		ui_editCol(table,val,row,COLUMN_VARIETY);
	}
	
	private void ui_editCommentCol(SeedTable table, String val, int row) {
		ui_editCol(table,val,row,COLUMN_COMMENT);
	}
	
	private void ui_editQuantityCol(SeedTable table, Quantity val, int row) {

		ui_editCol(table,val.unit,row,ui_getQuantityUnitCol(table));
		ui_editCol(table,val.quantity,row,ui_getQuantityValCol(table));
	}
	
	
	
	@Test
	public void testDisplayedRows() {
		SeedCollection col = super.loadSeperatev2Seeds();
		SeedList file = col.iterator().next();
		assertNotEquals(0, file.seedsEntries.size());
		SeedTable table = new SeedTable(file);
		assertNotEquals(0, file.size());
		assertEquals(file.size(),ui_rowCount(table));
	}
	
	@Test
	public void fileAdds() {
		SeedList file = new SeedList("test");
		SeedTable table = new SeedTable(file);
		
		assertEquals(file.size(),ui_rowCount(table));
		int oldSize = file.size();
		file.add(new PlantOrUnregistered(Resources.plantList().getPlant(44)), "TESTVARIETY",
                new Quantity(1,"testUnit"), "testComment", file.getDate(),
                null);
		oldSize++;
		assertEquals(file.size(),oldSize);
		assertEquals(file.size(),ui_rowCount(table));
		SeedEntry inUI =  ui_getRow(table,0);
		
		assertEquals(44,ui_getPlantCol(table).getCellData(0).plant.getId());

		assertEquals("TESTVARIETY",ui_getVarietyCol(table).getCellData(0));

		Quantity o = ui_getQuantityCol(table).getCellData(0);
		assertEquals(1,o.quantity);
		assertEquals("testUnit",o.unit);
		
		assertEquals("testComment",ui_getCommentCol(table).getCellData(0));
		
	}
	
	@Test
	public void uiAdds() {
		SeedList file = new SeedList("test");
		SeedTable table = new SeedTable(file);
		
		assertEquals(file.size(),ui_rowCount(table));
		ui_addRow(table);
		assertEquals(file.size(),ui_rowCount(table));
		SeedEntry item = ui_getRow(table,0);
		assertEquals(item,file.get(0));
	}
	
	@Test
	public void uiSetPlant() {
		SeedList file = new SeedList("test");
		SeedTable table = new SeedTable(file);
		ui_addRow(table);
		SeedEntry item = ui_getRow(table,0);
		ui_editPlantCol(table,new PlantOrUnregistered(Resources.plantList().getPlant(44)),0);
		assertEquals(44,item.getPlant().plant.getId());
	}
	
	@Test
	public void uiSetVariety() {
		SeedList file = new SeedList("test");
		SeedTable table = new SeedTable(file);
		ui_addRow(table);
		SeedEntry item =  ui_getRow(table,0);
		ui_editVarietyCol(table,"TESTVARIETY",0);
		assertEquals("TESTVARIETY",item.getVariety());
	}
	
	@Test
	public void uiSetComment() {
		SeedList file = new SeedList("test");
		SeedTable table = new SeedTable(file);
		ui_addRow(table);
		SeedEntry item =  ui_getRow(table,0);
		ui_editCommentCol(table,"testComment",0);
		assertEquals("testComment",item.getComment());
	}
	
	@Test
	public void uiSetQuantity() {
		SeedList file = new SeedList("test");
		SeedTable table = new SeedTable(file);
		ui_addRow(table);
		SeedEntry item =  ui_getRow(table,0);
		ui_editQuantityCol(table,new Quantity(5,"testUnit"),0);
		assertEquals(5,item.getQty());
		assertEquals("testUnit",item.getQtyUnit());
	}
	
	@Test
	public void uiDel() {
		SeedList file = getNonEmptySeedList();
		SeedTable table = new SeedTable(file);
		
		assertEquals(file.size(),ui_rowCount(table));
		int oldSize = file.size();
		table.table.getSelectionModel().select(0);
		table.delBut.getOnAction().handle(null);
		oldSize--;
		assertEquals(file.size(),oldSize);
		assertEquals(file.size(),ui_rowCount(table));
	}
	

	private void changeDateboxToIndex(SeedTable t, int index){
		t.dateBox.getSelectionModel().select(index);
		t.dateBox.getOnAction().handle(null);
	}
	
	@Test
	public void uiDates() {
		SeedList file = getNonEmptySeedList();
		SeedTable table = new SeedTable(file);
		assertEquals(true,table.dateBox.getItems().size()>1);
		table.dateBox.getItems().get(1);
		assertEquals(0, table.dateBox.getSelectionModel().getSelectedIndex());
		changeDateboxToIndex(table,1);

		int oldSize = file.size();
		assertEquals(file.size(),ui_rowCount(table));
		ui_addRow(table);
		assertEquals(file.size(),ui_rowCount(table));
		assertEquals(oldSize+1,file.size());
		changeDateboxToIndex(table,0);
		assertEquals(oldSize,file.size());
	}

}
