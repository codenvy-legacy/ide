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
