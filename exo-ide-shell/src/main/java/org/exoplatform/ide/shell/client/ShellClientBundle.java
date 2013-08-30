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
package org.exoplatform.ide.shell.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * Client bundle for Shell application.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Mar 6, 2012 12:08:36 PM anya $
 */
public interface ShellClientBundle extends ClientBundle {
    /** Instance of {@link ShellClientBundle}. */
    ShellClientBundle INSTANCE = GWT.<ShellClientBundle>create(ShellClientBundle.class);

    /** CSS resources for console. */
    @Source("org/exoplatform/ide/shell/client/Shell.css")
    Style css();

    /** CSS styles. */
    public interface Style extends CssResource {
        String shellContainer();

        String content();

        String term();

        String serverResponseLabelError();

        String blink();

        String cursor();

        String crashAutocomplete();
    }
}
