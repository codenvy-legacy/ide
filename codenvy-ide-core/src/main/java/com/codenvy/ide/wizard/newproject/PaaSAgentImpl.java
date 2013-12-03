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
package com.codenvy.ide.wizard.newproject;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.api.paas.PaaSAgent;
import com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard;
import com.codenvy.ide.api.ui.wizard.paas.AbstractPaasPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.JsonStringMap;
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
    private static final String NONE_PAAS_ID = "None";

    private class NonePaaS extends PaaS {
        public NonePaaS(@NotNull String id, @NotNull String title, @Nullable ImageResource image) {
            super(id, title, image, Collections.<Array<String>>createStringMap(), false);
        }

        /** {@inheritDoc} */
        @Override
        public boolean isAvailable(@NotNull String primaryNature, @NotNull Array<String> secondaryNature) {
            return true;
        }
    }

    private       NewProjectWizard    newProjectWizard;
    private final JsonStringMap<PaaS> registeredPaaS;

    /** Create agent. */
    @Inject
    protected PaaSAgentImpl(NewProjectWizard newProjectWizard) {
        this.newProjectWizard = newProjectWizard;
        this.registeredPaaS = Collections.createStringMap();
        registeredPaaS.put(NONE_PAAS_ID, new NonePaaS(NONE_PAAS_ID, NONE_PAAS_ID, null));
    }

    /** {@inheritDoc} */
    @Override
    public void register(@NotNull String id,
                         @NotNull String title,
                         @Nullable ImageResource image,
                         @NotNull JsonStringMap<Array<String>> natures,
                         @NotNull Array<Provider<? extends AbstractPaasPage>> wizardPages,
                         boolean provideTemplate) {
        if (registeredPaaS.containsKey(id)) {
            Window.alert("PaaS with " + id + " id already exists");
            return;
        }

        PaaS paas = new PaaS(id, title, image, natures, provideTemplate);
        registeredPaaS.put(id, paas);
        for (Provider<? extends AbstractPaasPage> provider : wizardPages.asIterable()) {
            newProjectWizard.addPaaSPage(provider);
        }
    }

    /** @return all available PaaSes. */
    public Array<PaaS> getPaaSes() {
        return registeredPaaS.getValues();
    }
}