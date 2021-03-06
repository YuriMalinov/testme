create schema tm;
create table tm.app_user (
  id serial primary key,
  user_name text not null,
  full_name text not null,
  is_admin boolean not null default false
);

create table tm.test_pass (
  id serial primary key,
  code text not null,
  test_code text not null,
  test_title text not null,
  test_description text not null,
  test_default_time int not null,
  app_user_id int not null references tm.app_user(id),
  created timestamp not null default now(),
  current_question_num int not null default 0
);

create unique index ix_test_pass_code on tm.test_pass(code);

create table tm.question_answer (
  id serial primary key,
  test_pass_id int not null references tm.test_pass(id) on delete cascade,
  question_json text not null,
  original_index int not null,
  num int not null,
  started timestamp,
  answered timestamp,
  answers int[],
  criterias_met int[],
  text_answer text,
  mark double precision,
  marked_by_id int references tm.app_user(id) on delete set null,
  comment text
);

create table persistent_logins (username varchar(64) not null, series varchar(64) primary key, token varchar(64) not null, last_used timestamp not null);