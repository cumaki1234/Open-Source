package org.sourceforge.kga;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

public class JavaFXTest extends KGATest{
	
	public JavaFXTest() {
        thrown= new ConcurrentHashMap<>();
	}

	@BeforeAll
    public static void evaluate() throws Throwable {
        setupJavaFX();
        
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                countDownLatch.countDown();
            }});
        
        countDownLatch.await();
    }

    protected static void setupJavaFX() throws InterruptedException {
        
        long timeMillis = System.currentTimeMillis();
        
        final CountDownLatch latch = new CountDownLatch(1);
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // initializes JavaFX environment
                new JFXPanel(); 
                
                latch.countDown();
            }
        });
        
        System.out.println("javafx initialising...");
        latch.await();
        System.out.println("javafx is initialised in " + (System.currentTimeMillis() - timeMillis) + "ms");
    }
    
    Map<Thread,Throwable> thrown;
    
    
}
