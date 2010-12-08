/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gitshortcuts;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import org.netbeans.api.diff.Difference;
import org.netbeans.api.diff.StreamSource;
import org.openide.util.Exceptions;

/**
 *
 * @author luis
 */
public class DiffStreamSource extends StreamSource {

    private StreamSource source;
    private boolean editable = false;
    private File file;

    public DiffStreamSource(String name, String title, String MIMEType, File file, Boolean editable) {
        source = StreamSource.createSource(name, title, MIMEType, file);
        this.file = file;
        if (editable != null) {
            this.editable = editable;
            this.file.setWritable(editable);
        }


    }

    public void save() {
//        try {
//            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
//            InputStreamReader isr = new InputStreamReader(inputStream);
//                    BufferedReader br = new BufferedReader(isr);
//                    String line = null;
//                    while ((line = br.readLine()) != null) {
//                        System.out.println(line);
//                    }
//        } catch (Exception ex) {
//            Exceptions.printStackTrace(ex);
//        } 
               
    }

    @Override
    public String getName() {
        return source.getName();
    }

    @Override
    public String getTitle() {
        return source.getTitle();
    }

    @Override
    public String getMIMEType() {
        return source.getMIMEType();
    }

    @Override
    public Reader createReader() throws IOException {
        return source.createReader();
    }

    @Override
    public Writer createWriter(Difference[] conflicts) throws IOException {
        return source.createWriter(conflicts);
    }

    @Override
    public boolean isEditable() {
        return editable;
    }
}
