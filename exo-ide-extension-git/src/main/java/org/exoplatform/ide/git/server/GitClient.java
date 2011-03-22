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
package org.exoplatform.ide.git.server;

import org.exoplatform.ide.git.shared.AddRequest;
import org.exoplatform.ide.git.shared.Branch;
import org.exoplatform.ide.git.shared.BranchCheckoutRequest;
import org.exoplatform.ide.git.shared.BranchCreateRequest;
import org.exoplatform.ide.git.shared.BranchDeleteRequest;
import org.exoplatform.ide.git.shared.BranchListRequest;
import org.exoplatform.ide.git.shared.CloneRequest;
import org.exoplatform.ide.git.shared.CommitRequest;
import org.exoplatform.ide.git.shared.DiffRequest;
import org.exoplatform.ide.git.shared.FetchRequest;
import org.exoplatform.ide.git.shared.InitRequest;
import org.exoplatform.ide.git.shared.LogRequest;
import org.exoplatform.ide.git.shared.MergeRequest;
import org.exoplatform.ide.git.shared.MergeResult;
import org.exoplatform.ide.git.shared.MoveRequest;
import org.exoplatform.ide.git.shared.PullRequest;
import org.exoplatform.ide.git.shared.PushRequest;
import org.exoplatform.ide.git.shared.ResetRequest;
import org.exoplatform.ide.git.shared.Revision;
import org.exoplatform.ide.git.shared.RmRequest;
import org.exoplatform.ide.git.shared.StatusRequest;
import org.exoplatform.ide.git.shared.Tag;
import org.exoplatform.ide.git.shared.TagCreateRequest;
import org.exoplatform.ide.git.shared.TagDeleteRequest;
import org.exoplatform.ide.git.shared.TagListRequest;

import java.net.URISyntaxException;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: GitClient.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public interface GitClient
{
   /**
    * @param request
    * @throws GitException
    * @throw IllegalArgumentException
    */
   void add(AddRequest request) throws GitException;

   /**
    * @param request
    * @throws GitException
    * @throw IllegalArgumentException
    */
   void branchCheckout(BranchCheckoutRequest request) throws GitException;

   /**
    * @param request
    * @return
    * @throws GitException
    * @throw IllegalArgumentException
    */
   Branch branchCreate(BranchCreateRequest request) throws GitException;

   /**
    * @param request
    * @throws GitException
    * @throw IllegalArgumentException
    */
   void branchDelete(BranchDeleteRequest request) throws GitException;

   /**
    * @param request
    * @return
    * @throws GitException
    */
   List<Branch> branchList(BranchListRequest request) throws GitException;

   /**
    * @param request
    * @return
    * @throws URISyntaxException
    * @throws GitException
    */
   GitClient clone(CloneRequest request) throws URISyntaxException, GitException;

   /**
    * @param request
    * @return
    * @throws GitException
    * @throws IllegalArgumentException
    */
   Revision commit(CommitRequest request) throws GitException;

   /**
    * @param request
    * @return
    * @throws GitException
    */
   InfoPage diff(DiffRequest request) throws GitException;

   /**
    * @param request
    * @throws GitException
    * @throws IllegalArgumentException
    */
   void fetch(FetchRequest request) throws GitException;

   /**
    * @param request
    * @return
    * @throws GitException
    */
   GitClient init(InitRequest request) throws GitException;

   /**
    * @param request
    * @return
    * @throws GitException
    * @throws IllegalArgumentException
    */
   InfoPage log(LogRequest request) throws GitException;

   /**
    * @param request
    * @return
    * @throws IOException
    * @throws IllegalArgumentException
    */
   MergeResult merge(MergeRequest request) throws GitException;

   /**
    * @param request
    * @throws IOException
    * @throws IllegalArgumentException
    */
   void mv(MoveRequest request) throws GitException;

   /**
    * @param request
    * @throws GitException
    */
   void pull(PullRequest request) throws GitException;

   /**
    * @param request
    * @throws GitException
    * @throws IllegalArgumentException
    */
   void push(PushRequest request) throws GitException;

   /**
    * @param request
    * @throws GitException
    * @throws IllegalArgumentException
    */
   void reset(ResetRequest request) throws GitException;

   /**
    * @param request
    * @throws GitException
    * @throws IllegalArgumentException
    */
   void rm(RmRequest request) throws GitException;

   /**
    * @param request
    * @return
    * @throws GitException
    */
   InfoPage status(StatusRequest request) throws GitException;

   /**
    * @param request
    * @throws GitException
    * @throws IllegalArgumentException
    */
   Tag tagCreate(TagCreateRequest request) throws GitException;

   /**
    * @param request
    * @throws GitException
    * @throws IllegalArgumentException
    */
   void tagDelete(TagDeleteRequest request) throws GitException;

   /**
    * @param request
    * @return
    * @throws GitException
    */
   List<Tag> tagList(TagListRequest request) throws GitException;
   
   void close();
}
