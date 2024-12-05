package it.unisannio.studenti.qualitag.repository;

import it.unisannio.studenti.qualitag.model.Tag;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TagRepository extends MongoRepository<Tag, String> {

  //List<Tag> findByCreatedBy(String createdBy);
  List<Tag> findTagByCreatedBy(String value);     // created by username


  List<Tag> findByTagValueContaining(String value);


}
