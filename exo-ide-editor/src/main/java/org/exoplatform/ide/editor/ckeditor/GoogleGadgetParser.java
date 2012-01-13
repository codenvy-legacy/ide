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
package org.exoplatform.ide.editor.ckeditor;

public class GoogleGadgetParser
{

   private static String prefixPattern = "<Content\\s+type\\s*=\\s*[\"']html[\"']\\s*>[\\s\\r\\n]*<!\\[CDATA\\[";

   private static String suffixPattern = "\\]\\]>[\\s\\r\\n]*<\\/Content\\s*>";

   /**
    * @param code - all gadget content
    * @return prefix of gadget from start to CDATA open tag "<![CDATA["
    */
   protected static native String getPrefix(String code) /*-{
                                                         if (@org.exoplatform.ide.editor.ckeditor.GoogleGadgetParser::hasContentSection(Ljava/lang/String;)(code))
                                                         {
                                                         var prefixPattern = new RegExp(@org.exoplatform.ide.editor.ckeditor.GoogleGadgetParser::prefixPattern, "im");
                                                         
                                                         var codeWhithoutPrefix = code.split(prefixPattern)[1];
                                                         var codeWhithoutPrefixStartPosition = code.indexOf(codeWhithoutPrefix);
                                                         return code.substring(0, codeWhithoutPrefixStartPosition);
                                                         }
                                                         else
                                                         {
                                                         return "";
                                                         }
                                                         }-*/;

   /**
    * @param code - gadget content
    * @return Content section between Content-CDATA tags
    */
   protected static native String getContentSection(String code) /*-{     
                                                                 if (@org.exoplatform.ide.editor.ckeditor.GoogleGadgetParser::hasContentSection(Ljava/lang/String;)(code))
                                                                 {
                                                                 var prefixPattern = new RegExp(@org.exoplatform.ide.editor.ckeditor.GoogleGadgetParser::prefixPattern, "im");
                                                                 var suffixPattern = new RegExp(@org.exoplatform.ide.editor.ckeditor.GoogleGadgetParser::suffixPattern, "im");
                                                                 
                                                                 var codeWhithoutPrefix = code.split(prefixPattern)[1];
                                                                 var suffixCodeStartPosition = codeWhithoutPrefix.search(suffixPattern);
                                                                 
                                                                 code = codeWhithoutPrefix.substring(0, suffixCodeStartPosition);
                                                                 }

                                                                 return code;
                                                                 }-*/;

   /**
    * @param code - all gadget content
    * @return suffix of gadget from end of Content section to end of file
    */
   protected static native String getSuffix(String code) /*-{
                                                         if (@org.exoplatform.ide.editor.ckeditor.GoogleGadgetParser::hasContentSection(Ljava/lang/String;)(code))
                                                         {
                                                         var suffixCodeStartPosition = code.search(new RegExp(@org.exoplatform.ide.editor.ckeditor.GoogleGadgetParser::suffixPattern, "im"));
                                                         return code.substring(suffixCodeStartPosition);
                                                         }
                                                         else
                                                         {
                                                         return "";
                                                         }
                                                         }-*/;

   /**
    * @param code - gadget content
    * @return <b>true</b>
    */
   protected static native boolean hasContentSection(String code) /*-{
                                                                  var prefixPattern = new RegExp(@org.exoplatform.ide.editor.ckeditor.GoogleGadgetParser::prefixPattern, "im");
                                                                  var suffixPattern = new RegExp(@org.exoplatform.ide.editor.ckeditor.GoogleGadgetParser::suffixPattern, "im");
                                                                  var prefixCodeStartPosition = code.search(prefixPattern);
                                                                  var suffixCodeStartPosition = code.search(suffixPattern);
                                                                  
                                                                  return prefixPattern.test(code) 
                                                                  && suffixPattern.test(code)
                                                                  && (prefixCodeStartPosition < suffixCodeStartPosition);
                                                                  }-*/;
}