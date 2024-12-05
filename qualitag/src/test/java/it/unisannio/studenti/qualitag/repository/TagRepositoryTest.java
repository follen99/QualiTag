package it.unisannio.studenti.qualitag.repository;

import it.unisannio.studenti.qualitag.model.Tag;
import it.unisannio.studenti.qualitag.model.User;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/*@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")*/
@SpringBootTest
public class TagRepositoryTest {

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private UserRepository userRepository;

  private final String username = "user1";


  @BeforeEach
  public void setUp() {
    tagRepository.deleteAll();

    User testUser = new User(
        this.username,
        "pietrosmusi@gmail.com",
        "passwordHased",
        "Pietro",
        "Smusi");

    userRepository.save(testUser);

    // the user has created 3 tags
    // tag value shoud be saved in uppercase
    tagRepository.save(new Tag("tag1", this.username, "#FFFFFF"));
    tagRepository.save(new Tag("tag2", this.username, "#000000"));
    tagRepository.save(new Tag("tag3", this.username, "#FF0000"));

  }

  @AfterEach
  public void cleanUp() {
    tagRepository.deleteAll();
  }

  @Test
  public void testFindTagByCreatedByUsername() {
    List<Tag> tags = tagRepository.findTagByCreatedBy(this.username);
    assert tags.size() == 3;
    System.out.println("PRINTING TAGS" + tags);

  }

  @Test
  public void testFindByTagValueContaining() {
    List<Tag> tags = tagRepository.findByTagValueContaining("TAG1");  // case insensitive
    assert tags.size() == 1;
  }

}
