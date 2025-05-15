
CREATE TABLE gender (
    code VARCHAR(6) NOT NULL PRIMARY KEY,
    name VARCHAR(7) NOT NULL UNIQUE
);

INSERT INTO gender VALUES
('MALE', 'мужской'),
('FEMALE', 'женский');



CREATE TABLE person (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(1000) NOT NULL,
    gender VARCHAR(6) NOT NULL,
    snils VARCHAR(14) NOT NULL UNIQUE,
    phone VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    birth_date DATE NOT NULL,
    workplace VARCHAR(255),
    "position" VARCHAR(255),

    FOREIGN KEY (gender) REFERENCES gender(code)
);



CREATE TABLE blood_group (
    code VARCHAR(3) PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

INSERT INTO blood_group (code, name) VALUES
('A+', 'A Positive'),
('A-', 'A Negative'),
('B+', 'B Positive'),
('B-', 'B Negative'),
('AB+', 'AB Positive'),
('AB-', 'AB Negative'),
('O+', 'O Positive'),
('O-', 'O Negative');



CREATE TABLE donor (
    id BIGSERIAL PRIMARY KEY,
    person_id INT UNIQUE NOT NULL,
    blood_group VARCHAR(3),

    FOREIGN KEY (blood_group) REFERENCES blood_group(code),
    FOREIGN KEY (person_id) REFERENCES person(id) ON DELETE CASCADE
);



CREATE TABLE donation (
    id BIGSERIAL PRIMARY KEY,
    donor_id INT NOT NULL,
    donation_date DATE NOT NULL,
    blood_volume INT NOT NULL,

    FOREIGN KEY (donor_id) REFERENCES donor(id) ON DELETE CASCADE
);



CREATE TABLE blacklist (
    id BIGSERIAL PRIMARY KEY,
    donor_id INT UNIQUE NOT NULL,
    entry_date DATE NOT NULL,
    comment TEXT NOT NULL CHECK (LENGTH(comment) <= 1024),

    FOREIGN KEY (donor_id) REFERENCES donor(id) ON DELETE CASCADE
);



CREATE TABLE blood_bank (
    id BIGSERIAL PRIMARY KEY,
    blood_group VARCHAR(3) UNIQUE,
    blood_volume BIGINT NOT NULL,

    FOREIGN KEY (blood_group) REFERENCES blood_group(code)
);

INSERT INTO blood_bank (blood_group, blood_volume) VALUES
('A+', 0),
('A-', 0),
('B+', 0),
('B-', 0),
('AB+', 0),
('AB-', 0),
('O+', 0),
('O-', 0);

