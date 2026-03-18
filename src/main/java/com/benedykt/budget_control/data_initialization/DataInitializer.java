package com.benedykt.budget_control.data_initialization;


import com.benedykt.budget_control.enums.UserRole;
import com.benedykt.budget_control.role.entity.Role;
import com.benedykt.budget_control.role.repo.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepo roleRepo;

    @Override
    public void run(String... args) throws Exception {
        creteRoleIfNotExists(UserRole.USER_BASIC);
        creteRoleIfNotExists(UserRole.ADMIN);

    }

    private void creteRoleIfNotExists(UserRole userRole){
        if (!roleRepo.existsByName(userRole)){
            roleRepo.save(
                    Role.builder().name(userRole).build()
            );
        }
    }
}
