package com.app.job.quartz.dto.res;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
//@Schema(name = "quartzLiveJobsDto", description = "quartzLiveJobsDto")
public class QuartzLiveJobsResponseDto {

//    @Schema(description = "쥅명", example = "")
    private String jobName;

//    @Schema(description = "그룹명", example = "")
    private String groupName;

//    @Schema(description = "다음 실행시간", example = "")
    private String nextFireTime;

//    @Schema(description = "상태", example = "")
    private String status;

}