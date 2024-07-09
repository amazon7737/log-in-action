insert into user (username, password, nickname, activated) values ('admin', '$2a$12$4iEYUBjwlreN/ynbyg177uxOif3aRS/oP05GfFNj2GBTEzrSskNye', 'admin', 1);
insert into user (username, password, nickname, activated) values ('user', '$2a$12$4iEYUBjwlreN/ynbyg177uxOif3aRS/oP05GfFNj2GBTEzrSskNye', 'user', 1);

insert into authority (authority_name) values ('ROLE_USER');
insert into authority (authority_name) values ('ROLE_ADMIN');

insert into user_authority (user_id, authority_name) values (1, 'ROLE_USER');
insert into user_authority (user_id, authority_name) values (1, 'ROLE_ADMIN');
insert into user_authority (user_id, authority_name) values (2, 'ROLE_USER');