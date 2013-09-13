/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.client.framework.util;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import org.exoplatform.ide.client.framework.discovery.RestServicesList;
import org.exoplatform.ide.client.framework.invite.GoogleContact;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.framework.workspaceinfo.CurrentWorkspaceInfo;
import org.exoplatform.ide.client.framework.workspaceinfo.WorkspaceInfo;
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
     * A factory method for a information about workspace bean.
     *
     * @return an {@link AutoBean} of type {@link WorkspaceInfo}
     */
    AutoBean<WorkspaceInfo> workspaceInfo();
    
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
