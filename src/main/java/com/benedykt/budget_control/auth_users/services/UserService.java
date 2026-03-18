package com.benedykt.budget_control.auth_users.services;

import com.benedykt.budget_control.auth_users.dtos.UpdatePasswordRequest;
import com.benedykt.budget_control.auth_users.dtos.UserDTO;
import com.benedykt.budget_control.auth_users.entity.AppUser;
import com.benedykt.budget_control.res.Response;
import org.springframework.data.domain.Page;

public interface UserService {

    AppUser getCurrentLoggedInUser();
    Response<UserDTO> getMyProfile();
    Response<Page<UserDTO>> getAllUsers(int page, int size);
    Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest);

//    Response<?> uploadProfilePicture(MultipartFile file);

}
