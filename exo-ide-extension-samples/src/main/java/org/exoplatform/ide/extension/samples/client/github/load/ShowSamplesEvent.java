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
package org.exoplatform.ide.extension.samples.client.github.load;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event to show list of repositories with sample projects on GitHub.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: GithubSamplesShowEvent.java Aug 30, 2011 12:07:32 PM vereshchaka $
 */
public class ShowSamplesEvent extends GwtEvent<ShowSamplesHandler> {
    public static final GwtEvent.Type<ShowSamplesHandler> TYPE = new GwtEvent.Type<ShowSamplesHandler>();

    public ShowSamplesEvent() {
    }

    @Override
    protected void dispatch(ShowSamplesHandler handler) {
        handler.onShowSamples(this);
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ShowSamplesHandler> getAssociatedType() {
        return TYPE;
    }

}
