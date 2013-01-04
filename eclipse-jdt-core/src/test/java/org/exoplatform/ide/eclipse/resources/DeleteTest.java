/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.eclipse.resources;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests deleting resources.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: DeleteTest.java Jan 3, 2013 11:10:48 AM azatsarynnyy $
 *
 */
public class DeleteTest extends ResourcesBaseTest
{
   private WorkspaceResource ws;

   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      ws = new WorkspaceResource(vfs);
   }

   @Test
   @Ignore("TODO")
   public void testDeleteProject() throws Exception
   {
   }

   @Test
   @Ignore("TODO")
   public void testDeleteFolder() throws Exception
   {
   }

   @Test
   @Ignore("TODO")
   public void testDeleteFile() throws Exception
   {
   }

}
