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

import com.google.gwt.json.client.JSONObject;

import org.exoplatform.ide.git.client.marshaller.CloneRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.Constants;
import org.exoplatform.ide.git.shared.CloneRequest;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 28, 2011 9:55:30 AM anya $
 * 
 */
public class CloneMarshallerGwtTest extends BaseGwtTest
{
   /**
    * Test clone request marshaller.
    */
   public void testCloneRequestMarshaller()
   {
      String remoteUri = "path/to/remote/repository";
      String remoteName = "myRemote";
      String workingDir = "path/to/my/repo";

      CloneRequest cloneRequest = new CloneRequest(remoteUri, null, workingDir, remoteName, 0);
      CloneRequestMarshaller marshaller = new CloneRequestMarshaller(cloneRequest);
      String json = marshaller.marshal();

      assertNotNull(json);

      JSONObject jsonObject = new JSONObject(build(json));
      assertTrue(jsonObject.containsKey(Constants.REMOTE_URI));
      assertEquals(remoteUri, jsonObject.get(Constants.REMOTE_URI).isString().stringValue());

      assertTrue(jsonObject.containsKey(Constants.WORKNG_DIR));
      assertEquals(workingDir, jsonObject.get(Constants.WORKNG_DIR).isString().stringValue());

      assertTrue(jsonObject.containsKey(Constants.REMOTE_NAME));
      assertEquals(remoteName, jsonObject.get(Constants.REMOTE_NAME).isString().stringValue());
   }
}
