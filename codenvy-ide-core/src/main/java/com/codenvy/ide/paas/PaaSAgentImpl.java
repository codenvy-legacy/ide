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
package com.codenvy.ide.paas;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.api.paas.PaaSAgent;
import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;


/**
 * The implementation of {@link PaaSAgent}.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class PaaSAgentImpl implements PaaSAgent {
    private class NonePaaS extends PaaS {
        public NonePaaS(@NotNull String id, @NotNull String title, @Nullable ImageResource image) {
            super(id, title, image, JsonCollections.<JsonArray<String>>createStringMap(), false);
        }

        /** {@inheritDoc} */
        @Override
        public boolean isAvailable(@NotNull String primaryNature, @NotNull JsonArray<String> secondaryNature) {
            return true;
        }
    }

    private       NewProjectWizard newProjectWizard;
    private final JsonArray<PaaS>  registeredPaaS;

    /** Create agent. */
    @Inject
    protected PaaSAgentImpl(NewProjectWizard newProjectWizard) {
        this.newProjectWizard = newProjectWizard;
        this.registeredPaaS = JsonCollections.createArray();
        registeredPaaS.add(new NonePaaS("None", "None", null));
    }

    /** {@inheritDoc} */
    @Override
    public void register(@NotNull String id,
                         @NotNull String title,
                         @Nullable ImageResource image,
                         @NotNull JsonStringMap<JsonArray<String>> natures,
                         @NotNull JsonArray<Provider<? extends WizardPage>> wizardPages,
                         boolean provideTemplate) {
        if (isIdExist(id)) {
            Window.alert("PaaS with " + id + " id already exists");
        }

        PaaS paas = new PaaS(id, title, image, natures, provideTemplate);
        registeredPaaS.add(paas);
        if (wizardPages != null) {
            for (Provider<? extends WizardPage> provider : wizardPages.asIterable()) {
                newProjectWizard.addPage(provider);
            }
        }
    }

    /**
     * Returns whether the paas with this id already exists.
     *
     * @return <code>true</code> if the paas already exists, and <code>false</code> if it doesn't
     */
    private boolean isIdExist(@NotNull String id) {
        for (PaaS paas : registeredPaaS.asIterable()) {
            if (paas.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /** @return all available PaaSes. */
    public JsonArray<PaaS> getPaaSes() {
        return registeredPaaS;
    }
}