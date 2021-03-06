/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package hhs.zzz.test.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Locale;

import javax.swing.UIManager;

/**
 * @author wjohnson000
 *
 */
public class MainApp {

    boolean packFrame = false;

    public MainApp() {
        AdminUI frame = new AdminUI();

        // Validate frames that have preset sizes
        // Pack frames that have useful preferred size info, e.g. from their layout
        if (packFrame) {
            frame.pack();
        } else {
            frame.validate();
        }

        // Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

        frame.setVisible(true);
    }

    /** Main method */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch(Exception e) {
            e.printStackTrace();
        }

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainApp();
            }
        });

        System.out.println("Locale: " + Locale.getDefault());
    }
}
