package com.lpg3.projeto1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

public class UserUpdateDTO {

    @NotBlank(message = "Nome obrigatório")
    private String name;

    @NotBlank(message = "Telefone obrigatório")
    private String phone;

    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDate birthDate;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
}