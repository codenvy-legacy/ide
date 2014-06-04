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
package com.codenvy.ide.extension;

import com.codenvy.ide.api.mvp.View;

import java.util.List;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface ExtensionManagerView extends View<ExtensionManagerView.ActionDelegate> {

    void setExtensions(List<ExtensionDescription> extensions);

    public interface ActionDelegate {

        void setDirty();
    }

}
