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
package org.exoplatform.ide.extension.java.jdi.client;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import org.exoplatform.ide.extension.java.jdi.shared.ApplicationInstance;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPointEvent;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPointEventList;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPointList;
import org.exoplatform.ide.extension.java.jdi.shared.DebugApplicationInstance;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerEventList;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerEventListWS;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerInfo;
import org.exoplatform.ide.extension.java.jdi.shared.Field;
import org.exoplatform.ide.extension.java.jdi.shared.StackFrameDump;
import org.exoplatform.ide.extension.java.jdi.shared.StepEvent;
import org.exoplatform.ide.extension.java.jdi.shared.UpdateVariableRequest;
import org.exoplatform.ide.extension.java.jdi.shared.Value;
import org.exoplatform.ide.extension.java.jdi.shared.Variable;
import org.exoplatform.ide.extension.java.jdi.shared.VariablePath;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public interface DebuggerAutoBeanFactory extends AutoBeanFactory
{

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

   AutoBean<DebugApplicationInstance> debugApplicationInstance();

   AutoBean<UpdateVariableRequest> updateVariableRequest(UpdateVariableRequest request);

   AutoBean<DebuggerEventListWS> debuggerEventListWS();

}
