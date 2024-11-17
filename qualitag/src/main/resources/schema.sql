CREATE TABLE TAG (
    tag_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id VARCHAR(255),
    user_id VARCHAR(255),
    tag_value VARCHAR(255),
    color_hex VARCHAR(7)
);