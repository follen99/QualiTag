package it.unisannio.studenti.qualitag.repository;

import it.unisannio.studenti.qualitag.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, String> {

}
