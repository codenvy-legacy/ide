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
package com.codenvy.ide.extension.maven.client.build;

import com.codenvy.ide.api.mvp.View;
import com.google.gwt.event.logical.shared.ValueChangeEvent;

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

        void onSkipTestValueChange(ValueChangeEvent<Boolean> event);

        void onUpdateSnapshotValueChange(ValueChangeEvent<Boolean> event);

        void onOfflineValueChange(ValueChangeEvent<Boolean> event);

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


    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();

    /** Performs then user select skip test. */
    boolean isSkipTestSelected();

}