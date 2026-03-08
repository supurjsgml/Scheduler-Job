package com.app.job.jobKorea.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * 
 * @user  : guney
 * @date  : 2025. 2. 27.
 * @since : 1.0
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberReqDTO {
    private String id;
    private String pw;
    private String token;
}

