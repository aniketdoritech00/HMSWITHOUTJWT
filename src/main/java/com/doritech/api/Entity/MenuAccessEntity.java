package com.doritech.api.Entity;

import javax.persistence.*;

@Entity
@Table(name = "menu_access")
public class MenuAccessEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;
    @Column(name="role_id")
    private int roleId;
    @Column(name="role_name")
    private String roleName;
    @Column(name = "menu_id")
    private int menuId;

    public MenuAccessEntity() {
    }

    public MenuAccessEntity(int id, int roleId, String roleName, int menuId) {
        this.id = id;
        this.roleId = roleId;
        this.roleName = roleName;
        this.menuId = menuId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    @Override
    public String toString() {
        return "MenuAccessEntity{" +
                "id=" + id +
                ", roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", menuId=" + menuId +
                '}';
    }
}
