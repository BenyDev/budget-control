package com.benedykt.budget_control.role.services;

import com.benedykt.budget_control.res.Response;
import com.benedykt.budget_control.role.entity.Role;

import java.util.List;

public interface RoleService {

    Response<Role> createRole(Role roleRequest);
    Response<Role> updateRole(Role roleRequest);
    Response<List<Role>> getAllRoles();
    Response<?> deleteRole(Long id);
}
