package com.doritech.api.Entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "menu_master")
public class MenuMasterEntity {
    @Id
    @Column(name = "menu_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name="parent_id")
    private int parentId;
    @Column(name = "child_id")
    private int childId;
    @Column(name = "menu_name")
    private String menuName;
    @Column(name = "menu_handler_name")
    private String menuHandlerName;
    @Column(name = "menu_icon")
    private String menuIcon;
    public MenuMasterEntity() {
    }

    public MenuMasterEntity(int id, int parentId, int childId, String menuName, String menuHandlerName, String menuIcon) {
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
