package org.exoplatform.ide.extension.ssh.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * See {@link org.exoplatform.ide.extension.ssh.shared.ListKeyItem}.
 */
public class ListKeyItemImpl implements ListKeyItem {
    private List<KeyItem> keys;

    public ListKeyItemImpl(List<KeyItem> keys) {
        this.keys = keys;
    }

    public ListKeyItemImpl() {
        keys = new ArrayList<KeyItem>();
    }

    @Override
    public List<KeyItem> getKeys() {
        return keys;
    }

    @Override
    public void setKeys(List<KeyItem> keys) {
        this.keys = keys;
    }
}
