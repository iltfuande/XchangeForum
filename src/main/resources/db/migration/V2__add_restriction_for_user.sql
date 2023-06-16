ALTER TABLE "user" ADD CONSTRAINT email_unique UNIQUE (email);
ALTER TABLE "user" ADD CONSTRAINT nickname_unique UNIQUE (nickname);
