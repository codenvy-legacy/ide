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

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.ide.git.client.marshaller.RemoteListUnmarshaller;
import org.exoplatform.ide.git.shared.Remote;

import java.util.ArrayList;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 28, 2011 9:55:30 AM anya $
 * 
 */
public class RemoteListUnmarshallerGwtTest extends BaseGwtTest
{
   private final String REMOTE_LIST_RESPONSE = "[{\"name\":\"origin\",\"url\":\"git/anya/remoteToAdd\"}]";

   /**
    * Test remote list response unmarshaller.
    */
   public void testLogResponseUnmarshaller()
   {
      java.util.List<Remote> remotes = new ArrayList<Remote>();

      RemoteListUnmarshaller unmarshaller = new RemoteListUnmarshaller(remotes);
      try
      {
         unmarshaller.unmarshal(new MockResponse(REMOTE_LIST_RESPONSE));
      }
      catch (UnmarshallerException e)
      {
         fail(e.getMessage());
      }

      assertEquals(1, remotes.size());
      Remote remote = remotes.get(0);
      assertEquals("git/anya/remoteToAdd", remote.getUrl());
      assertEquals("origin", remote.getName());
   }
}
