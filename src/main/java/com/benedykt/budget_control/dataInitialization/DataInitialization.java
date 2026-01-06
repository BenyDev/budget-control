package com.benedykt.budget_control.dataInitialization;


import com.benedykt.budget_control.role.entity.Role;
import com.benedykt.budget_control.role.repo.RoleRepo;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class DataInitialization implements CommandLineRunner {

    private final RoleRepo roleRepo;


    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if(roleRepo.findByName("ADMIN").isEmpty()){
            Role admin = Role.builder()
                    .name("ADMIN")
                    .build();
            roleRepo.save(admin);
        }
        if(roleRepo.findByName("CUSTOMER").isEmpty()){
            Role admin = Role.builder()
                    .name("CUSTOMER")
                    .build();
            roleRepo.save(admin);
        }
    }
}
