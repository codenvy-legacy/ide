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
package com.codenvy.ide.wizard.project.importproject;

import com.codenvy.ide.api.projecttype.wizard.ImportProjectWizard;
import com.codenvy.ide.api.projecttype.wizard.ImportProjectWizardRegistry;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;

/**
 * Implementation for {@link ImportProjectWizardRegistry}.
 *
 * @author Ann Shumilova
 */
public class ImportProjectWizardRegistryImpl implements ImportProjectWizardRegistry {

    private StringMap<ImportProjectWizard> map = Collections.createStringMap();

    /** {@inheritDoc} */
    @Override
    public void addWizard(String importerId, ImportProjectWizard wizard) {
        map.put(importerId, wizard);
    }

    /** {@inheritDoc} */
    @Override
    public ImportProjectWizard getWizard(String importerId) {
        return map.get(importerId);
    }

}
