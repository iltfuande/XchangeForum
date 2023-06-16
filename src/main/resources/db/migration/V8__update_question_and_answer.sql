alter table question alter column title type varchar(100) using title::varchar(100);

alter table answer add constraint only_one_answer unique (question_id, user_id);