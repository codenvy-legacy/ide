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
package org.exoplatform.ide.git.server;

import org.exoplatform.ide.git.shared.Commiters;
import org.exoplatform.ide.git.shared.GitUser;

import java.util.List;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: CommitersBean.java Aug 3, 2012
 */
public class CommitersBean implements Commiters {
    private List<GitUser> commiters;

    public CommitersBean() {
    }

    public CommitersBean(List<GitUser> commiters) {
        this.commiters = commiters;
    }


    /** @see org.exoplatform.ide.git.shared.Commiters#getCommiters() */
    @Override
    public List<GitUser> getCommiters() {
        return commiters;
    }

    /** @see org.exoplatform.ide.git.shared.Commiters#setCommiters(java.util.List) */
    @Override
    public void setCommiters(List<GitUser> commiters) {
        this.commiters = commiters;
    }

}
