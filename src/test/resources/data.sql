TRUNCATE first.role RESTART IDENTITY CASCADE;
TRUNCATE first.type RESTART IDENTITY CASCADE;
TRUNCATE first."user" RESTART IDENTITY CASCADE;
TRUNCATE first.document RESTART IDENTITY CASCADE;
TRUNCATE first.file RESTART IDENTITY CASCADE;
TRUNCATE user_roles RESTART IDENTITY CASCADE;

INSERT INTO first.role (name)
VALUES ('Admin'),
       ('User'),
       ('Manager'),
       ('Supervisor'),
       ('Editor');

INSERT INTO first.type (name)
VALUES ('PDF'),
       ('Word'),
       ('Excel'),
       ('Text'),
       ('Image');

INSERT INTO first.type (id, name)
VALUES (0, 'brak typu');


INSERT INTO first."user" (email, name, surname, password)
VALUES ('user1@example.com', 'John', 'Doe', '$2a$10$elYhffIJwKQ.7audmc0buuWbBKGD/MrT/kP9K7RCU3HmYJl6m5m7.'),
       ('user2@example.com', 'Alice', 'Smith', '$2a$10$pHjMoACTdxjGnDLi.As.IewI41gBLkw4/UjAP4i3KoJiUAgcOg6ha'),
       ('user3@example.com', 'Bob', 'Johnson', '$2a$10$pHjMoACTdxjGnDLi.As.IewI41gBLkw4/UjAP4i3KoJiUAgcOg6ha'),
       ('user4@example.com', 'Emily', 'Williams', '$2a$10$pHjMoACTdxjGnDLi.As.IewI41gBLkw4/UjAP4i3KoJiUAgcOg6ha'),
       ('user5@example.com', 'Michael', 'Brown', '$2a$10$pHjMoACTdxjGnDLi.As.IewI41gBLkw4/UjAP4i3KoJiUAgcOg6ha');

INSERT INTO user_roles (user_id, roles_id)
VALUES (1, 3),
       (1, 2),
       (2, 2),
       (3, 2),
       (4, 2),
       (5, 2);

INSERT INTO first.document (cost, date, paid, title, owner_id, type_id)
VALUES (19.99, '2023-01-15 14:30:00', true, 'Document 1', 1, 1),
       (24.99, NULL, false, 'Document 2', 2, 2),
       (14.99, '2023-03-10 17:15:00', true, 'Document 3', 3, 3),
       (29.99, '2023-04-05 11:00:00', false, 'Document 4', 4, 4),
       (9.99, '2023-05-22 13:20:00', true, 'Document 5', 5, 5);

INSERT INTO first.file (document_id, extension)
VALUES (1, 'application/pdf'),
       (2, 'application/msword'),
       (3, 'application/octet-stream'),
       (4, 'application/vnd.oasis.opendocument.text'),
       (5, 'text/plain');