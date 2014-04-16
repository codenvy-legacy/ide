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
package com.codenvy.ide.extension.maven.client.build;

import com.codenvy.ide.extension.maven.client.MavenLocalizationConstant;
import com.codenvy.ide.extension.maven.client.MavenResources;
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

/**
 * The implementation of {@link MavenBuildView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class MavenBuildViewImpl extends Window implements MavenBuildView {
    @Override
    protected void onClose() {

    }

    interface MavenBuildViewImplUiBinder extends UiBinder<Widget, MavenBuildViewImpl> {
    }

    private static MavenBuildViewImplUiBinder ourUiBinder = GWT.create(MavenBuildViewImplUiBinder.class);

    @UiField
    CheckBox skipTest;
    @UiField
    CheckBox updateSnapshot;
    @UiField
    CheckBox offline;
    @UiField
    TextBox  buildCommand;
    Button   btnStartBuild;
    Button   btnCancel;
    @UiField(provided = true)
    final   MavenResources            res;
    @UiField(provided = true)
    final   MavenLocalizationConstant locale;
    private ActionDelegate            delegate;

    /**
     * Create view.
     *
     * @param resources
     * @param locale
     */
    @Inject
    protected MavenBuildViewImpl(MavenResources resources, MavenLocalizationConstant locale) {
        this.res = resources;
        this.locale = locale;
        Widget widget = ourUiBinder.createAndBindUi(this);
        this.setTitle(locale.mavenBuilder());
        this.setWidget(widget);
        skipTest.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                delegate.onSkipTestValueChange(event);
            }
        });

        updateSnapshot.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                delegate.onUpdateSnapshotValueChange(event);
            }
        });

        offline.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                delegate.onOfflineValueChange(event);
            }
        });
        createButtons();
    }
    
    private void createButtons(){
        btnCancel = createButton(locale.buttonCancel(), "project-buildWithOptions-cancel", new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });
        getFooter().add(btnCancel);
        
        btnStartBuild = createButton(locale.startBuild(), "project-buildWithOptions-startBuild", new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                delegate.onStartBuildClicked();
            }
        });
        getFooter().add(btnStartBuild);
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getBuildCommand() {
        return buildCommand.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setBuildCommand(@NotNull String buildCommand) {
        this.buildCommand.setText(buildCommand);
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

    @Override
    public boolean isSkipTestSelected() {
        return skipTest.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }
}