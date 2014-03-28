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

import com.codenvy.ide.api.mvp.View;

import javax.validation.constraints.NotNull;

/**
 * The view of {@link MavenBuilderPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface MavenBuildView extends View<MavenBuildView.ActionDelegate> {

    /** Needs for delegate some function into Commit view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Commit button. */
        void onStartBuildClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

    }

    /** @return entered buildCommand */
    @NotNull
    String getBuildCommand();

    /**
     * Set content into buildCommand field.
     *
     * @param message
     *         text what need to insert
     */
    void setBuildCommand(@NotNull String message);

    /**
     * Change the enable state of the maven command field.
     *
     * @param enable
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableMavenCommandField(boolean enable);


    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();

    /** Performs then user select skip test. */
    boolean isSkipTestSelected();

}