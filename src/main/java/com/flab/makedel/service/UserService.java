package com.flab.makedel.service;

import com.flab.makedel.dto.UserDTO;
import com.flab.makedel.mapper.UserMapper;
import com.flab.makedel.utils.PasswordEncrypter;
import java.util.Optional;
import org.springframework.stereotype.Service;

/*
    @Service 어노테이션은 비즈니스 로직을 처리하는 서비스라는 것을 알려주는 어노테이션이다.
    Component Scan을 통하여 @Service 어노테이션이 붙은 클래스를 스프링이 빈으로 등록하고 이 빈의 라이프사이클을 관리한다.
*/
@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public void signUp(UserDTO user) {
        UserDTO encryptedUser = encryptUser(user);
        userMapper.insertUser(encryptedUser);
    }

    public boolean isExistsId(String id) {
        return userMapper.isExistsId(id);
    }

    public UserDTO encryptUser(UserDTO user) {
        String encryptedPassword = PasswordEncrypter.encrypt(user.getPassword());
        UserDTO encryptedUser = UserDTO.builder()
            .id(user.getId())
            .password(encryptedPassword)
            .email(user.getEmail())
            .name(user.getName())
            .phone(user.getPhone())
            .address(user.getAddress())
            .build();
        return encryptedUser;
    }

    public Optional<UserDTO> findUserByIdAndPassword(String id, String password) {

        Optional<UserDTO> user = Optional.ofNullable(userMapper.selectUserById(id));

        if (!user.isPresent()) {
            return Optional.empty();
        }

        boolean isSamePassword = PasswordEncrypter.isMatch(password, user.get().getPassword());

        if (!isSamePassword) {
            return Optional.empty();
        }

        return user;
    }

}

