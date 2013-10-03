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
package com.codenvy.ide.wizard.newproject2;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.api.template.Template;
import com.codenvy.ide.api.ui.wizard.WizardContext;
import com.codenvy.ide.api.ui.wizard.WizardModel;
import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.wizard.newproject.ProjectTypeData;
import com.codenvy.ide.wizard.newproject2.pages.start.NewProjectPagePresenter;
import com.codenvy.ide.wizard.newproject2.pages.template.TemplatePagePresenter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@Singleton
public class NewProjectWizardModel implements WizardModel {
    public static final WizardContext.Key<PaaS>            PAAS         = new WizardContext.Key<PaaS>("PaaS");
    public static final WizardContext.Key<Template>        TEMPLATE     = new WizardContext.Key<Template>("Template");
    public static final WizardContext.Key<ProjectTypeData> PROJECT_TYPE = new WizardContext.Key<ProjectTypeData>("Project type");

    private Provider<NewProjectPagePresenter>                        newProjectPage;
    private Provider<TemplatePagePresenter>                          templatePage;
    private UpdateDelegate                                           delegate;
    private Map<PaaS, JsonArray<Provider<? extends WizardPage>>>     paasPages;
    private Map<Template, JsonArray<Provider<? extends WizardPage>>> templatePages;
    private JsonArray<WizardPage>                                    flippedPages;
    private WizardContext                                            wizardContext;

    private int index;

    @Inject
    public NewProjectWizardModel(Provider<NewProjectPagePresenter> newProjectPage,
                                 Provider<TemplatePagePresenter> templatePage) {
        this.newProjectPage = newProjectPage;
        this.templatePage = templatePage;
        this.paasPages = new HashMap<PaaS, JsonArray<Provider<? extends WizardPage>>>();
        this.templatePages = new HashMap<Template, JsonArray<Provider<? extends WizardPage>>>();
        this.flippedPages = JsonCollections.createArray();
        this.wizardContext = new WizardContext();
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdateDelegate(@NotNull UpdateDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getTitle() {
        return "Create project";
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public WizardPage flipToFirst() {
        flippedPages.clear();
        WizardPage page = getPage(newProjectPage);
        flippedPages.add(page);
        index = 0;

        return page;
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public WizardPage flipToNext() {
        index++;

        if (index == 1) {
            flippedPages.add(getPage(templatePage));
        } else if (index == 2) {
            JsonArray<Provider<? extends WizardPage>> templatePageProviders = templatePages.get(wizardContext.getData(TEMPLATE));
            if (templatePageProviders != null) {
                for (Provider<? extends WizardPage> provider : templatePageProviders.asIterable()) {
                    flippedPages.add(getPage(provider));
                }
            }

            JsonArray<Provider<? extends WizardPage>> paasPageProviders = paasPages.get(wizardContext.getData(PAAS));
            if (paasPageProviders != null) {
                for (Provider<? extends WizardPage> provider : paasPageProviders.asIterable()) {
                    flippedPages.add(getPage(provider));
                }
            }
        }

        return flippedPages.get(index);
    }

    private WizardPage getPage(Provider<? extends WizardPage> provider) {
        WizardPage page = provider.get();
        page.setUpdateDelegate(delegate);
        page.setContext(wizardContext);
        return page;
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public WizardPage flipToPrevious() {
        index--;

        if (!flippedPages.isEmpty() && flippedPages.size() < 2) {
            return flippedPages.remove(index);
        } else {
            if (flippedPages.size() == 2) {
                for (int i = flippedPages.size() - 1; i >= 2; i--) {
                    flippedPages.remove(i);
                }
            }

            return flippedPages.get(index);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext() {
        // TODO
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasPrevious() {
        return !flippedPages.isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public boolean canFinish() {
        boolean isComplited = true;
        for (WizardPage page : flippedPages.asIterable()) {
            isComplited &= page.isCompleted();
        }
        return flippedPages.size() >= 2 && isComplited;
    }

    /** {@inheritDoc} */
    @Override
    public void onCancel() {
        // TODO may be not needed
    }

    /** {@inheritDoc} */
    @Override
    public void onFinish() {
        // TODO

    }

    public void addPaaSPages(@NotNull PaaS paas, @NotNull JsonArray<Provider<? extends WizardPage>> pages) {
        paasPages.put(paas, pages);
    }

    public void addTemplatePages(@NotNull Template template, @NotNull JsonArray<Provider<? extends WizardPage>> pages) {
        templatePages.put(template, pages);
    }
}