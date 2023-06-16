CREATE TABLE question_tag
(
    question_id INTEGER REFERENCES question,
    tag VARCHAR(30)
)