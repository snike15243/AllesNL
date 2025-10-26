package nl.allesnl.template;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.mockStatic;

@SpringBootTest
class TemplateApplicationTests {

	@Test
	void contextLoads() {
	}

    @Test
    void mainRunsSpringApplication() {
        try (var mocked = mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(TemplateApplication.class, new String[]{}))
                    .thenReturn(null);

            TemplateApplication.main(new String[]{});

            mocked.verify(() -> SpringApplication.run(TemplateApplication.class, new String[]{}));
        }
    }

}
