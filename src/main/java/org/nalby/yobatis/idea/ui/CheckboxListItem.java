package org.nalby.yobatis.idea.ui;

import org.nalby.yobatis.core.mybatis.TableElement;

public class CheckboxListItem {
    private String label;
    private boolean isSelected = false;

    public CheckboxListItem(String label, boolean isSelected) {
        this.label = label;
        this.isSelected = isSelected;
    }

    public CheckboxListItem(String label) {
        this.label = label;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String toString() {
        return label;
    }

    public static CheckboxListItem fromTableElement(TableElement tableElement) {
        return new CheckboxListItem(tableElement.getName(), tableElement.isEnabled());
    }

    public TableElement asTableElement() {
        return new TableElement(label, isSelected);
    }

}
