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
package org.exoplatform.ide.extension.java.jdi.server;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.ClassNotPreparedException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Location;
import com.sun.jdi.NativeMethodException;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VMCannotBeModifiedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.InvalidRequestStateException;
import org.exoplatform.ide.extension.java.jdi.server.model.BreakPointImpl;
import org.exoplatform.ide.extension.java.jdi.server.model.FieldImpl;
import org.exoplatform.ide.extension.java.jdi.server.model.StackFrameDumpImpl;
import org.exoplatform.ide.extension.java.jdi.server.model.VariableImpl;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;
import org.exoplatform.ide.extension.java.jdi.shared.StackFrameDump;
import org.exoplatform.ide.extension.java.jdi.shared.Variable;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Connects to JVM over Java Debug Wire Protocol handle its events. All methods of this class may throws
 * DebuggerException. Typically such exception caused by errors in underlying JDI (Java Debug Interface), e.g.
 * connection errors
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Debugger
{
   private static final Log LOG = ExoLogger.getLogger(Debugger.class);

   private final String host;
   private final int port;

   /** Target Java VM representation. */
   private VirtualMachine vm;
   private EventsCollector eventsCollector;
   /** Current stack frame. Initialized when break point is reached and set to <code>null</code> if JVM is resumed. */
   private JdiStackFrame stackFrame;

   /**
    * Create debugger and connect it to the JVM which already running at the specified host and port.
    *
    * @param host the host where JVM running
    * @param port the Java Debug Wire Protocol (JDWP) port
    */
   public Debugger(String host, int port)
   {
      this.host = host;
      this.port = port;
   }

   /**
    * Attach to a JVM that is already running at specified host. Calling this method has no effect is Debugger already
    * connected.
    *
    * @throws VMConnectException when connection to Java VM is not established
    */
   public void connect() throws VMConnectException
   {
      if (isConnected())
      {
         return;
      }
      AttachingConnector connector = connector();
      if (connector == null)
      {
         throw new VMConnectException("Unable connect to target Java VM. Requested connector not found. ");
      }
      Map<String, Connector.Argument> arguments = connector.defaultArguments();
      arguments.get("hostname").setValue(host);
      ((Connector.IntegerArgument)arguments.get("port")).setValue(port);
      try
      {
         vm = connector.attach(arguments);
         eventsCollector = new EventsCollector(vm.eventQueue(), new EventsHandler()
         {
            @Override
            public void handleEvents(EventSet events) throws DebuggerException
            {
               boolean resume = true;
               for (Event event : events)
               {
                  LOG.debug("New event: {}", event);
                  if (event instanceof BreakpointEvent)
                  {
                     try
                     {
                        stackFrame = new JdiStackFrameImpl(((BreakpointEvent)event).thread().frame(0));
                     }
                     catch (IncompatibleThreadStateException e)
                     {
                        throw new DebuggerException(e.getMessage(), e);
                     }
                     resume = false;
                  }
               }
               // Resume target Java VM. We are interesting (at the moment) for breakpoints events only.
               if (resume)
               {
                  events.resume();
               }
            }
         });
      }
      catch (IOException ioe)
      {
         throw new VMConnectException(ioe.getMessage(), ioe);
      }
      catch (IllegalConnectorArgumentsException e)
      {
         throw new VMConnectException(e.getMessage(), e);
      }
   }

   private AttachingConnector connector()
   {
      for (AttachingConnector c : Bootstrap.virtualMachineManager().attachingConnectors())
      {
         if ("com.sun.jdi.SocketAttach".equals(c.name()))
         {
            return c;
         }
      }
      return null;
   }

   /**
    * Check is debugger already connected to the target JVM.
    *
    * @return <code>true</code> if debugger established and <code>false</code>  otherwise
    * @see #connect()
    */
   public boolean isConnected()
   {
      return vm != null;
   }

   /**
    * Close connection to the target JVM.
    *
    * @throws DebuggerException when failed to close connection
    */
   public void disconnect() throws DebuggerException
   {
      if (isConnected())
      {
         eventsCollector.stop();
         vm.dispose();
         vm = null;
         LOG.debug("Close connection");
      }
   }

   /**
    * Add new break point.
    *
    * @param breakPoint break point description
    * @throws InvalidBreakPointException if description of break point is invalid (specified line number or class name
    * is invalid)
    * @throws DebuggerException when other JDI error occurs
    */
   public void addBreakPoint(BreakPoint breakPoint) throws InvalidBreakPointException, DebuggerException
   {
      List<ReferenceType> classes = vm.classesByName(breakPoint.getClassName());
      if (classes.isEmpty())
      {
         throw new InvalidBreakPointException("Class " + breakPoint.getClassName() + " not found. ");
      }
      ReferenceType clazz = classes.get(0);
      List<Location> locations;
      try
      {
         locations = clazz.locationsOfLine(breakPoint.getLineNumber());
      }
      catch (AbsentInformationException e)
      {
         throw new DebuggerException(e.getMessage(), e);
      }
      catch (ClassNotPreparedException e)
      {
         throw new DebuggerException(e.getMessage(), e);
      }

      if (locations.isEmpty())
      {
         throw new InvalidBreakPointException("Line " + breakPoint.getLineNumber()
            + " not found in class " + breakPoint.getClassName());
      }

      Location location = locations.get(0);
      if (location.method() == null)
      {
         // Line is out of method.
         throw new InvalidBreakPointException("Invalid line " + breakPoint.getLineNumber()
            + " in class " + breakPoint.getClassName());
      }
      try
      {
         EventRequest breakPointRequest = vm.eventRequestManager().createBreakpointRequest(location);
         breakPointRequest.setSuspendPolicy(EventRequest.SUSPEND_ALL);
         breakPointRequest.setEnabled(breakPoint.isEnabled());
      }
      catch (VMCannotBeModifiedException e)
      {
         throw new DebuggerException(e.getMessage(), e);
      }
      catch (NativeMethodException e)
      {
         throw new DebuggerException(e.getMessage(), e);
      }
      catch (InvalidRequestStateException e)
      {
         throw new DebuggerException(e.getMessage(), e);
      }
      catch (IllegalThreadStateException e)
      {
         throw new DebuggerException(e.getMessage(), e);
      }
      LOG.debug("Add breakpoint: {}", breakPoint);
   }

   /**
    * Get all break points which set for current debugger.
    *
    * @return list of break points
    * @throws DebuggerException when any JDI errors occurs when try to access current break points
    */
   public List<BreakPoint> getBreakPoints() throws DebuggerException
   {
      List<BreakpointRequest> breakpointRequests;
      try
      {
         breakpointRequests = vm.eventRequestManager().breakpointRequests();
      }
      catch (VMCannotBeModifiedException e)
      {
         // If target VM in read-only state then list of break point always empty.
         return Collections.emptyList();
      }
      List<BreakPoint> breakPoints = new ArrayList<BreakPoint>(breakpointRequests.size());
      for (BreakpointRequest breakpointRequest : breakpointRequests)
      {
         Location location = breakpointRequest.location();
         breakPoints.add(
            new BreakPointImpl(location.declaringType().name(), location.lineNumber(), breakpointRequest.isEnabled()));
      }
      return breakPoints;
   }

   /**
    * Switch enable status of break point.
    *
    * @param breakPoint break point description
    * @throws DebuggerException when any JDI errors occurs when try to update break point
    */
   public void switchBreakPoint(BreakPoint breakPoint) throws DebuggerException
   {
      try
      {
         for (BreakpointRequest breakpointRequest : vm.eventRequestManager().breakpointRequests())
         {
            Location location = breakpointRequest.location();
            if (location.declaringType().name().equals(breakPoint.getClassName())
               && location.lineNumber() == breakPoint.getLineNumber())
            {
               breakpointRequest.setEnabled(breakPoint.isEnabled());
               break;
            }
         }
      }
      catch (VMCannotBeModifiedException e)
      {
         throw new DebuggerException(e.getMessage(), e);
      }
      catch (InvalidRequestStateException e)
      {
         throw new DebuggerException(e.getMessage(), e);
      }
      catch (IllegalThreadStateException e)
      {
         throw new DebuggerException(e.getMessage(), e);
      }
   }

   /**
    * Resume suspended JVM.
    *
    * @throws VMConnectException if Debugger id not connected yet
    * @throws DebuggerException when failed to resume target JVM
    */
   public void resume() throws VMConnectException, DebuggerException
   {
      if (!isConnected())
      {
         throw new VMConnectException("Debugger is not connected to target JVM.");
      }
      try
      {
         vm.resume();
         LOG.debug("Resume VM");
      }
      catch (VMCannotBeModifiedException e)
      {
         throw new DebuggerException(e.getMessage(), e);
      }
      finally
      {
         stackFrame = null;
      }
   }

   /**
    * Get dump of fields and local variable of current object and current frame.
    *
    * @return dump of current stack frame
    * @throws VMConnectException if Debugger id not connected yet
    * @throws DebuggerStateException when target JVM is not suspended
    * @throws DebuggerException when any other errors occur when try to access the current state of target JVM
    */
   public StackFrameDump dumpStackFrame() throws VMConnectException, DebuggerException
   {
      if (!isConnected())
      {
         throw new VMConnectException("Debugger is not connected to target JVM.");
      }
      if (stackFrame == null)
      {
         throw new DebuggerStateException("Unable get dump. Target Java VM is not suspended. ");
      }
      StackFrameDumpImpl dump = new StackFrameDumpImpl();
      for (JdiField f : stackFrame.getFields())
      {
         dump.addField(new FieldImpl(f.getName(), f.getValue().getAsString(), f.getTypeName(), f.isFinal(),
            f.isStatic(), f.isTransient(), f.isVolatile()));
      }
      for (JdiLocalVariable var : stackFrame.getLocalVariables())
      {
         dump.addLocalVariable(new VariableImpl(var.getName(), var.getValue().getAsString(), var.getTypeName()));
      }
      return dump;
   }

   /**
    * Get variable with specified path. Each item in path is name of variable.
    * <p/>
    * Path must be specified according to the following rules:
    * <ol>
    * <li>If need to get field of this object of current frame then first element in array always should be
    * 'this'.</li>
    * <li>If need to get static field in current frame then first element in array always should be 'static'.</li>
    * <li>If need to get local variable in current frame then first element should be the name of local variable.</li>
    * </ol>
    * <p/>
    * Here is example. <br/>
    * Assume we have next hierarchy of classes and breakpoint set in line: <i>// breakpoint</i>:
    * <pre>
    *    class A {
    *       private String str;
    *       ...
    *    }
    *
    *    class B {
    *       private A a;
    *       ....
    *
    *       void method() {
    *          A var = new A();
    *          var.setStr(...);
    *          a = var;
    *          // breakpoint
    *       }
    *    }
    * </pre>
    * There are two ways to access variable <i>str</i> in class <i>A</i>:
    * <ol>
    * <li>Through field <i>a</i> in class <i>B</i>: ['this', 'a', 'str']</li>
    * <li>Through local variable <i>var</i> in method <i>B.method()</i>: ['var', 'str']</li>
    * </ol>
    *
    * @param variablePath path to variable
    * @return variable or <code>null</code> if variable not found
    * @throws VMConnectException if Debugger id not connected yet
    * @throws DebuggerStateException when target JVM is not suspended
    * @throws DebuggerException when any other errors occur when try to access the variable
    */
   public Variable getVariable(String[] variablePath) throws VMConnectException, DebuggerException
   {
      if (variablePath == null || variablePath.length == 0)
      {
         throw new IllegalArgumentException("Path to variable may not be null or empty. ");
      }
      if (!isConnected())
      {
         throw new VMConnectException("Debugger is not connected to target JVM.");
      }
      if (stackFrame == null)
      {
         throw new DebuggerStateException("Unable get variable. Target Java VM is not suspended. ");
      }
      JdiVariable variable = null;
      int offset;
      if ("this".equals(variablePath[0]) || "static".equals(variablePath[0]))
      {
         if (variablePath.length < 2)
         {
            throw new IllegalArgumentException("Name of field required. ");
         }
         variable = stackFrame.getFieldByName(variablePath[1]);
         offset = 2;
      }
      else
      {
         variable = stackFrame.getLocalVariableByName(variablePath[0]);
         offset = 1;
      }

      for (int i = offset; variable != null && i < variablePath.length; i++)
      {
         variable = variable.getValue().getVariableByName(variablePath[i]);
      }

      if (variable == null)
      {
         return null;
      }

      if (variable instanceof JdiField)
      {
         JdiField field = (JdiField)variable;
         return new FieldImpl(field.getName(), field.getValue().getAsString(), field.getTypeName(), field.isFinal(),
            field.isStatic(), field.isTransient(), field.isVolatile());
      }
      return new VariableImpl(variable.getName(), variable.getValue().getAsString(), variable.getTypeName());
   }

   /**
    * Returns the name of the target Java VM.
    *
    * @return JVM name
    * @throws DebuggerStateException when target JVM is not suspended
    * @throws DebuggerException when any other JDI errors occur
    */
   public String getVmName() throws DebuggerStateException, DebuggerException
   {
      if (!isConnected())
      {
         throw new VMConnectException("Debugger is not connected to target JVM.");
      }
      return vm.name();
   }

   /**
    * Returns the version of the target Java VM.
    *
    * @return JVM version
    * @throws DebuggerStateException when target JVM is not suspended
    * @throws DebuggerException when any other JDI errors occur
    */
   public String getVmVersion() throws DebuggerStateException, DebuggerException
   {
      if (!isConnected())
      {
         throw new VMConnectException("Debugger is not connected to target JVM.");
      }
      return vm.version();
   }

   public String getHost()
   {
      return host;
   }

   public int getPort()
   {
      return port;
   }
}
