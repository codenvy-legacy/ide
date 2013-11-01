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
package com.codenvy.ide.wizard;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.api.ui.wizard.newresource.CreateResourceHandler;
import com.codenvy.ide.api.ui.wizard.newresource.NewResourceAgent;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.wizard.newresource.ResourceData;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;

import javax.inject.Singleton;

/**
 * Implements register wizards and returns all available wizard.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class NewResourceWizardAgentImpl implements NewResourceAgent {
    private final JsonStringMap<ResourceData> newResourceWizardDatas;

    /** Create NewResourceAgent */
    @Inject
    protected NewResourceWizardAgentImpl() {
        newResourceWizardDatas = JsonCollections.createStringMap();
    }

    /** {@inheritDoc} */
    @Override
    public void register(@NotNull String id,
                         @NotNull String title,
                         @Nullable ImageResource icon,
                         @Nullable String extension,
                         @NotNull CreateResourceHandler handler) {
        if (newResourceWizardDatas.containsKey(id)) {
            Window.alert("Resource with " + id + " id already exists");
            return;
        }

        ResourceData newResourceData = new ResourceData(id, title, icon, extension, handler);
        newResourceWizardDatas.put(id, newResourceData);
    }

    /** @return all registered wizards for creating new resource */
    public JsonArray<ResourceData> getNewResourceWizards() {
        return newResourceWizardDatas.getValues();
    }
}