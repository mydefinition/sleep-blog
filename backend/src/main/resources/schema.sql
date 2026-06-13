CREATE TABLE IF NOT EXISTS user (
    id          INTEGER     PRIMARY KEY AUTOINCREMENT,
    nickname    VARCHAR(50) NOT NULL UNIQUE,
    password    VARCHAR(80) NOT NULL,
    role        VARCHAR(10) NOT NULL,
    email       VARCHAR(50) NOT NULL UNIQUE,
    created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS category (
    id          INTEGER     PRIMARY KEY AUTOINCREMENT,
    name        VARCHAR(50) NOT NULL,
    level1_id   INTEGER     NOT NULL REFERENCES category(id) DEFAULT 0,
    level2_id   INTEGER     NOT NULL REFERENCES category(id) DEFAULT 0,
    UNIQUE(name, level1_id, level2_id)
);

CREATE TABLE IF NOT EXISTS article (
    id              INTEGER         PRIMARY KEY AUTOINCREMENT,
    title           VARCHAR(200)    NOT NULL,
    content         TEXT            NOT NULL,
    summary         VARCHAR(300),
    author_id       INTEGER         NOT NULL REFERENCES user(id),
    is_published    TINYINT         NOT NULL DEFAULT 0,
    is_deleted      TINYINT         NOT NULL DEFAULT 0,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME,
    category_id     INTEGER         REFERENCES category(id)
);

CREATE TABLE IF NOT EXISTS tag (
    id      INTEGER     PRIMARY KEY AUTOINCREMENT,
    name    VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS article_tag (
    article_id  INTEGER NOT NULL REFERENCES article(id),
    tag_id      INTEGER NOT NULL REFERENCES tag(id),
    PRIMARY KEY (article_id, tag_id)
);

CREATE TABLE IF NOT EXISTS comment (
    id          INTEGER     PRIMARY KEY AUTOINCREMENT,
    content     TEXT        NOT NULL,
    article_id  INTEGER     NOT NULL REFERENCES article(id),
    user_id     INTEGER     NOT NULL REFERENCES user(id),
    is_deleted  TINYINT     NOT NULL DEFAULT 0,
    created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS article_image(
    article_id  INTEGER     NOT NULL REFERENCES article(id),
    file_id     INTEGER     NOT NULL REFERENCES file_storage(id)
);

CREATE TABLE IF NOT EXISTS file_storage (
    id              INTEGER         PRIMARY KEY AUTOINCREMENT,
    mine_type       VARCHAR(30)     NOT NULL,
    name            VARCHAR(50)     NOT NULL,
    size            BIGINT          NOT NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cloud_file (
    id          INTEGER         PRIMARY KEY AUTOINCREMENT,
    file_id     INTEGER         REFERENCES file_storage(id),
    parent_id   INTEGER         NOT NULL,
    owner_id    INTEGER,
    name        VARCHAR(255)    NOT NULL,
    is_dir      TINYINT         NOT NULL,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS article_image (
    article_id  INTEGER NOT NULL REFERENCES article(id),
    file_id     INTEGER NOT NULL REFERENCES file_storage(id),
    PRIMARY KEY(article_id,file_id)
);

CREATE INDEX IF NOT EXISTS idx_article_status
    ON article(is_published, is_deleted, created_at);

CREATE INDEX IF NOT EXISTS idx_article_created_at
    ON article(created_at DESC);

CREATE INDEX IF NOT EXISTS idx_tag_article
    ON article_tag(tag_id, article_id);
