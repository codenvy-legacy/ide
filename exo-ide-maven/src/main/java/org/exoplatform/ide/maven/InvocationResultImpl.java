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
package org.exoplatform.ide.maven;

import org.apache.maven.shared.invoker.InvocationResult;
import org.codehaus.plexus.util.cli.CommandLineException;

import java.io.File;
import java.io.IOException;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: InvocationResultImpl.java 16504 2011-02-16 09:27:51Z andrew00x $
 */
public class InvocationResultImpl implements InvocationResult
{
   private final int exitCode;
   private final CommandLineException cle;
   private final File projectDirectory;
   private final ResultGetter resultGetter;

   public InvocationResultImpl(int exitCode,
                               CommandLineException cle,
                               File projectDirectory,
                               ResultGetter resultGetter)
   {
      this.exitCode = exitCode;
      this.cle = cle;
      this.projectDirectory = projectDirectory;
      this.resultGetter = resultGetter;
   }

   /** @see org.apache.maven.shared.invoker.InvocationResult#getExitCode() */
   public int getExitCode()
   {
      return exitCode;
   }

   /** @see org.apache.maven.shared.invoker.InvocationResult#getExecutionException() */
   public CommandLineException getExecutionException()
   {
      return cle;
   }

   /**
    * Result of maven build.
    *
    * @return maven build result (typically artifact). May be <code>null</code> if build is failed. If build is
    *         successful (<code>exitCode == 0</code>) then may not be <code>null</code>.
    */
   public Result getResult() throws IOException
   {
      return 0 == exitCode ? resultGetter.getResult(projectDirectory) : null;
   }

}