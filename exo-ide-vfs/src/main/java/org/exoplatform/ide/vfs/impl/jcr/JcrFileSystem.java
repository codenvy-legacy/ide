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
package org.exoplatform.ide.vfs.impl.jcr;

import org.exoplatform.ide.vfs.ConstraintException;
import org.exoplatform.ide.vfs.InvalidArgumentException;
import org.exoplatform.ide.vfs.ItemList;
import org.exoplatform.ide.vfs.LockException;
import org.exoplatform.ide.vfs.LockToken;
import org.exoplatform.ide.vfs.NotSupportedException;
import org.exoplatform.ide.vfs.ObjectId;
import org.exoplatform.ide.vfs.ObjectNotFoundException;
import org.exoplatform.ide.vfs.PermissionDeniedException;
import org.exoplatform.ide.vfs.Property;
import org.exoplatform.ide.vfs.PropertyFilter;
import org.exoplatform.ide.vfs.Query;
import org.exoplatform.ide.vfs.VersionId;
import org.exoplatform.ide.vfs.VirtualFileSystem;
import org.exoplatform.ide.vfs.model.AccessControlEntry;
import org.exoplatform.ide.vfs.model.Document;
import org.exoplatform.ide.vfs.model.Item;
import org.exoplatform.ide.vfs.model.VirtualFileSystemInfo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class JcrFileSystem implements VirtualFileSystem
{
   private static void checkName(String name) throws InvalidArgumentException
   {
      if (name == null || name.trim().length() == 0)
         throw new InvalidArgumentException("Item's name is not set. ");
   }

   protected final Session session;

   public JcrFileSystem(Session session)
   {
      this.session = session;
      //System.out.println("JcrFileSystem()");
   }

   /*
    * Since we are use path of JCR node as identifier we need such method for
    * creation item in root folder. Path in this case may be:
    * 
    * 1. .../document/?[Query_Parameters] --- SLASH
    * 2. .../document?[Query_Parameters]  --- NO SLASH
    * 
    * Path without slash at the end does not match to method
    * createDocument(ObjectId, String, MediaType, InputStream, List<LockToken>).
    * It is possible to create special template to fix this issue:
    * 
    * document{X:(/)?}/{parent:.*} --- Make final slash optional if {parent} is not specified (Root item)  
    * 
    * But to keep URI template more simple and clear is better to have one more
    * method with template matched to root folder.
    */
   @POST
   @Path("document")
   @Produces({MediaType.APPLICATION_JSON})
   public ObjectId __createDocument(@QueryParam("name") String name, @QueryParam("mediaType") MediaType mediaType,
      InputStream content, @QueryParam("lockTokens") List<LockToken> lockTokens) throws ObjectNotFoundException,
      InvalidArgumentException, LockException, PermissionDeniedException
   {
      return createDocument(new ObjectId("/"), name, mediaType, content, lockTokens);
   }

   /*
    * This method has the same purposes as __createDocument(String, MediaType, InputStream, List<LockToken>)
    */
   @POST
   @Path("folder")
   @Produces({MediaType.APPLICATION_JSON})
   public ObjectId __createFolder(@QueryParam("name") String name, @QueryParam("lockTokens") List<LockToken> lockTokens)
      throws ObjectNotFoundException, InvalidArgumentException, LockException, PermissionDeniedException
   {
      return createFolder(new ObjectId("/"), name, lockTokens);
   }

   boolean isDocument(Node node) throws RepositoryException
   {
      return node.isNodeType("nt:file") && node.getNode("jcr:content").isNodeType("nt:resource");
   }

   // ------------------- VirtualFileSystem ---------------------

   /**
    * @see org.exoplatform.ide.vfs.VirtualFileSystem#copy(org.exoplatform.ide.vfs.ObjectId,
    *      org.exoplatform.ide.vfs.ObjectId, java.util.List)
    */
   public ObjectId copy(@PathParam("source") ObjectId source, @QueryParam("parent") ObjectId parent,
      @QueryParam("lockTokens") List<LockToken> lockTokens) throws ObjectNotFoundException, InvalidArgumentException,
      LockException, PermissionDeniedException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.vfs.VirtualFileSystem#createDocument(org.exoplatform.ide.vfs.ObjectId,
    *      java.lang.String, javax.ws.rs.core.MediaType, java.io.InputStream,
    *      java.util.List)
    */
   //@Path("document{X:(/)?}{parent:.*}")
   @Path("document/{parent:.*}")
   public ObjectId createDocument(
      @PathParam("parent") ObjectId parent, //
      @QueryParam("name") String name, //
      @DefaultValue(MediaType.APPLICATION_OCTET_STREAM) @QueryParam("mediaType") MediaType mediaType,
      InputStream content, //
      @QueryParam("lockTokens") List<LockToken> lockTokens //
   ) throws ObjectNotFoundException, InvalidArgumentException, LockException, PermissionDeniedException
   {
      checkName(name);
      String path = parent.getId();
      if (path.charAt(0) != '/')
         path = '/' + path;

      if (lockTokens != null && lockTokens.size() > 0)
         for (LockToken lt : lockTokens)
            session.addLockToken(lt.getLockToken());

      try
      {
         Node parentNode = (Node)session.getItem(path);
         if (isDocument(parentNode))
            throw new InvalidArgumentException("Item specified as parent is not a folder. ");
         // TODO customize node types ???
         Node documentNode = parentNode.addNode(name, "nt:file");
         Node contentNode = documentNode.addNode("jcr:content", "nt:resource");
         contentNode.setProperty("jcr:mimeType", (mediaType.getType() + "/" + mediaType.getSubtype()));
         String encoding = mediaType.getParameters().get("charset");
         if (encoding != null)
            contentNode.setProperty("jcr:encoding", encoding);
         contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
         contentNode.setProperty("jcr:data", content == null ? new ByteArrayInputStream(new byte[0]) : content);
         session.save();
         return new ObjectId(documentNode.getPath());
      }
      catch (PathNotFoundException e)
      {
         throw new ObjectNotFoundException("Parent object " + path + " does not exists. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException(e.getMessage());
      }
      catch (ItemExistsException e)
      {
         throw new InvalidArgumentException("Item with the same name: " + name
            + " already exists in specified folder. ");
      }
      catch (javax.jcr.lock.LockException e)
      {
         throw new LockException(e.getMessage());
      }
      catch (NoSuchNodeTypeException e)
      {
         // TODO Proper response status if node type specified by client.
         throw new WebApplicationException(e);
      }
      catch (RepositoryException e)
      {
         throw new WebApplicationException(e);
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.VirtualFileSystem#createFolder(org.exoplatform.ide.vfs.ObjectId,
    *      java.lang.String, java.util.List)
    */
   //@Path("folder{X:(/)?}{parent:.*}")
   @Path("folder/{parent:.*}")
   public ObjectId createFolder(@PathParam("parent") ObjectId parent, @QueryParam("name") String name,
      @QueryParam("lockTokens") List<LockToken> lockTokens) throws ObjectNotFoundException, InvalidArgumentException,
      LockException, PermissionDeniedException
   {
      checkName(name);
      String path = parent.getId();
      if (path.charAt(0) != '/')
         path = '/' + path;

      if (lockTokens != null && lockTokens.size() > 0)
         for (LockToken lt : lockTokens)
            session.addLockToken(lt.getLockToken());

      try
      {
         Node parentNode = (Node)session.getItem(path);
         if (isDocument(parentNode))
            throw new InvalidArgumentException("Item specified as parent is not a folder. ");
         // TODO customize node type ???
         Node folderNode = parentNode.addNode(name, "nt:folder");
         session.save();
         return new ObjectId(folderNode.getPath());
      }
      catch (PathNotFoundException e)
      {
         throw new ObjectNotFoundException("Parent object " + path + " does not exists. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException(e.getMessage());
      }
      catch (ItemExistsException e)
      {
         throw new InvalidArgumentException("Item with the same name: " + name
            + " already exists in specified folder. ");
      }
      catch (javax.jcr.lock.LockException e)
      {
         throw new LockException(e.getMessage());
      }
      catch (NoSuchNodeTypeException e)
      {
         // TODO Proper response status if node type specified by client.
         throw new WebApplicationException(e);
      }
      catch (RepositoryException e)
      {
         throw new WebApplicationException(e);
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.VirtualFileSystem#delete(org.exoplatform.ide.vfs.ObjectId,
    *      java.util.List)
    */
   @Path("delete/{identifier:.*}")
   public void delete(@PathParam("identifier") ObjectId identifier, @QueryParam("lockTokens") List<LockToken> lockTokens)
      throws ObjectNotFoundException, ConstraintException, LockException, PermissionDeniedException
   {
      String path = identifier.getId();
      if (path.charAt(0) != '/')
         path = '/' + path;

      if (lockTokens != null && lockTokens.size() > 0)
         for (LockToken lt : lockTokens)
            session.addLockToken(lt.getLockToken());
      try
      {
         session.getItem(path).remove();
         session.save();
      }
      catch (PathNotFoundException e)
      {
         throw new ObjectNotFoundException("Oject " + path + " does not exists. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException(e.getMessage());
      }
      catch (javax.jcr.lock.LockException e)
      {
         throw new LockException(e.getMessage());
      }
      catch (RepositoryException e)
      {
         throw new WebApplicationException(e);
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.VirtualFileSystem#getACL(org.exoplatform.ide.vfs.ObjectId)
    */
   public List<AccessControlEntry> getACL(@PathParam("identifier") ObjectId identifier) throws NotSupportedException,
      ObjectNotFoundException, PermissionDeniedException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.vfs.VirtualFileSystem#getChildren(org.exoplatform.ide.vfs.ObjectId,
    *      int, int, org.exoplatform.ide.vfs.PropertyFilter)
    */
   public ItemList<Item> getChildren(@PathParam("identifier") ObjectId parent, @QueryParam("maxItems") int maxItems,
      @QueryParam("skipCount") int skipCount, @QueryParam("propertyFilter") PropertyFilter propertyFilter)
      throws ObjectNotFoundException, InvalidArgumentException, PermissionDeniedException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.vfs.VirtualFileSystem#getContent(org.exoplatform.ide.vfs.ObjectId)
    */
   public Response getContent(@PathParam("identifier") ObjectId identifier) throws ObjectNotFoundException,
      InvalidArgumentException, PermissionDeniedException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.vfs.VirtualFileSystem#getInfo(javax.ws.rs.core.UriInfo)
    */
   public VirtualFileSystemInfo getInfo(@Context UriInfo uriInfo)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.vfs.VirtualFileSystem#getItem(org.exoplatform.ide.vfs.ObjectId,
    *      org.exoplatform.ide.vfs.PropertyFilter)
    */
   public Item getItem(@PathParam("identifier") ObjectId identifier,
      @QueryParam("propertyFilter") PropertyFilter propertyFilter) throws ObjectNotFoundException,
      PermissionDeniedException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.vfs.VirtualFileSystem#getVersion(org.exoplatform.ide.vfs.ObjectId,
    *      org.exoplatform.ide.vfs.VersionId)
    */
   public Response getVersion(@PathParam("identifier") ObjectId identifier,
      @PathParam("versionIdentifier") VersionId versionIdentifier) throws ObjectNotFoundException,
      InvalidArgumentException, PermissionDeniedException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.vfs.VirtualFileSystem#getVersions(org.exoplatform.ide.vfs.ObjectId,
    *      int, int, org.exoplatform.ide.vfs.PropertyFilter)
    */
   public ItemList<Document> getVersions(@PathParam("identifier") ObjectId identifier,
      @QueryParam("maxItems") int maxItems, @QueryParam("skipCount") int skipCount,
      @QueryParam("propertyFilter") PropertyFilter propertyFilter) throws ObjectNotFoundException,
      InvalidArgumentException, PermissionDeniedException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.vfs.VirtualFileSystem#lock(org.exoplatform.ide.vfs.ObjectId,
    *      java.lang.Boolean)
    */
   public LockToken lock(@PathParam("identifier") ObjectId identifier, @QueryParam("isDeep") Boolean isDeep)
      throws NotSupportedException, ObjectNotFoundException, LockException, PermissionDeniedException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.vfs.VirtualFileSystem#move(org.exoplatform.ide.vfs.ObjectId,
    *      org.exoplatform.ide.vfs.ObjectId, java.util.List)
    */
   public ObjectId move(@PathParam("identifier") ObjectId identifier, @QueryParam("newparent") ObjectId newparent,
      @QueryParam("lockTokens") List<LockToken> lockTokens) throws ObjectNotFoundException, LockException,
      PermissionDeniedException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.vfs.VirtualFileSystem#query(org.exoplatform.ide.vfs.Query,
    *      int, int)
    */
   public ItemList<Item> query(@QueryParam("statement") Query query, @QueryParam("maxItems") int maxItems,
      @QueryParam("skipCount") int skipCount) throws NotSupportedException, InvalidArgumentException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.vfs.VirtualFileSystem#query(javax.ws.rs.core.MultivaluedMap,
    *      int, int)
    */
   public ItemList<Item> query(MultivaluedMap<String, String> query, @QueryParam("maxItems") int maxItems,
      @QueryParam("skipCount") int skipCount) throws NotSupportedException, InvalidArgumentException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.vfs.VirtualFileSystem#unlock(org.exoplatform.ide.vfs.ObjectId,
    *      java.util.List)
    */
   public void unlock(@PathParam("identifier") ObjectId identifier, @QueryParam("lockTokens") List<LockToken> lockTokens)
      throws NotSupportedException, ObjectNotFoundException, LockException, PermissionDeniedException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.vfs.VirtualFileSystem#updateACL(org.exoplatform.ide.vfs.ObjectId,
    *      java.util.List, java.lang.Boolean, java.util.List)
    */
   public void updateACL(@PathParam("identifier") ObjectId identifier, List<AccessControlEntry> acl,
      @QueryParam("override") Boolean override, @QueryParam("lockTokens") List<LockToken> lockTokens)
      throws NotSupportedException, ObjectNotFoundException, LockException, PermissionDeniedException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.vfs.VirtualFileSystem#updateContent(org.exoplatform.ide.vfs.ObjectId,
    *      javax.ws.rs.core.MediaType, java.io.InputStream, java.util.List)
    */
   public void updateContent(@PathParam("identifier") ObjectId identifier,
      @QueryParam("mediaType") MediaType mediaType, InputStream newcontent,
      @QueryParam("lockTokens") List<LockToken> lockTokens) throws ObjectNotFoundException, InvalidArgumentException,
      LockException, PermissionDeniedException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.vfs.VirtualFileSystem#updateProperties(org.exoplatform.ide.vfs.ObjectId,
    *      java.util.List, java.util.List)
    */
   public void updateProperties(@PathParam("identifier") ObjectId identifier, List<Property<?>> properties,
      @QueryParam("lockTokens") List<LockToken> lockTokens) throws ObjectNotFoundException, LockException,
      PermissionDeniedException
   {
      // TODO Auto-generated method stub

   }
}
