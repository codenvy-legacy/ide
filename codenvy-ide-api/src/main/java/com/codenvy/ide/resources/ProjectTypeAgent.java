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
package com.codenvy.ide.resources;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.resources.client.ImageResource;

/**
 * Provides a way to register a new project type.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface ProjectTypeAgent {
    /**
     * Registers new project type.
     *
     * @param typeName
     * @param title
     * @param icon
     */
    void register(@NotNull String typeName, @NotNull String title, @Nullable ImageResource icon, @NotNull String primaryNature,
                  @NotNull JsonArray<String> secondaryNature);
}