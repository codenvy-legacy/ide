/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.newresource;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.event.NodeChangedEvent;
import com.codenvy.ide.api.projecttree.AbstractTreeNode;
import com.codenvy.ide.api.projecttree.generic.StorableNode;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.ui.dialogs.askValue.AskValueCallback;
import com.codenvy.ide.ui.dialogs.askValue.AskValueDialog;
import com.codenvy.ide.util.loging.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Action to create new folder.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class NewFolderAction extends DefaultNewResourceAction {
    private CoreLocalizationConstant localizationConstant;

    @Inject
    public NewFolderAction(AppContext appContext,
                           CoreLocalizationConstant localizationConstant,
                           SelectionAgent selectionAgent,
                           Resources resources,
                           ProjectServiceClient projectServiceClient,
                           EventBus eventBus,
                           AnalyticsEventLogger eventLogger,
                           DtoUnmarshallerFactory unmarshallerFactory) {
        super(localizationConstant.actionNewFolderTitle(),
              localizationConstant.actionNewFolderDescription(),
              null,
              resources.defaultFolder(),
              appContext,
              selectionAgent,
              null,
              projectServiceClient,
              eventBus,
              eventLogger,
              unmarshallerFactory);
        this.localizationConstant = localizationConstant;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);

        new AskValueDialog(localizationConstant.newResourceTitle(localizationConstant.actionNewFolderTitle()),
                           localizationConstant.newResourceLabel(localizationConstant.actionNewFolderTitle().toLowerCase()), new AskValueCallback() {
            @Override
            public void onOk(String value) {
                final StorableNode parent = getParent();
                projectServiceClient.createFolder(getParent().getPath() + '/' + value, new AsyncRequestCallback<ItemReference>() {
                    @Override
                    protected void onSuccess(ItemReference result) {
                        eventBus.fireEvent(NodeChangedEvent.createNodeChildrenChangedEvent((AbstractTreeNode<?>)parent));
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        Log.error(NewFolderAction.class, exception);
                    }
                });
            }
        }
        ).show();
    }
}
