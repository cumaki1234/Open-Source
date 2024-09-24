package org.sourceforge.kga.gui;

import java.util.Timer;
import java.util.TimerTask;

import org.sourceforge.kga.KitchenGardenAid;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
//import javafx.scene.web.WebView;
import javafx.util.Duration;

public class ClickableTooltip extends Tooltip {
	static Timer timer = new Timer();
	
	TimerTask mytask;
	
	Runnable removeOldContentListeners;
	Node attachedTo;
	exitListener exiter;
	

	private class exitListener implements EventHandler<MouseEvent>  {

		@Override
		public void handle(MouseEvent event) {
			TimerTask myNewtask = new TimerTask() {

			@Override
			public void run() {
				Platform.runLater(() -> {hide();
				});					
			}

		};
		timer.schedule(myNewtask, 300);   
		mytask=myNewtask;
		}
		
	}
	
	private void init(Parent content, Node attachTo) {
		super.setMaxHeight(Control.USE_PREF_SIZE);
		super.setMinHeight(Control.USE_PREF_SIZE);
		exiter = new exitListener();
		setPrefHeight(40);
		setHideDelay(Duration.INDEFINITE);
    	setShowDelay(new Duration(500));
		content.setOnMouseEntered(w -> {if(mytask!=null) {mytask.cancel();mytask=null;}});
		content.setOnMouseExited(w -> {hide();});
		super.setOnShown(w -> {
			attachTo.setOnMouseExited(exiter);
		});
		setGraphic(content);
		content.layout();
        Tooltip.install(attachTo, this);
        attachedTo=attachTo;
		//attachTo.setTooltip(this);
	}
	
	public void uninstall() {
		exiter.handle(null);
		Tooltip.uninstall(attachedTo, this);
	}
	
	public ClickableTooltip(Region content, Node attachTo) {
		super();
		init(content,attachTo);
		configListener(content);
	}
	
	private void configListener(Region content) {
		//this.prefHeightProperty().unbind();
		//this.setPrefHeight(400);
		this.prefHeightProperty().bind(content.heightProperty().add(10));
		content.layout();
		this.setMinHeight(USE_PREF_SIZE);
		this.setMaxHeight(USE_PREF_SIZE);
	}
	

	/*public ClickableTooltip(WebView content, Node attachTo) {
		super();
		init(content,attachTo);
		content.getEngine().getLoadWorker().stateProperty().addListener((observable, oldvalue, newValue) -> {
			setPrefHeight(getContentHeight(content)+20);
			content.resize(getContentWidth(content), getContentHeight(content)+5);

			if(content.getEngine().getDocument()!=null) {
				NodeList list = content.getEngine().getDocument().getElementsByTagName("a");
				for (int i = 0;i<list.getLength();i++){
					org.w3c.dom.Node curr = list.item(i);
					((EventTarget)curr).addEventListener("click",new EventListener() {

						@Override
						public void handleEvent(Event evt) {
							EventTarget target = evt.getCurrentTarget();
							HTMLAnchorElement anchorElement = (HTMLAnchorElement) target;
							String href = anchorElement.getHref();
							KitchenGardenAid.getInstance().getHostServices().showDocument(href);
							evt.preventDefault();

						}

					}, false);
				}
			}
		});	

	}
	
	public static double getContentWidth(WebView content) { 
    	String widthText = content.getEngine().executeScript("window.getComputedStyle(document.body, null).getPropertyValue('width')").toString();
    	double width = Double.valueOf(widthText.replace("px", ""));  
    	width=(width<400)?width:400;
    	return width;
	}
	
	public static double getContentHeight(WebView content) {

    	String heightText = content.getEngine().executeScript("window.getComputedStyle(document.body, null).getPropertyValue('height')").toString();
    	double height = Double.valueOf(heightText.replace("px", "")); 
    	height=(height<400)?height:400; 
    	return height;
	}
	*/

}
