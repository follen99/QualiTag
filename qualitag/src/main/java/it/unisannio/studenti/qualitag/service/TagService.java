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

    public TagService(TagRepository tagRepository, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

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

    public ResponseEntity<?> getAllTags() {
        List<Tag> tags = tagRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(tags);
    }

}
