package com.codenvy.ide.factory.shared;

import com.codenvy.api.factory.dto.Replacement;
import com.codenvy.api.factory.dto.Variable;

import java.util.List;


/** Implementation of {@link com.codenvy.api.factory.dto.Variable} */
public class VariableImpl implements Variable {
    private List<String>      files;
    private List<Replacement> entries;

    public VariableImpl() {
    }

    public VariableImpl(List<String> files, List<Replacement> entries) {
        this.files = files;
        this.entries = entries;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public List<Replacement> getEntries() {
        return entries;
    }

    public void setEntries(List<Replacement> entries) {
        this.entries = entries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VariableImpl variable = (VariableImpl)o;

        if (entries != null ? !entries.equals(variable.entries) : variable.entries != null) return false;
        if (files != null ? !files.equals(variable.files) : variable.files != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = files != null ? files.hashCode() : 0;
        result = 31 * result + (entries != null ? entries.hashCode() : 0);
        return result;
    }
}
