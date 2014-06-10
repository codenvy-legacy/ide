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

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.dto.shared.RoutingType;
import com.google.gwt.webworker.client.messages.CompactJsonMessage;
import com.google.gwt.webworker.client.messages.Message;
import com.google.gwt.webworker.client.messages.SerializationIndex;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@RoutingType(type = Message.NON_ROUTABLE_TYPE)
public interface Problem extends Message, CompactJsonMessage {

    @SerializationIndex(1)
    String originatingFileName();

    @SerializationIndex(2)
    String message();

    @SerializationIndex(3)
    int id();

    @SerializationIndex(4)
    Array<String> stringArguments();

    @SerializationIndex(5)
    int severity();

    @SerializationIndex(6)
    int startPosition();

    @SerializationIndex(7)
    int endPosition();

    @SerializationIndex(8)
    int line();

    @SerializationIndex(9)
    int column();
}
