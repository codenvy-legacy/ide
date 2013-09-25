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
package com.codenvy.ide.openproject;

import elemental.html.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import com.codenvy.ide.Resources;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.ui.list.SimpleList;
import com.codenvy.ide.ui.list.SimpleList.View;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ScrollPanel;
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
public class OpenProjectViewImpl extends DialogBox implements OpenProjectView {
    interface OpenProjectViewImplUiBinder extends UiBinder<Widget, OpenProjectViewImpl> {
    }

    private static OpenProjectViewImplUiBinder uiBinder = GWT.create(OpenProjectViewImplUiBinder.class);

    @UiField
    com.codenvy.ide.ui.Button btnCancel;
    @UiField
    com.codenvy.ide.ui.Button btnOpen;
    @UiField
    ScrollPanel               listPanel;
    @UiField(provided = true)
    Resources                 res;
    private ActionDelegate     delegate;
    private SimpleList<String> list;
    private SimpleList.ListItemRenderer<String>  listItemRenderer = new SimpleList.ListItemRenderer<String>() {
        @Override
        public void render(Element itemElement, String itemData) {
            TableCellElement label = Elements.createTDElement();
            label.setInnerHTML(itemData);
            itemElement.appendChild(label);
        }

        @Override
        public Element createElement() {
            return Elements.createTRElement();
        }
    };
    private SimpleList.ListEventDelegate<String> listDelegate     = new SimpleList.ListEventDelegate<String>() {
        public void onListItemClicked(Element itemElement, String itemData) {
            list.getSelectionModel().setSelectedItem(itemData);
            delegate.selectedProject(itemData);
        }

        public void onListItemDoubleClicked(Element listItemBase, String itemData) {
        }
    };

    /**
     * Create view.
     *
     * @param resources
     */
    @Inject
    protected OpenProjectViewImpl(Resources resources) {
        this.res = resources;

        Widget widget = uiBinder.createAndBindUi(this);

        TableElement tableElement = Elements.createTableElement();
        tableElement.setAttribute("style", "width: 100%");
        list = SimpleList.create((View)tableElement, res.defaultSimpleListCss(), listItemRenderer, listDelegate);
        this.listPanel.add(list);

        this.setText("Open Project");
        this.setWidget(widget);
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
    public void setProjects(JsonArray<String> projects) {
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
        this.center();
        this.show();
    }

    @UiHandler("btnCancel")
    void onBtnCancelClick(ClickEvent event) {
        delegate.onCancelClicked();
    }

    @UiHandler("btnOpen")
    void onBtnOpenClick(ClickEvent event) {
        delegate.onOpenClicked();
    }
}