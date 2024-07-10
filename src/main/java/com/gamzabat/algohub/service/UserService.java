package com.gamzabat.algohub.service;

import com.gamzabat.algohub.dto.*;
import com.gamzabat.algohub.exception.UncorrectedPasswordException;
import org.hibernate.sql.Delete;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gamzabat.algohub.common.jwt.TokenProvider;
import com.gamzabat.algohub.domain.User;
import com.gamzabat.algohub.enums.Role;
import com.gamzabat.algohub.exception.UserValidationException;
import com.gamzabat.algohub.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final ImageService imageService;
	private final TokenProvider tokenProvider;
	private final AuthenticationManagerBuilder authManager;

	public void register(RegisterRequest request, MultipartFile profileImage) {
		checkEmailDuplication(request.email());
		String imageUrl = imageService.saveImage(profileImage);
		String encodedPassword = passwordEncoder.encode(request.password());
		userRepository.save(User.builder()
			.email(request.email())
			.password(encodedPassword)
			.nickname(request.nickname())
			.profileImage(imageUrl)
			.role(Role.USER)
			.build());
		log.info("success to register");
	}

	public SignInResponse signIn(SignInRequest request) {
		UsernamePasswordAuthenticationToken authenticationToken
			= new UsernamePasswordAuthenticationToken(request.email(),request.password());
		Authentication authenticate = authManager.getObject().authenticate(authenticationToken);

		JwtDTO token = tokenProvider.generateToken(authenticate);
		return new SignInResponse(token.getToken());
	}

	private void checkEmailDuplication(String email){
		if(userRepository.existsByEmail(email))
			throw new UserValidationException("이미 가입 된 이메일 입니다.");
	}


	public UserInfoResponse userInfo(String email) {
		Optional<User> user = userRepository.findByEmail(email);
		return new UserInfoResponse(user.get().getEmail(), user.get().getNickname(),user.get().getProfileImage());
	}

	public void userUpdate(String email, UpdateRequest updateRequest, MultipartFile profileImage) {
		Optional<User> userOptional = userRepository.findByEmail(email);
		if (userOptional.isEmpty()) {
			throw new UserValidationException("사용자를 찾을 수 없습니다.");
		}

		User user = userOptional.get();

		if (profileImage != null && !profileImage.isEmpty()){
			String imageUrl = imageService.saveImage(profileImage);
			user.editProfileImage(imageUrl);
		}

		if (updateRequest.getNickname() != null && !updateRequest.getNickname().isEmpty()) {
			user.editNickname(updateRequest.getNickname());
		}
	}

	public boolean deleteUser(String email, DeleteRequest deleteRequest) {
		Optional<User> userOptional = userRepository.findByEmail(email);
		if (userOptional.isEmpty()) {
			throw new UserValidationException("사용자를 찾을 수 없습니다.");
		}

		if (!passwordEncoder.matches(deleteRequest.password(),userOptional.get().getPassword()))
		{
			return false;
		}
		userRepository.deleteByEmail(email);
		return true;
	}
}
