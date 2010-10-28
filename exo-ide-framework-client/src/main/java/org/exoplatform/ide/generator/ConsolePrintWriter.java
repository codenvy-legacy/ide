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
      //super(System.out, true);
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
