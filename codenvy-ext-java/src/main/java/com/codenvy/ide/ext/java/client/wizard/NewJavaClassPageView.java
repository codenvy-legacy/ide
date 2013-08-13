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
package com.codenvy.ide.ext.java.client.wizard;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.json.JsonArray;


/**
 * View for new Java class wizard.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface NewJavaClassPageView extends View<NewJavaClassPageView.ActionDelegate> {
    /** Action delegate for new Java class wizard */
    public interface ActionDelegate {

        /**
         * Selected parent changed.
         *
         * @param index
         *         the parent index
         */
        void parentChanged(int index);

        /** New type name changed, validate it. */
        void checkTypeName();
    }

    /**
     * Get new class name
     *
     * @return new class name
     */
    String getClassName();

    /**
     * Get new class type (class, interface, enum, annotation)
     *
     * @return new class type
     */
    String getClassType();

    /**
     * Set new class types
     *
     * @param classTypes
     *         the array of class types
     */
    void setClassTypes(JsonArray<String> classTypes);

    /**
     * Set parent names for new type
     *
     * @param parentNames
     *         the array of names
     */
    void setParents(JsonArray<String> parentNames);

    /**
     * Select parent by index
     *
     * @param index
     *         of the parent in the list
     */
    void selectParent(int index);

    /** Disable all ui components. */
    void disableAllUi();
}
