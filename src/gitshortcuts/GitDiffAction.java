/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gitshortcuts;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.netbeans.api.diff.DiffController;
import org.netbeans.api.diff.Difference;
import org.netbeans.api.diff.StreamSource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.spi.diff.DiffProvider;
import org.openide.awt.NotificationDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.TopComponent;

public final class GitDiffAction extends CookieAction implements PropertyChangeListener {

    @Override
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
                    diff2(FileUtil.toFile(primaryFile), temp, primaryFile.getNameExt(), primaryFile.getMIMEType());
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void diff2(final File file, final File temp, final String fileName, final String mimeType) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    StreamSource remote = StreamSource.createSource("HEAD",
                            "HEAD", mimeType, temp);
                    final DiffStreamSource local = new DiffStreamSource(fileName,
                            fileName, mimeType, file, true);
                  

                    DiffProvider diffProvider = Lookup.getDefault().lookup(DiffProvider.class);
                    Difference[] diffs = diffProvider.computeDiff(remote.createReader(), local.createReader());

                    if (diffs.length > 0) {
                         DiffController create = DiffController.createEnhanced(remote, local);
                        TopComponent tc = new TopComponent() {

                            @Override
                            protected void componentClosed() {
                                super.componentClosed();
                                temp.deleteOnExit();
                                temp.delete();
                            }



                        };
                        tc.setDisplayName("Diff Viewer - " + fileName);
                        tc.setLayout(new BorderLayout());
                        JPanel panel = new JPanel();
                        panel.setLayout(new GridLayout(2, 1));
                        panel.add(create.getJComponent(), BorderLayout.CENTER);
                        final JButton jButton = new JButton("OK");
                        jButton.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {



//                                    System.out.println(Lookup.getDefault().lookup(EditorCookie.class));

//                                try {
//                                    Lookup.getDefault().lookup(SaveCookie.class).save();
//                                } catch (IOException ex) {
//                                    Exceptions.printStackTrace(ex);
//                                }
                            }
                        });
                        panel.add(jButton);
                        tc.add(panel);
                        tc.open();
                        tc.requestActive();
                    } else {
                        NotificationDisplayer.getDefault().notify("Git diff", new ImageIcon(), "No diff found!!!!!", null);
                    }
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
