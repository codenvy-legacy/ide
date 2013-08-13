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
package com.codenvy.ide.ext.appfog.client.wizard;

import com.codenvy.ide.ext.appfog.client.AppfogResources;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link AppFogPageView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class AppFogPageViewImpl extends Composite implements AppFogPageView {
    interface AppFogPageViewImplUiBinder extends UiBinder<Widget, AppFogPageViewImpl> {
    }

    private static AppFogPageViewImplUiBinder ourUiBinder = GWT.create(AppFogPageViewImplUiBinder.class);

    @UiField
    TextBox targetField;
    @UiField
    TextBox nameField;
    @UiField
    TextBox urlField;
    @UiField
    ListBox infraField;
    @UiField(provided = true)
    final   AppfogResources res;
    private ActionDelegate  delegate;

    /**
     * Create view.
     *
     * @param resources
     */
    @Inject
    protected AppFogPageViewImpl(AppfogResources resources) {
        this.res = resources;

        initWidget(ourUiBinder.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return nameField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setName(String name) {
        nameField.setText(name);
    }

    /** {@inheritDoc} */
    @Override
    public String getUrl() {
        return urlField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setUrl(String url) {
        urlField.setText(url);
    }

    /** {@inheritDoc} */
    @Override
    public String getTarget() {
        return targetField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setTarget(String target) {
        targetField.setText(target);
    }

    /** {@inheritDoc} */
    @Override
    public String getInfra() {
        int selectedIndex = infraField.getSelectedIndex();
        return selectedIndex != -1 ? infraField.getItemText(selectedIndex) : "";
    }

    /** {@inheritDoc} */
    @Override
    public void setInfra(String infra) {
        int count = this.infraField.getItemCount();
        boolean isItemFound = false;

        // Looks up entered server into available list of servers
        int i = 0;
        while (i < count && !isItemFound) {
            String item = this.infraField.getItemText(i);
            isItemFound = item.equals(infra);

            i++;
        }

        // If item was found then it will be shown otherwise do nothing
        if (isItemFound) {
            this.infraField.setSelectedIndex(i - 1);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setInfras(JsonArray<String> infras) {
        infraField.clear();
        for (int i = 0; i < infras.size(); i++) {
            infraField.addItem(infras.get(i));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("urlField")
    void onUrlFieldKeyUp(KeyUpEvent event) {
        delegate.onUrlChanged();
    }

    @UiHandler("nameField")
    void onNameFieldKeyUp(KeyUpEvent event) {
        delegate.onNameChanged();
    }

    @UiHandler("infraField")
    void onInfraFieldChange(ChangeEvent event) {
        delegate.onInfraChanged();
    }
}