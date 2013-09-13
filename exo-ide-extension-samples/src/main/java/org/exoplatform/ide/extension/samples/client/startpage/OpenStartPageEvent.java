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
package org.exoplatform.ide.extension.samples.client.startpage;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: WelcomeViewOpen.java Aug 25, 2011 1:11:16 PM vereshchaka $
 */
public class OpenStartPageEvent extends GwtEvent<OpenStartPageHandler> {
    public static final GwtEvent.Type<OpenStartPageHandler> TYPE = new GwtEvent.Type<OpenStartPageHandler>();

    @Override
    protected void dispatch(OpenStartPageHandler handler) {
        handler.onOpenStartPage(this);
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<OpenStartPageHandler> getAssociatedType() {
        return TYPE;
    }

}
