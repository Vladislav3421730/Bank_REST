package com.example.bankcards.security;

import com.example.bankcards.model.User;
import com.example.bankcards.repository.UserRepository;

import com.example.bankcards.util.UserDetailsWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with username %s not found ", username)));
        UserDetailsWrapper userDetailsWrapper = new UserDetailsWrapper(user);
        if (!userDetailsWrapper.isEnabled()) {
            throw new DisabledException("You were banned");
        }
        return userDetailsWrapper;
    }

}
