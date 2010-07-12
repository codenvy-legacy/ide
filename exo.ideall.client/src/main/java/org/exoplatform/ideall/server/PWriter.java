/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.server;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class PWriter extends PrintWriter
{
   
   public PWriter(OutputStream out)
   {
      super(out);
      // TODO Auto-generated constructor stub
   }

   private PrintWriter nativeWriter;
   
   public PWriter(PrintWriter nativeWriter) {
      super(new ByteArrayOutputStream());
      this.nativeWriter = nativeWriter;
   }
   
   @Override
   public void print(String s)
   {
      System.out.print(s);
      nativeWriter.print(s);
   }
   
   @Override
   public void println()
   {
      System.out.println();
      nativeWriter.println();
   }   

}
