package com.doritech.api.DTO;

public class MenuAccessDTO {
    private int id;
    private int roleId;
    private String roleName;
    private int menuId;

    public MenuAccessDTO() {
    }

    public MenuAccessDTO(int id, int roleId, String roleName, int menuId) {
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
