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

package org.exoplatform.ide.client.operation.autocompletion;

import org.exoplatform.gwtframework.commons.util.BrowserResolver;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@RolesAllowed({"developer"})
public class OpenAutocompleteControl extends SimpleControl implements IDEControl {

    public static final String ID = "Edit/Do Autocomplete...";

    public OpenAutocompleteControl() {
        super(ID);

        if (BrowserResolver.isMacOs()) {
            setHotKey("Alt+Space");
        } else {
            setHotKey("Ctrl+Space");
        }

        setIgnoreDisable(true);
        setEvent(new AutocompleteCalledEvent());
        setImages(IDEImageBundle.INSTANCE.autocomplete(), IDEImageBundle.INSTANCE.autocompleteDisabled());
    }

    @Override
    public void initialize() {
    }

}
