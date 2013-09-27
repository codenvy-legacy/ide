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

package org.exoplatform.ide.client.project.properties;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ShowProjectPropertiesEvent extends GwtEvent<ShowProjectPropertiesHandler> {

    public static final GwtEvent.Type<ShowProjectPropertiesHandler> TYPE =
            new GwtEvent.Type<ShowProjectPropertiesHandler>();

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ShowProjectPropertiesHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ShowProjectPropertiesHandler handler) {
        handler.onShowProjectProperties(this);
    }

}
