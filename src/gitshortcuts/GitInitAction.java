/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gitshortcuts;

import java.awt.Color;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.NotificationDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbCollections;
import org.openide.util.Utilities;
import org.openide.util.actions.ActionInvoker;
import org.openide.util.actions.CookieAction;
import org.openide.windows.IOColorLines;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

public final class GitInitAction extends CookieAction {

    protected void performAction(Node[] activatedNodes) {
        DataObject dataObject = activatedNodes[0].getLookup().lookup(DataObject.class);
        FileObject primaryFile = dataObject.getPrimaryFile();
        final String projectPath = File.separator + primaryFile.getParent().getPath();
        Project[] openProjects = OpenProjects.getDefault().getOpenProjects();
        try {
            InputOutput io = IOProvider.getDefault().getIO("Git Init", false);
            io.setFocusTaken(true);
            io.getOut().reset();
            IOColorLines.println(io, "Initializing a new git repository....", Color.RED);
            for (Project project : openProjects) {
                String path = project.getProjectDirectory().getPath();
                if (projectPath.contains(path)) {
                    IOColorLines.println(io, "Checking for a git repository at " + path, Color.RED);
                    File file = new File(path);
                    Boolean isDirectory = false;
                    try {
                        isDirectory = new File(path + File.separator + ".git").isDirectory();
                    } catch (Exception e) {
                        IOColorLines.println(io, e.getMessage(), Color.BLUE);
                    }
                    if (isDirectory) {
//                        final Node[] nodes = activatedNodes;
//                        ActionListener actionListener = new ActionListener() {
//
//                            @Override
//                            public void actionPerformed(ActionEvent e) {
//
//                                GitStatusAction lookup = Lookup.getDefault().lookup(GitStatusAction.class);
//                                lookup.performAction(nodes);
//
//                            }
//                        };
                        NotificationDisplayer.getDefault().notify("Git Init", new ImageIcon(), "Already exists a git repository!!!", null);
                        IOColorLines.println(io, "Already exists a git repository!!!", Color.RED);
                    } else {
                        IOColorLines.println(io, "Creating on " + path, Color.RED);
                        Runtime.getRuntime().exec("git init", null, file);
                        IOColorLines.println(io, "Git repository created!!!", Color.RED);
                        NotificationDisplayer.getDefault().notify("Git Init", new ImageIcon(), "Git repository created !!!", null);
                    }
                }
            }
            IOColorLines.println(io, "DONE", Color.RED);
        } catch (IOException ex) {
            Logger.getLogger(GitInitAction.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(GitInitAction.class, "CTL_GitInitAction");
    }

    protected Class[] cookieClasses() {
        return new Class[]{EditorCookie.class};
    }

    @Override
    protected String iconResource() {
        return "gitshortcuts/init2.gif";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
