package it.unisannio.studenti.qualitag.service;

import it.unisannio.studenti.qualitag.dto.tag.TagCreateDto;
import it.unisannio.studenti.qualitag.mapper.TagMapper;
import it.unisannio.studenti.qualitag.model.Tag;
import it.unisannio.studenti.qualitag.repository.TagRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    /**
     * Constructs a new TagService.
     *
     * @param tagRepository The tag repository.
     * @param tagMapper     The tag mapper.
     */
    public TagService(TagRepository tagRepository, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    /**
     * Adds a tag.
     *
     * @param tagCreateDto The tag to add.
     * @return The response entity.
     */
    public ResponseEntity<?> addTag(TagCreateDto tagCreateDto) {
        if (tagCreateDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("TagCreateDto is null");
        }
        if (tagCreateDto.tagValue() == null || tagCreateDto.tagValue().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tag name is null");
        }

        Tag tag = tagMapper.toEntity(tagCreateDto);
        this.tagRepository.save(tag);
        return null;
    }

    /**
     * Gets all tags.
     *
     * @return The response entity.
     */
    public ResponseEntity<?> getAllTags() {
        List<Tag> tags = tagRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(tags);
    }

    /**
     * Deletes a tag by its id.
     *
     * @param id The id of the tag to delete.
     * @return The response entity.
     */
    public ResponseEntity<?> deleteTag(String id) {
        if (id == null || id.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tag id is null");
        }

        if (!tagRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tag not found");
        }

        tagRepository.deleteById(id);
        return null;
    }
}
