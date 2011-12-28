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
package org.exoplatform.ide.shell;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Dec 26, 2011 3:17:11 PM evgen $
 *
 */
public class ShellTestWatcher extends TestWatcher
{

   /**
    * @see org.junit.rules.TestWatcher#failed(java.lang.Throwable, org.junit.runner.Description)
    */
   @Override
   protected void failed(Throwable e, Description description)
   {
      // Get test method name
      String testMethodName = null;
      for (StackTraceElement stackTrace : e.getStackTrace())
      {
         if (stackTrace.getClassName().equals(this.getClass().getName()))
         {
            testMethodName = stackTrace.getMethodName();
            break;
         }
      }

      try
      {
         byte[] sc = ((TakesScreenshot)BaseTest.driver).getScreenshotAs(OutputType.BYTES);
         File parent = new File("target/screenshots");
         parent.mkdirs();
         File file = new File(parent, this.getClass().getName() + "." + testMethodName + ".png");
         file.createNewFile();
         FileOutputStream outputStream = new FileOutputStream(file);
         outputStream.write(sc);
         outputStream.close();
      }
      catch (WebDriverException ex)
      {
//         ex.printStackTrace();
      }
      catch (FileNotFoundException ex)
      {
         ex.printStackTrace();
      }
      catch (IOException ex)
      {
         ex.printStackTrace();
      }
      finally
      {
         super.failed(e, description);
      }
   }

}
