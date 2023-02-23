create table users(
    id serial primary key,
    name varchar(255) not null,
    surname varchar(255) not null,
    identity_no varchar(11) not null unique,
    gender varchar(11) not null,
    urole  varchar(16)  not null
);

create table lecture(
    id serial primary key,
    name varchar(255) not null,
    teacher_id integer not null,
    CONSTRAINT fk_teacher_id
        FOREIGN KEY (teacher_id)
            REFERENCES users (id) on delete cascade
);

create table user_lectures(
    user_id integer not null,
    lecture_id integer not null,
    CONSTRAINT fk_user_id
        FOREIGN KEY (user_id)
            REFERENCES users (id) on delete cascade,
    CONSTRAINT fk_lecture_id
        FOREIGN KEY (lecture_id)
            REFERENCES lecture (id) on delete cascade
);