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
package org.exoplatform.ide.extension.ssh.client.marshaller;

import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.ide.extension.ssh.shared.GenKeyRequest;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: GenerateKeysMarshaller May 19, 2011 11:02:54 AM evgen $
 * 
 */
public class GenerateSshKeysMarshaller implements Marshallable
{

   private GenKeyRequest genKey;

   /**
    * @param genKey
    */
   public GenerateSshKeysMarshaller(GenKeyRequest genKey)
   {
      super();
      this.genKey = genKey;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal()
    */
   @Override
   public String marshal()
   {
      JSONObject jsonObject = new JSONObject();
      JSONValue hostValue = (genKey.getHost() == null) ? JSONNull.getInstance() : new JSONString(genKey.getHost());
      jsonObject.put("host", hostValue);

      JSONValue commentValue =
         (genKey.getComment() == null) ? JSONNull.getInstance() : new JSONString(genKey.getComment());
      jsonObject.put("comment", commentValue);

      JSONValue passphraseValue =
         (genKey.getPassphrase() == null) ? JSONNull.getInstance() : new JSONString(genKey.getPassphrase());
      jsonObject.put("passphrase", passphraseValue);

      return jsonObject.toString();
   }
}
