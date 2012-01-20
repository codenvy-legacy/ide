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

import org.exoplatform.gwtframework.commons.rest.copy.UnmarshallerException;
import org.exoplatform.ide.git.client.marshaller.StatusResponse;
import org.exoplatform.ide.git.client.marshaller.StatusResponseUnmarshaller;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 28, 2011 9:55:30 AM anya $
 * 
 */
public class StatusResponseUnmarshallerGwtTest extends BaseGwtTest
{
   private final String STATUS_RESPONSE =
      "{\"changedNotCommited\":[{\"status\":\"NEW\",\"path\":\"file.xml\"}],\"branchName\":\"master\",\"changedNotUpdated\":[{\"status\":\"MODIFIED\",\"path\":\"testPullFromRemote.xml\"}],\"untracked\":[{\"status\":\"UNTRACKED\",\"path\":\"file.html\"},{\"status\":\"UNTRACKED\",\"path\":\"test1.jsp\"}]}";

   /**
    * Test the status response unmarshaller.
    */
   public void testStatusResponseUnmarshaller()
   {
      StatusResponse statusResponse = new StatusResponse();
      StatusResponseUnmarshaller unmarshaller = new StatusResponseUnmarshaller(statusResponse, false);
      try
      {
         unmarshaller.unmarshal(new MockResponse(STATUS_RESPONSE));
      }
      catch (UnmarshallerException e)
      {
         fail(e.getMessage());
      }

      assertNotNull(statusResponse.getUntracked());
      assertEquals(2, statusResponse.getUntracked().size());
      assertEquals(1, statusResponse.getChangedNotUpdated().size());
      assertEquals(1, statusResponse.getChangedNotCommited().size());

      assertEquals("file.xml", statusResponse.getChangedNotCommited().get(0).getPath());
      assertEquals("testPullFromRemote.xml", statusResponse.getChangedNotUpdated().get(0).getPath());
      assertEquals("file.html", statusResponse.getUntracked().get(0).getPath());
      assertEquals("test1.jsp", statusResponse.getUntracked().get(1).getPath());
   }
}
