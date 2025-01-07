CREATE TABLE IF NOT EXISTS artist (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS song (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    title VARCHAR(255) NOT NULL,
                                    artist_id BIGINT NOT NULL,
                                    genre VARCHAR(100),
                                    length INT,
                                    FOREIGN KEY (artist_id) REFERENCES artist(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `user` (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      username VARCHAR(255) NOT NULL UNIQUE,
                                      password VARCHAR(255) NOT NULL
);

INSERT INTO artist (name) VALUES ('Britta Reinwart');
INSERT INTO artist (name) VALUES ('Maximilian Neid');
INSERT INTO artist (name) VALUES ('Emma Trenkwalder');
INSERT INTO artist (name) VALUES ('Nils Hubmann');
INSERT INTO artist (name) VALUES ('Tarik Begeta');
INSERT INTO artist (name) VALUES ('Georg Savic');
INSERT INTO artist (name) VALUES ('Valerie Hirsch');
INSERT INTO artist (name) VALUES ('Maximilian Koeing');
INSERT INTO artist (name) VALUES ('Luki Schodl');
INSERT INTO artist (name) VALUES ('Tobias Pammer');
INSERT INTO artist (name) VALUES ('Helene D.');
INSERT INTO artist (name) VALUES ('Sienna P.');
INSERT INTO artist (name) VALUES ('Marius Hladik');
INSERT INTO artist (name) VALUES ('Amelie Seebacher');
INSERT INTO artist (name) VALUES ('Leya H.');

INSERT INTO song (title, artist_id, genre, length) VALUES ('Song 1', 1, 'Pop', 240);
INSERT INTO song (title, artist_id, genre, length) VALUES ('Song 2', 2, 'Rock', 300);
INSERT INTO song (title, artist_id, genre, length) VALUES ('Song 3', 3, 'Jazz', 180);
INSERT INTO song (title, artist_id, genre, length) VALUES ('Song 4', 4, 'Country', 450);
INSERT INTO song (title, artist_id, genre, length) VALUES ('Song 5', 5, 'Hip hop', 320);
INSERT INTO song (title, artist_id, genre, length) VALUES ('Song 6', 6, 'Jazz', 120);
INSERT INTO song (title, artist_id, genre, length) VALUES ('Song 7', 7, 'Electronic', 300);
INSERT INTO song (title, artist_id, genre, length) VALUES ('Song 8', 8, 'Country', 180);
INSERT INTO song (title, artist_id, genre, length) VALUES ('Song 9', 9, 'Pop', 450);
INSERT INTO song (title, artist_id, genre, length) VALUES ('Song 10', 10, 'Blues', 320);
INSERT INTO song (title, artist_id, genre, length) VALUES ('Song 11', 11, 'Blues', 120);
INSERT INTO song (title, artist_id, genre, length) VALUES ('Song 12', 12, 'Electronic', 300);
INSERT INTO song (title, artist_id, genre, length) VALUES ('Song 13', 13, 'Hip hop', 180);
INSERT INTO song (title, artist_id, genre, length) VALUES ('Song 14', 14, 'Blues', 450);
INSERT INTO song (title, artist_id, genre, length) VALUES ('Song 15', 15, 'Rock', 320);


//cost-factor (in diesem Fall 10)
INSERT INTO `user` (id, username, password)
VALUES (1, 'hugo', '$2a$10$MkLTGxdivqj427wEbGrwu.Qbx7G.2z.d31xVb1Qe9UCwimEdpqp1a');

//neuesPasswort123
INSERT INTO `user` (id, username, password)
VALUES (2, 'britta', '$2a$12$kA9tnptUIYfWlqKwybRU9ecZmnEjPvGm6fYB4C4XvRR3Euzs75J42');

SELECT * FROM `user`;




