package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.teemo.shopping.Main;
import com.teemo.shopping.account.AccountAlreadyExist;
import com.teemo.shopping.account.AccountService;
import com.teemo.shopping.security.filter.LoginFilter;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = Main.class)
class ServiceTest {
	@Autowired
	private AccountService accountService;
	@Autowired
	private LoginFilter filter;
	@Test
	@DisplayName("Login in test")
	void loginTest() throws Exception{

		//Given
		String username = "chickenWorld1231";
		String password = "123j-12dkspfjgb-hjgr";
		String rawJSON = "{username:\""+username+"\", password:\"" + password + "\"}";
		try {
			accountService.register(username, password);	//<- 이것도 mocking 해야하나
		} catch (AccountAlreadyExist e) {

		}
		HttpServletRequest mockedHttpServletRequest = mock(HttpServletRequest.class);
		when(mockedHttpServletRequest.getMethod()).thenReturn("POST");
		when(mockedHttpServletRequest.getInputStream()).thenReturn(new DelegatingServletInputStream(rawJSON));
		HttpServletResponse mockedHttpServletResponse = mock(HttpServletResponse.class);

		//When
		boolean isAuthenticated = false;
		try {
			isAuthenticated = filter.attemptAuthentication(mockedHttpServletRequest, mockedHttpServletResponse).isAuthenticated();
		} catch (Exception err) {
			System.err.println(err.getMessage());
		}

		//then
		assertTrue(isAuthenticated);
	}

	@Test
	@DisplayName("Register Test")
	void register() throws Exception{

		//Given
		String username = "dassaodj";
		String password = "g2g23fd3";


		//when
		boolean isSuccessRegister = true;
		boolean isOverrapedRegister = false;
		try {
			accountService.register(username, password);
		} catch (AccountAlreadyExist e) {
			isSuccessRegister = false;
		}

		try {
			accountService.register(username, password);
			isOverrapedRegister = true;
		} catch (AccountAlreadyExist e) {

		}

		//then
		assertTrue(isSuccessRegister);
		assertFalse(isOverrapedRegister);
	}

}

class DelegatingServletInputStream extends ServletInputStream { // 이딴게 테스트?
	private int index = 0;
	private byte[] bytes;
	public DelegatingServletInputStream(String str) {
		this.bytes = str.getBytes(StandardCharsets.UTF_8);
	}
	@Override
	public boolean isFinished() {
		return index == this.bytes.length;
	}
	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void setReadListener(ReadListener listener) {
	}
	@Override
	public int read() throws IOException {
		if(isFinished()) return -1;
		return bytes[index++];
	}
}