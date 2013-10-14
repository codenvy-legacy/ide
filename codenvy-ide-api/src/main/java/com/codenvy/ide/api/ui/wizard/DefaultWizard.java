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
package com.codenvy.ide.api.ui.wizard;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * The default implementation of {@link Wizard}. This one can be usable for situation when user can control position of pages. Otherwise it
 * can provide different problem.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class DefaultWizard implements Wizard, WizardPage.CommitCallback {
    private NotificationManager   notificationManager;
    private String                title;
    private UpdateDelegate        delegate;
    private WizardContext         wizardContext;
    private JsonArray<WizardPage> wizardPages;
    private int                   index;

    /**
     * Create default wizard.
     *
     * @param notificationManager
     *         manager of notification
     * @param title
     *         title of wizard
     */
    public DefaultWizard(NotificationManager notificationManager, String title) {
        this.notificationManager = notificationManager;
        this.title = title;
        wizardContext = new WizardContext();
        wizardPages = JsonCollections.createArray();
    }

    /**
     * Add pages to wizard.
     *
     * @param page
     *         page that need to add
     */
    public void addPage(@NotNull WizardPage page) {
        page.setContext(wizardContext);
        wizardPages.add(page);
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdateDelegate(@NotNull UpdateDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return title;
    }

    /** {@inheritDoc} */
    @Override
    public WizardPage flipToFirst() {
        index = -1;
        wizardContext.clear();

        return getNextEnablePage();
    }

    /** {@inheritDoc} */
    @Override
    public WizardPage flipToNext() {
        return getNextEnablePage();
    }

    @Nullable
    private WizardPage getNextEnablePage() {
        while (++index < wizardPages.size()) {
            WizardPage page = wizardPages.get(index);
            if (page.inContext() && !page.canSkip()) {
                page.setUpdateDelegate(delegate);
                return page;
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public WizardPage flipToPrevious() {
        for (int i = index; i > 0; i--) {
            WizardPage page = wizardPages.get(index);
            if (page.inContext() && !page.canSkip()) {
                return page;
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext() {
        for (int i = index; i < wizardPages.size(); i++) {
            WizardPage page = wizardPages.get(index);
            if (page.inContext() && !page.canSkip()) {
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasPrevious() {
        for (int i = index; i > 0; i--) {
            WizardPage page = wizardPages.get(index);
            if (page.inContext() && !page.canSkip()) {
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canFinish() {
        WizardPage page = wizardPages.get(index);
        return !hasNext() && page.isCompleted();
    }

    /** {@inheritDoc} */
    @Override
    public void onFinish() {
        index = -1;
        commit();
    }

    /** Commit changes on current page. */
    private void commit() {
        WizardPage page = getNextPage();
        if (page != null) {
            page.commit(this);
        }
    }

    @Nullable
    private WizardPage getNextPage() {
        while (++index < wizardPages.size()) {
            WizardPage page = wizardPages.get(index);
            if (page.inContext()) {
                return page;
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void onSuccess() {
        if (index + 1 < wizardPages.size()) {
            commit();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onFailure(@NotNull Throwable exception) {
        Notification notification = new Notification(exception.getMessage(), ERROR);
        notificationManager.showNotification(notification);
    }
}