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
package com.codenvy.ide.openproject;

import elemental.dom.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import com.codenvy.api.project.shared.dto.ProjectReference;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ui.list.SimpleList;
import com.codenvy.ide.ui.window.Window;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * The implementation of {@link OpenProjectView}.
 * Provides selecting project what want to open.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class OpenProjectViewImpl extends Window implements OpenProjectView {
    private static OpenProjectViewImplUiBinder uiBinder = GWT.create(OpenProjectViewImplUiBinder.class);

    Button btnCancel;
    Button btnOpen;
    @UiField
    ScrollPanel               listPanel;
    @UiField(provided = true)
    com.codenvy.ide.Resources res;
    private CoreLocalizationConstant     localization;
    private ActionDelegate               delegate;
    private SimpleList<ProjectReference> list;
    private SimpleList.ListItemRenderer<ProjectReference>  listItemRenderer = new SimpleList.ListItemRenderer<ProjectReference>() {
        @Override
        public void render(Element itemElement, ProjectReference itemData) {
            TableCellElement label = Elements.createTDElement();
            label.setInnerHTML(itemData.getName());
            itemElement.appendChild(label);
            UIObject.ensureDebugId((com.google.gwt.dom.client.Element)itemElement, "file-openProject-" + itemData);
        }

        @Override
        public Element createElement() {
            return Elements.createTRElement();
        }
    };
    private SimpleList.ListEventDelegate<ProjectReference> listDelegate     = new SimpleList.ListEventDelegate<ProjectReference>() {
        public void onListItemClicked(Element itemElement, ProjectReference itemData) {
            list.getSelectionModel().setSelectedItem(itemData);
            delegate.selectedProject(itemData);
        }

        public void onListItemDoubleClicked(Element listItemBase, ProjectReference itemData) {
        }
    };

    /**
     * Create view.
     *
     * @param resources
     */
    @Inject
    protected OpenProjectViewImpl(com.codenvy.ide.Resources resources, CoreLocalizationConstant localization) {
        this.res = resources;
        this.localization = localization;

        Widget widget = uiBinder.createAndBindUi(this);

        TableElement tableElement = Elements.createTableElement();
        tableElement.setAttribute("style", "width: 100%");
        list = SimpleList.create((SimpleList.View)tableElement, res.defaultSimpleListCss(), listItemRenderer, listDelegate);
        this.listPanel.add(list);

        this.setTitle("Open Project");
        this.setWidget(widget);
        createButtons();
    }

    private void createButtons() {
        btnOpen = createButton(localization.open(), "file-openProject-open", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onOpenClicked();
            }
        });

        btnCancel = createButton(localization.cancel(), "file-openProject-cancel", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });

        getFooter().add(btnCancel);
        getFooter().add(btnOpen);
    }

    @Override
    protected void onClose() {
        delegate.onCancelClicked();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setOpenButtonEnabled(boolean isEnabled) {
        btnOpen.setEnabled(isEnabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setProjects(Array<ProjectReference> projects) {
        list.render(projects);
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.show();
    }

    interface OpenProjectViewImplUiBinder extends UiBinder<Widget, OpenProjectViewImpl> {
    }
}