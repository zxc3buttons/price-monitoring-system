package ru.tokarev.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tokarev.dto.RoleDto;
import ru.tokarev.dto.userdto.UserDto;
import ru.tokarev.entity.User;
import ru.tokarev.service.userservice.UserService;
import ru.tokarev.utils.MapperUtil;

import java.util.List;

@Slf4j
@RequestMapping("/api/users")
@RestController
public class UserController {

    private final UserService userService;

    private final ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {

        log.info("GET request for /users with no data");

        List<User> userList = userService.getAll();
        List<UserDto> userDtoList = MapperUtil.convertList(userList, this::convertToUserDto);

        log.info("Response for GET request for /users with data {}", userDtoList);
        for(UserDto userDto : userDtoList) {
            log.info("id {}, username {}, firstName {}, lastName {}, email {}, password {}, roleId {}",
                    userDto.getId(), userDto.getUsername(), userDto.getFirstName(), userDto.getLastName(),
                    userDto.getEmail(), userDto.getPassword(), userDto.getRoleDto().getId());
        }

        return new ResponseEntity<>(userDtoList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {

        log.info("GET request for /users/{} with data {}", id, id);

        User user = userService.getById(id);
        UserDto userDto = convertToUserDto(user);

        log.info("Response for GET request for /users/{} with data:" +
                " id {}, username {}, firstName {}, lastName {}, email {}, password {}, roleId {}", id,
                userDto.getId(), userDto.getUsername(), userDto.getFirstName(), userDto.getLastName(),
                userDto.getEmail(), userDto.getPassword(), userDto.getRoleDto().getId());

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {

        log.info("PATCH request for /users/{} with data:" +
                " username {}, firstName {}, lastName {}, email {}, password {}, roleId {}", id,
                userDto.getUsername(), userDto.getFirstName(), userDto.getLastName(),
                userDto.getEmail(), userDto.getPassword(), userDto.getRoleDto().getId());

        User user = convertToUserEntity(userDto);
        User updatedUser = userService.updateUser(id, user);
        UserDto updatedUserDto = convertToUserDto(updatedUser);

        log.info("Response for PATCH request for /users/{} with data:" +
                " id {}, username {}, firstName {}, lastName {}, email {}, password {}, roleId {}", id,
                updatedUserDto.getId(), updatedUserDto.getUsername(), updatedUserDto.getFirstName(),
                updatedUserDto.getLastName(), updatedUser.getEmail(), updatedUser.getPassword(),
                updatedUserDto.getRoleDto().getId());

        return new ResponseEntity<>(updatedUserDto, HttpStatus.OK);
    }

    @PatchMapping(value = "/{id}/update-role", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> updateUserRole(@PathVariable Long id, @RequestBody UserDto userDto) {

        log.info("PATCH request for /users/{}/update_role with data:" +
                " username {}, firstName {}, lastName {}, email {}, password {}, roleId {}", id,
                userDto.getUsername(), userDto.getFirstName(), userDto.getLastName(),
                userDto.getEmail(), userDto.getPassword(), userDto.getRoleDto().getId());

        User user = convertToUserEntity(userDto);
        User updatedUser = userService.updateUserRole(id, user);
        UserDto updatedUserDto = convertToUserDto(updatedUser);

        log.info("Response for PATCH request for /users/{}/update_role with data:" +
                        " id {}, username {}, firstName {}, lastName {}, email {}, password {}, roleId {}", id,
                updatedUserDto.getId(), updatedUserDto.getUsername(), updatedUserDto.getFirstName(),
                updatedUserDto.getLastName(), updatedUser.getEmail(), updatedUser.getPassword(),
                updatedUserDto.getRoleDto().getId());

        return new ResponseEntity<>(updatedUserDto, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {

        log.info("DELETE request for /users/{} with data {}", id, id);

        userService.deleteUser(id);

        log.info("Response for DELETE request for /users/{} with data {}", id, id);

        return ResponseEntity.ok().body("User deleted successfully");
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
