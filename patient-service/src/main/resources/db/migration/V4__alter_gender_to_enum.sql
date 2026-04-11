-- Convertit 'gender' en type ENUM pour correspondre aux attentes d'Hibernate
-- Adapte les valeurs si besoin (M/F/OTHER doivent correspondre à votre enum Java)
ALTER TABLE patients
    MODIFY COLUMN gender ENUM('M','F','OTHER') NOT NULL;