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
package com.codenvy.ide.core;

import com.codenvy.ide.api.ui.Icon;
import com.codenvy.ide.api.ui.IconRegistry;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link IconRegistry}.
 *
 * @author Vitaly Parfonov
 * @author Artem Zatsarynnyy
 */
@Singleton
public class IconRegistryImpl implements IconRegistry {

    private Map<String, Icon> icons = new HashMap<>();

    @Override
    public void registerIcon(Icon icon) {
        icons.put(icon.getId(), icon);
    }

    @Override
    public Icon getIcon(String id) {
        Icon icon = icons.get(id);
        if (icon == null) {
            final String prefix = id.split("\\.")[0];
            final String defaultIconId = id.replaceFirst(prefix, "default");
            icon = icons.get(defaultIconId);
            if (icon == null) {
                icon = getGenericIcon();
            }
        }
        return icon;
    }

    @Override
    public Icon getIconIfExist(String id) {
        return icons.get(id);
    }

    @Override
    public Icon getGenericIcon() {
        return icons.get("default");
    }
}
