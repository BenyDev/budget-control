package com.benedykt.budget_control.auth_users.services.impl;

import com.benedykt.budget_control.auth_users.dtos.UpdatePasswordRequest;
import com.benedykt.budget_control.auth_users.dtos.UserDTO;
import com.benedykt.budget_control.auth_users.entity.AppUser;
import com.benedykt.budget_control.auth_users.repo.UserRepo;
import com.benedykt.budget_control.auth_users.services.UserService;
import com.benedykt.budget_control.exceptions.BadRequestException;
import com.benedykt.budget_control.exceptions.NotFoudException;
import com.benedykt.budget_control.notification.dtos.NotificationDTO;
import com.benedykt.budget_control.notification.services.NotificationService;
import com.benedykt.budget_control.res.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    private final String uploadDir = "uploads/profile-pictures/";


    @Override
    public AppUser getCurrentLoggedInUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new NotFoudException("AppUesr not found");
        }

        String email = authentication.getName();
        return userRepo.findByEmail(email).orElseThrow(() -> new NotFoudException("AppUesr not found"));
    }

    @Override
    public Response<UserDTO> getMyProfile() {
        AppUser appUser = getCurrentLoggedInUser();
        UserDTO userProfile = modelMapper.map(appUser, UserDTO.class);

        return Response.<UserDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("AppUesr retrieved")
                .data(userProfile)
                .build();

    }

    @Override
    public Response<Page<UserDTO>> getAllUsers(int page, int size) {
        Page<AppUser> users = userRepo.findAll(PageRequest.of(page, size));

        Page<UserDTO> usersDTO = users.map(user -> modelMapper.map(user, UserDTO.class));

        return Response.<Page<UserDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Users retrieved")
                .data(usersDTO)
                .build();
    }

    @Override
    public Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        AppUser appUser = getCurrentLoggedInUser();

         String oldPassword = updatePasswordRequest.getOldPassword();
         String newPassword =  updatePasswordRequest.getNewPassword();

         if( oldPassword == null || newPassword == null){
             throw new BadRequestException("Old Password or New Password required");
         }

         if(!passwordEncoder.matches(oldPassword, appUser.getPassword())){
             throw new BadRequestException("Old password is not correct");
         }

         appUser.setPassword(passwordEncoder.encode(newPassword));
         userRepo.save(appUser);

        //Send password change confirmation email.
        Map<String,Object> vars = new HashMap<>();
        vars.put("name", appUser.getFirstName());

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(appUser.getEmail())
                .subject("Your password was Successfully Changed :)")
                .templateName("password-change")
                .templateVariables(vars)
                .build();

        notificationService.sendEmail(notificationDTO, appUser);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Password Changed Successfully")
                .build();


    }

//    @Override
//    public Response<?> uploadProfilePicture(MultipartFile file) {
//
//        AppUesr appUesr = getCurrentLoggedInUser();
//
//        try{
//            Path uploadPath = Paths.get(uploadDir);
//
//            if(!Files.exists(uploadPath)){
//                Files.createDirectories(uploadPath);
//            }
//            if(appUesr.getProfilePictureUrl() != null && !appUesr.getProfilePictureUrl().isEmpty()){
//                Path oldFile = Paths.get(appUesr.getProfilePictureUrl());
//                if(Files.exists(oldFile)){
//                    Files.delete(oldFile);
//                }
//            }
//
//            String originalFilename = file.getOriginalFilename();
//            String fileExtension = "";
//
//            if(originalFilename != null && originalFilename.contains(".")){
//                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
//            }
//
//            String newFileName = UUID.randomUUID() + fileExtension;
//            Path filePath = uploadPath.resolve(newFileName);
//
//            Files.copy(file.getInputStream(), filePath);
//
//            String fileUrl = uploadDir + newFileName;
//
//            appUesr.setProfilePictureUrl(fileUrl);
//
//            userRepo.save(appUesr);
//
//            return Response.builder()
//                    .statusCode(HttpStatus.OK.value())
//                    .message("Profile Picture Uploaded Successfully")
//                    .data(fileUrl)
//                    .build();
//
//
//        }catch (IOException e){
//            throw new RuntimeException(e.getMessage());
//        }
//
//    }
}
