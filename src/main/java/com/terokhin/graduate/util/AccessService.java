package com.terokhin.graduate.util;

import com.terokhin.graduate.security.Role;
import com.terokhin.graduate.security.UserPrincipal;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
public class AccessService {

    private static ModelMapper modelMapper;

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        AccessService.modelMapper = modelMapper;
    }

    public static UserPrincipal getUserPrincipal() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? modelMapper.map(authentication.getPrincipal(), UserPrincipal.class) : null;
    }
    
    public static boolean isModerator() {
        return Role.MODERATOR == getCurrentUserRole();
    }
    
    public static boolean isUser() {
        return Role.USER == getCurrentUserRole();
    }
    
    public static boolean isViewer() {
        return getCurrentUserId() == null;
    }

    public static Long getCurrentUserId() {
        return getCurrentUser(UserPrincipal::getId);
    }

    public static String getCurrentUserNickname() {
        return getCurrentUser(UserPrincipal::getNickname);
    }

    public static String getCurrentUserEmail() {
        return getCurrentUser(UserPrincipal::getEmail);
    }

    public static Role getCurrentUserRole() {
        return getCurrentUser(UserPrincipal::getRole);
    }

    private static <T> T getCurrentUser(Function<UserPrincipal, T> mapper) {
        return Optional.ofNullable(AccessService.getUserPrincipal()).map(mapper).orElse(null);
    }
}
