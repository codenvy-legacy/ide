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
package org.exoplatform.ide.extension.maven;

import org.apache.maven.shared.invoker.InvocationResult;
import org.codehaus.plexus.util.cli.CommandLineException;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: InvocationResultImpl.java 16504 2011-02-16 09:27:51Z andrew00x $
 */
public final class InvocationResultImpl implements InvocationResult
{
   private final int exitCode;
   private CommandLineException cle;

   public InvocationResultImpl(CommandLineException cle, int exitCode)
   {
      this.cle = cle;
      this.exitCode = exitCode;
   }

   public InvocationResultImpl(int exitCode)
   {
      this.exitCode = exitCode;
   }

   /**
    * @see org.apache.maven.shared.invoker.InvocationResult#getExecutionException()
    */
   public CommandLineException getExecutionException()
   {
      return cle;
   }

   /**
    * @see org.apache.maven.shared.invoker.InvocationResult#getExitCode()
    */
   public int getExitCode()
   {
      return exitCode;
   }
}