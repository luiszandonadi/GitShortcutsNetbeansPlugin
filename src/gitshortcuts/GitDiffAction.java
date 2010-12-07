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

public final class GitDiffAction extends CookieAction {

    protected void performAction(Node[] activatedNodes) {
        DataObject dataObject = activatedNodes[0].getLookup().lookup(DataObject.class);
        FileObject primaryFile = dataObject.getPrimaryFile();
        try {
            String relativePath = null;
            final String projectPath = File.separator + primaryFile.getParent().getPath();
            Project[] openProjects = OpenProjects.getDefault().getOpenProjects();
            for (Project project : openProjects) {
                String path = project.getProjectDirectory().getPath();
                if (projectPath.contains(path)) {
                    relativePath = primaryFile.getPath().replaceAll(path + File.separator, "");
                    File file = new File(path);
                    Process exec = Runtime.getRuntime().exec("git show HEAD:" + relativePath, null, file);
                    InputStream stderr = exec.getInputStream();
                    InputStreamReader isr = new InputStreamReader(stderr);
                    BufferedReader br = new BufferedReader(isr);

                    String line = null;
                    File temp = File.createTempFile(primaryFile.getNameExt(), ".temp");
                    BufferedWriter out = new BufferedWriter(new FileWriter(temp));
                    while ((line = br.readLine()) != null) {
                        out.write(line);
                        out.newLine();
                    }
                    out.close();


                    diff2(new File(primaryFile.getPath()), temp, primaryFile.getNameExt());


//                    System.out.println("-----------------------------------------");
//                    InputStream errorStream = exec.getErrorStream();
//                    InputStreamReader reader = new InputStreamReader(errorStream);
//                    String texto2 = "";
//                    BufferedReader br1 = new BufferedReader(reader);
//                    while ((line = br1.readLine()) != null) {
//                        texto2 += line;
//                    }
//                    System.out.println(texto2);
//                    System.out.println("-----------------------------------------");
                }
            }


        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }





    }

    public void diff2(final File file, final File temp, final String fileName) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {

                    StreamSource remote = StreamSource.createSource("name2",
                            "HEAD", "text/plain", temp);

                    StreamSource local = StreamSource.createSource("name1",
                            fileName, "text/plain", file);



                    DiffView view = Diff.getDefault().createDiff(local, remote);
                    TopComponent tc = new TopComponent() {

                        @Override
                        protected void componentClosed() {
                            super.componentClosed();
                            temp.delete();
                        }
                    };

                    tc.setDisplayName("Diff Viewer - " + fileName);
                    tc.setLayout(new BorderLayout());
                    tc.add(view.getComponent(), BorderLayout.CENTER);
                    tc.open();
                    tc.requestActive();
                } catch (IOException ex) {
                }
            }
        });
    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(GitDiffAction.class, "CTL_GitDiffAction");
    }

    protected Class[] cookieClasses() {
        return new Class[]{EditorCookie.class};
    }

    @Override
    protected String iconResource() {
        return "gitshortcuts/diff.png";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
