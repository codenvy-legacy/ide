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
package com.codenvy.ide.ui;

import com.google.gwt.i18n.client.Messages;

/**
 * @author Vitaly Parfonov
 */
public interface Locale extends Messages {

    @Key("ok")
    @DefaultMessage("OK")
    String ok();

    @Key("cancel")
    @DefaultMessage("Cancel")
    String cancel();

}
