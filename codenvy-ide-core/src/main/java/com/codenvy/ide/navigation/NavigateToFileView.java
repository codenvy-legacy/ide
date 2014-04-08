/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.navigation;

import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * View for file navigation (find file by name and open it).
 *
 * @author Ann Shumilova
 * @author Artem Zatsarynnyy
 */
public interface NavigateToFileView extends View<NavigateToFileView.ActionDelegate> {
    /** Needs for delegate some function into NavigateToFile view. */
    public interface ActionDelegate {
        /**
         * Called when suggestions are requested.
         *
         * @param query
         *         query string
         * @param callback
         *         callback
         */
        void onRequestSuggestions(String query, AsyncCallback<Array<String>> callback);

        /** Called when file selected. */
        void onFileSelected();
    }

    /**
     * Returns chosen item's path.
     *
     * @return chosen item's path
     */
    String getItemPath();

    /** Put cursor in input field. */
    void focusInput();

    /** Clear input. */
    void clearInput();

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}
