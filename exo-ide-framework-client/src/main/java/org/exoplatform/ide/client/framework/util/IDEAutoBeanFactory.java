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
package org.exoplatform.ide.client.framework.util;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import org.exoplatform.ide.client.framework.discovery.RestServicesList;
import org.exoplatform.ide.client.framework.invite.GoogleContact;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.framework.workspaceinfo.CurrentWorkspaceInfo;
import org.exoplatform.ide.vfs.shared.*;

import java.util.List;

/**
 * The interface for the {@link AutoBean} generating.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: IDEAutoBeanFactory.java Mar 27, 2012 9:57:25 AM azatsarynnyy $
 */
@AutoBeanFactory.Category(IDEAutoBeanFactory.ItemCategory.class)
public interface IDEAutoBeanFactory extends AutoBeanFactory {
    class ItemCategory {
        public static boolean hasProperty(AutoBean<Item> item, String property) {
            return false;
        }

        public static String getPropertyValue(AutoBean<Item> item, String name) {
            return null;
        }

        public static List<String> getPropertyValues(AutoBean<Item> item, String name) {
            return null;
        }

        public static Link getLinkByRelation(AutoBean<Item> item, String rel) {
            return null;
        }

        public static Property getProperty(AutoBean<Item> item, String name) {
            return null;
        }
    }

    /**
     * A factory method for a REST-services list bean.
     *
     * @return an {@link AutoBean} of type {@link RestServicesList}
     */
    AutoBean<RestServicesList> restServicesList();

    /**
     * A factory method for a lock token bean.
     *
     * @return an {@link AutoBean} of type {@link LockToken}
     */
    AutoBean<LockToken> lockToken();

    /**
     * A factory method for a information about user bean.
     *
     * @return an {@link AutoBean} of type {@link UserInfo}
     */
    AutoBean<UserInfo> userInfo();
    
    /**
     * A factory method for a information about current workspace bean.
     *
     * @return an {@link AutoBean} of type {@link CurrentWorkspaceInfo}
     */
    AutoBean<CurrentWorkspaceInfo> currentWorkspace();

    /**
     * A factory method for a information about user bean.
     *
     * @return an {@link AutoBean} of type {@link UserInfo}
     */
    AutoBean<GoogleContact> googleContact();

    AutoBean<ItemNode> itemNode();

    AutoBean<Item> item();

    AutoBean<Link> link();

    AutoBean<Property> property();

}
