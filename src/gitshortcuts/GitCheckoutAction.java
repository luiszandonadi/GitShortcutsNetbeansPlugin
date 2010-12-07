/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gitshortcuts;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.netbeans.api.diff.Diff;
import org.netbeans.api.diff.DiffView;
import org.netbeans.api.diff.StreamSource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.TopComponent;

public final class GitCheckoutAction extends CookieAction {

    protected void performAction(Node[] activatedNodes) {
        DataObject dataObject = activatedNodes[0].getLookup().lookup(DataObject.class);
        FileObject primaryFile = dataObject.getPrimaryFile();
        try {
            final String projectPath = File.separator + primaryFile.getParent().getPath();
            Project[] openProjects = OpenProjects.getDefault().getOpenProjects();
            for (Project project : openProjects) {
                String path = project.getProjectDirectory().getPath();
                if (projectPath.contains(path)) {
                    new DialogCheckout(null, true, path);
                }
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

 

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(GitCheckoutAction.class, "CTL_GitCheckoutAction");
    }

    protected Class[] cookieClasses() {
        return new Class[]{EditorCookie.class};
    }

    @Override
    protected String iconResource() {
        return "gitshortcuts/checkout.png";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
