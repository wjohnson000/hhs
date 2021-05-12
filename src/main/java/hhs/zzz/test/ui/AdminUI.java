/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package hhs.zzz.test.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FileUtils;

import hhs.zzz.test.ui.client.AdminClient;
import hhs.zzz.test.ui.component.S3TreeCellRenderer;
import hhs.zzz.test.ui.helper.AppConfiguration;
import hhs.zzz.test.ui.helper.ConfigurationDialog;
import hhs.zzz.test.ui.helper.FontChooserDialog;
import hhs.zzz.test.ui.helper.S3Helper;
import hhs.zzz.test.ui.model.CollectionTreeModel;
import hhs.zzz.test.ui.model.FolderNode;
import hhs.zzz.test.ui.model.FolderType;

/**
 * @author wjohnson000
 *
 */
public class AdminUI extends JFrame {

    private static final long serialVersionUID = -1234567890L;

    S3Helper s3Helper = new S3Helper();

    private JMenuBar  mainMenuBar = new JMenuBar();
    private JMenu     fileMenu    = new JMenu("File");
    private JMenuItem quitItem    = new JMenuItem("Quit");
    private JMenuItem confItem    = new JMenuItem("Config");
    private JMenu     editMenu    = new JMenu("Edit");
    private JMenuItem fontItem    = new JMenuItem("Font");
    private JMenuItem downItem    = new JMenuItem("Download");
    private JMenuItem deleItem    = new JMenuItem("Delete");
    private JMenuItem deleS3Item  = new JMenuItem("Delete [S3]");

    private JPanel contentPane;
    private JTree  s3Tree;
    private CollectionTreeModel treeModel;
    private JLabel statusBar = new JLabel();

    private Font defaultFont = new Font("Monospaced", Font.PLAIN, 18);

    public AdminUI() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        this.enableInputMethods(false);
        try {
            setupS3Tree();

            swingInit();
            buildMenus();
            setFontAll(defaultFont);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    protected void swingInit() {
        // Main pane stuff
        contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(new GridBagLayout());
        this.setSize(new Dimension(1200, 880));
        this.setTitle("Homelands Admin UI");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Status Bar
        statusBar.setText(">> Status <<");

        // Content Pane
        JScrollPane scrollPane = new JScrollPane(s3Tree);
        contentPane.add(scrollPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                                        GridBagConstraints.WEST, GridBagConstraints.BOTH,
                                        new Insets(0, 0, 0, 0), 0, 0));
        contentPane.add(statusBar, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                                       GridBagConstraints.WEST, GridBagConstraints.NONE,
                                       new Insets(0, 0, 0, 0), 0, 0));
    }

    protected void buildMenus() {
        this.setJMenuBar(mainMenuBar);

        mainMenuBar.add(fileMenu);
        mainMenuBar.add(editMenu);
        fileMenu.add(confItem);
        fileMenu.add(quitItem);
        editMenu.add(fontItem);
        editMenu.add(downItem);
        editMenu.add(deleItem);
        editMenu.add(deleS3Item);

        confItem.addActionListener(ae -> setConfiguration());
        quitItem.addActionListener(ae -> quitApp());
        fontItem.addActionListener(ae -> chooseFont());
        downItem.addActionListener(ae -> downloadFile());
        deleItem.addActionListener(ae -> deleteFile());
        deleS3Item.addActionListener(ae -> deleteFileS3());
    }

    protected void setupS3Tree() {
        s3Helper = new S3Helper();
        List<FolderNode> s3Nodes = s3Helper.getDetails();
        treeModel = new CollectionTreeModel(s3Nodes);
        s3Tree = new JTree(treeModel);
        s3Tree.setCellRenderer(new S3TreeCellRenderer());
    }

    protected void setConfiguration() {
        ConfigurationDialog.setConfiguration(this);
    }

    protected void quitApp() {
        dispose();
        System.exit(0);
    }

    protected void chooseFont() {
        setFontAll(FontChooserDialog.GetFont(this));
    }

    protected void downloadFile() {
        TreePath[] paths = s3Tree.getSelectionPaths();
        for (TreePath path : paths) {
            if (path.getLastPathComponent() instanceof FolderNode) {
                FolderNode folder = (FolderNode)path.getLastPathComponent();
                if (folder.getType() == FolderType.FILE) {
                    byte[] contents = AdminClient.readFile(folder, AppConfiguration.getSessionId(), AppConfiguration.isProd());
                    if (contents == null) {
                        System.out.println("No File Contents!!");
                    } else {
                        File file = getFileToSave(folder.getId());
                        if (file != null  &&  ! file.isDirectory()) {
                            try {
                                FileUtils.writeByteArrayToFile(file, contents);
                            } catch(Exception ex) {
                                System.out.println("Save file failed!");
                            }
                        } else {
                            System.out.println("No file or illegal file: " + file);
                        }
                    }
                }
            }
        }
    }

    protected void deleteFile() {
        TreePath[] paths = s3Tree.getSelectionPaths();
        for (TreePath path : paths) {
            if (path.getLastPathComponent() instanceof FolderNode) {
                FolderNode folder = (FolderNode)path.getLastPathComponent();
                List<FolderNode> allFolders = getChildren(folder);
                allFolders.forEach(ff -> AdminClient.deleteFolder(ff, AppConfiguration.getSessionId(), AppConfiguration.isProd()));
                CollectionTreeModel model = (CollectionTreeModel)s3Tree.getModel();
                model.removeFolder(folder);
            }
        }
    }

    protected void deleteFileS3() {
        TreePath[] paths = s3Tree.getSelectionPaths();
        for (TreePath path : paths) {
            if (path.getLastPathComponent() instanceof FolderNode) {
                FolderNode folder = (FolderNode)path.getLastPathComponent();
                List<FolderNode> allFolders = getChildren(folder);
                allFolders.forEach(ff -> s3Helper.deleteFile(ff));
                CollectionTreeModel model = (CollectionTreeModel)s3Tree.getModel();
                model.removeFolder(folder);
            }
        }
    }

    protected List<FolderNode> getChildren(FolderNode folder) {
        List<FolderNode> allFolders = new ArrayList<>();

        allFolders.add(folder);
        folder.getChildren().forEach(child -> allFolders.addAll(getChildren(child)));

        return allFolders;
    }

    protected void setFontAll(Font newFont) {
        if (newFont != null) {
            Font boldFont = new Font(newFont.getName(), Font.BOLD, newFont.getSize());
            mainMenuBar.setFont(boldFont);
            fileMenu.setFont(boldFont);
            confItem.setFont(boldFont);
            quitItem.setFont(boldFont);
            editMenu.setFont(boldFont);
            fontItem.setFont(boldFont);
            downItem.setFont(boldFont);
            deleItem.setFont(boldFont);
            deleS3Item.setFont(boldFont);
            s3Tree.setFont(newFont);
            statusBar.setFont(newFont);
        }
    }

    protected File getFileToSave(String name) {
        JFileChooser jfcDialog = new JFileChooser();
        jfcDialog.setDialogTitle("Save file ... ");
        jfcDialog.setSelectedFile(new File("C:/temp", name));
        jfcDialog.setMultiSelectionEnabled(false);

        int retVal = jfcDialog.showSaveDialog(this);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            return jfcDialog.getSelectedFile();
        } else {
            return null;
        }
    }
}
