create table if not exists category(
    id serial primary key,
    name varchar(55) not null
);

create table if not exists product(
    id serial primary key,
    title varchar(255) not null,
    price real not null,
    category_id int not null,
    constraint fk_category_id foreign key (category_id) references category(id)
);