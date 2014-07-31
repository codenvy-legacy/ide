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
package com.codenvy.ide.wizard.newproject;

import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.api.paas.PaaSAgent;
import com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard;
import com.codenvy.ide.api.ui.wizard.paas.AbstractPaasPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * The implementation of {@link PaaSAgent}.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class PaaSAgentImpl implements PaaSAgent {
    private static final String NONE_PAAS_ID = "None";
    private final StringMap<PaaS>  registeredPaaS;
    private       NewProjectWizard newProjectWizard;

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
                         @NotNull Array<String> projectTypeIds,
                         @NotNull Array<Provider<? extends AbstractPaasPage>> wizardPages,
                         boolean provideTemplate) {
        if (registeredPaaS.containsKey(id)) {
            Window.alert("PaaS with " + id + " id already exists");
            return;
        }

        PaaS paas = new PaaS(id, title, image, projectTypeIds, provideTemplate);
        registeredPaaS.put(id, paas);
//        for (Provider<? extends AbstractPaasPage> provider : wizardPages.asIterable()) {
//            newProjectWizard.addPaaSPage(provider);
//        }
    }

    /** @return all available PaaSes. */
    public Array<PaaS> getPaaSes() {
        return registeredPaaS.getValues();
    }

    private class NonePaaS extends PaaS {
        public NonePaaS(@NotNull String id, @NotNull String title, @Nullable ImageResource image) {
            super(id, title, image, Collections.<String>createArray(), false);
        }

        /** {@inheritDoc} */
        @Override
        public boolean isAvailable(@NotNull String primaryNature) {
            return true;
        }
    }
}