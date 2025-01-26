package it.unisannio.studenti.qualitag.repository;

import com.google.common.base.Optional;
import it.unisannio.studenti.qualitag.model.Tag;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository interface for managing Tag entities in MongoDB.
 */
public interface TagRepository extends MongoRepository<Tag, String> {

  /**
   * Finds tags created by a specific user.
   *
   * @param value The username of the creator.
   * @return A list of tags created by the specified user.
   */
  List<Tag> findTagByCreatedBy(String value); // created by username

  /**
   * Finds tags that contain the specified value.
   *
   * @param value The value to search for within tag values.
   * @return A list of tags containing the specified value.
   */
  List<Tag> findByTagValueContaining(String value);

  /**
   * Finds a tag by its ID.
   *
   * @param tagId The ID of the tag to find.
   * @return The tag with the specified ID.
   */
  Tag findTagByTagId(String tagId);

  /**
   * Finds a tag by its value if belongs to the userid.
   *
   * @param tagValue The value of the tag to find.
   * @param createdBy The user that created the tag.
   * @return The tag with the specified value.
   */
  Optional<Tag> findTagByTagValueAndCreatedBy(String tagValue, String createdBy);
}
