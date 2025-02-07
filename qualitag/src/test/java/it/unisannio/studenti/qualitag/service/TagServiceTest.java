package it.unisannio.studenti.qualitag.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.unisannio.studenti.qualitag.constants.TagConstants;
import it.unisannio.studenti.qualitag.dto.tag.TagCreateDto;
import it.unisannio.studenti.qualitag.dto.tag.TagResponseDto;
import it.unisannio.studenti.qualitag.dto.tag.TagUpdateDto;
import it.unisannio.studenti.qualitag.exception.TagValidationException;
import it.unisannio.studenti.qualitag.mapper.TagMapper;
import it.unisannio.studenti.qualitag.model.Artifact;
import it.unisannio.studenti.qualitag.model.Tag;
import it.unisannio.studenti.qualitag.model.User;
import it.unisannio.studenti.qualitag.repository.ArtifactRepository;
import it.unisannio.studenti.qualitag.repository.TagRepository;
import it.unisannio.studenti.qualitag.repository.UserRepository;
import it.unisannio.studenti.qualitag.security.model.CustomUserDetails;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class TagServiceTest {

  @Mock
  private TagRepository tagRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  ArtifactRepository artifactRepository;
  @Mock
  private TagMapper tagMapper;

  @InjectMocks
  private TagService tagService;

  private Tag tag1, tag2;
  private Artifact artifact;
  private User user;

  private TagCreateDto tagCreateDto1, tagCreateDto2;
  private TagUpdateDto tagUpdateDto;

  /**
   * Sets up the test environment.
   */
  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);

    //Initialize the objects
    tag1 = new Tag("TAG1", "6798e2740b80b85362a8ba90", "#fff8de");
    tag1.setTagId("6744ba6c60e0564864250e89");
    tag2 = new Tag("TAG2", "6798e2740b80b85362a8ba90", "#295f98");
    tag2.setTagId("6755b79afc22f97c06a34275");

    user = new User("user1", "user1@example.com",
        "password1", "Jane", "Doe");
    user.setUserId("6798e2740b80b85362a8ba90");
    user.setTagIds(new ArrayList<>
        (Arrays.asList(tag1.getTagId(), tag2.getTagId())));

    artifact = new Artifact("artifactName",
        "projectId", "teamId", "filePath");
    artifact.setArtifactId("6754705c8d6446369ca02b62");
    artifact.setTags(new ArrayList<>(Arrays.asList(tag1.getTagId()
        , tag2.getTagId())));
    tag1.setArtifactIds(new ArrayList<>(List.of(artifact.getArtifactId())));
    tag2.setArtifactIds(new ArrayList<>(List.of(artifact.getArtifactId())));

    //Initialize the DTO
    tagCreateDto1 = new TagCreateDto("TAG3",
        "6798e2740b80b85362a8ba90", "#fff8de");
    tagCreateDto2 = new TagCreateDto("TAG4",
        "6798e2740b80b85362a8ba90", "#295f98");
    tagUpdateDto = new TagUpdateDto("NEWVALUE", "#d0e8c5");

    //Initialize authorization details
    // Mock SecurityContextHolder to provide an authenticated user
    Authentication authentication = mock(Authentication.class);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getName()).thenReturn("user1");
    when(authentication.getPrincipal()).thenReturn(new CustomUserDetails(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
  }

  /**
   * Tests a successful execution of the createTag method.
   *
   * @throws TagValidationException if the tag is invalid
   */
  @Test
  public void testCreateTag_Success()
      throws TagValidationException {
    //Arrange
    Tag newTag = new Tag("TAG3",
        "6798e2740b80b85362a8ba90", "#fff8de");

    when(userRepository.findByUserId(user.getUserId())).thenReturn(user);
    when(userRepository.existsByUsername("user1")).thenReturn(true);
    when(userRepository.existsById(user.getUserId())).thenReturn(true);
    when(userRepository.save(user)).thenReturn(user);
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(tagRepository.save(any(Tag.class))).thenReturn(newTag);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);

    when(tagMapper.toEntity(any(TagCreateDto.class))).thenReturn(newTag);

    //Act
    ResponseEntity<?> response = tagService.createTag(tagCreateDto1);

    //Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag added successfully.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createTag method when the tag has the same values but different
   * artifacts
   */
  @Test
  public void testCreateTag_SameTagButDifferentArtifacts() {
    //Arrange
    Tag newTag = new Tag("TAG2",
        "6798e2740b80b85362a8ba90", "#fff8de");
    newTag.setArtifactIds(new ArrayList<>(List.of("6754705c8d6446369ca02b64")));

    when(userRepository.findByUserId(user.getUserId())).thenReturn(user);
    when(userRepository.existsByUsername("user1")).thenReturn(true);
    when(userRepository.existsById(user.getUserId())).thenReturn(true);
    when(userRepository.save(user)).thenReturn(user);
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(tagRepository.save(any(Tag.class))).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);

    when(tagMapper.toEntity(any(TagCreateDto.class))).thenReturn(newTag);

    //Act
    ResponseEntity<?> response = tagService.createTag(tagCreateDto1);

    //Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag added successfully.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createTag method with an invalid tag.
   */
  @Test
  public void testCreateTag_InvalidTag() {
    //Arrange
    TagCreateDto invalidTagCreateDto = new TagCreateDto("", "", "");

    //Act
    ResponseEntity<?> response = tagService.createTag(invalidTagCreateDto);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag information is not valid.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createTag method with a tag value that is too short.
   */
  @Test
  public void testCreateTag_ValueTooShort() {
    //Arrange
    TagCreateDto invalidTagCreateDto = new TagCreateDto("AB",
        "6798e2740b80b85362a8ba90", "#fff8de");

    //Act
    ResponseEntity<?> response = tagService.createTag(invalidTagCreateDto);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag value must be at least "
        + TagConstants.MIN_TAG_VALUE_LENGTH + " characters long.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createTag method with a tag value that is too long.
   */
  @Test
  public void testCreateTag_ValueTooLong() {
    //Arrange
    TagCreateDto invalidTagCreateDto = new TagCreateDto("SIXTEENCHARACTERS",
        "6798e2740b80b85362a8ba90", "#fff8de");

    //Act
    ResponseEntity<?> response = tagService.createTag(invalidTagCreateDto);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag value cannot be longer than "
        + TagConstants.MAX_TAG_VALUE_LENGTH + " characters.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createTag method with a tag color that is too long.
   */
  @Test
  public void testCreateTag_ColorTooLong() {
    //Arrange
    TagCreateDto invalidTagCreateDto = new TagCreateDto("tag3",
        "6798e2740b80b85362a8ba90", "#ffff8de");

    //Act
    ResponseEntity<?> response = tagService.createTag(invalidTagCreateDto);

    //Assert
    int expectedLength = TagConstants.TAG_COLOR_LENGTH + 1;
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag color cannot be longer than " + expectedLength
        + " characters including '#' symbol.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createTag method with a tag color that is too long (without #)
   */
  @Test
  public void testCreateTag_ColorTooLongWithoutSymbol() {
    //Arrange
    TagCreateDto invalidTagCreateDto = new TagCreateDto("tag3",
        "6798e2740b80b85362a8ba90", "ffff8de");

    //Act
    ResponseEntity<?> response = tagService.createTag(invalidTagCreateDto);

    //Assert
    int expectedLength = TagConstants.TAG_COLOR_LENGTH;
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag color cannot be longer than " + expectedLength
        + " characters including '#' symbol.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createTag method with a tag color is not a hexadecimal value.
   */
  @Test
  public void testCreateTag_ColorNotHexadecimal() {
    //Arrange
    TagCreateDto invalidTagCreateDto = new TagCreateDto("tag3",
        "6798e2740b80b85362a8ba90", "#ffff8z");

    //Act
    ResponseEntity<?> response = tagService.createTag(invalidTagCreateDto);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag color must be a hexadecimal value.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createTag method when createdBy camp is too long
   */
  @Test
  public void testCreateTag_CreatedByTooLong() {
    //Arrange
    TagCreateDto invalidTagCreateDto = new TagCreateDto("tag3",
        "InvalidUserItsInvalidOhMy", "#fff8de");

    //Act
    ResponseEntity<?> response = tagService.createTag(invalidTagCreateDto);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "User does not exist.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the createTag method when the tag already exist
   */
  @Test
  public void testCreateTag_TagAlreadyExist() {
    //Arrange
    Tag newTag = new Tag("TAG3",
        "6798e2740b80b85362a8ba90", "#fff8de");
    newTag.setArtifactIds(new ArrayList<>(List.of("6754705c8d6446369ca02b62")));

    when(userRepository.findByUserId(user.getUserId())).thenReturn(user);
    when(userRepository.existsByUsername("user1")).thenReturn(true);
    when(userRepository.existsById(user.getUserId())).thenReturn(true);
    when(userRepository.save(user)).thenReturn(user);
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(tagRepository.save(any(Tag.class))).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(newTag);

    when(tagMapper.toEntity(any(TagCreateDto.class))).thenReturn(newTag);

    //Act
    ResponseEntity<?> response = tagService.createTag(tagCreateDto1);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag already exists.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the addTagsToArtifactAndUser method
   *
   * @throws NoSuchMethodException     if the method does not exist
   * @throws InvocationTargetException if the method cannot be invoked
   * @throws IllegalAccessException    if the method cannot be accessed
   */
  @Test
  public void testAddTagsToArtifactAndUser_Success()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    //Arrange
    TagService tagServiceSpy = Mockito.spy(tagService);
    Tag newTag1 = new Tag("TAG3",
        "6798e2740b80b85362a8ba90", "#fff8de");
    newTag1.setTagId("67a1bc2d90e123456789ab76");
    Tag newTag2 = new Tag("TAG4",
        "6798e2740b80b85362a8ba90", "#295f98");
    newTag2.setTagId("67a1bc2d90e123456789ab77");

    List<TagCreateDto> tagList = new ArrayList<>(Arrays.asList(tagCreateDto1, tagCreateDto2));
    when(userRepository.findByUserId(user.getUserId())).thenReturn(user);
    when(userRepository.existsByUsername("user1")).thenReturn(true);
    when(userRepository.existsById(user.getUserId())).thenReturn(true);
    when(userRepository.save(user)).thenReturn(user);
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(tagRepository.save(newTag1)).thenReturn(newTag1);
    when(tagRepository.save(newTag2)).thenReturn(newTag2);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);
    when(tagRepository.findTagByTagId(newTag1.getTagId())).thenReturn(newTag1);
    when(tagRepository.findTagByTagId(newTag2.getTagId())).thenReturn(newTag2);
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);

    Method validateTagMethod = TagService.class.getDeclaredMethod(
        "validateTag", TagCreateDto.class);
    validateTagMethod.setAccessible(true);

    TagCreateDto validatedTagCreateDto1 =
        (TagCreateDto) validateTagMethod.invoke(tagServiceSpy, tagCreateDto1);

    TagCreateDto validatedTagCreateDto2 =
        (TagCreateDto) validateTagMethod.invoke(tagServiceSpy, tagCreateDto2);

    when(tagMapper.toEntity(validatedTagCreateDto1)).thenReturn(newTag1);
    when(tagMapper.toEntity(validatedTagCreateDto2)).thenReturn(newTag2);

    //Act
    ResponseEntity<?> response = tagService.addTagsToArtifactAndUser(tagList,
        artifact.getArtifactId());

    //Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tags added successfully.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the addTagsToArtifactAndUser method when one of the tags already exists
   */
  @Test
  public void testAddTagsToArtifactAndUser_TagAlreadyExist()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    //Arrange
    TagService tagServiceSpy = Mockito.spy(tagService);
    Tag newTag1 = new Tag("TAG3",
        "6798e2740b80b85362a8ba90", "#fff8de");
    newTag1.setTagId("67a1bc2d90e123456789ab76");
    Tag newTag2 = new Tag("TAG2",
        "6798e2740b80b85362a8ba90", "#295f98");
    newTag2.setTagId("67a1bc2d90e123456789ab77");

    List<TagCreateDto> tagList = new ArrayList<>(Arrays.asList(tagCreateDto1, tagCreateDto2));
    when(userRepository.findByUserId(user.getUserId())).thenReturn(user);
    when(userRepository.existsByUsername("user1")).thenReturn(true);
    when(userRepository.existsById(user.getUserId())).thenReturn(true);
    when(userRepository.save(user)).thenReturn(user);
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(tagRepository.save(newTag1)).thenReturn(newTag1);
    when(tagRepository.save(newTag2)).thenReturn(newTag2);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);
    when(tagRepository.findTagByTagId(newTag1.getTagId())).thenReturn(newTag1);
    when(tagRepository.findTagByTagId(newTag2.getTagId())).thenReturn(newTag2);
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);

    Method validateTagMethod = TagService.class.getDeclaredMethod(
        "validateTag", TagCreateDto.class);
    validateTagMethod.setAccessible(true);

    TagCreateDto validatedTagCreateDto1 =
        (TagCreateDto) validateTagMethod.invoke(tagServiceSpy, tagCreateDto1);

    TagCreateDto validatedTagCreateDto2 =
        (TagCreateDto) validateTagMethod.invoke(tagServiceSpy, tagCreateDto2);

    when(tagMapper.toEntity(validatedTagCreateDto1)).thenReturn(newTag1);
    when(tagMapper.toEntity(validatedTagCreateDto2)).thenReturn(newTag2);

    //Act
    ResponseEntity<?> response = tagService.addTagsToArtifactAndUser(tagList,
        artifact.getArtifactId());

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag already exists.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the getTagById method
   */
  @Test
  public void testGetTagById_Success() {
    //Arrange
    when(tagRepository.findById(tag1.getTagId())).thenReturn(java.util.Optional.of(tag1));

    //Act
    ResponseEntity<?> response = tagService.getTagById(tag1.getTagId());

    //Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag found.");
    responseBody.put("tag", tag1);
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getTagById method when the tag is null
   */
  @Test
  public void testGetTagById_TagIsNull() {
    //Arrange
    String tagId = null;

    //Act
    ResponseEntity<?> response = tagService.getTagById(tagId);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getTagById method when the tag is empty
   */
  @Test
  public void testGetTagById_TagIsEmpty() {
    //Arrange
    String tagId = "";

    //Act
    ResponseEntity<?> response = tagService.getTagById(tagId);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getTagById method when the tag is not found
   */
  @Test
  public void testGetTagById_TagNotFound() {
    //Arrange
    when(tagRepository.findById(tag1.getTagId())).thenReturn(java.util.Optional.empty());

    //Act
    ResponseEntity<?> response = tagService.getTagById(tag1.getTagId());

    //Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getTagsByArtifactId method when the logged-in user is not the creator
   * of the tag
   */
  @Test
  public void testGetTagsById_NotCreator() {

    tag1.setCreatedBy("67557b9355d04b383badf456");

    when(tagRepository.findById(tag1.getTagId())).thenReturn(java.util.Optional.of(tag1));

    //Act
    ResponseEntity<?> response = tagService.getTagById(tag1.getTagId());

    //Assert
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "You are not the creator of the tag.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of getTagsByValue method
   */
  @Test
  public void testGetTagsByValue_Success() {
    //Arrange
    TagResponseDto tagResponseDto = new TagResponseDto(tag1.getTagId(), tag1.getTagValue(),
        tag1.getCreatedBy(), tag1.getColorHex());

    when(tagRepository.findByTagValueContaining(tag1.getTagValue().toUpperCase()))
        .thenReturn(new ArrayList<>(List.of(tag1)));
    when(tagMapper.getResponseDtoList(new ArrayList<>(List.of(tag1))))
        .thenReturn(new ArrayList<>(List.of(tagResponseDto)));

    //Act
    ResponseEntity<?> response = tagService.getTagsByValue(tag1.getTagValue());

    //Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(List.of(tagResponseDto), response.getBody());
  }

  /**
   * Tests an execution of the getTagsByValue method when the tag value is null
   */
  @Test
  public void testGetTagsByValue_TagValueIsNull() {
    //Arrange
    String tagValue = null;

    //Act
    ResponseEntity<?> response = tagService.getTagsByValue(tagValue);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Tag value is null or empty.", response.getBody());
  }

  /**
   * Tests an execution of the getTagsByValue method when the tag value is empty
   */
  @Test
  public void testGetTagsByValue_TagValueIsEmpty() {
    //Arrange
    String tagValue = "";

    //Act
    ResponseEntity<?> response = tagService.getTagsByValue(tagValue);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Tag value is null or empty.", response.getBody());
  }

  /**
   * Tests an execution of the getTagsByValue method when no tags are found
   */
  @Test
  public void testGetTagsByValue_NoTagsFound() {
    //Arrange
    when(tagRepository.findByTagValueContaining(tag1.getTagValue().toUpperCase()))
        .thenReturn(new ArrayList<>());

    //Act
    ResponseEntity<?> response = tagService.getTagsByValue(tag1.getTagValue());

    //Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("No tags found for the given value.", response.getBody());
  }

  /**
   * Tests a successful execution of the getTagsByUser method (Input: UserId)
   */
  @Test
  public void testGetTagsByUserId() {
    //Arrange
    when(userRepository.findByUserId(user.getUserId())).thenReturn(user);
    when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);

    //Act
    ResponseEntity<?> response = tagService.getTagsByUser(user.getUserId());

    //Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    List<Tag> tags = new ArrayList<>(Arrays.asList(tag1, tag2));
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("tags", tags);
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the getTagsByUser method (Input: Email)
   */
//  @Test
//  public void testGetTagsByUserEmail() {
//    //Arrange
//    when(userRepository.findByUserId(user.getUserId())).thenReturn(user);
//    when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
//    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
//    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
//    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);
//
//    //Act
//    ResponseEntity<?> response = tagService.getTagsByUser(user.getEmail());
//
//    //Assert
//    assertEquals(HttpStatus.OK, response.getStatusCode());
//    List<Tag> tags = new ArrayList<>(Arrays.asList(tag1, tag2));
//    Map<String, Object> responseBody = new HashMap<>();
//    responseBody.put("tags", tags);
//    assertEquals(responseBody, response.getBody());
//  }

  /**
   * Tests a successful execution of the getTagsByUser method (Input: Username)
   */
  @Test
  public void testGetTagsByUsername() {
    //Arrange
    when(userRepository.findByUserId(user.getUserId())).thenReturn(user);
    when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);

    //Act
    ResponseEntity<?> response = tagService.getTagsByUser(user.getUsername());

    //Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    List<Tag> tags = new ArrayList<>(Arrays.asList(tag1, tag2));
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("tags", tags);
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getTagsByUser method when the user id is null
   */
  @Test
  public void testGetTagsByUser_UserIdIsNull() {
    //Arrange
    String userId = null;

    //Act
    ResponseEntity<?> response = tagService.getTagsByUser(userId);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "User id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getTagsByUser method when the user id is empty
   */
  @Test
  public void testGetTagsByUser_UserIdIsEmpty() {
    //Arrange
    String userId = "";

    //Act
    ResponseEntity<?> response = tagService.getTagsByUser(userId);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "User id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getTagsByUser method when the user is not found
   */
  @Test
  public void testGetTagsByUser_UserNotFound() {
    //Arrange
    when(userRepository.findByUserId(user.getUserId())).thenReturn(null);

    //Act
    ResponseEntity<?> response = tagService.getTagsByUser(user.getUserId());

    //Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "User not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getTagsByUser method when the user doesn't have any tags
   */
  @Test
  public void testGetTagsByUser_UserHasNoTags() {
    //Arrange
    user.setTagIds(new ArrayList<>());
    when(userRepository.findByUserId(user.getUserId())).thenReturn(user);

    //Act
    ResponseEntity<?> response = tagService.getTagsByUser(user.getUserId());

    //Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("tags", new ArrayList<>());
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the getTagsByUser method when the tags are not found
   */
  @Test
  public void testGetTagsByUser_TagsNotFound() {
    //Arrange
    when(userRepository.findByUserId(user.getUserId())).thenReturn(user);
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(null);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(null);

    //Act
    ResponseEntity<?> response = tagService.getTagsByUser(user.getUserId());

    //Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successful execution of the deleteTag method
   */
  @Test
  public void testDeleteTag_Success() {
    //Arrange
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(userRepository.findByUserId(user.getUserId())).thenReturn(user);
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId()))
        .thenReturn(artifact);
    when(tagRepository.existsById(tag1.getTagId())).thenReturn(false);

    //Act
    ResponseEntity<?> response = tagService.deleteTag(tag1.getTagId());

    //Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag deleted successfully.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteTag method when the tag id is null
   */
  @Test
  public void testDeleteTag_TagIdNull() {
    //Arrange
    String tagIdNull = null;

    //Act
    ResponseEntity<?> response = tagService.deleteTag(tagIdNull);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteTag method when the tag id is null
   */
  @Test
  public void testDeleteTag_TagIdEmpty() {
    //Act
    ResponseEntity<?> response = tagService.deleteTag("");

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteTag method where the tag is not found
   */
  @Test
  public void testDeleteTag_TagNotFound() {
    //Arrange
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(null);

    //Act
    ResponseEntity<?> response = tagService.deleteTag(tag1.getTagId());

    //Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteTag method where the user is not the creator of the tag
   */
  @Test
  public void testDeleteTag_UserNotCreator() {
    //Arrange
    tag1.setCreatedBy("67557b9355d04b383badf456");
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);

    //Act
    ResponseEntity<?> response = tagService.deleteTag(tag1.getTagId());

    System.out.println(response.getBody());

    //Assert
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "You are not the creator of the tag.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the deleteTag method where the tag is not deleted
   */
  @Test
  public void testDeleteTag_TagNotDeleted() {
    //Arrange
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(userRepository.findByUserId(user.getUserId())).thenReturn(user);
    when(artifactRepository.findArtifactByArtifactId(artifact.getArtifactId())).
        thenReturn(artifact);
    when(tagRepository.existsById(tag1.getTagId())).thenReturn(true);

    //Act
    ResponseEntity<?> response = tagService.deleteTag(tag1.getTagId());

    //Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag not deleted.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests a successuful execution of the updateTag method
   */
  @Test
  public void testUpdateTag_Success() {
    //Arrange
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);
    when(userRepository.findByUserId(tag1.getCreatedBy())).thenReturn(user);
    when(tagRepository.save(tag1)).thenReturn(tag1);

    //Act
    ResponseEntity<?> response = tagService.updateTag(tagUpdateDto, tag1.getTagId());

    //Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag updated successfully.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateTag method where the tagId is null
   */
  @Test
  public void testUpdateTag_TagIdNull() {
    //Arrange
    String tagIdNull = null;

    //Act
    ResponseEntity<?> response = tagService.updateTag(tagUpdateDto, tagIdNull);

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateTag method where the tagId is empty
   */
  @Test
  public void testUpdateTag_TagIdEmpty() {
    //Act
    ResponseEntity<?> response = tagService.updateTag(tagUpdateDto, "");

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag id is null or empty.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateTag method where the tag is not found
   */
  @Test
  public void testUpdateTag_TagNotFound() {
    //Arrange
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(null);

    //Act
    ResponseEntity<?> response = tagService.updateTag(tagUpdateDto, tag1.getTagId());

    //Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag not found.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateTag method where the user is not the creator of the tag
   */
  @Test
  public void testUpdateTag_UserNotCreator() {
    //Arrange
    tag1.setCreatedBy("67557b9355d04b383badf456");
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);

    //Act
    ResponseEntity<?> response = tagService.updateTag(tagUpdateDto, tag1.getTagId());

    //Assert
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "You are not the creator of the tag.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateTag method where the new tag information is not valid
   */
  @Test
  public void testUpdateTag_InvalidTag() {
    //Arrange
    TagUpdateDto invalidTagUpdateDto = new TagUpdateDto("", "");
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);

    //Act
    ResponseEntity<?> response = tagService.updateTag(invalidTagUpdateDto, tag1.getTagId());

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag information is not valid.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateTag method where the tag value is too short
   */
  @Test
  public void testUpdateTag_ValueTooShort() {
    //Arrange
    TagUpdateDto invalidTagUpdateDto = new TagUpdateDto("AB", "#d0e8c5");
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);

    //Act
    ResponseEntity<?> response = tagService.updateTag(invalidTagUpdateDto, tag1.getTagId());

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag value must be at least "
        + TagConstants.MIN_TAG_VALUE_LENGTH + " characters long.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateTag method where the tag value is too long
   */
  @Test
  public void testUpdateTag_ValueTooLong() {
    //Arrange
    TagUpdateDto invalidTagUpdateDto = new TagUpdateDto("SIXTEENCHARACTERS", "#d0e8c5");
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);

    //Act
    ResponseEntity<?> response = tagService.updateTag(invalidTagUpdateDto, tag1.getTagId());

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag value cannot be longer than "
        + TagConstants.MAX_TAG_VALUE_LENGTH + " characters.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateTag method where the tag color is too long
   */
  @Test
  public void testUpdateTag_ColorTooLong() {
    //Arrange
    TagUpdateDto invalidTagUpdateDto = new TagUpdateDto("tag3", "#ffff8de");
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);

    //Act
    ResponseEntity<?> response = tagService.updateTag(invalidTagUpdateDto, tag1.getTagId());

    //Assert
    int expectedLength = TagConstants.TAG_COLOR_LENGTH + 1;
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag color cannot be longer than " + expectedLength
        + " characters including '#' symbol.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateTag method where the tag color is too long (without #)
   */
  @Test
  public void testUpdateTag_ColorTooLongWithoutSymbol() {
    //Arrange
    TagUpdateDto invalidTagUpdateDto = new TagUpdateDto("tag3", "ffff8de");
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);

    //Act
    ResponseEntity<?> response = tagService.updateTag(invalidTagUpdateDto, tag1.getTagId());

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag color cannot be longer than " + TagConstants.TAG_COLOR_LENGTH
        + " characters including '#' symbol.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateTag method where the tag color is not a hexadecimal value
   */
  @Test
  public void testUpdateTag_ColorNotHexadecimal() {
    //Arrange
    TagUpdateDto invalidTagUpdateDto = new TagUpdateDto("tag3", "#ffff8z");
    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);

    //Act
    ResponseEntity<?> response = tagService.updateTag(invalidTagUpdateDto, tag1.getTagId());

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "Tag color must be a hexadecimal value.");
    assertEquals(responseBody, response.getBody());
  }

  /**
   * Tests an execution of the updateTag method where the user already has a tag with the same
   * value
   */
  @Test
  public void testUpdateTag_TagAlreadyExist() {
    //Arrange
    TagUpdateDto sameTagUpdateDto = new TagUpdateDto("TAG2", "#d0e8c5");

    when(tagRepository.findTagByTagId(tag1.getTagId())).thenReturn(tag1);
    when(tagRepository.findTagByTagId(tag2.getTagId())).thenReturn(tag2);
    when(userRepository.findByUserId(tag1.getCreatedBy())).thenReturn(user);
    when(tagRepository.save(tag1)).thenReturn(tag1);

    //Act
    ResponseEntity<?> response = tagService.updateTag(sameTagUpdateDto, tag1.getTagId());

    //Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("msg", "User already has a tag with the same value.");
    assertEquals(responseBody, response.getBody());
  }

}


