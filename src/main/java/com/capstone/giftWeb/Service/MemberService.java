package com.capstone.giftWeb.Service;
import com.capstone.giftWeb.auth.AuthInfo;
import com.capstone.giftWeb.dto.LogInCommand;
import com.capstone.giftWeb.exception.IdPasswordNotMatchingException;
import com.capstone.giftWeb.repository.MemberRepository;
import com.capstone.giftWeb.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;

    @Transactional
    public Long createMember(Member member){
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        memberRepository.findByEmail(member.getEmail())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

    public AuthInfo loginAuth(LogInCommand logInCommand) throws Exception{
        Member member = memberRepository.findByEmail(logInCommand.getEmail()).get() ;
        if(member == null) {
            throw new IdPasswordNotMatchingException();
        }
        if(!member.matchPassword(logInCommand.getPassword())) {
            throw new IdPasswordNotMatchingException();
        }
        return new AuthInfo(member.getEmail(), member.getName());
    }

}