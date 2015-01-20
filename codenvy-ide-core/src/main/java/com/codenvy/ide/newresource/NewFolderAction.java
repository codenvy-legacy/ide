/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.newresource;

import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.event.NodeChangedEvent;
import com.codenvy.ide.api.projecttree.AbstractTreeNode;
import com.codenvy.ide.api.projecttree.generic.StorableNode;
import com.codenvy.ide.json.JsonHelper;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.dialogs.InputCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to create new folder.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class NewFolderAction extends AbstractNewResourceAction {
    private CoreLocalizationConstant localizationConstant;

    @Inject
    public NewFolderAction(CoreLocalizationConstant localizationConstant, Resources resources) {
        super(localizationConstant.actionNewFolderTitle(),
              localizationConstant.actionNewFolderDescription(),
              resources.defaultFolder());
        this.localizationConstant = localizationConstant;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);

        dialogFactory.createInputDialog(
                localizationConstant.newResourceTitle(localizationConstant.actionNewFolderTitle()),
                localizationConstant.newResourceLabel(localizationConstant.actionNewFolderTitle().toLowerCase()),
                new InputCallback() {
                    @Override
                    public void accepted(String value) {
                        final StorableNode parent = getParent();
                        projectServiceClient.createFolder(getParent().getPath() + '/' + value, new AsyncRequestCallback<ItemReference>() {
                            @Override
                            protected void onSuccess(ItemReference result) {
                                eventBus.fireEvent(NodeChangedEvent.createNodeChildrenChangedEvent((AbstractTreeNode<?>)parent));
                            }

                            @Override
                            protected void onFailure(Throwable exception) {
                                dialogFactory.createMessageDialog("", JsonHelper.parseJsonMessage(exception.getMessage()), null).show();
                            }
                        });
                    }
                }, null).withValidator(folderNameValidator).show();
    }
}
