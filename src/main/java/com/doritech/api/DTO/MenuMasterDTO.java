package com.doritech.api.DTO;

import javax.persistence.Column;
import java.util.List;

public class MenuMasterDTO {
    private int id;
    private int parentId;
    private int childId;
    private String menuName;
    private String menuHandlerName;
    private String menuIcon;
    public MenuMasterDTO() {
    }

    public MenuMasterDTO(int id, int parentId, int childId, String menuName, String menuHandlerName, String menuIcon) {
        this.id = id;
        this.parentId = parentId;
        this.childId = childId;
        this.menuName = menuName;
        this.menuHandlerName = menuHandlerName;
        this.menuIcon = menuIcon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuHandlerName() {
        return menuHandlerName;
    }

    public void setMenuHandlerName(String menuHandlerName) {
        this.menuHandlerName = menuHandlerName;
    }

    public String getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon;
    }

    @Override
    public String toString() {
        return "MenuMasterEntity{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", childId=" + childId +
                ", menuName='" + menuName + '\'' +
                ", menuHandlerName='" + menuHandlerName + '\'' +
                ", menuIcon='" + menuIcon + '\'' +
                '}';
    }
}
