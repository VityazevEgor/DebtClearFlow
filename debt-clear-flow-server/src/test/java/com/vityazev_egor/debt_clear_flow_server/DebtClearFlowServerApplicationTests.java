package com.vityazev_egor.debt_clear_flow_server;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.vityazev_egor.debt_clear_flow_server.Misc.Shared;


@SpringBootTest
class DebtClearFlowServerApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void testEmailRegex(){
		String[] validEmails = new String[] {
			"lolkek@mail.com",
			"m2103087@edu.misis.ru",
			"m2103097@edu.misis.ru"
		};
		for (String email : validEmails) {
			assertTrue(Shared.isEmail(email), "Expected valid email but got invalid: " + email);
		}

		String[] invalidEmails = new String[] {
			"amogus",
			"lolkekche burek"
		};
		for (String email : invalidEmails) {
			assertFalse(Shared.isEmail(email), "Expected invalid email but got valid: " + email);
		}
	}

	@Test
	void testFullNameRegex(){
		String[] validFullNames = new String[] {
			"Иванов Иван Иванович",
			"Иванов Егор Денисович",
			"Иванов Ванек Ваниванович"
		};
		for  (String fullName  : validFullNames)  {
			assertTrue(Shared.isFullName(fullName), "Expected valid full name but got invalid: " + fullName);
		}

		String[] invalidFullNames = new String[]  {
			"Иванов Иванович",
			"Иванов eгор денисович",
			"ЛР-1-07, ЛР-1-12"
		};

		for   (String fullName   : invalidFullNames)   {
			assertFalse(Shared.isFullName(fullName), "Expected invalid full name but got valid: " + fullName);
		}
	}
}
