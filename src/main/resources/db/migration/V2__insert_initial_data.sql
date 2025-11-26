-- =====================================
-- USUÁRIOS
-- =====================================

INSERT INTO usuario (id, nome, email, senha, departamento, ativo)
VALUES
(1, 'Admin',        'admin@empresa.com', '$2a$10$BUH6GeEU20oiZG7nMIoi8.SNOaX9aZdoFW9QK8yvkEMf7bhOMNfKe', 'TI', TRUE),
(2, 'João Silva',   'joao@empresa.com',  '$2a$10$BUH6GeEU20oiZG7nMIoi8.SNOaX9aZdoFW9QK8yvkEMf7bhOMNfKe', 'FINANCEIRO', TRUE),
(3, 'Maria Santos', 'maria@empresa.com', '$2a$10$BUH6GeEU20oiZG7nMIoi8.SNOaX9aZdoFW9QK8yvkEMf7bhOMNfKe', 'RH', TRUE),
(4, 'Pedro Lima',   'pedro@empresa.com', '$2a$10$BUH6GeEU20oiZG7nMIoi8.SNOaX9aZdoFW9QK8yvkEMf7bhOMNfKe', 'OPERACOES', TRUE);

-- =====================================
-- PAPEIS DOS USUÁRIOS
-- =====================================

INSERT INTO usuario_papeis (usuario_id, papeis)
VALUES
(1, 'ADMIN'),
(1, 'USUARIO'),
(2, 'USUARIO'),
(3, 'USUARIO'),
(4, 'USUARIO');

-- =====================================
-- MÓDULOS
-- =====================================

INSERT INTO modulo (id, tipo, descricao, ativo)
VALUES
(1, 'PORTAL_COLABORADOR',    'Acesso geral ao portal', TRUE),
(2, 'RELATORIOS_GERENCIAIS', 'Relatórios de gestão', TRUE),
(3, 'GESTAO_FINANCEIRA',     'Módulo de gestão financeira', TRUE),
(4, 'APROVADOR_FINANCEIRO',  'Aprovação de despesas', TRUE),
(5, 'SOLICITANTE_FINANCEIRO','Solicitação financeira', TRUE),
(6, 'ADMINISTRADOR_RH',      'Admin RH', TRUE),
(7, 'COLABORADOR_RH',        'Colaborador no RH', TRUE),
(8, 'GESTAO_ESTOQUE',        'Controle de estoque', TRUE),
(9, 'COMPRAS',               'Gestão de compras', TRUE),
(10,'AUDITORIA',             'Módulo de auditoria', TRUE);

-- =====================================
-- DEPARTAMENTOS PERMITIDOS POR MÓDULO
-- (AJUSTADO: nome da coluna é "departamento")
-- =====================================

INSERT INTO modulo_departamentos_permitidos VALUES
(1, 'TI'), (1, 'FINANCEIRO'), (1, 'RH'), (1, 'OPERACOES'), (1, 'OUTROS'),
(2, 'TI'), (2, 'FINANCEIRO'),
(3, 'FINANCEIRO'),
(4, 'FINANCEIRO'),
(5, 'FINANCEIRO'), (5, 'OPERACOES'),
(6, 'RH'),
(7, 'RH'),
(8, 'OPERACOES'),
(9, 'OPERACOES'), (9, 'FINANCEIRO'),
(10, 'TI');

-- =====================================
-- INCOMPATIBILIDADES DE MÓDULOS
-- =====================================

INSERT INTO modulo_incompatibilidades (modulo_id, incompat_id) VALUES
(3, 4),
(4, 3),
(6, 7),
(7, 6);
