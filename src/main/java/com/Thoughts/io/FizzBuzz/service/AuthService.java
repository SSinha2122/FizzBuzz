package com.Thoughts.io.FizzBuzz.service;

import static com.Thoughts.io.FizzBuzz.util.Constants.ACTIVATION_EMAIL;
import static java.time.Instant.now;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.Thoughts.io.FizzBuzz.config.AppConfig;
import com.Thoughts.io.FizzBuzz.dto.RefreshTokenRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Thoughts.io.FizzBuzz.dto.AuthenticationResponse;
import com.Thoughts.io.FizzBuzz.dto.LoginRequest;
import com.Thoughts.io.FizzBuzz.dto.RegisterRequest;
import com.Thoughts.io.FizzBuzz.exception.FizzBuzzException;
import com.Thoughts.io.FizzBuzz.exception.UsernameNotFoundException;
import com.Thoughts.io.FizzBuzz.model.NotificationEmail;
import com.Thoughts.io.FizzBuzz.model.User;
import com.Thoughts.io.FizzBuzz.model.VerificationToken;
import com.Thoughts.io.FizzBuzz.repository.UserRepository;
import com.Thoughts.io.FizzBuzz.repository.VerificationTokenRepository;
import com.Thoughts.io.FizzBuzz.security.JwtProvider;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class AuthService {

	private final RefreshTokenService refreshTokenService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtProvider jwtProvider;
	private final MailService mailService;
	private final VerificationTokenRepository verificationTokenRepository;
	private final AppConfig appConfig;

	@Transactional
	public void signup(RegisterRequest registerRequest) {
		User user = new User();
		user.setUsername(registerRequest.getUsername());
		user.setEmail(registerRequest.getEmail());
		user.setPassword(encodePassword(registerRequest.getPassword()));
		user.setCreated(now());
		user.setEnabled(false);

		userRepository.save(user);

		String token = generateVerificationToken(user);
		String message =
				"Thank you for signing up to FizzBuzz, please click on the below url to activate your account : "
						+appConfig.getAppUrl()+"/api/auth/accountVerification"+"/" + token;

		mailService.sendMail(new NotificationEmail("Please Activate your account", user.getEmail(), message));
	}

	private String generateVerificationToken(User user) {
		String token = UUID.randomUUID().toString();
		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setToken(token);
		verificationToken.setUser(user);
		verificationTokenRepository.save(verificationToken);
		return token;
	}

	private String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	public void verifyAccount(String token) {
		Optional<VerificationToken> verificationTokenOptional = verificationTokenRepository.findByToken(token);
		verificationTokenOptional.orElseThrow(() -> new FizzBuzzException("Invalid Token"));
		fetchUserAndEnable(verificationTokenOptional.get());
	}

	@Transactional
	private void fetchUserAndEnable(VerificationToken verificationToken) {
		String username = verificationToken.getUser().getUsername();
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new FizzBuzzException("User Not Found with id - " + username));
		user.setEnabled(true);
		userRepository.save(user);
	}

	public AuthenticationResponse login(LoginRequest loginRequest) {
		Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
				loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authenticate);
		String token = jwtProvider.generateToken(authenticate);
		return AuthenticationResponse.builder()
				.authenticationToken(token)
				.refreshToken(refreshTokenService.generateRefreshToken().getToken())
				.expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
				.username(loginRequest.getUsername())
				.build();
	}

	@Transactional(readOnly = true)
    User getCurrentUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getUsername()));
    }

	public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
		refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
		String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
		return AuthenticationResponse.builder()
				.authenticationToken(token)
				.refreshToken(refreshTokenRequest.getRefreshToken())
				.expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
				.username(refreshTokenRequest.getUsername())
				.build();
	}
}
