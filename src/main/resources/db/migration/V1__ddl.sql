CREATE TABLE "user"
(
    id           SERIAL PRIMARY KEY,
    email        VARCHAR(50)  NOT NULL,
    password     VARCHAR(100) NOT NULL,
    nickname     VARCHAR(50)  NOT NULL,
    first_name   VARCHAR(50)  NOT NULL,
    last_name    VARCHAR(50)  NOT NULL,
    time_created TIMESTAMP    NOT NULL
);

CREATE TABLE question
(
    id              SERIAL PRIMARY KEY,
    created_user_id INTEGER REFERENCES "user" NOT NULL,
    moderator_id    INTEGER REFERENCES "user",
    close           BOOLEAN                   NOT NULL,
    title           VARCHAR(60)               NOT NULL,
    description     TEXT                      NOT NULL,
    tag             VARCHAR(30)[],
    viewed          INTEGER                   NOT NULL,
    time_created    TIMESTAMP                 NOT NULL,
    time_modify     TIMESTAMP
);

CREATE TABLE answer
(
    id          SERIAL PRIMARY KEY,
    question_id INTEGER REFERENCES question,
    user_id     INTEGER REFERENCES "user",
    respond     TEXT NOT NULL
);

ALTER TABLE question
    ADD COLUMN solution_id INTEGER REFERENCES answer;
