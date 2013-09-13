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
package org.exoplatform.ide.editor.python.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: PythonClientBundle.java May 29, 2012 3:50:10 PM azatsarynnyy $
 */
public interface PythonClientBundle extends ClientBundle {
    PythonClientBundle INSTANCE = GWT.create(PythonClientBundle.class);

    @Source("org/exoplatform/ide/editor/python/client/images/python.png")
    ImageResource python();

    @Source("org/exoplatform/ide/editor/python/client/images/python-disabled.png")
    ImageResource pythonDisabled();
}
