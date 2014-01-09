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
package org.exoplatform.ide.editor.javascript.client.syntaxvalidator;

import com.codenvy.ide.client.util.ScheduledCommandExecutor;
import com.codenvy.ide.json.client.JsoArray;
import com.google.collide.client.disable.DisableEnableCollaborationEvent;
import com.google.collide.client.disable.DisableEnableCollaborationHandler;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.user.client.Timer;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.commons.util.Log;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.marking.Markable;
import org.exoplatform.ide.editor.client.marking.Marker;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IRegion;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JavaScript code controller which is used to parse file content.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: JavaScriptCodeController.java Sep 18, 2012 12:14:48 PM azatsarynnyy $
 */
public class JavaScriptCodeController implements 
    EditorFileContentChangedHandler, EditorFileOpenedHandler, EditorFileClosedHandler,
    EditorActiveFileChangedHandler,
    DisableEnableCollaborationHandler {

    /**
     * Mapping opened files to editors.
     */
    private Map<String, Markable> editors = new HashMap<String, Markable>();

    /**
     * Executor of a parse command.
     */
    private ParseCommand parseCommandExecutor = new ParseCommand();
    
    /**
     * Active file.
     */
    private FileModel activeFile;    

    /**
     * Creates a new instance of this {@link JavaScriptCodeController}
     */
    public JavaScriptCodeController() {
        IDE.addHandler(EditorFileContentChangedEvent.TYPE, this);
        IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
        IDE.addHandler(EditorFileClosedEvent.TYPE, this);
        
        IDE.addHandler(DisableEnableCollaborationEvent.TYPE, this);
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
    }

    /**
     * @see org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedHandler#onEditorFileContentChanged(org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedEvent)
     */
    @Override
    public void onEditorFileContentChanged(final EditorFileContentChangedEvent event) {
        parseFile(event.getFile());
    }
    
    /**
     * Parses file
     * 
     * @param file file to parse
     */
    private void parseFile(final FileModel file) {
        if (file == null) {
            return;
        }
        
        final String mimeType = file.getMimeType();
        if (!mimeType.equals(MimeType.APPLICATION_JAVASCRIPT) && !mimeType.equals(MimeType.APPLICATION_X_JAVASCRIPT)
            && !mimeType.equals(MimeType.TEXT_JAVASCRIPT)) {
            return;
        }

        if (editors.containsKey(file.getId())) {
            Timer timer = new Timer() {
                @Override
                public void run() {
                    parseCommandExecutor.scheduleParse(file);
                }
            };
            timer.schedule(1000);
        }
    }
    
    /**
     * @see org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler#onEditorFileOpened(org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent)
     */
    @Override
    public void onEditorFileOpened(final EditorFileOpenedEvent event) {
        if (event.getFile() == null || event.getEditor() == null) {
            return;
        }
        final String mimeType = event.getFile().getMimeType();
        if (!mimeType.equals(MimeType.APPLICATION_JAVASCRIPT) && !mimeType.equals(MimeType.APPLICATION_X_JAVASCRIPT)
            && !mimeType.equals(MimeType.TEXT_JAVASCRIPT)) {
            return;
        }

        editors.put(event.getFile().getId(), (Markable)event.getEditor());

        Timer timer = new Timer() {
            @Override
            public void run() {
                parseCommandExecutor.scheduleParse(event.getFile());
            }
        };
        timer.schedule(3000);
    }

    /**
     * @see org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler#onEditorFileClosed(org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent)
     */
    @Override
    public void onEditorFileClosed(EditorFileClosedEvent event) {
        if (event.getFile() != null) {
            editors.remove(event.getFile().getId());
        }
    }

    /**
     * Starts parse the active file content.
     * 
     * @param file file to parse
     */
    private void startParsing(FileModel file) {
        try {
            JsoArray<JsError> jsErrors = doParse(file.getContent());
            markErrors(file, jsErrors);
        } catch (JavaScriptException e) {
            String description = e.getDescription();
            if (description != null && description.contains("Line ")) {
                JsoArray<JsError> jsErrors = JsoArray.<JsError> create();
                jsErrors.add(e.getException().<JsError> cast());
                markErrors(file, jsErrors);
            }
        }
    }

    /**
     * Perform parse text content and returns {@link JsoArray} that may contains {@link JsError}.
     * 
     * @param content text content
     * @return array that contains parsing errors
     */
    private native JsoArray<JsError> doParse(String content)
    /*-{
        return $wnd.esprima.parse(content, {tolerant: true}).errors;
    }-*/;

    /**
     * Mark all errors in the editor.
     * 
     * @param file file to mark an errors
     * @param jsErrors {@link JsoArray} that contains parsing errors
     */
    private void markErrors(FileModel file, JsoArray<JsError> jsErrors) {
        Markable editor = editors.get(file.getId());
        if (editor == null) {
            return;
        }
        editor.unmarkAllProblems();
        try {
            if (jsErrors != null && jsErrors.size() != 0) {
                List<Marker> markers = new ArrayList<Marker>();
                for (int i = 0; i < jsErrors.size(); i++) {
                    JsError jsError = jsErrors.get(i);
                    IRegion errorLineInformation;
                    errorLineInformation = ((Editor)editor).getDocument().getLineInformation(jsError.getLineNumber() - 1);

                    int start = errorLineInformation.getOffset();
                    int end = start + errorLineInformation.getLength() - 1; // /n

                    JsProblem jsProblem = new JsProblem(jsErrors.get(i), start, end);
                    markers.add(jsProblem);
                }
                editor.addProblems(markers.toArray(new Marker[markers.size()]));
            }
        } catch (BadLocationException e) {
            Log.info(e.getMessage());
        }
    }

    /**
     * Executor of a parse command.
     */
    private class ParseCommand extends ScheduledCommandExecutor {
        
        /**
         * File to parse.
         */
        private FileModel file;

        /**
         * @see com.codenvy.ide.client.util.ScheduledCommandExecutor#execute()
         */
        @Override
        protected void execute() {
            if (file != null) {
                startParsing(file);
            }
        }

        /**
         * @param file
         */
        public void scheduleParse(FileModel file) {
            if (file != null) {
                this.file = file;
                scheduleDeferred();
            }
        }
    }

    /**
     * @see com.google.collide.client.disable.DisableEnableCollaborationHandler#onDisableEnableCollaboration(com.google.collide.client.disable.DisableEnableCollaborationEvent)
     */
    @Override
    public void onDisableEnableCollaboration(DisableEnableCollaborationEvent event) {
        parseFile(activeFile);
    }
    
    /**
     * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
     */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        activeFile = event.getFile();
    }
    
}
