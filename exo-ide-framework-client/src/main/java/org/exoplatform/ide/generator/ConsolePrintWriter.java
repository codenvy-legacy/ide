/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.generator;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * Used for debuging the class generation.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 28, 2010 $
 * 
 */
public class ConsolePrintWriter extends PrintWriter
{

   private PrintWriter nativePrintWriter;

   /**
    * @param nativePrintWriter
    */
   public ConsolePrintWriter(PrintWriter nativePrintWriter)
   {
      // super(System.out, true);
      super(new ByteArrayOutputStream(), true);
      this.nativePrintWriter = nativePrintWriter;
   }

   /**
    * @see java.io.PrintWriter#write(java.lang.String)
    */
   public void write(String s)
   {
      nativePrintWriter.write(s);
      super.write(s);
   }

   /**
    * @param format
    * @param args
    */
   public void write(String format, Object... args)
   {
      nativePrintWriter.printf(format, args);
      nativePrintWriter.println();
      super.write(String.format(format, args));
      super.println();
   }

   /**
    * @see java.io.PrintWriter#print(java.lang.String)
    */
   @Override
   public void print(String s)
   {
      nativePrintWriter.print(s);
      super.println(s);
   }

   /**
    * @see java.io.PrintWriter#println()
    */
   @Override
   public void println()
   {
      nativePrintWriter.println();
      super.println();
   }

   /**
    * @see java.io.PrintWriter#flush()
    */
   @Override
   public void flush()
   {
      nativePrintWriter.flush();
      super.flush();
   }

   /**
    * @see java.io.PrintWriter#close()
    */
   @Override
   public void close()
   {
      nativePrintWriter.close();
      super.close();
   }

   /**
    * @see java.io.PrintWriter#checkError()
    */
   @Override
   public boolean checkError()
   {
      return nativePrintWriter.checkError();
   }
}
