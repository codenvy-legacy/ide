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
package org.exoplatform.ide.git.client.marshaller;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.ide.git.shared.GitUser;
import org.exoplatform.ide.git.shared.Revision;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 31, 2011 11:15:57 AM anya $
 *
 */
public class RevisionUnmarshaller extends JSONUmarshaller
{

   /**
    * Represents revision info.
    */
   private Revision revision;

   /**
    * @param revision revision information
    */
   public RevisionUnmarshaller(Revision revision)
   {
      this.revision = revision;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      JavaScriptObject json = build(response.getText());
      if (json == null)
         return;
      JSONObject revisionObject = new JSONObject(json).isObject();
      if (revisionObject == null)
         return;

      String id =
         (revisionObject.get(ID) != null && revisionObject.get(ID).isString() != null) ? revisionObject.get(ID)
            .isString().stringValue() : "";
      revision.setId(id);
      String message =
         (revisionObject.get(MESSAGE) != null && revisionObject.get(MESSAGE).isString() != null) ? revisionObject
            .get(MESSAGE).isString().stringValue() : "";
      revision.setMessage(message);
      long commitTime =
         (long)((revisionObject.get(COMMIT_TIME) != null && revisionObject.get(COMMIT_TIME).isNumber() != null)
            ? revisionObject.get(COMMIT_TIME).isNumber().doubleValue() : 0);
      System.out.println("RevisionUnmarshaller.unmarshal()" + commitTime);
      revision.setCommitTime(commitTime);
      if (revisionObject.get(COMMITTER) != null && revisionObject.get(COMMITTER).isObject() != null)
      {
         JSONObject committerObject = revisionObject.get(COMMITTER).isObject();
         String name =
            (committerObject.containsKey(NAME) && committerObject.get(NAME).isString() != null) ? committerObject
               .get(NAME).isString().stringValue() : "";
         String email =
            (committerObject.containsKey(EMAIL) && committerObject.get(EMAIL).isString() != null) ? committerObject
               .get(EMAIL).isString().stringValue() : "";

         GitUser gitUser = new GitUser(name, email);
         revision.setCommitter(gitUser);
      }
   }
}
