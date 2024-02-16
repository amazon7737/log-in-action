package com.example.test4.repository;

import com.example.test4.domain.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member){
        em.persist(member);
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m" ,Member.class)
                .getResultList(); // jpql. Entity 객체를 대상으로 쿼리를 날린다.

    }
}
