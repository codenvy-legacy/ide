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

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;

import java.io.IOException;
import java.util.Map;

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
   }

   /** Close connection to the target JVM. */
   public void disconnect()
   {
      vm.dispose();
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
