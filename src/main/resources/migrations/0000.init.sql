create schema tm;

create table tm.app_user (
  id serial primary key,
  user_name text not null,
  full_name text not null,
  password text,
  is_active boolean not null default true,
  is_admin boolean not null default false
);

create table tm.test (
  id serial primary key,
  title text not null,
  description text not null,
  time_description text,
  default_time int not null,
  shuffle_questions boolean not null,
  shuffle_answers boolean not null,
  created timestamp not null
);

create table tm.question (
  id serial primary key,
  test_id int not null references tm.test(id) on delete cascade,
  question text not null,
  weight double precision not null,
  force_multi_answer boolean not null,
  time_override int,
  category text not null,
  advanced_weight boolean not null
);

create table tm.question_answer_variant (
  id serial primary key,
  question_id int not null references tm.question(id) on delete cascade,
  text text not null,
  correct boolean not null
);

create table tm.question_criteria (
  id serial primary key,
  question_id int not null references tm.question(id) on delete cascade,
  text text not null
);

create table tm.test_pass (
  id serial primary key,
  code text not null,
  test_id int not null references tm.test(id) on delete no action, -- explicit delete: data loss
  app_user_id int not null references tm.app_user(id),
  created timestamp not null default now(),
  current_question_num int not null default 0,
  shuffle_answers boolean not null default false
);

create unique index ix_test_pass_code on tm.test_pass(code);

create table tm.question_answer (
  id serial primary key,
  test_pass_id int not null references tm.test_pass(id) on delete cascade,
  time int not null,
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