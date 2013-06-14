/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
