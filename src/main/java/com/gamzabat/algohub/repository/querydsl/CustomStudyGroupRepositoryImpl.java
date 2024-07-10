package com.gamzabat.algohub.repository.querydsl;

import static com.gamzabat.algohub.domain.QGroupMember.*;
import static com.gamzabat.algohub.domain.QStudyGroup.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.gamzabat.algohub.domain.StudyGroup;
import com.gamzabat.algohub.domain.User;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class CustomStudyGroupRepositoryImpl implements CustomStudyGroupRepository{
	private final JPAQueryFactory queryFactory;

	@Override
	public List<StudyGroup> findByUser(User user) {
		JPAQuery<StudyGroup> ownerGroups = queryFactory.selectFrom(studyGroup)
			.where(studyGroup.owner.eq(user));
		JPAQuery<StudyGroup> memberGroups = queryFactory.select(groupMember.studyGroup)
			.from(groupMember)
			.where(groupMember.user.eq(user));

		return queryFactory.selectFrom(studyGroup)
			.where(studyGroup.in(
				JPAExpressions.selectFrom(studyGroup)
					.where(studyGroup.in(ownerGroups)
						.or(studyGroup.in(memberGroups)))
			))
			.fetch();
	}
}