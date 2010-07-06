/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gitshortcuts;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.openide.windows.IOColorLines;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

public final class GitStatusAction extends CookieAction {

    protected void performAction(Node[] activatedNodes) {

        DataObject dataObject = activatedNodes[0].getLookup().lookup(DataObject.class);
        FileObject primaryFile = dataObject.getPrimaryFile();
        final String projectPath = File.separator + primaryFile.getParent().getPath();
        Project[] openProjects = OpenProjects.getDefault().getOpenProjects();
        InputOutput io = IOProvider.getDefault().getIO("Git Status", false);
        try {

            io.getOut().reset();
            IOColorLines.println(io, "Reading git repository....", Color.BLACK);
            for (Project project : openProjects) {
                String path = project.getProjectDirectory().getPath();
                if (projectPath.contains(path)) {
                    IOColorLines.println(io, "Checking for a git repository at " + path, Color.BLACK);
                    File file = new File(path);
                    Boolean isDirectory = false;
                    try {
                        isDirectory = new File(path + File.separator + ".git").isDirectory();
                    } catch (Exception e) {
                        IOColorLines.println(io, e.getMessage(), Color.BLUE);
                    }
                    if (isDirectory) {
                        Process exec = Runtime.getRuntime().exec("git status", null, file);
                        InputStream stderr = exec.getInputStream();
                        InputStreamReader isr = new InputStreamReader(stderr);
                        BufferedReader br = new BufferedReader(isr);
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            if (line.contains("new")) {
                                IOColorLines.println(io, line, Color.GREEN);
                            } else if (line.contains("modified")) {
                                IOColorLines.println(io, line, Color.RED);
                            } else {
                                IOColorLines.println(io, line, Color.BLACK);
                            }
                        }
                    } else {
                        IOColorLines.println(io, path + " is not a git repository!!!", Color.RED);
                    }





                }
            }
            IOColorLines.println(io, "DONE", Color.RED);
        } catch (IOException ex) {
            try {
                Logger.getLogger(GitInitAction.class.getName()).log(Level.SEVERE, null, ex);
                IOColorLines.println(io, "ERROR", Color.RED);
            } catch (IOException ex1) {
                Exceptions.printStackTrace(ex1);
            }
        }



    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(GitStatusAction.class, "CTL_GitStatusAction");
    }

    protected Class[] cookieClasses() {
        return new Class[]{EditorCookie.class};
    }

    @Override
    protected String iconResource() {
        return "gitshortcuts/status.gif";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
