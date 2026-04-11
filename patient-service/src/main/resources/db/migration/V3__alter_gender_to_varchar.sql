-- Harmonise la colonne 'gender' avec le mapping @Enumerated(EnumType.STRING)
ALTER TABLE patients
    MODIFY COLUMN gender VARCHAR(10) NOT NULL;