package com.study.SpringSecurityMybatis.controller;

import com.study.SpringSecurityMybatis.security.principal.PrincipalUser;
import com.study.SpringSecurityMybatis.service.UserService;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return ResponseEntity.ok().body(userService.getUserInfo(id));
    }

    @GetMapping("/user/me")
    public ResponseEntity<?> getUserMe() {
        PrincipalUser principalUser =
        (PrincipalUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return ResponseEntity.ok().body(userService.getUserInfo(principalUser.getId()));
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok().body(userService.deleteUser(id));
    }
}
