package com.example.test4.domain;

import com.example.test4.service.MemberService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.parser.Entity;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    EntityManager em;

    @Test
    void 회원가입() throws Exception{

        // given
        Member member = new Member();
        member.setUsername("김강민2");

        // when
        Long savedId = memberService.join(member);

        // then
//        em.flush();
        List<Member> memberList = memberService.findMembers();

        assertNotNull(memberList);



    }
}
