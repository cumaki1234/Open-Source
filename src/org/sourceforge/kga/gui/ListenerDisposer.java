package org.sourceforge.kga.gui;

import java.util.HashSet;
import java.util.Set;

import org.sourceforge.kga.SeedList;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.util.Pair;

public class ListenerDisposer {
	private Set<Pair<Runnable,Runnable>>registeredListeners;
	private boolean attached;
	private Node toWatchInScene;
	private ChangeListener<Parent> sceneListener;
	
	
	public ListenerDisposer(Node toWatchInScene) {
		this.toWatchInScene=toWatchInScene;
		registeredListeners=new HashSet<Pair<Runnable,Runnable>>();
		attached=false;
		attach();
	}
	
	public boolean attach() {
		if (attached)
			return false;
		attached=true;
		if (toWatchInScene!=null) {
			sceneListener = (w,old,newVal)->{
				if(newVal==null) {
					Platform.runLater(()->{detach();});}};
			toWatchInScene.parentProperty().addListener(sceneListener);
		}
		for (Pair<Runnable,Runnable> curr : registeredListeners) {
			curr.getKey().run();
		}
		return true;
	}
	

	public boolean detach() {
		if (!attached)
			return false;
		attached=false;
		if(toWatchInScene!=null) {
			toWatchInScene.parentProperty().removeListener(sceneListener);
			sceneListener=null;
		}
		for (Pair<Runnable,Runnable> curr : registeredListeners) {
			curr.getValue().run();
		}
		System.gc();
		return true;
	
	}
	
	public void regsiter(Runnable registerFunction,Runnable deRegisterFunction) {
		registeredListeners.add(new Pair<Runnable,Runnable>(registerFunction, deRegisterFunction));
		if(attached)
			registerFunction.run();
	}
	
	public void addListener(SeedList seed, SeedList.Listener listener) {
		regsiter(()->{seed.addListener(listener);}, ()->{seed.removeListener(listener);});		
	}

}
