package it.unisannio.studenti.qualitag.repository;

import it.unisannio.studenti.qualitag.model.Tag;
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
  List<Tag> findTagsByCreatedBy(String value); // created by username

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

}
