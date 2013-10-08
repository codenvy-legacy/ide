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
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.api.template.Template;
import com.codenvy.ide.api.ui.wizard.Wizard;
import com.codenvy.ide.api.ui.wizard.WizardContext;
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
public class NewProjectWizard implements Wizard, WizardPage.CommitCallback {
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
    private NotificationManager                                      notificationManager;

    private int index;

    @Inject
    public NewProjectWizard(Provider<NewProjectPagePresenter> newProjectPage,
                            TemplatePageFactory templatePage,
                            NotificationManager notificationManager) {
        this.newProjectPage = newProjectPage;
        this.templatePage = templatePage;
        this.notificationManager = notificationManager;
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
        if (index == 0) {
            TemplatePagePresenter page = templatePage.create(wizardContext);
            page.setUpdateDelegate(delegate);
            flippedPages.add(page);
            if (page.canSkip()) {
                addPages(templatePages.get(wizardContext.getData(TEMPLATE)));
                addPages(paasPages.get(wizardContext.getData(PAAS)));
            }
        } else if (index == 1) {
            addPages(templatePages.get(wizardContext.getData(TEMPLATE)));
            addPages(paasPages.get(wizardContext.getData(PAAS)));
        }

        WizardPage page = null;
        boolean canSkip = true;
        while (canSkip && ++index < flippedPages.size()) {
            page = flippedPages.get(index);
            canSkip = page.canSkip();
        }

        return canSkip ? null : page;
    }

    private void addPages(@Nullable JsonArray<Provider<? extends WizardPage>> pages) {
        if (pages != null) {
            for (Provider<? extends WizardPage> provider : pages.asIterable()) {
                addPage(provider);
            }
        }
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
        if (index > 0) {
            WizardPage page = null;
            boolean canSkip = true;
            while (canSkip && --index > 0) {
                page = flippedPages.get(index);
                canSkip = page.canSkip();
            }

            if (index <= 2) {
                for (int i = flippedPages.size() - 1; i > index; i--) {
                    flippedPages.remove(i);
                }
            }

            return flippedPages.get(index);
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext() {
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
        if (index < 2) {
            int pageCount = 0;
            // TODO may be need to change to instance
            JsonArray<Provider<? extends WizardPage>> paasPages = this.paasPages.get(wizardContext.getData(PAAS));
            if (paasPages != null) {
                // TODO move to method
                for (Provider<? extends WizardPage> provider : paasPages.asIterable()) {
                    WizardPage page = provider.get();
                    pageCount += (page.canSkip() ? 0 : 1);
                }
            }

            // TODO may be need to change to instance
            JsonArray<Provider<? extends WizardPage>> templatePages = this.templatePages.get(wizardContext.getData(TEMPLATE));
            if (templatePages != null) {
                // TODO move to method
                for (Provider<? extends WizardPage> provider : templatePages.asIterable()) {
                    WizardPage page = provider.get();
                    pageCount += (page.canSkip() ? 0 : 1);
                }
            }

            TemplatePagePresenter page = templatePage.create(wizardContext);

            return page.canSkip() && pageCount == 0;
        } else {
            boolean isLastPage = true;
            for (int i = index + 1; i < flippedPages.size(); i++) {
                WizardPage page = flippedPages.get(i);
                if (!page.canSkip()) {
                    isLastPage = false;
                    break;
                }
            }

            return isLastPage;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCancel() {
        clear();
    }

    /** {@inheritDoc} */
    @Override
    public void onFinish() {
        index = 0;
        commit();
    }

    private void commit() {
        WizardPage page = flippedPages.get(index);
        page.commit(this);
    }

    public void addPaaSPages(@NotNull PaaS paas, @NotNull JsonArray<Provider<? extends WizardPage>> pages) {
        paasPages.put(paas, pages);
    }

    public void addTemplatePages(@NotNull Template template, @NotNull JsonArray<Provider<? extends WizardPage>> pages) {
        templatePages.put(template, pages);
    }

    /** {@inheritDoc} */
    @Override
    public void onSuccess() {
        if (++index < flippedPages.size()) {
            commit();
        } else {
            clear();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onFailure(Throwable exception) {
        Notification notification = new Notification(exception.getMessage(), Notification.Type.ERROR);
        notificationManager.showNotification(notification);
        clear();
    }

    private void clear() {
        wizardContext.clear();
    }
}