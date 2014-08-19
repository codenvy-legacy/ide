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
package com.codenvy.ide.wizard.buttonLoader;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.DataResource.MimeType;

/**
 * Resources for ButtonLoader.
 *
 * @author Oleksii Orel
 */
public interface ButtonLoaderResources extends ClientBundle {

    public interface ButtonLoaderCss extends CssResource {
        String buttonLoader();
    }

    @MimeType("image/png")
    @Source("loader.png")
    DataResource loader();

    @Source({"buttonLoader.css", "com/codenvy/ide/api/ui/style.css"})
    ButtonLoaderCss Css();

}
