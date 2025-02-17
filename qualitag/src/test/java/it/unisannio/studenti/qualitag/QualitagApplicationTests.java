package it.unisannio.studenti.qualitag;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QualitagApplicationTests {

  @Test
  void contextLoads() {}

  @Test
  void mainMethodRunsWithoutExceptions() {
    assertDoesNotThrow(() -> QualitagApplication.main(new String[] {}));
  }

}
