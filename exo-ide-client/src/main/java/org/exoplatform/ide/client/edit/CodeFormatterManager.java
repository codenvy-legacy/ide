/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
