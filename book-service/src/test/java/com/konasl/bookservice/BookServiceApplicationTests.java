package com.konasl.bookservice;

import com.konasl.bookservice.services.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes= BookService.class)
class BookServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
