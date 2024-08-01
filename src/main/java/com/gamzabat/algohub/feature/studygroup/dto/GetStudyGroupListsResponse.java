package com.gamzabat.algohub.feature.studygroup.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class GetStudyGroupListsResponse {
    private List<GetStudyGroupResponse> done;
    private List<GetStudyGroupResponse> inProgress;
    private List<GetStudyGroupResponse> queued;

    public GetStudyGroupListsResponse(List<GetStudyGroupResponse> done, List<GetStudyGroupResponse> inProgress, List<GetStudyGroupResponse> queued) {
        this.done = done;
        this.inProgress = inProgress;
        this.queued = queued;
    }
}
