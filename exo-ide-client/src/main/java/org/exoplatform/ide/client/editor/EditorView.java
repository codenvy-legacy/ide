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
package org.exoplatform.ide.client.editor;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;

import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.util.ImageUtil;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: EditorView Mar 21, 2011 4:33:38 PM evgen $
 */
public class EditorView extends ViewImpl implements ViewActivatedHandler {

    private static final String EDITOR_SWITCHER_BACKGROUND = Images.Editor.EDITOR_SWITCHER_BACKGROUND;
    private static final String FILE_IS_READ_ONLY          =
            org.exoplatform.ide.client.IDE.EDITOR_CONSTANT.editorControllerFileIsReadOnly();
    private static final int    BUTTON_HEIGHT              = 22;
    private static       int    editorIndex                = 0;
    int lastEditorHeight = 0;
    private List<Editor>       openedEditors = new ArrayList<Editor>();
    private List<ToggleButton> buttons       = new ArrayList<ToggleButton>();
    private int         currentEditorIndex;
    private LayoutPanel editorArea;
    private Map<Editor, EditorEventHandler> editorEventHandlers = new HashMap<Editor, EditorEventHandler>();
    private FileModel file;
    private boolean fileReadOnly;

    /**
     */
    public EditorView(FileModel file, boolean isFileReadOnly, Editor[] editors, int currentEditorIndex) {
        super("editor-" + editorIndex++, "editor", getFileTitle(file, isFileReadOnly),
              new Image(ImageUtil.getIcon(file.getMimeType())));
        fileReadOnly = isFileReadOnly;
        setCanShowContextMenu(true);

        this.file = file;
        openedEditors = new ArrayList<Editor>();

        IDE.addHandler(ViewActivatedEvent.TYPE, this);

        if (editors.length == 1 || isFileReadOnly) {
            Editor editor = editors[0];
            editor.setReadOnly(isFileReadOnly);
            addEditor(editor);
        } else {
            addEditors(editors);
            switchToEditor(0);
            getEditor().setFile(file);
            getEditor().setReadOnly(fileReadOnly);
        }
    }

    private static String getFileTitle(FileModel file, boolean isReadOnly) {
        boolean fileChanged = file.isContentChanged();

        String fileName = Utils.unescape(fileChanged ? file.getName() + "&nbsp;*" : file.getName());

        String mainHint = file.getName();

        String readonlyImage = (isReadOnly) ?
                               "<img id=\"fileReadonly\"  style=\"margin-left:-4px; margin-bottom: -4px;\" border=\"0\" suppress=\"true\"" +
                               " src=\"" +
                               Images.Editor.READONLY_FILE + "\" />" : "";

        mainHint = (isReadOnly) ? FILE_IS_READ_ONLY : mainHint;
        String title = "<span title=\"" + mainHint + "\">" + readonlyImage + "&nbsp;" + fileName + "&nbsp;</span>";

        return title;
    }

    private void addEditor(final Editor editor) {
        editor.asWidget().setHeight("100%");

        editor.setFile(file);
        add(editor);


        editorEventHandlers.put(editor, new EditorEventHandler(editor));
        openedEditors.add(editor);
    }

    private void addEditors(Editor[] editors) {
        editorArea = new LayoutPanel();

        AbsolutePanel editorSwitcherContainer = new AbsolutePanel();
        DOM.setStyleAttribute(editorSwitcherContainer.getElement(), "background",
                              "#FFFFFF url(" + EDITOR_SWITCHER_BACKGROUND + ") repeat-x");

        HorizontalPanel editorSwitcher = new HorizontalPanel();
        editorSwitcherContainer.add(editorSwitcher);
        editorSwitcherContainer.setHeight("" + BUTTON_HEIGHT);

        editorArea.add(editorSwitcherContainer);
        editorArea.setWidgetBottomHeight(editorSwitcherContainer, 0, Unit.PX, BUTTON_HEIGHT, Unit.PX);
        add(editorArea);

        int index = 0;
        for (Editor editor : editors) {
            editor.asWidget().setHeight("100%");
            editorArea.add(editor);
            editorArea.setWidgetTopBottom(editor, 0, Unit.PX, BUTTON_HEIGHT, Unit.PX);
            openedEditors.add(editor);
            editorEventHandlers.put(editor, new EditorEventHandler(editor));

            ToggleButton button = createButton(editor.getName(), editor.getName() + "ButtonID", index);
            buttons.add(button);
            editorSwitcher.add(button);

            index++;
        }
    }

    @Override
    protected void onUnload() {
        super.onUnload();

        for (EditorEventHandler editorEventHandler : editorEventHandlers.values()) {
            editorEventHandler.removeHandlers();
        }

        editorEventHandlers.clear();
    }

    /** @return the editor */
    public Editor getEditor() {
        return openedEditors.get(currentEditorIndex);
    }

    public FileModel getFile() {
        return file;
    }

    public void setFile(FileModel newFile) {
        this.file = newFile;
    }

    /**
     * Create button with label and icon
     *
     * @param label
     * @param id
     * @return {@link ToggleButton}
     */
    private ToggleButton createButton(String label, String id, int index) {
        ToggleButton button = new ToggleButton(label);
        button.setTitle(label);
        button.setHeight(String.valueOf(BUTTON_HEIGHT));
        button.getElement().setAttribute("editor-index", "" + index);

        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ToggleButton but = (ToggleButton)event.getSource();
                int editorIndex = Integer.parseInt(but.getElement().getAttribute("editor-index"));
                if (editorIndex == currentEditorIndex) {
                    return;
                }

                final String newFileContent = getEditor().getText();

                switchToEditor(editorIndex);
                getEditor().getDocument().set("");
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        getEditor().getDocument().set(newFileContent);
                    }
                });
                getEditor().setReadOnly(fileReadOnly);

            }
        });

        return button;
    }

    private void switchToEditor(int index) {
        for (int i = 0; i < openedEditors.size(); i++) {
            if (i == index) {
                editorArea.setWidgetVisible(openedEditors.get(i).asWidget(), true);
                buttons.get(i).setDown(true);
            } else {
                editorArea.setWidgetVisible(openedEditors.get(i).asWidget(), false);
                buttons.get(i).setDown(false);
            }
        }

        currentEditorIndex = index;
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                IDE.fireEvent(new EditorActiveFileChangedEvent(file, openedEditors.get(currentEditorIndex)));
            }
        });
    }

    public void setTitle(FileModel file, boolean isFileReadOnly) {
        super.setTitle(getFileTitle(file, isFileReadOnly));
    }

    @Override
    public void onViewActivated(ViewActivatedEvent event) {
        if (!event.getView().getId().equals(getId())) {
            return;
        }

        final Editor currentEditor = getEditor();
        currentEditor.setFocus();

//        new Timer() {
//            @Override
//            public void run() {
//                currentEditor.setFocus();
//            }
//        }.schedule(100);
    }

}
