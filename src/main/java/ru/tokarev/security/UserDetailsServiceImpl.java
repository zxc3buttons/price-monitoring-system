package ru.tokarev.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tokarev.dao.userdao.UserDao;
import ru.tokarev.entity.User;
import ru.tokarev.exception.userexception.UserNotFoundException;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserDao<User> userDao;

    @Autowired
    public void setDao(UserDao<User> userDao) {
        this.userDao = userDao;
        this.userDao.setClazz(ru.tokarev.entity.User.class);
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User Not Found with username: " + username));
        return UserDetailsImpl.build(user);

    }
}
