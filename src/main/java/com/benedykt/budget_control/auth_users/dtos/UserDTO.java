package com.benedykt.budget_control.auth_users.dtos;

import com.benedykt.budget_control.role.entity.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Jeśli wartość będzie równa null, to json zignoruje pole null i nie wyświetli go w odpowiedzi<resonse>
@JsonIgnoreProperties(ignoreUnknown = true) // JSON ignoruje pola które nie należą do klasy. Dzięki czemu nie zwraca błędu. User podaje więcej pól niż ma klasa
public class UserDTO {


    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;



    @Email
    @NotBlank
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @JsonIgnore
    private String password;

    private boolean active;

    private List<Role> roles;

//    @JsonManagedReference // To używamy w parencie, aby przy serializacji Json sie nie zapentlił, w AccountDto będzie pole UserDTO i tam trzeba dac @JsonBackReference
//    private List<AccountDTO> accounts;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;
}
