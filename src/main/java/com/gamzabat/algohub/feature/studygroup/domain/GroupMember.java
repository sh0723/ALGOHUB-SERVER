package com.gamzabat.algohub.feature.studygroup.domain;

import java.time.LocalDate;

import com.gamzabat.algohub.feature.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class GroupMember {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "study_group_id")
	private StudyGroup studyGroup;
	private LocalDate joinDate;

	@Builder
	public GroupMember(User user, StudyGroup studyGroup, LocalDate joinDate) {
		this.user = user;
		this.studyGroup = studyGroup;
		this.joinDate = joinDate;
	}
}
