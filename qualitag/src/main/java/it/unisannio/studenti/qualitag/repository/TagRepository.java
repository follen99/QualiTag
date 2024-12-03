package it.unisannio.studenti.qualitag.repository;

import it.unisannio.studenti.qualitag.model.Tag;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TagRepository extends MongoRepository<Tag, String> {
    List<Tag> findByCreatedBy(String createdBy);

    List<Tag> findByTagValueContaining(String value);


}
