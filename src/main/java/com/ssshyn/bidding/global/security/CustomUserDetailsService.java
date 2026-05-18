package com.ssshyn.bidding.global.security;

import com.ssshyn.bidding.domain.user.repository.UserRepository;
import com.ssshyn.bidding.global.exception.ErrorCode;
import com.ssshyn.bidding.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        return userRepository.findByLoginId(loginId)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new ErrorException(ErrorCode.USER_NOT_FOUND));
    }
}
