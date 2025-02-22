

drop table if exists address;

create table address
(
    id   bigserial not null primary key,
    street varchar(50)
);



drop table if exists phone;

create table phone
(
    id   bigserial not null primary key,
    number varchar(50),
    client_id bigint not null,

    foreign key (client_id) references client(id)
);



alter table client
    add column address_id bigint unique,
    add constraint fk_address foreign key (address_id) references address(id);
