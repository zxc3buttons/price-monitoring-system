package ru.tokarev.service.userservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.tokarev.dao.roledao.RoleDao;
import ru.tokarev.dao.userdao.UserDao;
import ru.tokarev.entity.Role;
import ru.tokarev.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao<User> userDao;

    @Mock
    private RoleDao<Role> roleDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void givenUser_whenGetById_thenReturnUser() {
        //arrange
        User createdUser = new User(1L, "oleg", "Oleg",
                "Tokarev", "oleg@mail.ru", "12345qwert",
                new Role(1L, "ADMIN"), new Date(), new Date());
        given(userDao.findById(1L)).willReturn(Optional.of(createdUser));

        //act
        User user = userService.getById(1L);

        //assert
        assertEquals(user, createdUser);
    }

    @Test
    void givenUsers_whenGetAll_thenReturnUsers() {
        //arrange
        User createdUser1 = new User(1L, "oleg", "Oleg",
                "Tokarev", "oleg@mail.ru", "12345qwert",
                new Role(1L, "ADMIN"), new Date(), new Date());
        User createdUser2 = new User(2L, "misha", "Misha",
                "Alikov", "misha@mail.ru", "123qwe",
                new Role(2L, "USER"), new Date(), new Date());
        given(userDao.findAll()).willReturn(Optional.of(List.of(createdUser1, createdUser2)));

        //act
        List<User> userList = userService.getAll();

        //assert
        assertEquals(userList, List.of(createdUser1, createdUser2));
    }

    @Test
    void givenCreatedUser_whenCreateUser_thenReturnCreatedUser() {

        //arrange
        User userToCreate = new User(1L, "oleg", "Oleg", "Tokarev",
                "oleg@mail.ru", passwordEncoder.encode("1234qwer"), new Role(3L,
                "ROLE_UNDEFINED"),
                new Date(), new Date());
        given(userDao.create(userToCreate)).willReturn(Optional.of(userToCreate));

        //act
        User user = userService.createUser(userToCreate);

        //assert
        assertEquals(user, userToCreate);

    }

    @Test
    void givenUserToUpdate_whenUpdateUser_thenReturnUpdatedUser() {

        //arrange
        User existingUser = new User(1L, "oleg", "Oleg", "Tokarev",
                "oleg@mail.ru", passwordEncoder.encode("1234qwer"), new Role(3L, "UNDEFINED"),
                new Date(), new Date());
        User updUser = new User(1L, "olegqwe", "Oleg", "Tokarev",
                "oleg@mail.ru", passwordEncoder.encode("1234qwer"), new Role(3L, "UNDEFINED"),
                new Date(), new Date());
        given(userDao.findById(1L)).willReturn(Optional.of(existingUser));
        given(userDao.update(existingUser)).willReturn(Optional.of(updUser));

        //act
        User user = userService.updateUser(1L, existingUser);

        //assert
        assertEquals(user, updUser);

    }

    @Test
    void givenUserRoleToUpdate_whenUpdateUserRole_thenReturnUpdatedUserRole() {

        //arrange
        Role role = new Role(2L, "USER");
        User existingUser = new User(1L, "oleg", "Oleg", "Tokarev",
                "oleg@mail.ru", passwordEncoder.encode("1234qwer"), role,
                new Date(), new Date());

        given(userDao.findById(1L)).willReturn(Optional.of(existingUser));
        given(roleDao.findById(2L)).willReturn(Optional.of(role));
        given(userDao.update(existingUser)).willReturn(Optional.of(existingUser));

        //act
        User user = userService.updateUserRole(1L, existingUser);

        //assert
        assertEquals(user, existingUser);
    }

    @Test
    void givenUser_whenDeleteUser_thenNothing() {

        //arrange
        User existingUser = new User(1L, "oleg", "Oleg",
                "Tokarev", "oleg@mail.ru", "12345qwert",
                new Role(1L, "ADMIN"), new Date(), new Date());
        given(userDao.findById(1L)).willReturn(Optional.of(existingUser));
        willDoNothing().given(userDao).deleteById(1L);

        //act
        userService.deleteUser(1L);

        //assert
        verify(userDao, times(1)).deleteById(1L);
    }
}