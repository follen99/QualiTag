package it.unisannio.studenti.qualitag.mapper;

import it.unisannio.studenti.qualitag.dto.tag.TagCreateDto;
import it.unisannio.studenti.qualitag.model.DefaultColor;
import it.unisannio.studenti.qualitag.model.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {

    public TagMapper() {}

    /**
     * Converts a TagCreateDto to a Tag entity.
     *
     * @param dto The TagCreateDto to convert.
     * @return The Tag entity.
     */
    public Tag toEntity(TagCreateDto dto) {
        if (dto == null) {
            return null;
        }
        String colorHex = dto.colorHex();
        if (colorHex == null || colorHex.isEmpty()) {
            colorHex = this.chooseColorRandomlyFromDefaults();
        }
        return new Tag(
            dto.tagValue().toUpperCase(),
            dto.createdBy(),
            colorHex
        );
    }

    public TagCreateDto toDto(Tag entity) {
        if (entity == null) {
            return null;
        }
        return new TagCreateDto(
            entity.getTagValue(),
            entity.getCreatedBy(),
            entity.getColorHex()
        );
    }

    /**
     * Chooses a color randomly from the default colors.
     *
     * @return The color in hex format.
     */
    private String chooseColorRandomlyFromDefaults() {
        DefaultColor[] colors = DefaultColor.values();
        int randomIndex = (int) (Math.random() * colors.length);
        return colors[randomIndex].getRgb();
    }
}
