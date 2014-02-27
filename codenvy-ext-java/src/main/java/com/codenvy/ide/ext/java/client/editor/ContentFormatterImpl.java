package com.codenvy.ide.ext.java.client.editor;

import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.Region;
import com.codenvy.ide.text.edits.TextEdit;
import com.codenvy.ide.texteditor.api.ContentFormatter;
import com.google.inject.Inject;

/**
 * ContentFormatter implementation
 *
 * @author Roman Nikitenko
 */
public class ContentFormatterImpl implements ContentFormatter, JavaParserWorker.ApplyFormatCallback{

    private JavaParserWorker javaParserWorker;
    private Document document;

    @Inject
    public ContentFormatterImpl(JavaParserWorker javaParserWorker){
        this.javaParserWorker = javaParserWorker;
    }

    @Override
    public void format(Document doc, Region region) {
        document = doc;
        int offset = region.getOffset();
        int length = region.getLength();
        try {
            javaParserWorker.format(offset, length, doc.get(offset,length), ContentFormatterImpl.this);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onApplyFormat(TextEdit edit) {
        try {
            edit.apply(document);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
