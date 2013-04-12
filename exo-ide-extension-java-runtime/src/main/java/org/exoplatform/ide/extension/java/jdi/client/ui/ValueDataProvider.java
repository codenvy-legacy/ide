/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.jdi.client.ui;

import com.google.gwt.http.client.RequestException;
import com.google.gwt.view.client.ListDataProvider;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.java.jdi.client.DebuggerClientService;
import org.exoplatform.ide.extension.java.jdi.client.DebuggerExtension;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerInfo;
import org.exoplatform.ide.extension.java.jdi.shared.Value;
import org.exoplatform.ide.extension.java.jdi.shared.Variable;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class ValueDataProvider extends ListDataProvider<Variable> {

    public ValueDataProvider(Variable var, DebuggerInfo debuggerInfo) {
        AutoBean<Value> autoBean = DebuggerExtension.AUTO_BEAN_FACTORY.create(Value.class);
        AutoBeanUnmarshaller<Value> unmarshaller = new AutoBeanUnmarshaller<Value>(autoBean);
        try {
            DebuggerClientService.getInstance().getValue(debuggerInfo.getId(), var,
                                                         new AsyncRequestCallback<Value>(unmarshaller) {

                                                             @Override
                                                             protected void onSuccess(Value result) {
                                                                 if (result != null) {
                                                                     if (!(result.getVariables() == null ||
                                                                           result.getVariables().isEmpty()))
                                                                         setList(result.getVariables());
                                                                 }
                                                             }

                                                             @Override
                                                             protected void onFailure(Throwable exception) {
                                                                 IDE.eventBus().fireEvent(new ExceptionThrownEvent(exception));
                                                             }
                                                         });
        } catch (RequestException e) {
            IDE.eventBus().fireEvent(new ExceptionThrownEvent(e));
        }
    }

}
