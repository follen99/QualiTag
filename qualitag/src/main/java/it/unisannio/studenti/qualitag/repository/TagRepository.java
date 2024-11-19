package it.unisannio.studenti.qualitag.repository;

import it.unisannio.studenti.qualitag.model.Tag;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TagRepository extends MongoRepository<Tag, String> {

}
