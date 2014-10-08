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

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Vitaly Parfonov
 * @author Roman Nikitenko
 */
@Singleton
public class ProjectImporterRegistry {
    private final Map<String, ProjectImporter>       importers;
    private final Map<String, ImporterPagePresenter> importersPages;

    @Inject
    public ProjectImporterRegistry(Set<ProjectImporter> importers,
                                   Set<ImporterPagePresenter> importersPages) {
        this.importers = new HashMap<>();
        this.importersPages = new HashMap<>();

        for (ProjectImporter importer : importers) {
            registerImporter(importer);
        }

        for (ImporterPagePresenter importerPage : importersPages) {
            registerImporterPage(importerPage);
        }
    }

    public void registerImporter(ProjectImporter importer) {
        importers.put(importer.getId(), importer);
    }

    public ProjectImporter unregisterImporter(String id) {
        if (id == null) {
            return null;
        }
        return importers.remove(id);
    }

    public ProjectImporter getImporter(String id) {
        if (id == null) {
            return null;
        }
        return importers.get(id);
    }

    public List<ProjectImporter> getImporters() {
        return new ArrayList<>(importers.values());
    }

    public void registerImporterPage(ImporterPagePresenter importerPage) {
        importersPages.put(importerPage.getId(), importerPage);
    }

    public ImporterPagePresenter getImporterPage(String id) {
        if (id == null) {
            return null;
        }
        return importersPages.get(id);
    }
}

