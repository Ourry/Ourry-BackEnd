package com.bluewhaletech.Ourry;

import com.bluewhaletech.Ourry.domain.Member;
import com.bluewhaletech.Ourry.dto.EmailDTO;
import com.bluewhaletech.Ourry.exception.MemberNotFoundException;
import com.bluewhaletech.Ourry.repository.JpaMemberRepository;
import com.bluewhaletech.Ourry.service.MailServiceImpl;
import com.bluewhaletech.Ourry.util.RedisUtil;
import jakarta.mail.MessagingException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.UnsupportedEncodingException;

@SpringBootTest
class OurryApplicationTests {
	@Autowired
	private RedisUtil redisUtil;
	private MailServiceImpl mailService;
	private JpaMemberRepository jpaMemberRepository;

	@Test
	public void redisTest() {
		//given
		String email = "sth4881@naver.com";
		String code = "aaa111";

		//when
		redisUtil.setAuthenticationExpire(email, code, 5L);

		//then
		Assertions.assertThat(code).isEqualTo(redisUtil.getAuthenticationCode(email));
	}

	@Test
	public String sendAuthenticationCode(String email) throws MessagingException, UnsupportedEncodingException {
		/* 이메일(회원) 존재 확인 */
		Member member = jpaMemberRepository.findByEmail(email).orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

		/* 인증코드 유효기간 5분으로 설정 */
		redisUtil.setAuthenticationExpire(member.getEmail(), "AAA", 5L);

		String text = "";
		text += "안녕하세요. Ourry입니다.";
		text += "<br/>";
		text += "이메일 인증 코드 보내드립니다.";
		text += "<br/>";
		text += "인증코드 : <b>ASDF</b>";

		EmailDTO dto = EmailDTO.builder()
				.email(member.getEmail())
				.title("이메일 인증코드 발송 메일입니다.")
				.text(text)
				.build();
		mailService.sendMail(dto);
		return "SUCCESS";
	}
}