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

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * @author Roman Nikitenko
 */
public interface ProjectImporterResource extends ClientBundle {
    public interface Css extends CssResource {
        String inputError();

    }

    @Source({"com/codenvy/ide/api/projectimporter/importer.css"})
    Css css();

}
