package org.sourceforge.kga.gui.analyze;

import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sourceforge.kga.analyze.FXField;
import org.sourceforge.kga.analyze.Query;
import org.sourceforge.kga.analyze.QueryField;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;

public class DelayedQueryTable  extends BorderPane{
	Runnable inProgress;
	Query<?,?> inProgressQuery;
	Query<?,?> shown;
	
	public DelayedQueryTable() {
	}
	
	public synchronized <T> void updateQuery(Query<T,FXField<T,?>> query) {
		long startTime = System.currentTimeMillis();
		killInProgress();
		setCenter(new ProgressIndicator(-1.0));
		Runnable myTask = new Runnable(){
			public void run() {
				QueryFXTable<T> table = new QueryFXTable<T>(query);
				table.waitForTasks();
				inProgressCompleted(table,this);
				Logger.getGlobal().log(Level.INFO,"Analysis query completed in: "+(System.currentTimeMillis()-startTime)+"ms.");
			}
		};
		inProgress=myTask;
		inProgressQuery=query;
		ForkJoinPool.commonPool().execute(myTask);
	}
	
	private synchronized void inProgressCompleted(QueryFXTable<?> toShow, Runnable task) {
		if(task == inProgress) {
			inProgress=null;
			inProgressQuery=null;
			Platform.runLater(()->{
				setCenter(toShow);	
			});			
		}else {
			Logger.getGlobal().log(Level.INFO,"Non-in progress task completed");
		}
	}
	
	private synchronized void killInProgress() {
		//todo: kill in progress calculation
		Query<?,?> toKill = inProgressQuery;
		if(toKill!=null)
			inProgressQuery.shutdownQuery();
		inProgress = null;
		inProgressQuery = null;
	}

}
