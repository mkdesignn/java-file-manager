create table email_messages
(
    id                   int primary key auto_increment,
    subject              varchar(255),
    text                 text,
    attachment_file_name varchar(255),
    attachment_file_path varchar(255)
);

create table email_receivers
(
    id    int primary key auto_increment,
    email varchar(255) unique not null
);

create table email_message_receivers
(
    id          int primary key auto_increment,
    email_id    int,
    receiver_id int
);