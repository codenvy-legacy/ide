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
package com.codenvy.ide.api.ui.wizard.newproject;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.api.template.Template;
import com.codenvy.ide.api.ui.wizard.DefaultWizard;
import com.codenvy.ide.api.ui.wizard.WizardContext;
import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The wizard for creating new project. A user pre-defines behavior of the wizard already from the start page by choosing a technology and
 * PaaS. Depending on the PaaS choice made, the wizard shows different pages after 'choose a template' page.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class NewProjectWizard2 extends DefaultWizard {
    public static final WizardContext.Key<PaaS>     PAAS     = new WizardContext.Key<PaaS>("PaaS");
    public static final WizardContext.Key<Template> TEMPLATE = new WizardContext.Key<Template>("Template");

    private int chooseTemplate;
    private int lastTemplatePage;

    /**
     * Create new project wizard.
     *
     * @param notificationManager
     */
    @Inject
    public NewProjectWizard2(NotificationManager notificationManager) {
        super(notificationManager, "New project");
        chooseTemplate = 1;
        lastTemplatePage = 1;
    }

    /**
     * Add a page after the first page in the wizard, namely between the start page and template choosing page.
     *
     * @param wizardPage
     *         page that need to add
     */
    public void addPageAfterFirst(@NotNull WizardPage wizardPage) {
        addPage(wizardPage, chooseTemplate++, false);
    }

    /**
     * Add a page after 'choose template' page.
     *
     * @param wizardPage
     *         page that need to add
     */
    public void addPageAfterChooseTemplate(@NotNull WizardPage wizardPage) {
        addPage(wizardPage, chooseTemplate + 1, false);
        lastTemplatePage++;
    }

    /**
     * Add a page before paas pages.
     *
     * @param wizardPage
     *         page that need to add
     */
    public void addPageBeforePaas(@NotNull WizardPage wizardPage) {
        addPage(wizardPage, lastTemplatePage + 1, false);
    }
}