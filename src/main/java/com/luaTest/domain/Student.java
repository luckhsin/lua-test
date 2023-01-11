package com.luaTest.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description TODO
 * @Author dinghaodong
 * @Date 2022/11/24 16:09
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    private String name;
    private String phone;
    private String gender;
    private Integer score;
    private String password;
}
