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
package org.exoplatform.ide.client.properties;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Anna Zhuleva</a>
 * @version $Id:
 */

public class ShowPropertiesEvent extends GwtEvent<ShowPropertiesHandler> {
    public static final GwtEvent.Type<ShowPropertiesHandler> TYPE = new GwtEvent.Type<ShowPropertiesHandler>();

    private boolean showProperties;

    public ShowPropertiesEvent(boolean showProperties) {
        this.showProperties = showProperties;
    }

    public boolean isShowProperties() {
        return showProperties;
    }

    @Override
    protected void dispatch(ShowPropertiesHandler handler) {
        handler.onShowProperties(this);
    }

    @Override
    public Type<ShowPropertiesHandler> getAssociatedType() {
        return TYPE;
    }

}
