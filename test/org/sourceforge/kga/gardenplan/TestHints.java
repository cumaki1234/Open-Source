package org.sourceforge.kga.gardenplan;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.sourceforge.kga.KGATest;
import org.sourceforge.kga.Point;
import org.sourceforge.kga.gui.ProjectFileWithChanges;
import org.sourceforge.kga.gui.gardenplan.EditableGarden;
import org.sourceforge.kga.rules.HintList;
import org.sourceforge.kga.rules.Rule;

public class TestHints extends KGATest{
	
	@Test
	public void test1sqAdj2Square() {
		ProjectFileWithChanges pf = super.getEmptyProject();
		EditableGarden g = pf.getGarden();
		int year = super.firstYear(pf);
		super.getPlantVariety(ID_BELL_PEPPER, "").setSize(new Point(2,2));;
		g.addPlant(year, new Point(2,2), super.getPlantVariety(ID_BELL_PEPPER, ""));
		g.addPlant(year, new Point(4,2), super.getPlantVariety(ID_BASIL, ""));
		HintList hints = Rule.getHints(g, year, new Point(3,2), false, super.getListOf(1, ID_BELL_PEPPER));
		assertTrue(hints.iterator().hasNext());
		Point neighborGrid = hints.iterator().next().getNeighborGrid();
		assertEquals(4,neighborGrid.x);
		assertEquals(2,neighborGrid.y);
	}

}
