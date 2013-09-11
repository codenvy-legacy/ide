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
package org.exoplatform.ide.extension.gadget.server.opensocial.model;

/**
 * Groups are owned by people, and are used to tag or categorize people and their relationships.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 19, 2010 $
 */
public class Group {
    /** Unique ID for this group (required). */
    private String id;

    /** Title of group (required). */
    private String title;

    /** Description of group (optional). */
    private String description;

    /** @return the id */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *         the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /** @return the title */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *         the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @return the description */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *         the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
