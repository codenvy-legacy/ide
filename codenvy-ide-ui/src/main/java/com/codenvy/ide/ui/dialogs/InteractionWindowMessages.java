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
package com.codenvy.ide.ui.dialogs;

import com.google.gwt.i18n.client.Messages;

/**
 * I18n messages interface for the confirmation and message windows.
 *
 * @author "Mickaël Leduque"
 */
public interface InteractionWindowMessages extends Messages {

    @DefaultMessage("OK")
    String okButtonText();

    @DefaultMessage("Cancel")
    String cancelButtonText();
}
