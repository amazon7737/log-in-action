package com.example.test4.service;

import com.example.test4.domain.Member;
import com.example.test4.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {


    private final MemberRepository memberRepository;

    @Transactional
    public Long join(Member member){

        memberRepository.save(member);
        return member.getId();

    }

    public List<Member> findMembers(){
        return memberRepository.findAll();

    }
}
