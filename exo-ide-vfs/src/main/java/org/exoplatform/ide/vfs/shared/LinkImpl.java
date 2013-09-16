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
package org.exoplatform.ide.vfs.shared;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: Link.java 79579 2012-02-17 13:27:25Z andrew00x $
 */
public class LinkImpl implements Link {

    /** URL of resource. */
    private String href;

    /** Produced media type of resource described by this link. */
    private String type;

    /** Relation attribute of link. Client may use it for choice links to retrieve specific info about resource. */
    private String rel;

    public LinkImpl(String href, String rel, String type) {
        this.href = href;
        this.rel = rel;
        this.type = type;
    }

    public LinkImpl() {
    }

    @Override
    public String getHref() {
        return href;
    }

    @Override
    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public String getRel() {
        return rel;
    }

    @Override
    public void setRel(String rel) {
        this.rel = rel;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString() {
        return "Link [href=" + href + ", type=" + type + ", rel=" + rel + ']';
    }
}
