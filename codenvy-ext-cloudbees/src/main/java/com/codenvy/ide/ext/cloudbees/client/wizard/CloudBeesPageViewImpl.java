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
package com.codenvy.ide.ext.cloudbees.client.wizard;

import com.codenvy.ide.ext.cloudbees.client.CloudBeesResources;
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
 * The implementation of {@link CloudBeesPageView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class CloudBeesPageViewImpl extends Composite implements CloudBeesPageView {
    interface CloudBeesPageViewImplUiBinder extends UiBinder<Widget, CloudBeesPageViewImpl> {
    }

    private static CloudBeesPageViewImplUiBinder ourUiBinder = GWT.create(CloudBeesPageViewImplUiBinder.class);

    @UiField
    ListBox targetField;
    @UiField
    TextBox nameField;
    @UiField
    TextBox urlField;
    @UiField(provided = true)
    final   CloudBeesResources res;
    private ActionDelegate     delegate;

    /**
     * Create view.
     *
     * @param resources
     */
    @Inject
    protected CloudBeesPageViewImpl(CloudBeesResources resources) {
        this.res = resources;

        initWidget(ourUiBinder.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
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
    public String getDomain() {
        int selectedIndex = targetField.getSelectedIndex();
        return selectedIndex != -1 ? targetField.getItemText(selectedIndex) : "";
    }

    /** {@inheritDoc} */
    @Override
    public void setDomain(String domain) {
        int count = this.targetField.getItemCount();
        boolean isItemFound = false;

        // Looks up entered server into available list of servers
        int i = 0;
        while (i < count && !isItemFound) {
            String item = this.targetField.getItemText(i);
            isItemFound = item.equals(domain);

            i++;
        }

        // If item was found then it will be shown otherwise do nothing
        if (isItemFound) {
            this.targetField.setSelectedIndex(i - 1);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setDomainValues(JsonArray<String> servers) {
        targetField.clear();
        for (int i = 0; i < servers.size(); i++) {
            targetField.addItem(servers.get(i));
        }
    }

    @UiHandler("nameField")
    void onNameFieldKeyUp(KeyUpEvent event) {
        delegate.onValueChanged();
    }

    @UiHandler("targetField")
    void onTargetFieldChange(ChangeEvent event) {
        delegate.onValueChanged();
    }
}