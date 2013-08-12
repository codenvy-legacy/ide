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
package com.codenvy.ide.api.ui.wizard.newfile;

import com.codenvy.ide.api.mvp.View;

/**
 * Interface of NewGenericFile view
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface NewGenericFilePageView extends View<NewGenericFilePageView.ActionDelegate> {
    /**
     * Returns file name.
     *
     * @return
     */
    String getFileName();

    /**
     * Sets file name.
     *
     * @param fileName
     */
    void setFileName(String fileName);

    /** Needs for delegate some function into NewGenericFile view. */
    public interface ActionDelegate {
        /** Checks entered information(file name and etc). */
        void onValueChanged();
    }
}