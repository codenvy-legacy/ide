package com.codenvy.ide.ext.java.messages;

import com.codenvy.ide.dto.shared.RoutingType;
import com.google.gwt.webworker.client.messages.Message;

/**
 * @author Roman Nikitenko
 */
@RoutingType(type = RoutingTypes.FORMAT)
public interface FormatMessage extends Message {

    String id();

    int offset();

    int length();

    String content();

}
