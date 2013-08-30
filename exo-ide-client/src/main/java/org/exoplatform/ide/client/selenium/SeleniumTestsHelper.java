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
package org.exoplatform.ide.client.selenium;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorOpenFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorOpenFileHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.theme.ThemeChangedEvent;
import org.exoplatform.ide.client.theme.ThemeChangedHandler;
import org.exoplatform.ide.client.theme.ThemeManager;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.List;

/**
 * This class sets hidden values to DOM which allows to do faster time passing the test.
 * <p/>
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SeleniumTestsHelper
        implements EditorActiveFileChangedHandler, EditorOpenFileHandler, ItemsSelectedHandler, ThemeChangedHandler {

    /** Panel where stores the widget with debug information. */
    private AbsolutePanel debugPanel;

    /** Widget for storing the URL of active file in editor. */
    private Widget editorActiveFile;

    /** Widget for storing the URL of previous active file. */
    private Widget editorPreviousActiveFile;

    /** Widget for storing the URL of selected file in Workspace or Search Result views. */
    private Widget selectedFile;

    /** Widget for storing current theme name. */
    private Widget currentTheme;

    public SeleniumTestsHelper() {
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        IDE.addHandler(EditorOpenFileEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(ThemeChangedEvent.TYPE, this);

        debugPanel = new AbsolutePanel();
        debugPanel.getElement().setId("debug-panel");
        debugPanel.setSize("300px", "150px");
        debugPanel.setVisible(false);
        debugPanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
        RootPanel.get().add(debugPanel, -5000, -5000);

        editorActiveFile = createDebugEntry("debug-editor-active-file-url");
        editorActiveFile.addDomHandler(editorActiveFileClickHandler, ClickEvent.getType());
        editorPreviousActiveFile = createDebugEntry("debug-editor-previous-active-file-url");
        selectedFile = createDebugEntry("debug-navigation-selected-file");

        currentTheme = createDebugEntry("debug-current-theme-name");
        currentTheme.getElement().setInnerText(ThemeManager.DEFAULT_THEME_NAME);
    }

    private ClickHandler editorActiveFileClickHandler = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            editorActiveFile.getElement().setInnerText("");
            ;
        }
    };

    private Widget createDebugEntry(String id) {
        FlowPanel panel = new FlowPanel();
        panel.getElement().setId(id);
        panel.getElement().setInnerText("");
        debugPanel.add(panel);
        return panel;
    }

    /**
     * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     *      .ide.client.framework.editor.event.EditorActiveFileChangedEvent)
     */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        String previousActiveFile = editorActiveFile.getElement().getInnerText();
        editorPreviousActiveFile.getElement().setInnerText(previousActiveFile);

        if (event.getFile() == null) {
            editorActiveFile.getElement().setInnerText("");
        } else {
            editorActiveFile.getElement().setInnerText(event.getFile().getPath());
        }
    }

    @Override
    public void onEditorOpenFile(EditorOpenFileEvent event) {
        editorActiveFile.getElement().setInnerText("");
    }

    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        List<Item> selectedItems = event.getSelectedItems();
        if (selectedItems.size() == 0) {
            selectedFile.getElement().setInnerText("");
        } else {
            selectedFile.getElement().setInnerText(selectedItems.get(0).getPath());
        }
    }

    @Override
    public void onThemeChanged(ThemeChangedEvent event) {
        currentTheme.getElement().setInnerText(event.getTheme());
    }

}
