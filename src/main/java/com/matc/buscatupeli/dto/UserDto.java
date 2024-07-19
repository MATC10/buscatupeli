package com.matc.buscatupeli.dto;

import com.matc.buscatupeli.models.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto
{
    private Long id;
    @NotEmpty
    private String nickname;
    @NotEmpty(message = "Password no debe estar vacío")
    private String password;
    @NotEmpty(message = "Email no puede estar vacío")
    @Email
    private String email;
    private List<Role> roles = new ArrayList<>();

    public UserDto(String nickname, String password, String email) {
        this.nickname = nickname;
        this.password = password;
        this.email = email;
    }

}
