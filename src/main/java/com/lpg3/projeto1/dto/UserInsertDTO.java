package com.lpg3.projeto1.dto;

import com.lpg3.projeto1.entities.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class UserInsertDTO extends UserDTO {

    @Size(min = 8, max = 30, message = "Senha deve ter entre 8 e 30 caracteres")
    private String password;

    public UserInsertDTO() {
        super();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}