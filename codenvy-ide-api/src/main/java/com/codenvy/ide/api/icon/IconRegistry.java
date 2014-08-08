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
package com.codenvy.ide.api.icon;

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
