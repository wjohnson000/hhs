/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.admin.ui.helper;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;

/**
 * @author wjohnson000
 *
 */
public class ConfigurationDialog extends JDialog {

    private static final long serialVersionUID = -1;

    private static Font plainFont = new java.awt.Font("Monospaced", Font.PLAIN, 16);
    private static Font boldFont  = new java.awt.Font("Monospaced", Font.BOLD, 16);

    public static void setConfiguration(Frame parent) {
        ConfigurationDialog dialog = new ConfigurationDialog(parent, "", true);
        Dimension dlgSize = dialog.getPreferredSize();
        Dimension frmSize = parent.getSize();
        Point loc = parent.getLocation();
        dialog.setLocation((frmSize.width-dlgSize.width)/2+loc.x, (frmSize.height-dlgSize.height)/2+loc.y);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setModal(true);

        dialog.userAccepted = false;
        if (AppConfiguration.isProd()) {
            dialog.isProdRB.setSelected(true);
        } else {
            dialog.isDevRB.setSelected(true);
        }
        dialog.devSessionTF.setText(AppConfiguration.getDevSessionId());
        dialog.prodSessionTF.setText(AppConfiguration.getProdSessionId());
        dialog.setVisible(true);

        if (dialog.userAccepted) {
            AppConfiguration.setIsProd(dialog.isProdRB.isSelected());
            AppConfiguration.setDevSessionId(dialog.devSessionTF.getText());
            AppConfiguration.setProdSessionId(dialog.prodSessionTF.getText());
        }
    }

    private boolean userAccepted = false;
    private JRadioButton isProdRB = null;
    private JRadioButton isDevRB = null;
    private ButtonGroup  systemBG = null;
    private JTextField prodSessionTF = null;
    private JTextField devSessionTF  = null;
    
    public ConfigurationDialog(Frame frame, String title, boolean modal) {
        super(frame, title, modal);
        buildUI();
    }

    void buildUI() {
        getContentPane().setLayout(new GridBagLayout());
        // ====================================================================
        // Set the primary label
        // ====================================================================
        JLabel jLabel01 = new JLabel("Application Configuration:");
        jLabel01.setFont(boldFont);

        getContentPane().add(
                jLabel01, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
                        GridBagConstraints.WEST, GridBagConstraints.NONE,
                        new Insets(12, 6, 0, 2), 160, 0));

        // ====================================================================
        // Add some spacing to make the panel prettier, and add the fonts
        // ====================================================================
        getContentPane().add(
                new JLabel(""), new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.WEST, GridBagConstraints.NONE,
                        new Insets(12, 6, 0, 2), 100, 0));

        // ====================================================================
        // Add a line for PROD vs. DEV system
        // ====================================================================
        JLabel jLabel02 = new JLabel("System:");
        jLabel02.setFont(plainFont);
        getContentPane().add(
                jLabel02, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(2, 12, 0, 2), 0, 0));

        isDevRB  = new JRadioButton("Dev");
        isProdRB = new JRadioButton("Prod");
        systemBG = new ButtonGroup();
        systemBG.add(isDevRB);
        systemBG.add(isProdRB);

        JPanel systemPanel = new JPanel();
        systemPanel.add(isDevRB);
        systemPanel.add(isProdRB);
        getContentPane().add(
                systemPanel, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0,
                        GridBagConstraints.WEST, GridBagConstraints.NONE,
                        new Insets(2, 2, 0, 2), 0, 0));

        // ====================================================================
        // Add a line for the DEV session ID
        // ====================================================================
        JLabel jLabel03 = new JLabel("DEV session:");
        jLabel03.setFont(plainFont);
        getContentPane().add(
                jLabel03, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(2, 12, 0, 2), 0, 0));

        devSessionTF = new JTextField(42);
        getContentPane().add(
                devSessionTF, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                        new Insets(2, 2, 0, 2), 0, 0));

        // ====================================================================
        // Add a line for the PROD session ID
        // ====================================================================
        JLabel jLabel04 = new JLabel("PROD session:");
        jLabel04.setFont(plainFont);
        getContentPane().add(
                jLabel04, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(2, 12, 0, 2), 0, 0));

        prodSessionTF = new JTextField(42);
        getContentPane().add(
                prodSessionTF, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                        new Insets(2, 2, 0, 2), 0, 0));

        // ====================================================================
        // Add the button line
        // ====================================================================
        JButton acceptBtn = new JButton("Accept");
        acceptBtn.setFont(plainFont);
        acceptBtn.addActionListener(ae -> {
            userAccepted = true;
            dispose();
        });

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(plainFont);
        cancelBtn.addActionListener(ae -> {
            userAccepted = false;
            dispose();
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(acceptBtn);
        btnPanel.add(cancelBtn);
        getContentPane().add(
                btnPanel, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                        new Insets(0, 6, 0, 2), 0, 0));

        pack();
    }
}
