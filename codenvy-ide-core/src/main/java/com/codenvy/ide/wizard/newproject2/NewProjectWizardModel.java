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
import com.codenvy.ide.wizard.newproject2.pages.template.TemplatePageFactory;
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
    private TemplatePageFactory                                      templatePage;
    private UpdateDelegate                                           delegate;
    private Map<PaaS, JsonArray<Provider<? extends WizardPage>>>     paasPages;
    private Map<Template, JsonArray<Provider<? extends WizardPage>>> templatePages;
    private JsonArray<WizardPage>                                    flippedPages;
    private WizardContext                                            wizardContext;

    private int index;

    @Inject
    public NewProjectWizardModel(Provider<NewProjectPagePresenter> newProjectPage,
                                 TemplatePageFactory templatePage) {
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
        index = 0;
        return addPage(newProjectPage);
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public WizardPage flipToNext() {
        index++;

        if (index == 1) {
            TemplatePagePresenter page = templatePage.create(wizardContext);
            page.setUpdateDelegate(delegate);
            flippedPages.add(page);
        } else if (index == 2) {
            JsonArray<Provider<? extends WizardPage>> templatePageProviders = templatePages.get(wizardContext.getData(TEMPLATE));
            if (templatePageProviders != null) {
                for (Provider<? extends WizardPage> provider : templatePageProviders.asIterable()) {
                    addPage(provider);
                }
            }

            JsonArray<Provider<? extends WizardPage>> paasPageProviders = paasPages.get(wizardContext.getData(PAAS));
            if (paasPageProviders != null) {
                for (Provider<? extends WizardPage> provider : paasPageProviders.asIterable()) {
                    addPage(provider);
                }
            }
        }

        WizardPage page;
        do {
            page = flippedPages.get(index);
        } while (index < flippedPages.size() && !page.canSkip());

        return page;
    }

    private WizardPage addPage(Provider<? extends WizardPage> provider) {
        WizardPage page = provider.get();
        page.setUpdateDelegate(delegate);
        page.setContext(wizardContext);
        flippedPages.add(page);
        return page;
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public WizardPage flipToPrevious() {
        if (!flippedPages.isEmpty() && flippedPages.size() <= 2) {
            flippedPages.remove(index);
        } else if (flippedPages.size() == 3) {
            for (int i = flippedPages.size() - 1; i >= 2; i--) {
                flippedPages.remove(i);
            }
        }

        index--;
        return flippedPages.get(index);
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext() {
        // TODO
//        return isLastPage();
        return !isLastPage();
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasPrevious() {
        return flippedPages.size() > 1;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canFinish() {
        // TODO need to check it
        boolean isCompleted = true;
        for (WizardPage page : flippedPages.asIterable()) {
            isCompleted &= page.isCompleted();
        }

        return isLastPage() && isCompleted;
    }

    private boolean isLastPage() {
        int pageCount = 0;
        JsonArray<Provider<? extends WizardPage>> paasPages = this.paasPages.get(wizardContext.getData(PAAS));
        if (paasPages != null) {
            for (Provider<? extends WizardPage> provider : paasPages.asIterable()) {
                WizardPage page = provider.get();
                pageCount += (page.canSkip() ? 0 : 1);
            }
        }

        JsonArray<Provider<? extends WizardPage>> templatePages = this.templatePages.get(wizardContext.getData(TEMPLATE));
        if (templatePages != null) {
            for (Provider<? extends WizardPage> provider : templatePages.asIterable()) {
                WizardPage page = provider.get();
                pageCount += (page.canSkip() ? 0 : 1);
            }
        }

        TemplatePagePresenter page = templatePage.create(wizardContext);

        pageCount += (page.canSkip() ? 0 : 1);

        return index == pageCount;
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