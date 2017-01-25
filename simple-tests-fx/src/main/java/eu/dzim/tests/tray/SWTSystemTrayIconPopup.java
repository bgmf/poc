package eu.dzim.tests.tray;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

/**
 * Note to myself: to add JavaFX, don't forget something like "javafx toolkit not initialized"<br>
 * <br>
 * see http://stackoverflow.com/questions/11273773/javafx-2-1-toolkit-not-initialized<br>
 * see https://rterp.wordpress.com/2015/04/04/javafx-toolkit-not-initialized-solved/<br>
 * ...
 */
public class SWTSystemTrayIconPopup {
	
	public static void main(String[] args) {
		
		Display display = new Display();
		Shell shell = new Shell(display);
		Image image = new Image(display, 16, 16);
		final Tray tray = display.getSystemTray();
		if (tray == null) {
			System.out.println("The system tray is not available");
		} else {
			final TrayItem item = new TrayItem(tray, SWT.NONE);
			item.setToolTipText("SWT TrayItem");
			item.addListener(SWT.Show, new Listener() {
				public void handleEvent(Event event) {
					System.out.println("show");
				}
			});
			item.addListener(SWT.Hide, new Listener() {
				public void handleEvent(Event event) {
					System.out.println("hide");
				}
			});
			item.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					System.out.println("selection");
				}
			});
			item.addListener(SWT.DefaultSelection, new Listener() {
				public void handleEvent(Event event) {
					System.out.println("default selection");
				}
			});
			final Menu menu = new Menu(shell, SWT.POP_UP);
			for (int i = 0; i < 8; i++) {
				MenuItem mi = new MenuItem(menu, SWT.PUSH);
				mi.setText("Item" + i);
				mi.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event) {
						System.out.println("selection " + event.widget);
					}
				});
				if (i == 0)
					menu.setDefaultItem(mi);
			}
			item.addListener(SWT.MenuDetect, new Listener() {
				public void handleEvent(Event event) {
					System.out.println("menu detect");
					menu.setVisible(true);
				}
			});
			item.setImage(image);
		}
		// shell.setBounds(50, 50, 300, 200);
		// shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		image.dispose();
		display.dispose();
	}
}