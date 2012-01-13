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
package org.exoplatform.ide.extension.java.shared;

import java.util.Map;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class MavenResponse
{
   private int exitCode;

   private String output;

   private Map<String, String> result;

   public MavenResponse(int exitCode, String output, Map<String, String> result)
   {
      this.exitCode = exitCode;
      this.output = output;
      this.result = result;
   }

   public MavenResponse()
   {
   }

   public int getExitCode()
   {
      return exitCode;
   }

   public void setExitCode(int exitCode)
   {
      this.exitCode = exitCode;
   }

   public String getOutput()
   {
      return output;
   }

   public void setOutput(String output)
   {
      this.output = output;
   }

   public Map<String, String> getResult()
   {
      return result;
   }

   public void setResult(Map<String, String> result)
   {
      this.result = result;
   }
}
