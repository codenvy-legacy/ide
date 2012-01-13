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
package org.exoplatform.ide.client.operation.uploadfile;

/**
 * Helper to parse response from server, that was got in SubmitCompleteHandler.
 * <p/>
 * Now it is used only for uploading files and zipped folders, but it can be used for other purposes.
 * <p/>
 * If while uploading was exception, than error message will be received in such form:
 * <code>&lt;pre&gt;Code: &lt;exit-code&gt; Text: &lt;error message&gt; &lt;/pre&gt;<code>
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: UploadHelper.java Nov 15, 2011 12:35:34 PM vereshchaka $
 */
public class UploadHelper
{

   public static class ErrorData
   {
      public ErrorData(int code, String text)
      {
         this.code = code;
         this.text = text;
      }

      public int code;

      public String text;
   }

   /**
    * Parse error message in such form:
    * <p/>
    * <code>&lt;pre&gt;Code: &lt;exit-code&gt; Text: &lt;error message&gt; &lt;/pre&gt;<code>.
    * 
    * @param errorMsg
    * @return
    */
   public static ErrorData parseError(String errorMsg)
   {
      String[] res = errorMsg.split("^<pre>Code: | Text: |</pre>$");
      return new ErrorData(Integer.valueOf(res[1]).intValue(), res[2]);
   }

}
