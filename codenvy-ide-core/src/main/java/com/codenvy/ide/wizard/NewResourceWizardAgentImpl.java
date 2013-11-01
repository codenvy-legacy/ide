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
import com.codenvy.ide.api.ui.wizard.newresource.NewResourceAgent;
import com.codenvy.ide.api.ui.wizard.newresource.ResourceData;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;

import javax.inject.Singleton;

/**
 * The implementation of {@link NewResourceAgent}. Also returning all registered resources.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class NewResourceWizardAgentImpl implements NewResourceAgent {
    private final JsonStringMap<ResourceData> resources;

    /** Create agent */
    @Inject
    protected NewResourceWizardAgentImpl() {
        resources = JsonCollections.createStringMap();
    }

    /** {@inheritDoc} */
    @Override
    public void register(@NotNull ResourceData resource) {
        String id = resource.getId();
        if (resources.containsKey(id)) {
            Window.alert("Resource with " + id + " id already exists");
            return;
        }

        resources.put(id, resource);
    }

    /** @return all registered resources */
    public JsonArray<ResourceData> getResources() {
        return resources.getValues();
    }
}