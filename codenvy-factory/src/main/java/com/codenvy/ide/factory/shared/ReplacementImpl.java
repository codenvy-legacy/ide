package com.codenvy.ide.factory.shared;

import com.codenvy.api.factory.dto.Replacement;

/**
 * Implementation of {@link com.codenvy.api.factory.dto.Replacement}
 */
public class ReplacementImpl implements Replacement {
    private String find;
    private String replace;
    private String replacemode = "variable_singlepass";

    public ReplacementImpl() {
    }

    public ReplacementImpl(String find, String replace) {
        this.find = find;
        this.replace = replace;
    }

    public ReplacementImpl(String find, String replace, String replacemode) {
        this.find = find;
        this.replace = replace;
        this.replacemode = replacemode;
    }

    public String getFind() {
        return find;
    }

    public void setFind(String find) {
        this.find = find;
    }

    public String getReplace() {
        return replace;
    }

    public void setReplace(String replace) {
        this.replace = replace;
    }

    public String getReplacemode() {
        return replacemode;
    }

    public void setReplacemode(String replacemode) {
        this.replacemode = replacemode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReplacementImpl that = (ReplacementImpl)o;

        if (find != null ? !find.equals(that.find) : that.find != null) return false;
        if (replace != null ? !replace.equals(that.replace) : that.replace != null) return false;
        if (replacemode != null ? !replacemode.equals(that.replacemode) : that.replacemode != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = find != null ? find.hashCode() : 0;
        result = 31 * result + (replace != null ? replace.hashCode() : 0);
        result = 31 * result + (replacemode != null ? replacemode.hashCode() : 0);
        return result;
    }
}
