
```sql
create table author(
id serial primary key,
name varchar(255) not null,
surname varchar(255) not null
);

create table books(
id serial primary key,
name varchar(255) not null,
price numeric not null
);

create table page(
id serial primary key,
content varchar(1000) not null,
book_id int,
CONSTRAINT fk_book
FOREIGN KEY (book_id)
REFERENCES books(id) ON DELETE CASCADE
);
```