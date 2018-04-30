drop table if exists friends;
drop table if exists pendingrequests;
drop table if exists users;

create table users (
	username varchar(30) not null primary key,
	pass varchar(64) not null
);

create table friends (
	username1 varchar(30) not null, 
	username2 varchar(30) not null, 
	primary key (username1, username2),
	foreign key (username1) references users (username)
            on delete cascade
            on update cascade,
	foreign key (username2) references users (username)
);

create table pendingrequests (
	requesterUser varchar(30) not null,
	requestedUser varchar(30) not null, 
	primary key (requesterUser, requestedUser),
	foreign key (requesterUser) references users (username)
            on delete cascade
            on update cascade,
	foreign key (requestedUser) references users (username)
            on delete cascade
            on update cascade
);

insert into users values ('miguel', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8');
insert into users values ('rosa', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8');
insert into friends values ('miguel', 'rosa'); 
