ALTER TABLE answer ADD COLUMN hidden BOOLEAN DEFAULT false;

ALTER TABLE question ADD COLUMN hidden BOOLEAN DEFAULT false;

ALTER TABLE "user" ADD COLUMN banned BOOLEAN DEFAULT false;