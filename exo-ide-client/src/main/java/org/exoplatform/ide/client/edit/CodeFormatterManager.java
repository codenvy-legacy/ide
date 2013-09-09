/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.client.edit;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.ide.client.framework.editor.AddCodeFormatterEvent;
import org.exoplatform.ide.client.framework.editor.AddCodeFormatterHandler;
import org.exoplatform.ide.client.framework.editor.CodeFormatter;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFormatTextEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFormatTextHandler;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.edits.MalformedTreeException;
import org.exoplatform.ide.editor.shared.text.edits.TextEdit;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 4:08:04 PM Apr 2, 2012 evgen $
 */
public class CodeFormatterManager implements AddCodeFormatterHandler, EditorFormatTextHandler,
                                             EditorActiveFileChangedHandler {

    private final HandlerManager eventBus;

    private Map<String, CodeFormatter> formatters = new HashMap<String, CodeFormatter>();

    private Editor editor;

    private FileModel activeFile;

    /** @param eventBus */
    public CodeFormatterManager(HandlerManager eventBus) {
        this.eventBus = eventBus;
        eventBus.addHandler(AddCodeFormatterEvent.TYPE, this);
        eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        eventBus.addHandler(EditorFormatTextEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.editor.AddCodeFormatterHandler#onAddCodeFormatter(org.exoplatform.ide.client.framework
     * .editor.AddCodeFormatterEvent) */
    @Override
    public void onAddCodeFormatter(AddCodeFormatterEvent event) {
        formatters.put(event.getMimeType(), event.getFormatter());
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorFormatTextHandler#onFormatFile(org.exoplatform.ide.client.framework
     * .editor.event.EditorFormatTextEvent) */
    @Override
    public void onFormatFile(EditorFormatTextEvent event) {
        if (editor == null) {
            return;
        }

        if (formatters.containsKey(activeFile.getMimeType())) {
            IDocument document = editor.getDocument();
            TextEdit textEdit = formatters.get(activeFile.getMimeType()).format(document);
            try {
                textEdit.apply(document);
            } catch (MalformedTreeException e) {
                e.printStackTrace();
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        } else {
            editor.formatSource();
        }
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        activeFile = event.getFile();
        editor = event.getEditor();
    }

}
