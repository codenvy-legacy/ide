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
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.paas.PaaS;
import com.codenvy.ide.template.Template;
import com.codenvy.ide.api.ui.wizard.Wizard;
import com.codenvy.ide.api.ui.wizard.WizardContext;
import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.wizard.newproject.pages.start.NewProjectPagePresenter;
import com.codenvy.ide.wizard.newproject.pages.template.TemplatePagePresenter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

/**
 * The wizard for creating new project.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class NewProjectWizard implements Wizard, WizardPage.CommitCallback {
    public static final WizardContext.Key<PaaS>            PAAS         = new WizardContext.Key<PaaS>("PaaS");
    public static final WizardContext.Key<Template>        TEMPLATE     = new WizardContext.Key<Template>("Template");
    public static final WizardContext.Key<ProjectTypeData> PROJECT_TYPE = new WizardContext.Key<ProjectTypeData>("Project type");
    private Provider<NewProjectPagePresenter>                        newProjectPage;
    private Provider<TemplatePagePresenter>                          templatePage;
    private UpdateDelegate                                           delegate;
    private Map<PaaS, JsonArray<Provider<? extends WizardPage>>>     paasPages;
    private Map<Template, JsonArray<Provider<? extends WizardPage>>> templatePages;
    private Map<Provider<? extends WizardPage>, WizardPage>          instancePages;
    private JsonArray<WizardPage>                                    flippedPages;
    private WizardContext                                            wizardContext;
    private NotificationManager                                      notificationManager;
    private int                                                      index;

    /**
     * Create presenter.
     *
     * @param newProjectPage
     * @param templatePage
     * @param notificationManager
     */
    @Inject
    public NewProjectWizard(Provider<NewProjectPagePresenter> newProjectPage,
                            Provider<TemplatePagePresenter> templatePage,
                            NotificationManager notificationManager) {
        this.newProjectPage = newProjectPage;
        this.templatePage = templatePage;
        this.notificationManager = notificationManager;
        this.paasPages = new HashMap<PaaS, JsonArray<Provider<? extends WizardPage>>>();
        this.templatePages = new HashMap<Template, JsonArray<Provider<? extends WizardPage>>>();
        this.instancePages = new HashMap<Provider<? extends WizardPage>, WizardPage>();
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
        wizardContext.clear();
        flippedPages.clear();
        instancePages.clear();
        index = 0;
        return addPage(newProjectPage);
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public WizardPage flipToNext() {
        addNextPages();

        while (++index < flippedPages.size()) {
            WizardPage page = flippedPages.get(index);
            if (!page.canSkip()) {
                return page;
            }
        }

        return null;
    }

    /** Add next pages if it is possible. */
    private void addNextPages() {
        PaaS paas = wizardContext.getData(PAAS);
        if (index == 0 && paas != null && paas.isProvideTemplate()) {
            addPages(paasPages.get(paas));
        } else if (index == 0 && paas != null && !paas.isProvideTemplate()) {
            WizardPage page = addPage(templatePage);
            if (page.canSkip()) {
                addPages(templatePages.get(wizardContext.getData(TEMPLATE)));
                addPages(paasPages.get(paas));
            }
        } else if (index == 1 && paas != null && !paas.isProvideTemplate()) {
            addPages(templatePages.get(wizardContext.getData(TEMPLATE)));
            addPages(paasPages.get(paas));
        }
    }

    /**
     * Add pages to list of available to flip pages.
     *
     * @param pages
     *         pages that need to add to list
     */

    private void addPages(@Nullable JsonArray<Provider<? extends WizardPage>> pages) {
        if (pages != null) {
            for (Provider<? extends WizardPage> provider : pages.asIterable()) {
                addPage(provider);
            }
        }
    }

    /**
     * Add page to list of available to flip pages.
     *
     * @param provider
     *         provider of page that need to add
     * @return wizard page that was added
     */
    private WizardPage addPage(@NotNull Provider<? extends WizardPage> provider) {
        WizardPage page = getInstance(provider);
        flippedPages.add(page);
        return page;
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public WizardPage flipToPrevious() {
        if (index <= 0) {
            return null;
        }

        boolean canSkip = true;
        while (canSkip && --index > 0) {
            WizardPage page = flippedPages.get(index);
            canSkip = page.canSkip();
        }

        if (index < 2) {
            for (int i = flippedPages.size() - 1; i > index; i--) {
                flippedPages.remove(i);
            }
        }

        return flippedPages.get(index);
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext() {
        return !isLastPage();
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canFinish() {
        for (WizardPage page : flippedPages.asIterable()) {
            if (!page.isCompleted()) {
                return false;
            }
        }
        return isLastPage();
    }

    /**
     * Returns whether the page is last page in the list or last enable page.
     *
     * @return <code>true</code> if the page is last, and <code>false</code> otherwise
     */
    private boolean isLastPage() {
        PaaS paas = wizardContext.getData(PAAS);
        if (index == 0 && paas != null && paas.isProvideTemplate()) {
            return !hasEnablePage(paasPages.get(paas));
        } else if (index == 0 && paas != null && !paas.isProvideTemplate()) {
            return getInstance(templatePage).canSkip() &&
                   !hasEnablePage(paasPages.get(paas)) &&
                   !hasEnablePage(templatePages.get(wizardContext.getData(TEMPLATE)));
        } else if (index == 1 && paas != null && !paas.isProvideTemplate()) {
            return !hasEnablePage(paasPages.get(paas)) && !hasEnablePage(templatePages.get(wizardContext.getData(TEMPLATE)));
        }
        return hasNextPage();
    }

    /**
     * Returns whether the flipped pages list have next page. A next page must be not skipped.
     *
     * @return <code>true</code> if the next page is exist, and <code>false</code> otherwise
     */
    private boolean hasNextPage() {
        for (int i = index + 1; i < flippedPages.size(); i++) {
            WizardPage page = flippedPages.get(i);
            if (!page.canSkip()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether the pages have enable pages.
     *
     * @param pages
     *         pages that need to check
     * @return <code>true</code> if the pages have enable pages, and <code>false</code> otherwise
     */
    private boolean hasEnablePage(@Nullable JsonArray<Provider<? extends WizardPage>> pages) {
        if (pages == null) {
            return false;
        }

        for (Provider<? extends WizardPage> provider : pages.asIterable()) {
            WizardPage page = getInstance(provider);
            if (!page.canSkip()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return instance of wizard page and initialize page with wizard update delegate and wizard context.
     *
     * @param provider
     *         provider of page that need to create
     * @return wizard page
     */
    private WizardPage getInstance(@NotNull Provider<? extends WizardPage> provider) {
        WizardPage page = instancePages.get(provider);
        if (page == null) {
            page = provider.get();
            page.setUpdateDelegate(delegate);
            page.setContext(wizardContext);
            instancePages.put(provider, page);
        }

        return page;
    }

    /** {@inheritDoc} */
    @Override
    public void onFinish() {
        addNextPages();
        index = 0;
        commit();
    }

    /** Commit changes on current page. */
    private void commit() {
        WizardPage page = flippedPages.get(index);
        page.commit(this);
    }

    /**
     * Add available pages for PaaS.
     *
     * @param paas
     *         PaaS that need given pages
     * @param pages
     *         pages for PaaS
     */
    public void addPaaSPages(@NotNull PaaS paas, @NotNull JsonArray<Provider<? extends WizardPage>> pages) {
        paasPages.put(paas, pages);
    }

    /**
     * Add available pages for template.
     *
     * @param template
     *         template that need given pages
     * @param pages
     *         pages for template
     */
    public void addTemplatePages(@NotNull Template template, @NotNull JsonArray<Provider<? extends WizardPage>> pages) {
        templatePages.put(template, pages);
    }

    /** {@inheritDoc} */
    @Override
    public void onSuccess() {
        if (++index < flippedPages.size()) {
            commit();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onFailure(@NotNull Throwable exception) {
        Notification notification = new Notification(exception.getMessage(), Notification.Type.ERROR);
        notificationManager.showNotification(notification);
    }
}