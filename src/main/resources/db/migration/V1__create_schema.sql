-- ==========================================
-- TABELA USUARIOS
-- ==========================================
CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    departamento VARCHAR(50) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

-- ==========================================
-- PAPEIS (ENUM STRING) - VIA TABELA PIVO
-- ==========================================
CREATE TABLE usuario_papeis (
    usuario_id BIGINT NOT NULL,
    papeis VARCHAR(50) NOT NULL,
    PRIMARY KEY (usuario_id, papeis),
    CONSTRAINT fk_usuario_papeis_user FOREIGN KEY (usuario_id)
        REFERENCES usuario(id) ON DELETE CASCADE
);

-- ==========================================
-- TABELA MODULO
-- ==========================================
CREATE TABLE modulo (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(100) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

-- ==========================================
-- MODULO → DEPARTAMENTOS PERMITIDOS
-- ==========================================
CREATE TABLE modulo_departamentos_permitidos (
    modulo_id BIGINT NOT NULL,
    departamentos_permitidos VARCHAR(100) NOT NULL,
    CONSTRAINT fk_modulo_dept FOREIGN KEY (modulo_id)
        REFERENCES modulo(id) ON DELETE CASCADE
);

-- ==========================================
-- INCOMPATIBILIDADES ENTRE MODULOS
-- ==========================================
CREATE TABLE modulo_incompatibilidades (
    modulo_id BIGINT NOT NULL,
    incompat_id BIGINT NOT NULL,
    PRIMARY KEY (modulo_id, incompat_id),
    CONSTRAINT fk_incomp_mod FOREIGN KEY (modulo_id)
        REFERENCES modulo(id) ON DELETE CASCADE,
    CONSTRAINT fk_incomp_incompat FOREIGN KEY (incompat_id)
        REFERENCES modulo(id) ON DELETE CASCADE
);

-- ==========================================
-- SOLICITACOES DE ACESSO (ROOT)
-- ==========================================
CREATE TABLE solicitacao_acesso (
    id BIGSERIAL PRIMARY KEY,
    protocolo VARCHAR(255) NOT NULL,
    usuario_id BIGINT NOT NULL,
    justificativa TEXT,
    urgente BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(50) NOT NULL,
    motivo_negacao TEXT,
    data_solicitacao TIMESTAMP NOT NULL,
    data_expiracao TIMESTAMP,
    solicitacao_anterior_id BIGINT,
    CONSTRAINT fk_solic_user FOREIGN KEY (usuario_id)
        REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_solic_anterior FOREIGN KEY (solicitacao_anterior_id)
        REFERENCES solicitacao_acesso(id) ON DELETE SET NULL
);

-- ==========================================
-- TABELA MANY-TO-MANY ENTRE SOLICITACAO ↔ MODULO
-- ==========================================
CREATE TABLE solicitacao_acesso_modulos (
    solicitacao_id BIGINT NOT NULL,
    modulo_id BIGINT NOT NULL,
    PRIMARY KEY (solicitacao_id, modulo_id),
    FOREIGN KEY (solicitacao_id) REFERENCES solicitacao_acesso(id) ON DELETE CASCADE,
    FOREIGN KEY (modulo_id) REFERENCES modulo(id) ON DELETE CASCADE
);

-- ==========================================
-- HISTORICO DE SOLICITACAO
-- ==========================================
CREATE TABLE historico_solicitacao (
    id BIGSERIAL PRIMARY KEY,
    solicitacao_id BIGINT NOT NULL,
    acao TEXT NOT NULL,
    data_registro TIMESTAMP NOT NULL,
    CONSTRAINT fk_hist_solic FOREIGN KEY (solicitacao_id)
        REFERENCES solicitacao_acesso(id) ON DELETE CASCADE
);
