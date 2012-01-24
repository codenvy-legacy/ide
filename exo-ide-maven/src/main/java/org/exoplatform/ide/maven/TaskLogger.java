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

import org.apache.maven.shared.invoker.InvocationOutputHandler;

import java.io.IOException;
import java.io.Reader;

/**
 * Maven build logger.
 *
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface TaskLogger extends InvocationOutputHandler
{
   /**
    * Get Reader of build log.
    *
    * @return reader
    * @throws java.io.IOException if any i/o errors occur
    */
   Reader getLogReader() throws IOException;

   /**
    * Get build log as String.
    *
    * @return build log as String
    * @throws java.io.IOException if any i/o errors occur
    */
   String getLogAsString() throws IOException;

   /** Close current TaskLogger. It should release any resources allocated by the TaskLogger such as file, etc. */
   void close();
}
