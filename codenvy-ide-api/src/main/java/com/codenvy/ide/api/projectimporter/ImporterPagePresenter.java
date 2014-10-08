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
package com.codenvy.ide.api.projectimporter;

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.wizard.Wizard;
import com.codenvy.ide.api.wizard.WizardContext;

/**
 * @author Roman Nikitenko
 */
public interface ImporterPagePresenter extends Presenter{

    /**
     * @return unique id of importer e.g git, zip, github
     */
    String getId();

    /**
     * Disable all page inputs.
     */
    public void disableInputs();

    /**
     * Enable all page inputs.
     */
    public void enableInputs();

    public void setContext(WizardContext wizardContext);

    public void setProjectWizardDelegate(Wizard.UpdateDelegate updateDelegate);

    public void clear();

}
