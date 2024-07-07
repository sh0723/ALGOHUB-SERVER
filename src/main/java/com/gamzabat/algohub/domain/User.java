package com.gamzabat.algohub.domain;

import java.time.LocalDateTime;

import com.gamzabat.algohub.enums.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String email;
	private String password;
	private String nickname;
	private String profileImage;
	private LocalDateTime deletedAt;
	private Role role;

	@Builder
	public User(String email, String password, String nickname, String profileImage, Role role) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.profileImage = profileImage;
		this.role = role;
		this.deletedAt = null;
	}
}
