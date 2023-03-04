package com.sametakbal.api.service;

import com.sametakbal.api.entity.User;
import com.sametakbal.api.entity.enums.Role;

import java.util.List;

public interface IUserService extends IService<User> {
    List<User> getUserByRole(Role role);

    List<User> getPotentialUsers(List<Integer> ids);
}
