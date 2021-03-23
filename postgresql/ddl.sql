drop table if exists like_table;
drop table if exists mp3_table;
drop table if exists user_table;

create table if not exists user_table(
	no varchar(12) not null unique,
	primary key(no)
);

create table if not exists mp3_table(
	genre varchar(15) not null,
	title varchar(30) not null,
	artist varchar(30) not null,
	played_times int not null default 0,
	mp3 bytea not null,
	image bytea not null,
	constraint pk_id primary key(title, artist)
);

create table if not exists like_table(
	no varchar(12) not null,
	title varchar(30) not null,
	artist varchar(30) not null,
	constraint pk_no_title_artist primary key(no, title, artist),
	constraint fk_no foreign key(no) references user_table(no) on delete cascade on update cascade,
	constraint fk_title_artist foreign key(title, artist) references mp3_table(title, artist) on delete cascade on update cascade
);

select * from mp3_table;