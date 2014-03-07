/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.factory.shared;

import com.codenvy.api.factory.dto.SimpleFactoryUrl;
import com.codenvy.api.factory.dto.Variable;

import java.util.*;

/** Implementation of {@link com.codenvy.api.factory.dto.SimpleFactoryUrl} */
public class SimpleFactoryUrlImpl implements SimpleFactoryUrl {
    // mandatory parameters
    private String v;
    private String vcs;
    private String vcsurl;
    private String commitid;

    // optional parameters
    private String action;
    private String openfile;
    private boolean vcsinfo = false;
    private String orgid;
    private String affiliateid;
    private String vcsbranch;
    private Map<String, String> projectattributes = Collections.emptyMap();
    private List<Variable>      variables         = Collections.emptyList();

    public SimpleFactoryUrlImpl() {
    }

    public SimpleFactoryUrlImpl(String version, String vcs, String vcsUrl, String commitId, String action, String openFile, boolean vcsInfo,
                                String orgid, String affiliateid, String vcsbranch, Map<String, String> projectAttributes,
                                List<Variable> variables) {
        this.v = version;
        this.vcs = vcs;
        this.vcsurl = vcsUrl;
        this.commitid = commitId;
        this.action = action;
        this.openfile = openFile;
        this.vcsinfo = vcsInfo;
        this.orgid = orgid;
        this.affiliateid = affiliateid;
        this.vcsbranch = vcsbranch;
        this.variables = variables;

        setProjectattributes(projectAttributes);
    }

    public void setV(String version) {
        this.v = version;
    }

    public void setVcs(String vcs) {
        this.vcs = vcs;
    }

    public void setVcsurl(String vcsUrl) {
        this.vcsurl = vcsUrl;
    }

    public void setCommitid(String commitId) {
        this.commitid = commitId;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setOpenfile(String openFile) {
        this.openfile = openFile;
    }

    public void setVcsinfo(boolean vcsInfo) {
        this.vcsinfo = vcsInfo;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public void setAffiliateid(String affiliateid) {
        this.affiliateid = affiliateid;
    }

    public void setVcsbranch(String vcsbranch) {
        this.vcsbranch = vcsbranch;
    }

    // Method mame should be lowercased to use correctly from json builder.
    public void setProjectattributes(Map<String, String> projectAttributes) {
        if (projectAttributes != null) {
            this.projectattributes = new HashMap<String, String>(projectAttributes);
        }
    }

    public String getV() {
        return v;
    }

    public String getVcs() {
        return vcs;
    }

    public String getVcsurl() {
        return vcsurl;
    }

    public String getCommitid() {
        return commitid;
    }

    public String getAction() {
        return action;
    }

    public String getOpenfile() {
        return openfile;
    }

    public boolean getVcsinfo() {
        return vcsinfo;
    }

    public String getOrgid() {
        return orgid;
    }

    public String getAffiliateid() {
        return affiliateid;
    }

    public String getVcsbranch() {
        return vcsbranch;
    }

    public Map<String, String> getProjectattributes() {
        return Collections.unmodifiableMap(projectattributes);
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleFactoryUrlImpl that = (SimpleFactoryUrlImpl)o;

        if (vcsinfo != that.vcsinfo) return false;
        if (action != null ? !action.equals(that.action) : that.action != null) return false;
        if (affiliateid != null ? !affiliateid.equals(that.affiliateid) : that.affiliateid != null) return false;
        if (commitid != null ? !commitid.equals(that.commitid) : that.commitid != null) return false;
        if (openfile != null ? !openfile.equals(that.openfile) : that.openfile != null) return false;
        if (orgid != null ? !orgid.equals(that.orgid) : that.orgid != null) return false;
        if (projectattributes != null ? !projectattributes.equals(that.projectattributes) : that.projectattributes != null) return false;
        if (v != null ? !v.equals(that.v) : that.v != null) return false;
        if (variables != null ? !variables.equals(that.variables) : that.variables != null) return false;
        if (vcs != null ? !vcs.equals(that.vcs) : that.vcs != null) return false;
        if (vcsbranch != null ? !vcsbranch.equals(that.vcsbranch) : that.vcsbranch != null) return false;
        if (vcsurl != null ? !vcsurl.equals(that.vcsurl) : that.vcsurl != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = v != null ? v.hashCode() : 0;
        result = 31 * result + (vcs != null ? vcs.hashCode() : 0);
        result = 31 * result + (vcsurl != null ? vcsurl.hashCode() : 0);
        result = 31 * result + (commitid != null ? commitid.hashCode() : 0);
        result = 31 * result + (action != null ? action.hashCode() : 0);
        result = 31 * result + (openfile != null ? openfile.hashCode() : 0);
        result = 31 * result + (vcsinfo ? 1 : 0);
        result = 31 * result + (orgid != null ? orgid.hashCode() : 0);
        result = 31 * result + (affiliateid != null ? affiliateid.hashCode() : 0);
        result = 31 * result + (vcsbranch != null ? vcsbranch.hashCode() : 0);
        result = 31 * result + (projectattributes != null ? projectattributes.hashCode() : 0);
        result = 31 * result + (variables != null ? variables.hashCode() : 0);
        return result;
    }
}
