package ru.tokarev.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tokarev.dto.RoleDto;
import ru.tokarev.dto.userdto.SignupDto;
import ru.tokarev.dto.userdto.UserDto;
import ru.tokarev.entity.User;
import ru.tokarev.service.userservice.UserService;

@Slf4j
@RequestMapping(value = "/api")
@RestController
public class RegistrationController {

    private final UserService userService;

    private final ModelMapper modelMapper;

    @Autowired
    public RegistrationController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> signup(@RequestBody SignupDto signupDto) {

        log.info("POST request for /signup with data: username {}, firstName {}, lastName {}, email {}, password {}",
                signupDto.getUsername(), signupDto.getFirstName(), signupDto.getLastName(),
                signupDto.getEmail(), signupDto.getPassword());

        User user = convertToUserEntity(signupDto);
        User createdUser = userService.createUser(user);
        UserDto createdUserDto = convertToUserDto(createdUser);

        log.info("Response for POST request with data: id {}, username {}, firstName {}, lastName {}, email {}," +
                " password {}, roleId {}", createdUserDto.getId(), createdUserDto.getUsername(),
                createdUserDto.getFirstName(), createdUserDto.getLastName(), createdUserDto.getEmail(),
                createdUserDto.getPassword(), createdUserDto.getRoleDto().getId());

        return new ResponseEntity<>(createdUserDto, HttpStatus.CREATED);
    }

    private UserDto convertToUserDto(User user) {
        RoleDto roleDto = modelMapper.map(user.getRole(), RoleDto.class);
        UserDto userDto = modelMapper.map(user, UserDto.class);
        userDto.setRoleDto(roleDto);

        return userDto;
    }

    private User convertToUserEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }
}
