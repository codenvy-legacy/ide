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

import com.codenvy.ide.api.ui.wizard.WizardAgent;
import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.wizard.newresource.NewResourceWizardData;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.inject.Singleton;

/**
 * Implements register wizards and returns all available wizard.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class WizardAgentImpl implements WizardAgent {
    private final JsonArray<NewResourceWizardData> newResourceWizardDatas;

    /** Create WizardAgent */
    @Inject
    protected WizardAgentImpl() {
        newResourceWizardDatas = JsonCollections.createArray();
    }

    /** {@inheritDoc} */
    @Override
    public void registerNewResourceWizard(String category, String title, ImageResource icon,
                                          Provider<? extends WizardPagePresenter> wizardPage) {
        NewResourceWizardData newResourceWizardData = new NewResourceWizardData(title, category, icon, wizardPage);
        newResourceWizardDatas.add(newResourceWizardData);
    }

    /**
     * Returns all registered wizards for creating new resource.
     *
     * @return
     */
    public JsonArray<NewResourceWizardData> getNewResourceWizards() {
        return newResourceWizardDatas;
    }
}