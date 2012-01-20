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
import org.exoplatform.ide.git.client.marshaller.LogResponse;
import org.exoplatform.ide.git.client.marshaller.LogResponseUnmarshaller;
import org.exoplatform.ide.git.shared.Revision;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 28, 2011 9:55:30 AM anya $
 * 
 */
public class LogUnmarshallerGwtTest extends BaseGwtTest
{
   private final String LOG_RESPONSE =
      "{\"commits\":[{\"id\":\"29d35254457424b88ba72b55decac7b4cc595987\",\"message\":\"added file for pull test\",\"commitTime\":1303915005000,\"committer\":{\"email\":\"\",\"name\":\"john\"}},"
         + "{\"id\":\"abeb5f9fc3785f4d97a6f5c8d056b0999a0c83e8\",\"message\":\"init\",\"commitTime\":1303914893000,\"committer\":{\"email\":\"admin@localhost\",\"name\":\"admin\"}}]}";

   /**
    * Test the log response unmarshaller.
    */
   public void testLogResponseUnmarshaller()
   {
      LogResponse logResponse = new LogResponse();
      LogResponseUnmarshaller unmarshaller = new LogResponseUnmarshaller(logResponse, false);
      try
      {
         unmarshaller.unmarshal(new MockResponse(LOG_RESPONSE));
      }
      catch (UnmarshallerException e)
      {
         fail(e.getMessage());
      }

      assertEquals(2, logResponse.getCommits().size());
      Revision revision = logResponse.getCommits().get(0);
      assertEquals("29d35254457424b88ba72b55decac7b4cc595987", revision.getId());
      assertEquals(1303915005000L, revision.getCommitTime());
      assertEquals("added file for pull test", revision.getMessage());
      assertNotNull(revision.getCommitter());
      assertEquals("", revision.getCommitter().getEmail());
      assertEquals("john", revision.getCommitter().getName());

      revision = logResponse.getCommits().get(1);
      assertEquals("abeb5f9fc3785f4d97a6f5c8d056b0999a0c83e8", revision.getId());
      assertEquals(1303914893000L, revision.getCommitTime());
      assertEquals("init", revision.getMessage());
      assertNotNull(revision.getCommitter());
      assertEquals("admin@localhost", revision.getCommitter().getEmail());
      assertEquals("admin", revision.getCommitter().getName());
   }
}
