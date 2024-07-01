package com.dony.fcfs_store.util;

import com.dony.fcfs_store.entity.User;
import com.dony.fcfs_store.exception.CustomException;
import com.dony.fcfs_store.exception.ErrorCode;
import com.dony.fcfs_store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticationFacade {

    private final UserRepository userRepository;

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public Integer getLoggedInUserId() {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            return ((User) principal).getId();
        } else if (principal instanceof Optional<?> optionalPrincipal) {
            if (optionalPrincipal.isPresent() && optionalPrincipal.get() instanceof User) {
                return ((User) optionalPrincipal.get()).getId();
            }
        }

        throw new UsernameNotFoundException("No authenticated user found");
    }

    public User getLoggedInUser() {
        Integer loggedInUserId = getLoggedInUserId();
        return userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }
}
