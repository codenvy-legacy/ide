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
