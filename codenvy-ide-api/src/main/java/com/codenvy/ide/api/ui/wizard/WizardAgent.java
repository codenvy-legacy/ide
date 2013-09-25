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

import com.codenvy.ide.api.extension.SDK;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Provider;


/**
 * Provides register wizards for creating new project and new resource.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@SDK(title = "ide.api.ui.wizard.newresource")
public interface WizardAgent {
    /**
     * Registers new wizard for creating new resource.
     *
     * @param category
     *         allows to show new resources wizard in tree view. it's name of parent node.
     * @param title
     *         the text what will be showed on wizard page
     * @param icon
     *         the icon what will be showed on wizard page
     * @param wizardPage
     *         first wizard page
     */
    void registerNewResourceWizard(String category, String title, ImageResource icon, Provider<? extends WizardPagePresenter> wizardPage);
}