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
package org.exoplatform.ide.extension.java.jdi.client;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import org.exoplatform.ide.extension.java.jdi.shared.*;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public interface DebuggerAutoBeanFactory extends AutoBeanFactory {

    AutoBean<BreakPoint> breakPoint();

    AutoBean<BreakPointEventList> breakPoinEventList();

    AutoBean<DebuggerEventList> debuggerEventList();

    AutoBean<StackFrameDump> stackFrameDump();

    AutoBean<BreakPoint> breakPoint(BreakPoint breakPoint);

    AutoBean<BreakPointList> breakPoints();

    AutoBean<DebuggerInfo> debuggerInfo();

    AutoBean<Field> field();

    AutoBean<Variable> variable();

    AutoBean<Value> value();

    AutoBean<VariablePath> variablePath();

    AutoBean<VariablePath> variablePath(VariablePath valuePath);

    AutoBean<BreakPointEvent> breakPoinEvent();

    AutoBean<StepEvent> stepEvent();

    AutoBean<ApplicationInstance> applicationInstance();

    AutoBean<ApplicationInstance> debugApplicationInstance();

    AutoBean<UpdateVariableRequest> updateVariableRequest(UpdateVariableRequest request);

}
