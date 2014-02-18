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
package org.exoplatform.ide.client.operation.cutcopy;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.NavigatorDisplay;
import org.exoplatform.ide.client.framework.project.PackageExplorerDisplay;
import org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
@RolesAllowed({"workspace/developer"})
public class PasteItemsCommand extends SimpleControl implements IDEControl, ItemsToPasteSelectedHandler,
                                                                PasteItemsCompleteHandler, ItemsSelectedHandler, VfsChangedHandler,
                                                                ViewActivatedHandler {
    public static final String ID = "Edit/Paste Item(s)";

    private final static String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.pasteItemsTitleControl();

    private final static String PROMPT = IDE.IDE_LOCALIZATION_CONSTANT.pasteItemsPromptControl();

    private boolean itemsToPasteSelected = false;

    private boolean browserPanelSelected = false;

    private List<Item> selectedItems;

    /**
     *
     */
    public PasteItemsCommand() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(IDEImageBundle.INSTANCE.paste(), IDEImageBundle.INSTANCE.pasteDisabled());
        setEvent(new PasteItemsEvent());
        setGroupName(GroupNames.CUT_COPY);
    }

    /** @see org.exoplatform.ide.client.navigation.control.MultipleSelectionItemsControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(ItemsToPasteSelectedEvent.TYPE, this);
        IDE.addHandler(PasteItemsCompleteEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ViewActivatedEvent.TYPE, this);
    }

    /**
     * @see org.exoplatform.ide.client.navigation.event.ItemsToPasteSelectedHandler#onItemsToPasteSelected(org.exoplatform.ide.client
     *      .navigation.event.ItemsToPasteSelectedEvent)
     */
    @Override
    public void onItemsToPasteSelected(ItemsToPasteSelectedEvent event) {
        itemsToPasteSelected = true;
        updateState();
    }

    /**
     * @see org.exoplatform.ide.client.navigation.event.PasteItemsCompleteHandler#onPasteItemsComlete(org.exoplatform.ide.client
     *      .navigation.event.PasteItemsCompleteEvent)
     */
    @Override
    public void onPasteItemsComlete(PasteItemsCompleteEvent event) {
        itemsToPasteSelected = false;
        updateState();
    }

    /**
     * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     *      .framework.navigation.event.ItemsSelectedEvent)
     */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        selectedItems = event.getSelectedItems();
        updateState();
    }

    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        setVisible(event.getVfsInfo() != null);
    }

    @Override
    public void onViewActivated(ViewActivatedEvent event) {
        browserPanelSelected = event.getView() instanceof NavigatorDisplay ||
                               event.getView() instanceof ProjectExplorerDisplay ||
                               event.getView() instanceof PackageExplorerDisplay;
        updateState();
    }

    protected void updateState() {
        setShowInContextMenu(browserPanelSelected);

        if (selectedItems == null || selectedItems.size() != 1) {
            setEnabled(false);
            return;
        }

        setEnabled(itemsToPasteSelected && browserPanelSelected);
    }
}
