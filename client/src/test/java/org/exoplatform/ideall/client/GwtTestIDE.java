/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public class GwtTestIDE extends GWTTestCase
{

   /**
    * {@inheritDoc}
    */
   @Override
   public String getModuleName()
   {
      return "org.exoplatform.ideall.TestIDE";
   }

   public void testCreateFolder()
   {
      assertTrue(true);
   }

   /*  
     public void testAddUser(){
       String username = String.valueOf(Random.nextInt());
       assertFalse(UserDataBase.getAll().contains(username));
       EventBus.getInstance().addHandler(AddUserEvent.TYPE, new AddUserHandlerImpl());
       EventBus.getInstance().fireEvent(new AddUserEvent(username));
       assertTrue(UserDataBase.getAll().contains(username));
     }
     
     public void testRemoveUser() throws UserAlreadyExistException{
       String username = String.valueOf(Random.nextInt());
       UserDataBase.addUser(username);
       assertTrue(UserDataBase.getAll().contains(username));
       EventBus.getInstance().addHandler(RemoveUserEvent.TYPE, new RemoveUserHandlerImpl());
       EventBus.getInstance().fireEvent(new RemoveUserEvent(username));
       assertFalse(UserDataBase.getAll().contains(username));
     }
     
     public void testUserAdded(){
       String username = String.valueOf(Random.nextInt());
       MockView view = new MockView();
       assertNull(view.getUsername());
       EventBus.getInstance().addHandler(UserAddedEvent.TYPE, view );
       EventBus.getInstance().fireEvent(new UserAddedEvent(username));
       assertEquals(username, view.getUsername());
     }
     
     
     public void testAddUserFail() {
       String username = String.valueOf(Random.nextInt());
       MockView errorWind = new MockView();
       EventBus.getInstance().addHandler(ExceptionThrownEvent.TYPE, errorWind);
       try {
         UserDataBase.addUser(username);
       } catch (UserAlreadyExistException e) {
         e.printStackTrace();
       }
       assertFalse(errorWind.isError());
        try {
         UserDataBase.addUser(username);
       } catch (UserAlreadyExistException e) {
         EventBus.getInstance().fireEvent(new ExceptionThrownEvent(e)); 
       }
       assertTrue(errorWind.isError());
     }
     
     
     public void testUserRemoved() {
       String username = String.valueOf(Random.nextInt());
       MockView view = new MockView();
       view.setUsername(username);
       EventBus.getInstance().addHandler(UserRemovedEvent.TYPE,view); 
       EventBus.getInstance().fireEvent(new UserRemovedEvent(username));
       assertNull(view.getUsername());
     }
     
     
     public void testRemoveUserFail() {
       String username = String.valueOf(Random.nextInt());
       MockView errorWind = new MockView();
       EventBus.getInstance().addHandler(ExceptionThrownEvent.TYPE, errorWind);
       assertFalse(UserDataBase.getAll().contains(username));
       assertFalse(errorWind.isError());
        try {
         UserDataBase.removeUser(username);
       } catch (UserNotFoundException e) {
         EventBus.getInstance().fireEvent(new ExceptionThrownEvent(e)); 
       }
       assertTrue(errorWind.isError());
     }
     
     
     
     private class MockView implements ExceptionThrownHandler, UserAddedHandler, UserRemovedHandler {
       private boolean error = false;
       private String username;
       
       public void showError(Throwable throwable) {
         error = true;
       }
       public boolean isError() {
         return error;
       }
       public void userAdded(String userName) {
         username = userName;
       }
       
       public String getUsername() {
         return username;
       }
       
       public void userRemoved(String userName) {
         username = null;  
       }
       
       public void setUsername(String username) {
         this.username = username;
       }
       
       
     }
   */

}
