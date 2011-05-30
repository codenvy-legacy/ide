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
package org.exoplatform.ide.extension.ssh.client;

import org.easymock.EasyMock;
import org.exoplatform.ide.extension.ssh.client.keymanager.SshKeyManagerPresenter;
import org.exoplatform.ide.extension.ssh.client.keymanager.SshPublicKeyPresenter;
import org.exoplatform.ide.extension.ssh.client.keymanager.ui.SshPublicKeyView;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class TestTest
{
   
   private HandlerManager eventBus = new HandlerManager(null);
   
   private SshKeyService keyService;
   
   private SshPublicKeyPresenter publicKeyPresenter;
   
   private SshKeyManagerPresenter keyManagerPresenter; 
   
   private SshPublicKeyView keyView;
   
   private KeyItem keyItem;
   
   @Before
   public void setUp()
   {
      keyService = GWT.create(SshKeyService.class);
      keyItem = new KeyItem("some.host", "publicKeyURL", "removeKeyURL");
      keyManagerPresenter = new SshKeyManagerPresenter(keyService);
      publicKeyPresenter = new SshPublicKeyPresenter(keyService, keyItem, eventBus);
      keyView = EasyMock.createStrictMock(SshPublicKeyView.class);
      
   }
   
   @Test
   public void testViewPublicKey()
   {
      
      
   }
   

}
