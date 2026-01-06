package com.benedykt.budget_control.dataInitialization;


import com.benedykt.budget_control.category.entity.Category;
import com.benedykt.budget_control.category.repo.CategoryRepo;
import com.benedykt.budget_control.role.entity.Role;
import com.benedykt.budget_control.role.repo.RoleRepo;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepo roleRepo;
    private final CategoryRepo categoryRepo;

    @Value("${database.initial.data.role-admin}")
    private  String roleAdmin;
    @Value("${database.initial.data.role-customer}")
    private  String roleCustomer;

    @Value("${database.initial.data.category-housing}")
    private  String categoryHousing;
    @Value("${database.initial.data.category-food_drinks}")
    private  String categoryFoodDrinks;
    @Value("${database.initial.data.category-pets}")
    private  String categoryPets;
    @Value("${database.initial.data.category-transport}")
    private  String categoryTransport;
    @Value("${database.initial.data.category-shopping}")
    private  String categoryShopping;
    @Value("${database.initial.data.category-entertainment}")
    private  String categoryEntertainment;
    @Value("${database.initial.data.category-other}")
    private  String categoryOther;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        ensureRole(roleCustomer);
        ensureRole(roleAdmin);

        ensureCategory(categoryHousing);
        ensureCategory(categoryFoodDrinks);
        ensureCategory(categoryPets);
        ensureCategory(categoryTransport);
        ensureCategory(categoryShopping);
        ensureCategory(categoryEntertainment);
        ensureCategory(categoryOther);

    }

    private void ensureRole(String roleName){
        if(!roleRepo.existsByName(roleName)){
            roleRepo.save(Role.builder().name(roleName).build());
        }
    }
    private void ensureCategory(String categoryName){
        if(!categoryRepo.existsByName(categoryName)){
            categoryRepo.save(Category.builder().name(categoryName).build());
        }
    }
}
