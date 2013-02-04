/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.mail;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class receives email
 */
public class MailReceiver
{
   private static final Logger LOG = LoggerFactory.getLogger(MailCheck.class);

   private static String HOST = "imap.gmail.com";

   private static Message[] MESSAGES;

   private static Folder FOLDER;

   private static Store STORE;

   /*
    * The method receives email messages from host
    */
   public static void receiveEmail(String user, String password) throws TimeoutException, MessagingException
   {
      Properties properties = System.getProperties(); // Get system properties
      Session session = Session.getDefaultInstance(properties); // Get the default Session object.
      Store store = session.getStore("imaps"); // Get a Store object that implements the specified protocol.
      try
      {
         store.connect(HOST, user, password); //Connect to the current host using the specified username and password.
      }
      catch (Exception e)
      {
         throw new TimeoutException("Impossible connect to host : " + HOST + " /user -> " + user + " /password -> "
            + password);
      }
      Folder folder = store.getFolder("inbox"); //Create a Folder object corresponding to the given name.
      folder.open(Folder.READ_ONLY); // Open the Folder.
      MailReceiver.STORE = store;
      MailReceiver.FOLDER = folder;
      MailReceiver.MESSAGES = folder.getMessages();
   }

   /*
    * The method receives email messages from host
    */
   public static void receiveInviteEmail(String user, String password) throws TimeoutException, MessagingException
   {
      Properties properties = System.getProperties(); // Get system properties
      Session session = Session.getDefaultInstance(properties); // Get the default Session object.
      Store store = session.getStore("imaps"); // Get a Store object that implements the specified protocol.
      try
      {
         store.connect(HOST, user, password); //Connect to the current host using the specified username and password.
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      Folder folder = store.getFolder("inbox"); //Create a Folder object corresponding to the given name.
      folder.open(Folder.READ_ONLY); // Open the Folder.

      Message[] messages = folder.getMessages();
      MailReceiver.STORE = store;
      MailReceiver.FOLDER = folder;
      MailReceiver.MESSAGES = messages;
   }

   public static void cleanMailBox(String user, String password) throws TimeoutException, MessagingException
   {
      Properties properties = System.getProperties(); // Get system properties
      Session session = Session.getDefaultInstance(properties); // Get the default Session object.
      Store store = session.getStore("imaps"); // Get a Store object that implements the specified protocol.
      try
      {
         store.connect(HOST, user, password); //Connect to the current host using the specified username and password.
      }
      catch (Exception e)
      {
         throw new TimeoutException("Impossible connect to host : " + HOST + " /user -> " + user + " /password -> "
            + password);
      }
      Folder folder = store.getFolder("inbox"); //Create a Folder object corresponding to the given name.
      folder.open(Folder.READ_WRITE); // Open the Folder.

      Message[] messages = folder.getMessages();
      for (int i = 0, n = messages.length; i < n; i++)
      {
         messages[i].setFlag(Flags.Flag.DELETED, true);

      }
      LOG.info(user + " mail box cleaned.");
      // Close connection 
      folder.close(true);
      store.close();
   }

   /*
    * The method gets appropriate string from message with specified parameters: Subject, Pattern for message body
    * to found 1-st e-mail: Subject = "Your Cloud IDE Domain", tenant = "/create?tenantName=selenium???&user-mail=test.cldide@gmail.com"
    * 2-nd e-mail: Subject = "Your Cloud IDE Domain", tenant = "Your Cloud IDE Domain","selenium???.cloud-ide.com/cloud/?username=test.cldide@gmail.com"
    * set correct tenantName = selenium???
    * @param subject - the subject of the message
    * @param tenantName - key string to search link
    * @return the link or null
    */
   public static String getLink(String subject, String tenantName) throws MessagingException, IOException
   {
      Date date = new Date(0);
      String msg = "null";
      for (int i = 0; i < MESSAGES.length; i++)
      {
         if (MESSAGES[i].getSubject().equals(subject) && (MESSAGES[i].getContent().toString().indexOf(tenantName) >= 0)
            && MESSAGES[i].getReceivedDate().after(date))
         {
            msg = MESSAGES[i].getContent().toString();
            date = MESSAGES[i].getReceivedDate();
            try
            {
               int startInd = msg.indexOf(tenantName);
               int endInd = msg.indexOf("\"", startInd + 10);
               msg = msg.substring(startInd, endInd);
            }
            catch (StringIndexOutOfBoundsException e)
            {
               throw new StringIndexOutOfBoundsException("Unable found the string in the message : " + msg);
            }
         }
      }
      return msg.equals("null") ? null : msg;
   }

   /**
    * Get full message
    * @param subject
    * @return invitation message
    * @throws MessagingException
    * @throws IOException
    */
   public String getFullMessage(String subject) throws MessagingException, IOException
   {
      Date date = new Date(0);
      String msg = "null";
      for (int i = 0; i < MESSAGES.length; i++)
      {
         if (MESSAGES[i].getSubject().equals(subject) && MESSAGES[i].getReceivedDate().after(date))
         {
            msg = MESSAGES[i].getContent().toString();
            date = MESSAGES[i].getReceivedDate();
         }
      }
      return msg.equals("null") ? null : msg;
   }

   @Override
   /*
    * The method closes opened resources before gc
    */
   protected void finalize() throws Throwable
   {
      try
      {
         FOLDER.close(true);
         STORE.close(); // close store
      }
      catch (Exception e)
      {
         throw new Exception("Error closing store MailReciever");
      }
      finally
      {
         super.finalize();
      }

   }

}