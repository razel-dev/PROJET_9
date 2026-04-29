-- Permet les creations de nouveaux patients sans fournir d'id
ALTER TABLE patients
    MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;
