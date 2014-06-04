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
package com.codenvy.ide.ext.java.messages;

import com.codenvy.ide.collections.js.JsoStringMap;
import com.codenvy.ide.dto.shared.RoutingType;
import com.google.gwt.webworker.client.messages.Message;

/**
 * @author Roman Nikitenko
 */
@RoutingType(type = RoutingTypes.PREFERENCE_FORMAT_SETTINGS)
public interface PreferenceFormatSetMessage extends Message {

    JsoStringMap<String> settings();
}
