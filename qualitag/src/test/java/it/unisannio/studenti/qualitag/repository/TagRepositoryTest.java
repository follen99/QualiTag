package it.unisannio.studenti.qualitag.repository;

import static org.mockito.Mockito.when;   // to not always write Mockito.when

import it.unisannio.studenti.qualitag.model.Tag;
import it.unisannio.studenti.qualitag.model.User;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * MOCKED Test class for the TagRepository.
 */
@SpringBootTest
public class TagRepositoryTest {

  @MockBean
  private TagRepository tagRepository;

  @MockBean
  private UserRepository userRepository;

  private final String username = "user1";

  /**
   * Set up the mock objects.
   */
  @BeforeEach
  public void setUp() {
    tagRepository.deleteAll();

    User testUser = new User(
        this.username,
        "pietrosmusi@gmail.com",
        "passwordHased",
        "Pietro",
        "Smusi");

    // when userRepository.save() is called in my program, return testUser (mocked)
    when(userRepository.save(Mockito.any(User.class))).thenReturn(testUser);

    // Tags
    Tag tag1 = new Tag("tag1", this.username, "#FFFFFF");
    Tag tag2 = new Tag("tag2", this.username, "#000000");
    Tag tag3 = new Tag("tag3", this.username, "#FF0000");

    when(tagRepository.findTagByCreatedBy(this.username))
            .thenReturn(List.of(tag1, tag2, tag3));

    when(tagRepository.findByTagValueContaining("TAG1"))
            .thenReturn(List.of(tag1));
  }

  /**
   * Clean up the repository after each test.
   */
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
