package org.sourceforge.kga.flowlist;

import org.sourceforge.kga.io.SaveableRecordRow;

public interface FlowListRecordRow <T extends FlowListRecordRow<T>> extends FlowListItem<T>,SaveableRecordRow{

}
