package com.gamzabat.algohub.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

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
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE study_group SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class StudyGroup {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String groupImage;
	private String groupCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id")
	private User owner;

	private LocalDateTime deletedAt;

	@Builder
	public StudyGroup(String name, String groupImage, String groupCode, User owner) {
		this.name = name;
		this.groupImage = groupImage;
		this.groupCode = groupCode;
		this.owner = owner;
	}

	public void editName(String name){this.name = name;}
	public void editGroupImage(String groupImage){this.groupImage = groupImage;}
}
