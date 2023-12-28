package com.bluewhaletech.Ourry;

import com.bluewhaletech.Ourry.util.RedisUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OurryApplicationTests {
	@Autowired
	private RedisUtil redisUtil;

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
}