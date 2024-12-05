package it.unisannio.studenti.qualitag.repository;

import static org.assertj.core.api.Assertions.assertThat;

import it.unisannio.studenti.qualitag.model.Tag;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class TagRepositoryTest {

  @Autowired
  private TagRepository tagRepository;

  @BeforeEach
  public void setUp() {
    tagRepository.deleteAll();
    tagRepository.save(new Tag("tag1", "user1", "#FFFFFF"));
    tagRepository.save(new Tag("tag2", "user2", "#000000"));
    tagRepository.save(new Tag("tag3", "user1", "#FF0000"));
  }

  @Test
  public void testFindTagByCreatedBy() {
    List<Tag> tags = tagRepository.findTagByCreatedBy("user1");
    assertThat(tags).hasSize(2);
    assertThat(tags).extracting(Tag::getTagValue).containsExactlyInAnyOrder("TAG1", "TAG3");
  }

  @Test
  public void testFindByTagValueContaining() {
    List<Tag> tags = tagRepository.findByTagValueContaining("tag");
    assertThat(tags).hasSize(3);
    assertThat(tags).extracting(Tag::getTagValue).containsExactlyInAnyOrder("TAG1", "TAG2", "TAG3");
  }
}
