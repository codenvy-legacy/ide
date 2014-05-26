/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 * [2012] - [$today.year] Codenvy, S.A. 
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
package com.codenvy.ide.api.ui;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Client-side singleton component that provides possibility to define icons for UI in extensions.
 * On IDE start it populated by application-scope 'default' icons and 'generic' icon (e.g. Codenvy logo).
 *
 * @author Vitaly Parfonov
 * @author Artem Zatsarynnyy
 */
public interface IconRegistry {

    /**
     * Register {@link Icon}.
     * If icon with the same id previously registered,
     * the old icon is replaced by the specified icon.
     *
     * @param icon
     *         icon to be registered
     */
    void registerIcon(Icon icon);

    /**
     * Returns {@link Icon} by its id.
     * If no such icon is registered, it returns the same named 'default' icon.
     * If it also not found, returns 'generic' icon.
     *
     * @param id
     *         icon id
     * @return registered icon or the same named "default" icon or "generic" icon
     */
    @NotNull
    Icon getIcon(String id);

    /**
     * Returns {@link Icon} by its id, or {@code null} if no icon with the specified id.
     *
     * @param id
     *         icon id
     * @return registered icon, or {@code null} if found no icon with the specified id
     */
    @Nullable
    Icon getIconIfExist(String id);

    /**
     * Returns 'generic' icon (e.g. Codenvy logo).
     * May be useful when no icon found by its id.
     *
     * @return 'generic' icon
     */
    Icon getGenericIcon();
}
