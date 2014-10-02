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
package com.codenvy.ide.wizard.project;

import com.codenvy.ide.wizard.project.main.MainPageViewImpl;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * @author Ann Shumilova
 */
public interface ProjectWizardResources extends ClientBundle {

    public interface Css extends CssResource {
        String buttonPanel();

        String button();

        String blueButton();

        String inputError();

    }

    @Source({"com/codenvy/ide/wizard/newproject/Wizard.css", "com/codenvy/ide/api/ui/style.css"})
    Css wizardCss();

    @Source({"main/MainPage.css", "com/codenvy/ide/api/ui/style.css", "com/codenvy/ide/ui/Styles.css"})
    MainPageViewImpl.Style mainPageStyle();
}




