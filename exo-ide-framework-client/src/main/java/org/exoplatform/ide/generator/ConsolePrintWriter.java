package org.exoplatform.ide.generator;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/*
 * Author Vitaliy Gulyy <mailto:gavrikvetal@gmail.com>
 */

public class ConsolePrintWriter extends PrintWriter {
   
   private PrintWriter nativePrintWriter;
   
   public ConsolePrintWriter(PrintWriter nativePrintWriter) {
      super(new ByteArrayOutputStream());
      this.nativePrintWriter = nativePrintWriter;
   }
   
   public void write(String s) {
      System.out.println(s);
      nativePrintWriter.write(s);
    }
   
   public void write(String format, Object... args) {
      System.out.println(String.format(format, args));
      nativePrintWriter.printf(format, args);
      System.out.println();
      nativePrintWriter.println();
    }
   
   @Override
   public void print(String s) {
      System.out.print(s);
      nativePrintWriter.print(s);
   }
   
   @Override
   public void println() {
      System.out.println();
      nativePrintWriter.println();
   }
   
   @Override
   public void println(String s) {
      System.out.println(s);
      nativePrintWriter.println(s);
   }
   
   @Override
   public void flush() {
      nativePrintWriter.flush();
      super.flush();
   }
   
   @Override
   public void close() {
      nativePrintWriter.close();
      super.close();
   }
   
   @Override
   public boolean checkError() {
      return nativePrintWriter.checkError();
   }

}
