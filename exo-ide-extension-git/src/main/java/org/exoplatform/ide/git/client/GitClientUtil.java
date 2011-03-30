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
package org.exoplatform.ide.git.client;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 23, 2011 4:05:56 PM anya $
 *
 */
public class GitClientUtil
{
   /**
    * The name of mounted folder on local file system.
    */
   public static final String MOUNTED_FOLDER = "git";

   /**
    * WebDav's context.
    */
   public static final String WEBDAV_CONTEXT = "jcr/";

   /**
    * Form the working directory of the Git repository 
    * (including the path to mounted folder on file system).
    * 
    * The working directory of href:<br>
    * http://[host]:[port]/rest/private/jcr/repository/dev-monit/test/<br>
    * is :<br>
    * [mounted folder]/test/
    * 
    * @param href location of the folder for Git repository
    * @param restContext rest context
    * @return {@link String} path to working directory
    */
   public static String getWorkingDirFromHref(String href, String restContext)
   {
      String context = (restContext.endsWith("/")) ? restContext + WEBDAV_CONTEXT : restContext + "/" + WEBDAV_CONTEXT;
      href = href.substring(href.indexOf(context) + context.length());
      String[] parts = href.split("/");
      href = (parts.length > 1) ? href.replace(parts[0] + "/" + parts[1], "") : href;
      href = (href.startsWith("/")) ? MOUNTED_FOLDER + href : MOUNTED_FOLDER + "/" + href;
      return href;
   }

   /**
    * Get the pattern of the file or folder, which can be used for setting patterns in Git requests.
    * For example, file with the following href : <br>
    *  <pre>
    *  http://127.0.0.1:8888/rest/private/jcr/repository/dev-monit/repo/test/file.html
    *  </pre>
    * working tree href:<br>
    * <pre>
    * http://127.0.0.1:8888/rest/private/jcr/repository/dev-monit/repo
    * </pre>
    * The patten is <pre>test/file.html</pre> .
    * 
    * @param href href of the item, for which to return pattern
    * @param workTree the location of the Git work tree, which contains the item pointed by href
    * @return {@link String} pattern
    */
   public static String getFilePatternByHref(String href, String workTree)
   {
      workTree = workTree.endsWith("/.git") ? workTree.substring(0, workTree.lastIndexOf("/.git")) : workTree;
      //Remove last "/" from path if exists:
      String pattern = href.endsWith("/") ? href.substring(0, href.length() - 1) : href;
      pattern = pattern.replace(workTree, "");
      //Remove first "/" if exists:
      pattern = (pattern.length() > 0 && pattern.startsWith("/")) ? pattern.replaceFirst("/", "") : pattern;
      return pattern;
   }
}
