package com.gamzabat.algohub.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.gamzabat.algohub.domain.StudyGroup;
import com.gamzabat.algohub.domain.User;
import com.gamzabat.algohub.enums.Role;
import com.gamzabat.algohub.repository.StudyGroupRepository;

@ExtendWith(MockitoExtension.class)
class StudyGroupServiceTest {
	@InjectMocks
	private StudyGroupService studyGroupService;
	@Mock
	private StudyGroupRepository studyGroupRepository;
	@Mock
	private ImageService imageService;
	private User user;
	@Captor
	private ArgumentCaptor<StudyGroup> groupCaptor;

	@BeforeEach
	void setUp() {
		user = User.builder().email("email").password("password").nickname("nickname")
			.role(Role.USER).profileImage("image").build();
	}

	@Test
	@DisplayName("그룹 생성 성공")
	void createGroup() {
		// given
		String name = "name";
		String imageUrl = "groupImage";
		MockMultipartFile profileImage = new MockMultipartFile("image",new byte[]{1,2,3});
		when(imageService.saveImage(profileImage)).thenReturn(imageUrl);
		// when
		studyGroupService.createGroup(user,name,profileImage);
		// then
		verify(studyGroupRepository,times(1)).save(groupCaptor.capture());
		StudyGroup result = groupCaptor.getValue();
		assertThat(result.getName()).isEqualTo(name);
		assertThat(result.getOwner()).isEqualTo(user);
		assertThat(result.getGroupImage()).isEqualTo(imageUrl);
	}
}