package com.study.SpringSecurityMybatis.service;

import com.study.SpringSecurityMybatis.dto.request.ReqSigninDto;
import com.study.SpringSecurityMybatis.dto.request.ReqSignupDto;
import com.study.SpringSecurityMybatis.dto.response.RespDeleteUserDto;
import com.study.SpringSecurityMybatis.dto.response.RespSigninDto;
import com.study.SpringSecurityMybatis.dto.response.RespSignupDto;
import com.study.SpringSecurityMybatis.entity.Role;
import com.study.SpringSecurityMybatis.entity.User;
import com.study.SpringSecurityMybatis.entity.UserRoles;
import com.study.SpringSecurityMybatis.exception.SignupException;
import com.study.SpringSecurityMybatis.repository.RoleMapper;
import com.study.SpringSecurityMybatis.repository.UserMapper;
import com.study.SpringSecurityMybatis.repository.UserRolesMapper;
import com.study.SpringSecurityMybatis.security.jwt.JwtProvider;
import com.study.SpringSecurityMybatis.security.principal.PrincipalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserRolesMapper userRolesMapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JwtProvider jwtProvider;

    public Boolean isDuplicateUsername(String username) {
        return userMapper.findByUsername(username) != null; // Optional.ofNullable(userMapper.findByUsername(username)).isPresent();
    }

    @Transactional(rollbackFor = SignupException.class) // 이 메서드 안에서 SignupException 예외가 터지면 바로 rollback 한다.
    public RespSignupDto insertUserAndUserRoles(ReqSignupDto dto) throws SignupException {
        User user = null;
        try {
            user = dto.toEntity(passwordEncoder);
            userMapper.save(user);

            Role role = roleMapper.findByName("ROLE_USER");

            if (role == null) {
                role = Role.builder().name("ROLE_USER").build();
                roleMapper.save(role);
            }

            UserRoles userRoles = UserRoles.builder()
                    .userId(user.getId())
                    .roleId(role.getId())
                    .build();

            userRolesMapper.save(userRoles);

            user.setUserRoles(Set.of(userRoles));
        } catch (Exception e) {
            throw new SignupException("회원가입 중 오류 발생");
        }

        return RespSignupDto.builder()
                .message("회원가입 완료")
                .user(user)
                .build();
    }

    public RespSigninDto getGeneratedAccessToken(ReqSigninDto dto) {
        User user = checkUsernameAndPassword(dto.getUsername(), dto.getPassword());
        jwtProvider.generateAccessToken(user);

        return RespSigninDto.builder()
                .expireDate(jwtProvider.getExpireDate().toLocaleString())
                .accessToken(jwtProvider.generateAccessToken(user))
                .build();
    }

    private User checkUsernameAndPassword(String username, String password) {
        User user = userMapper.findByUsername(username);

        if(user == null) {
            throw new UsernameNotFoundException("사용자 정보를 다시 확인하세요.");
        }

        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("사용자 정보를 다시 확인하세요.");
        }

        return user;
    }

    @Transactional(rollbackFor = SQLException.class)
    public RespDeleteUserDto deleteUser(Long id) {
            User user = null;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            PrincipalUser principalUser = (PrincipalUser) authentication.getPrincipal();
            if(principalUser.getId() != id) {
                throw new AuthenticationServiceException("삭제 할 수 있는 권한이 없습니다.");
            }
            user = userMapper.findById(id);
            if(user == null) {
                throw new AuthenticationServiceException("해당 사용자는 존재하지 않는 사용자입니다.");
            }
            userRolesMapper.deleteByUserId(id);
            userMapper.deleteById(id);

        return RespDeleteUserDto.builder()
                .isDeleting(true)
                .message("사용자 삭제 완료")
                .deletedUser(user)
                .build();
    }
}
