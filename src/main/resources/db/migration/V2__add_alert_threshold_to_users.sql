-- Adiciona a coluna para o usuário definir seu próprio limite de alerta.
-- Colocamos um valor padrão (DEFAULT) de 1000 para não quebrar os usuários antigos que já estão no banco.
ALTER TABLE users ADD COLUMN alert_threshold NUMERIC(19, 4) DEFAULT 1000.0000;