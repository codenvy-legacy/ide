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
package com.codenvy.ide.xml;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.newresource.AbstractNewResourceAction;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to create new XML file.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class NewXmlFileAction extends AbstractNewResourceAction {
    private static final String DEFAULT_CONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

    @Inject
    public NewXmlFileAction(CoreLocalizationConstant localizationConstant, Resources resources) {
        super(localizationConstant.actionNewXmlFileTitle(),
              localizationConstant.actionNewXmlFileDescription(),
              null,
              resources.defaultFile());
    }

    @Override
    protected String getExtension() {
        return "xml";
    }

    @Override
    protected String getDefaultContent() {
        return DEFAULT_CONTENT;
    }
}
