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
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.EventRequest;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Connects to JVM over Java Debug Wire Protocol handle its events.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Debugger
{
   /**
    * Attach to a JVM that is already running at specified host.
    *
    * @param host the host where JVM running
    * @param port the Java Debug Wire Protocol (JDWP) port
    * @return Debugger instance
    * @throws VMConnectException if connection to Java VM is not established
    */
   public static Debugger connect(String host, int port) throws VMConnectException
   {
      try
      {
         return new Debugger(host, port);
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

   /** Target Java VM representation. */
   private final VirtualMachine vm;
   private final Queue<BreakpointEvent> breakpointEvents;
   private final EventCollector eventCollector;

   private Debugger(String host, int port) throws IOException, IllegalConnectorArgumentsException
   {
      AttachingConnector connector = null;
      for (AttachingConnector c : Bootstrap.virtualMachineManager().attachingConnectors())
      {
         // Use socket attach connector.
         if ("com.sun.jdi.SocketAttach".equals(c.name()))
         {
            connector = c;
            break;
         }
      }
      Map<String, Connector.Argument> arguments = connector.defaultArguments();
      arguments.get("hostname").setValue(host);
      ((Connector.IntegerArgument)arguments.get("port")).setValue(port);
      vm = connector.attach(arguments);
      breakpointEvents = new LinkedList<BreakpointEvent>();
      eventCollector = new EventCollector(vm.eventQueue(), new EventHandler()
      {
         @Override
         public void handleEvents(EventSet events)
         {
            boolean resume = true;
            for (Event event : events)
            {
               if (event instanceof BreakpointEvent)
               {
                  breakpointEvents.offer((BreakpointEvent)event);
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

   /** Close connection to the target JVM. */
   public void disconnect()
   {
      eventCollector.stop();
      vm.dispose();
   }

   /**
    * Add new break point.
    *
    * @param breakPoint break point description
    * @throws InvalidBreakPoint if description of break point is invalid (specified line number or class name is
    * invalid)
    */
   public void addBreakPoint(BreakPoint breakPoint) throws InvalidBreakPoint
   {
      List<ReferenceType> classes = vm.classesByName(breakPoint.getClassName());
      if (classes.isEmpty())
      {
         throw new InvalidBreakPoint("Class " + breakPoint.getClassName() + " not found. ");
      }
      ReferenceType clazz = classes.get(0);
      List<Location> locations;
      try
      {
         locations = clazz.locationsOfLine(breakPoint.getLineNumber());
      }
      catch (AbsentInformationException e)
      {
         throw new InvalidBreakPoint(e.getMessage(), e);
      }
      catch (ClassNotPreparedException e)
      {
         throw new IllegalStateException(e.getMessage(), e);
      }

      if (locations.isEmpty())
      {
         throw new InvalidBreakPoint("Line " + breakPoint.getLineNumber()
            + " not found in class " + breakPoint.getClassName());
      }

      Location location = locations.get(0);
      if (location.method() == null)
      {
         // Line is out of method.
         throw new InvalidBreakPoint("Invalid line " + breakPoint.getLineNumber()
            + " in class " + breakPoint.getClassName());
      }

      EventRequest breakPointRequest = vm.eventRequestManager().createBreakpointRequest(location);
      breakPointRequest.setSuspendPolicy(EventRequest.SUSPEND_ALL);
      breakPointRequest.setEnabled(breakPoint.isEnabled());
   }

   /**
    * Get break points.
    *
    * @return list of break points
    */
   public List<BreakPoint> getBreakPoints()
   {
      List<BreakpointRequest> breakpointRequests = vm.eventRequestManager().breakpointRequests();
      List<BreakPoint> breakPoints = new ArrayList<BreakPoint>(breakpointRequests.size());
      for (BreakpointRequest breakpointRequest : breakpointRequests)
      {
         Location location = breakpointRequest.location();
         breakPoints.add(new BreakPointImpl(
            location.declaringType().name(),
            location.lineNumber(),
            breakpointRequest.isEnabled())
         );
      }
      return breakPoints;
   }

   /**
    * Switch enable status of break point.
    *
    * @param breakPoint break point description
    */
   public void switchBreakPoint(BreakPoint breakPoint)
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

   public void resume()
   {
      vm.resume();
   }

   /**
    * Returns the name of the target Java VM.
    *
    * @return JVM name
    */
   public String getVmName()
   {
      return vm.name();
   }

   /**
    * Returns the version of the target Java VM.
    *
    * @return JVM version
    */
   public String getVmVersion()
   {
      return vm.version();
   }
}
