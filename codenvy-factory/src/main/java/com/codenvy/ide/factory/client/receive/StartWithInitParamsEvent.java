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

package com.codenvy.ide.factory.client.receive;

import com.google.gwt.event.shared.GwtEvent;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: StartWithInitParamsEvent.java Nov 21, 2012 vetal $
 */
public class StartWithInitParamsEvent extends GwtEvent<StartWithInitParamsHandler> {

    public static final GwtEvent.Type<StartWithInitParamsHandler> TYPE = new GwtEvent.Type<StartWithInitParamsHandler>();

    private Map<String, List<String>> parameterMap;

    public StartWithInitParamsEvent(Map<String, List<String>> parameterMap) {
        this.parameterMap = parameterMap;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<StartWithInitParamsHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(StartWithInitParamsHandler handler) {
        handler.onStartWithInitParams(this);
    }

    public Map<String, List<String>> getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(Map<String, List<String>> parameterMap) {
        this.parameterMap = parameterMap;
    }

}
