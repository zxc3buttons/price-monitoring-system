package ru.tokarev.dto.userdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupDto extends UserDto {

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String password;
}
