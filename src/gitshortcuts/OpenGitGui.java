/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gitshortcuts;

import java.io.File;
import java.io.IOException;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class OpenGitGui extends CookieAction {

    @Override
    protected void performAction(Node[] activatedNodes) {
//      JTextComponent target = org.netbeans.editor.Utilities.getFocusedComponent();
        DataObject dataObject = activatedNodes[0].getLookup().lookup(DataObject.class);
        FileObject primaryFile = dataObject.getPrimaryFile();
//        File file = new File(File.separator + primaryFile.getPath().substring(0, primaryFile.getPath().lastIndexOf(File.separator)));
        File file = new File(File.separator + primaryFile.getParent().getPath());
        try {
            Runtime.getRuntime().exec("git gui", null, file);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(OpenGitGui.class, "CTL_OpenGitGui");
    }

    @Override
    protected Class[] cookieClasses() {
        return new Class[]{EditorCookie.class};
    }

    @Override
    protected String iconResource() {
        return "gitshortcuts/qgit.png";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}

