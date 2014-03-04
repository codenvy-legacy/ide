package com.codenvy.ide.ext.java.messages;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Jso;
import com.codenvy.ide.dto.shared.RoutingType;
import com.google.gwt.webworker.client.messages.Message;

import java.util.HashMap;

/**
 * @author Roman Nikitenko
 */
@RoutingType(type = RoutingTypes.FORMAT_RESULT)
public interface FormatResultMessage extends Message{

    String id();

    Jso textEdit();


}
