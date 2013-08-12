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

import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.api.paas.PaaSAgent;
import com.codenvy.ide.api.ui.preferences.PreferencesPagePresenter;
import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.google.gwt.resources.client.ImageResource;
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
    private final JsonArray<PaaS> registeredPaaS;
    private       PaaS            selectedPaaS;

    /** Create agent. */
    @Inject
    protected PaaSAgentImpl() {
        this.registeredPaaS = JsonCollections.createArray();
    }

    /** {@inheritDoc} */
    @Override
    public void registerPaaS(String id, String title, ImageResource image, JsonArray<String> requiredTypes,
                             Provider<? extends WizardPagePresenter> wizardPage, PreferencesPagePresenter preferencePage) {
        PaaS paas = new PaaS(id, title, image, requiredTypes, wizardPage);
        registeredPaaS.add(paas);

        // TODO preference page
    }

    /** {@inheritDoc} */
    @Override
    public PaaS getSelectedPaaS() {
        return selectedPaaS;
    }

    /**
     * Sets selected PaaS.
     *
     * @param paas
     */
    public void setSelectedPaaS(PaaS paas) {
        selectedPaaS = paas;
    }

    /**
     * Returns all available PaaSes.
     *
     * @return
     */
    public JsonArray<PaaS> getPaaSes() {
        return registeredPaaS;
    }
}