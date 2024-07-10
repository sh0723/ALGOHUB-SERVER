package com.gamzabat.algohub.dto;

import com.gamzabat.algohub.domain.StudyGroup;
import com.gamzabat.algohub.domain.User;

public record GetStudyGroupResponse(Long id,
									String name,
									String groupImage,
									String ownerNickname,
									boolean isOwner) {
	public static GetStudyGroupResponse toDTO(StudyGroup group, User user){
		return new GetStudyGroupResponse(
			group.getId(),
			group.getName(),
			group.getGroupImage(),
			group.getOwner().getNickname(),
			group.getOwner().getId().equals(user.getId())
		);
	}
}
