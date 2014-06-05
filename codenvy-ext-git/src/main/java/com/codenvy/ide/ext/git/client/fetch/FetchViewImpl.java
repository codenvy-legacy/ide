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
package com.codenvy.ide.ext.git.client.fetch;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitResources;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

/**
 * The implementation of {@link FetchView}.
 * 
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class FetchViewImpl extends Window implements FetchView {
    interface FetchViewImplUiBinder extends UiBinder<Widget, FetchViewImpl> {
    }

    private static FetchViewImplUiBinder ourUiBinder = GWT.create(FetchViewImplUiBinder.class);

    @UiField
    CheckBox                             removeDeletedRefs;
    @UiField
    CheckBox                             fetchAllBranches;
    @UiField
    ListBox                              repository;
    @UiField
    ListBox                              localBranch;
    @UiField
    ListBox                              remoteBranch;
    Button                               btnFetch;
    Button                               btnCancel;
    @UiField(provided = true)
    final GitResources                   res;
    @UiField(provided = true)
    final GitLocalizationConstant        locale;
    private ActionDelegate               delegate;

    /**
     * Create view.
     * 
     * @param resources
     * @param locale
     */
    @Inject
    protected FetchViewImpl(GitResources resources, GitLocalizationConstant locale) {
        this.res = resources;
        this.locale = locale;

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setTitle(locale.fetchTitle());
        this.setWidget(widget);
        
        btnCancel = createButton(locale.buttonCancel(), "git-remotes-fetch-cancel", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });
        getFooter().add(btnCancel);

        btnFetch = createButton(locale.buttonFetch(), "git-remotes-fetch-fetch", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onFetchClicked();
            }
        });
        getFooter().add(btnFetch);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRemoveDeletedRefs() {
        return removeDeletedRefs.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setRemoveDeleteRefs(boolean isRemoveDeleteRefs) {
        removeDeletedRefs.setValue(isRemoveDeleteRefs);
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getRepositoryName() {
        int index = repository.getSelectedIndex();
        return index != -1 ? repository.getItemText(index) : "";
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getRepositoryUrl() {
        int index = repository.getSelectedIndex();
        return repository.getValue(index);
    }

    /** {@inheritDoc} */
    @Override
    public void setRepositories(@NotNull Array<Remote> repositories) {
        this.repository.clear();
        for (int i = 0; i < repositories.size(); i++) {
            Remote repository = repositories.get(i);
            this.repository.addItem(repository.getName(), repository.getUrl());
        }
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getLocalBranch() {
        int index = localBranch.getSelectedIndex();
        return index != -1 ? localBranch.getItemText(index) : "";
    }

    /** {@inheritDoc} */
    @Override
    public void setLocalBranches(@NotNull Array<String> branches) {
        this.localBranch.clear();
        for (int i = 0; i < branches.size(); i++) {
            String branch = branches.get(i);
            this.localBranch.addItem(branch);
        }
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getRemoteBranch() {
        int index = remoteBranch.getSelectedIndex();
        return index != -1 ? remoteBranch.getItemText(index) : "";
    }

    /** {@inheritDoc} */
    @Override
    public void setRemoteBranches(@NotNull Array<String> branches) {
        this.remoteBranch.clear();
        for (int i = 0; i < branches.size(); i++) {
            String branch = branches.get(i);
            this.remoteBranch.addItem(branch);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableFetchButton(boolean enabled) {
        btnFetch.setEnabled(enabled);
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

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("fetchAllBranches")
    public void onValueChanged(ValueChangeEvent<Boolean> event) {
        delegate.onValueChanged();
    }
    
    @UiHandler("remoteBranch")
    public void onValueChanged(ChangeEvent event) {
        delegate.onRemoteBranchChanged();;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFetchAllBranches() {
        return fetchAllBranches.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setFetchAllBranches(boolean isFetchAllBranches) {
        fetchAllBranches.setValue(isFetchAllBranches, true);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableRemoteBranchField(boolean enabled) {
        remoteBranch.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableLocalBranchField(boolean enabled) {
        localBranch.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public void selectLocalBranch(String branch) {
        for (int i = 0; i < localBranch.getItemCount(); i++) {
            if (localBranch.getValue(i).equals(branch)) {
                localBranch.setItemSelected(i, true);
                break;
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void selectRemoteBranch(String branch) {
        for (int i = 0; i < remoteBranch.getItemCount(); i++) {
            if (remoteBranch.getValue(i).equals(branch)) {
                remoteBranch.setItemSelected(i, true);
                delegate.onRemoteBranchChanged();
                break;
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void onClose() {
    }
}
