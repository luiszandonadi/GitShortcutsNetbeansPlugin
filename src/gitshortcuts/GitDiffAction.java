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
import java.util.Collection;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import org.netbeans.api.diff.Diff;
import org.netbeans.api.diff.DiffView;
import org.netbeans.api.diff.Difference;
import org.netbeans.api.diff.StreamSource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.spi.diff.DiffControllerImpl;
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
private TopComponent topComponent;
private DiffStreamSource local;
private StreamSource remote;
private DiffView diffView;
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
                    diff2(primaryFile, FileUtil.toFile(primaryFile), temp, primaryFile.getNameExt(), primaryFile.getMIMEType());
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void diff2(final FileObject primaryFile, final File file, final File temp, final String fileName, final String mimeType) {
        SwingUtilities.invokeLater(new Runnable()    {

            @Override
            public void run() {
                try {
                    remote = StreamSource.createSource("HEAD",
                            "HEAD", mimeType, temp);
                    local = new DiffStreamSource(fileName,
                            fileName, mimeType, file, true);


                    final DiffProvider diffProvider = Lookup.getDefault().lookup(DiffProvider.class);
                    Difference[] diffs = diffProvider.computeDiff(remote.createReader(), local.createReader());

                    if (diffs.length > 0) {
                          diffView = Diff.getDefault().createDiff(remote, local);

//                        DiffController create = DiffController.createEnhanced(remote, local);


                        topComponent = new TopComponent()    {

                            @Override
                            protected void componentClosed() {
                                super.componentClosed();
                                temp.deleteOnExit();
                                temp.delete();
                            }
                        };
                        topComponent.setDisplayName("Diff Viewer - " + fileName);
                        topComponent.setLayout(new BorderLayout());
                        JPanel panel = new JPanel();
                        panel.setLayout(new GridLayout(2, 1));
//                        panel.add(create.getJComponent(), BorderLayout.CENTER);
                        panel.add(diffView.getComponent(), BorderLayout.CENTER);
                        final JButton jButton = new JButton("OK");
                        jButton.addActionListener(new ActionListener()    {

                            @Override
                            public void actionPerformed(ActionEvent e) {

                                save();




                            }
                        });
                        panel.add(jButton);
                        topComponent.add(panel);
                        topComponent.open();
                        topComponent.requestActive();
                        
                        
                    } else {
                        NotificationDisplayer.getDefault().notify("Git diff", new ImageIcon(), "No diff found!!!!!", null);
                    }
                } catch (IOException ex) {
                }
            }
        });
    }

    private void save() {
        //        System.out.println(topComponent.getDisplayName());
        //        Action[] actions = topComponent.getActions();
        //        for (Action action : actions) {
        //            System.out.println(action);
        //        }
        Collection<? extends Diff> all = Diff.getAll();
        for (Diff diff : all) {
//            System.out.println(diff.);
        }
        
        
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
