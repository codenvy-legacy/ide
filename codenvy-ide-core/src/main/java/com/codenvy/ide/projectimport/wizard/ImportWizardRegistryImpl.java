/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.projectimport.wizard;

import com.codenvy.ide.api.projectimport.wizard.ImportWizardRegistrar;
import com.codenvy.ide.api.projectimport.wizard.ImportWizardRegistry;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.util.loging.Log;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * Implementation for {@link ImportWizardRegistry}.
 *
 * @author Artem Zatsarynnyy
 */
public class ImportWizardRegistryImpl implements ImportWizardRegistry {
    private final StringMap<ImportWizardRegistrar> registrars;

    public ImportWizardRegistryImpl() {
        registrars = Collections.createStringMap();
    }

    @Inject(optional = true)
    private void register(Set<ImportWizardRegistrar> registrars) {
        for (ImportWizardRegistrar registrar : registrars) {
            final String id = registrar.getImporterId();
            if (this.registrars.containsKey(id)) {
                Log.warn(ImportWizardRegistryImpl.class, "Wizard for project importer " + id + " already registered.");
            } else {
                this.registrars.put(id, registrar);
            }
        }
    }

    @Nullable
    @Override
    public ImportWizardRegistrar getWizardRegistrar(@Nonnull String importerId) {
        return registrars.get(importerId);
    }
}
