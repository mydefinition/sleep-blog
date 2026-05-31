CREATE TABLE IF NOT EXISTS user (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   CHAR(60)     NOT NULL,
    role       VARCHAR(10)  NOT NULL DEFAULT 'USER',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS article (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    title      VARCHAR(200) NOT NULL,
    content    TEXT          NOT NULL,
    summary    VARCHAR(300),
    author_id  BIGINT        REFERENCES user(id),
    is_deleted TINYINT       NOT NULL DEFAULT 0,
    created_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME
);

CREATE TABLE IF NOT EXISTS tag (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    name      VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS article_tag (
    article_id BIGINT NOT NULL REFERENCES article(id),
    tag_id     BIGINT NOT NULL REFERENCES tag(id),
    PRIMARY KEY (article_id, tag_id)
);

CREATE TABLE IF NOT EXISTS comment (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    content    TEXT          NOT NULL,
    article_id BIGINT        NOT NULL REFERENCES article(id),
    user_id    BIGINT        NOT NULL REFERENCES user(id),
    is_deleted TINYINT       NOT NULL DEFAULT 0,
    created_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS file_storage (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    name       TEXT    NOT NULL,
    is_dir     INTEGER NOT NULL DEFAULT 0,
    size       INTEGER NULL,
    local_id   INTEGER NULL,
    hash       TEXT    NULL,
    user_id    INTEGER NULL,
    upload_at  TEXT    NULL,
    FOREIGN KEY (local_id) REFERENCES file_storage(id) ON DELETE CASCADE
);
INSERT OR IGNORE INTO file_storage (id, name, is_dir, local_id) VALUES (0, '', 1, NULL);