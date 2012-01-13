/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.extension.gadget.server.opensocial.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 19, 2010 $
 * 
 */
public class Message
{

   /**
    * Identifies the application that generated this message.
    */
   private String appUrl;

   /**
    * The main text of the message
    */
   private String body;

   /**
    * Specifies the message ID to use in the gadget xml.
    */
   private String bodyId;

   /**
    * Identifies the messages collection IDs this message is contained in.
    */
   private List<String> collectionIds;

   /**
    * Unique ID for this message.
    */
   private String id;

   /**
    * Message ID, used for threaded comments/messages.
    */
   private String inReplyTo;

   /**
    * Array of person IDs.
    */
   private List<String> recipients;

   /**
    * Array of message ids.
    */
   private List<String> replies;

   /**
    * Id of person who sent the message.
    */
   private String senderId;

   /**
    * Status of the message. (NEW, READ, DELETED).
    */
   private Status status;

   /**
    * UTC time message was sent.
    */
   private Date timeSent;

   /**
    * The title of the message.
    */
   private String title;

   /**
    * Specifies the message ID to use in the gadget xml.
    */
   private String titleId;

   /**
    * The type of the message.
    */
   private String type;

   /**
    * Last update for this message.
    */
   private Date updated;

   /**
    * List of related URLs for this message.
    */
   private List<String> urls;

   /**
    * @return the appUrl
    */
   public String getAppUrl()
   {
      return appUrl;
   }

   /**
    * @param appUrl the appUrl to set
    */
   public void setAppUrl(String appUrl)
   {
      this.appUrl = appUrl;
   }

   /**
    * @return the body
    */
   public String getBody()
   {
      return body;
   }

   /**
    * @param body the body to set
    */
   public void setBody(String body)
   {
      this.body = body;
   }

   /**
    * @return the bodyId
    */
   public String getBodyId()
   {
      return bodyId;
   }

   /**
    * @param bodyId the bodyId to set
    */
   public void setBodyId(String bodyId)
   {
      this.bodyId = bodyId;
   }

   /**
    * @return the collectionIds
    */
   public List<String> getCollectionIds()
   {
      if (collectionIds == null)
      {
         collectionIds = new ArrayList<String>();
      }
      return collectionIds;
   }

   /**
    * @param collectionIds the collectionIds to set
    */
   public void setCollectionIds(List<String> collectionIds)
   {
      this.collectionIds = collectionIds;
   }

   /**
    * @return the id
    */
   public String getId()
   {
      return id;
   }

   /**
    * @param id the id to set
    */
   public void setId(String id)
   {
      this.id = id;
   }

   /**
    * @return the inReplyTo
    */
   public String getInReplyTo()
   {
      return inReplyTo;
   }

   /**
    * @param inReplyTo the inReplyTo to set
    */
   public void setInReplyTo(String inReplyTo)
   {
      this.inReplyTo = inReplyTo;
   }

   /**
    * @return the recipients
    */
   public List<String> getRecipients()
   {
      if (recipients == null)
      {
         recipients = new ArrayList<String>();
      }
      return recipients;
   }

   /**
    * @param recipients the recipients to set
    */
   public void setRecipients(List<String> recipients)
   {
      if (recipients == null)
      {
         recipients = new ArrayList<String>();
      }
      this.recipients = recipients;
   }

   /**
    * @return the replies
    */
   public List<String> getReplies()
   {
      if (replies == null)
      {
         replies = new ArrayList<String>();
      }
      return replies;
   }

   /**
    * @param replies the replies to set
    */
   public void setReplies(List<String> replies)
   {
      this.replies = replies;
   }

   /**
    * @return the senderId
    */
   public String getSenderId()
   {
      return senderId;
   }

   /**
    * @param senderId the senderId to set
    */
   public void setSenderId(String senderId)
   {
      this.senderId = senderId;
   }

   /**
    * @return the status
    */
   public Status getStatus()
   {
      return status;
   }

   /**
    * @param status the status to set
    */
   public void setStatus(Status status)
   {
      this.status = status;
   }

   /**
    * @return the timeSent
    */
   public Date getTimeSent()
   {
      return timeSent;
   }

   /**
    * @param timeSent the timeSent to set
    */
   public void setTimeSent(Date timeSent)
   {
      this.timeSent = timeSent;
   }

   /**
    * @return the title
    */
   public String getTitle()
   {
      return title;
   }

   /**
    * @param title the title to set
    */
   public void setTitle(String title)
   {
      this.title = title;
   }

   /**
    * @return the titleId
    */
   public String getTitleId()
   {
      return titleId;
   }

   /**
    * @param titleId the titleId to set
    */
   public void setTitleId(String titleId)
   {
      this.titleId = titleId;
   }

   /**
    * @return the type
    */
   public String getType()
   {
      return type;
   }

   /**
    * @param type the type to set
    */
   public void setType(String type)
   {
      this.type = type;
   }

   /**
    * @return the updated
    */
   public Date getUpdated()
   {
      return updated;
   }

   /**
    * @param updated the updated to set
    */
   public void setUpdated(Date updated)
   {
      this.updated = updated;
   }

   /**
    * @return the urls
    */
   public List<String> getUrls()
   {
      if (urls == null)
      {
         urls = new ArrayList<String>();
      }
      return urls;
   }

   /**
    * @param urls the urls to set
    */
   public void setUrls(List<String> urls)
   {
      this.urls = urls;
   }
}
