-- ARQUIVO GERADO por database/gerar_h2.py a partir de database/citizentech.sql. Não edite.
-- 2) Carga inicial
-- Senhas (apenas para demonstração): admin@citizentech.com / Admin@2026
--                                    ana@citizentech.com   / Cidadao@2026
--                                    bruno@citizentech.com / Cidadao@2026
-- Armazenadas com hash BCrypt, nunca em texto puro.
-- ---------------------------------------------------------------------
INSERT INTO TB_CT_USUARIO (id_usuario, nm_usuario, ds_email, ds_senha_hash, tp_perfil)
VALUES (1, 'Administrador CitizenTech', 'admin@citizentech.com', '$2a$10$.rF1TzqelVg/U32K7RjwcuNuUgfG/arBIJqXGm2jgip934oLYjyS2', 'ADMIN');
INSERT INTO TB_CT_USUARIO (id_usuario, nm_usuario, ds_email, ds_senha_hash, tp_perfil)
VALUES (2, 'Ana Souza', 'ana@citizentech.com', '$2a$10$PdoPFO63wck4M1fmi.sHce6d8Q057V.sADdt9dWuHP83cAIQoEzY.', 'CIDADAO');
INSERT INTO TB_CT_USUARIO (id_usuario, nm_usuario, ds_email, ds_senha_hash, tp_perfil)
VALUES (3, 'Bruno Lima', 'bruno@citizentech.com', '$2a$10$PdoPFO63wck4M1fmi.sHce6d8Q057V.sADdt9dWuHP83cAIQoEzY.', 'CIDADAO');

INSERT INTO TB_CT_PREFERENCIA (id_usuario, fl_evitar_escadas, fl_priorizar_rampas, fl_alertas_visuais, fl_alto_contraste) VALUES (1, 1, 1, 1, 0);
INSERT INTO TB_CT_PREFERENCIA (id_usuario, fl_evitar_escadas, fl_priorizar_rampas, fl_alertas_visuais, fl_alto_contraste) VALUES (2, 1, 1, 1, 0);
INSERT INTO TB_CT_PREFERENCIA (id_usuario, fl_evitar_escadas, fl_priorizar_rampas, fl_alertas_visuais, fl_alto_contraste) VALUES (3, 0, 1, 1, 1);

INSERT INTO TB_CT_LOCAL (id_local, nm_local, ds_categoria, ds_endereco, st_acessibilidade, ds_resumo, ds_confianca, nr_ocupacao, dt_ultima_leitura)
VALUES ('biblioteca', 'Biblioteca Central', 'Serviço público', 'Rua das Palmeiras, 120 · Centro', 'PARCIAL',
        'Entrada principal nivelada. Há um trecho de calçada irregular na lateral.', 'Cadastro + relato comunitário', 42, CURRENT_TIMESTAMP - INTERVAL '10' MINUTE);
INSERT INTO TB_CT_LOCAL (id_local, nm_local, ds_categoria, ds_endereco, st_acessibilidade, ds_resumo, ds_confianca, nr_ocupacao, dt_ultima_leitura)
VALUES ('terminal', 'Terminal Central', 'Transporte', 'Av. Integração, 450 · Centro', 'ACESSIVEL',
        'Acesso principal com piso tátil e elevador sinalizado.', 'Cadastro local', 18, CURRENT_TIMESTAMP - INTERVAL '14' MINUTE);
INSERT INTO TB_CT_LOCAL (id_local, nm_local, ds_categoria, ds_endereco, st_acessibilidade, ds_resumo, ds_confianca, nr_ocupacao, dt_ultima_leitura)
VALUES ('centro-cultural', 'Centro Cultural', 'Cultura e lazer', 'Praça das Artes, 35 · Centro', 'ACESSIVEL',
        'Entrada sem degraus e espaços de circulação amplos.', 'Cadastro local', 9, CURRENT_TIMESTAMP - INTERVAL '17' MINUTE);
INSERT INTO TB_CT_LOCAL (id_local, nm_local, ds_categoria, ds_endereco, st_acessibilidade, ds_resumo, ds_confianca, nr_ocupacao, dt_ultima_leitura)
VALUES ('ubs-vila', 'UBS Vila Nova', 'Saúde', 'Rua Esperança, 88 · Vila Nova', 'INACESSIVEL',
        'Acesso apenas por escada; rampa provisória removida.', 'Relato comunitário', 0, NULL);

INSERT INTO TB_CT_ITEM_ACESSIBILIDADE (id_item, id_local, tp_item, ds_item) VALUES (1,  'biblioteca', 'RECURSO', 'Entrada principal com rampa');
INSERT INTO TB_CT_ITEM_ACESSIBILIDADE (id_item, id_local, tp_item, ds_item) VALUES (2,  'biblioteca', 'RECURSO', 'Banheiro acessível');
INSERT INTO TB_CT_ITEM_ACESSIBILIDADE (id_item, id_local, tp_item, ds_item) VALUES (3,  'biblioteca', 'RECURSO', 'Balcão de atendimento rebaixado');
INSERT INTO TB_CT_ITEM_ACESSIBILIDADE (id_item, id_local, tp_item, ds_item) VALUES (4,  'biblioteca', 'RECURSO', 'Piso tátil na entrada');
INSERT INTO TB_CT_ITEM_ACESSIBILIDADE (id_item, id_local, tp_item, ds_item) VALUES (5,  'biblioteca', 'OBSTACULO', 'Calçada lateral irregular');
INSERT INTO TB_CT_ITEM_ACESSIBILIDADE (id_item, id_local, tp_item, ds_item) VALUES (6,  'terminal', 'RECURSO', 'Rampas de acesso');
INSERT INTO TB_CT_ITEM_ACESSIBILIDADE (id_item, id_local, tp_item, ds_item) VALUES (7,  'terminal', 'RECURSO', 'Piso tátil direcional');
INSERT INTO TB_CT_ITEM_ACESSIBILIDADE (id_item, id_local, tp_item, ds_item) VALUES (8,  'terminal', 'RECURSO', 'Elevador sinalizado');
INSERT INTO TB_CT_ITEM_ACESSIBILIDADE (id_item, id_local, tp_item, ds_item) VALUES (9,  'terminal', 'RECURSO', 'Balcão preferencial');
INSERT INTO TB_CT_ITEM_ACESSIBILIDADE (id_item, id_local, tp_item, ds_item) VALUES (10, 'centro-cultural', 'RECURSO', 'Acesso sem degraus');
INSERT INTO TB_CT_ITEM_ACESSIBILIDADE (id_item, id_local, tp_item, ds_item) VALUES (11, 'centro-cultural', 'RECURSO', 'Banheiro acessível');
INSERT INTO TB_CT_ITEM_ACESSIBILIDADE (id_item, id_local, tp_item, ds_item) VALUES (12, 'centro-cultural', 'RECURSO', 'Assentos reservados');
INSERT INTO TB_CT_ITEM_ACESSIBILIDADE (id_item, id_local, tp_item, ds_item) VALUES (13, 'centro-cultural', 'RECURSO', 'Sinalização visual');
INSERT INTO TB_CT_ITEM_ACESSIBILIDADE (id_item, id_local, tp_item, ds_item) VALUES (14, 'ubs-vila', 'OBSTACULO', 'Escada sem alternativa na entrada');
INSERT INTO TB_CT_ITEM_ACESSIBILIDADE (id_item, id_local, tp_item, ds_item) VALUES (15, 'ubs-vila', 'OBSTACULO', 'Calçada estreita com poste');

INSERT INTO TB_CT_OCORRENCIA (id_ocorrencia, id_usuario, id_local, ds_categoria, ds_descricao, st_ocorrencia, dt_criacao, dt_atualizacao)
VALUES (1024, 2, 'biblioteca', 'Calçada irregular', 'Piso desnivelado na lateral da entrada, próximo à faixa de pedestres.', 'EM_ANALISE',
        CURRENT_TIMESTAMP - INTERVAL '4' HOUR, CURRENT_TIMESTAMP - INTERVAL '2' HOUR);
INSERT INTO TB_CT_OCORRENCIA (id_ocorrencia, id_usuario, id_local, ds_categoria, ds_descricao, st_ocorrencia, dt_criacao, dt_atualizacao)
VALUES (1018, 2, 'terminal', 'Elevador fora de serviço', 'Elevador parado na plataforma 2; voltou a operar após manutenção.', 'RESOLVIDA',
        CURRENT_TIMESTAMP - INTERVAL '1' DAY, CURRENT_TIMESTAMP - INTERVAL '5' HOUR);
INSERT INTO TB_CT_OCORRENCIA (id_ocorrencia, id_usuario, id_local, ds_categoria, ds_descricao, st_ocorrencia, dt_criacao, dt_atualizacao)
VALUES (1031, 3, 'ubs-vila', 'Ausência de rampa', 'A rampa provisória foi retirada e não há outra forma de entrar com cadeira de rodas.', 'ENCAMINHADA',
        CURRENT_TIMESTAMP - INTERVAL '3' DAY, CURRENT_TIMESTAMP - INTERVAL '1' DAY);
INSERT INTO TB_CT_OCORRENCIA (id_ocorrencia, id_usuario, id_local, ds_categoria, ds_descricao, st_ocorrencia, dt_criacao, dt_atualizacao)
VALUES (1040, 3, 'biblioteca', 'Piso tátil danificado', 'Piso tátil solto na rampa de acesso, risco de tropeço.', 'RECEBIDA',
        CURRENT_TIMESTAMP - INTERVAL '30' MINUTE, CURRENT_TIMESTAMP - INTERVAL '30' MINUTE);

INSERT INTO TB_CT_SENSOR (id_sensor, cd_sensor, tp_sensor, id_local, ds_unidade, vl_limite_alerta) VALUES (1, 'OCUP-BIB-01', 'OCUPACAO', 'biblioteca', 'pessoas', 35);
INSERT INTO TB_CT_SENSOR (id_sensor, cd_sensor, tp_sensor, id_local, ds_unidade, vl_limite_alerta) VALUES (2, 'OCUP-TER-01', 'OCUPACAO', 'terminal', 'pessoas', 60);
INSERT INTO TB_CT_SENSOR (id_sensor, cd_sensor, tp_sensor, id_local, ds_unidade, vl_limite_alerta) VALUES (3, 'OCUP-CUL-01', 'OCUPACAO', 'centro-cultural', 'pessoas', 40);
INSERT INTO TB_CT_SENSOR (id_sensor, cd_sensor, tp_sensor, id_local, ds_unidade, vl_limite_alerta) VALUES (4, 'CHUVA-CUL-01', 'CHUVA', 'centro-cultural', 'mm/h', 30);
INSERT INTO TB_CT_SENSOR (id_sensor, cd_sensor, tp_sensor, id_local, ds_unidade, vl_limite_alerta) VALUES (5, 'CHUVA-TER-01', 'CHUVA', 'terminal', 'mm/h', 30);

INSERT INTO TB_CT_LEITURA_SENSOR (id_leitura, id_sensor, vl_leitura, dt_leitura) VALUES (1, 1, 28, CURRENT_TIMESTAMP - INTERVAL '40' MINUTE);
INSERT INTO TB_CT_LEITURA_SENSOR (id_leitura, id_sensor, vl_leitura, dt_leitura) VALUES (2, 1, 42, CURRENT_TIMESTAMP - INTERVAL '10' MINUTE);
INSERT INTO TB_CT_LEITURA_SENSOR (id_leitura, id_sensor, vl_leitura, dt_leitura) VALUES (3, 2, 18, CURRENT_TIMESTAMP - INTERVAL '14' MINUTE);
INSERT INTO TB_CT_LEITURA_SENSOR (id_leitura, id_sensor, vl_leitura, dt_leitura) VALUES (4, 3, 9,  CURRENT_TIMESTAMP - INTERVAL '17' MINUTE);
INSERT INTO TB_CT_LEITURA_SENSOR (id_leitura, id_sensor, vl_leitura, dt_leitura) VALUES (5, 4, 12, CURRENT_TIMESTAMP - INTERVAL '2' HOUR);
INSERT INTO TB_CT_LEITURA_SENSOR (id_leitura, id_sensor, vl_leitura, dt_leitura) VALUES (6, 4, 35, CURRENT_TIMESTAMP - INTERVAL '8' MINUTE);
INSERT INTO TB_CT_LEITURA_SENSOR (id_leitura, id_sensor, vl_leitura, dt_leitura) VALUES (7, 5, 41, CURRENT_TIMESTAMP - INTERVAL '1' DAY);
INSERT INTO TB_CT_LEITURA_SENSOR (id_leitura, id_sensor, vl_leitura, dt_leitura) VALUES (8, 5, 6,  CURRENT_TIMESTAMP - INTERVAL '20' HOUR);

INSERT INTO TB_CT_ALERTA (id_alerta, id_leitura, tp_alerta, ds_titulo, ds_descricao, st_alerta, dt_criacao, dt_encerramento)
VALUES (205, 6, 'CLIMA', 'Chuva intensa no entorno do Centro Cultural',
        'Leitura SIMULADA de 35 mm/h ultrapassou o limite de demonstração (30 mm/h). Não é um alerta oficial da Defesa Civil.',
        'ATIVO', CURRENT_TIMESTAMP - INTERVAL '8' MINUTE, NULL);
INSERT INTO TB_CT_ALERTA (id_alerta, id_leitura, tp_alerta, ds_titulo, ds_descricao, st_alerta, dt_criacao, dt_encerramento)
VALUES (206, 2, 'OCUPACAO', 'Biblioteca Central com alta ocupação',
        'Leitura SIMULADA de 42 pessoas ultrapassou o limite de 35 pessoas. Contagem agregada, sem identificação de pessoas.',
        'ATIVO', CURRENT_TIMESTAMP - INTERVAL '10' MINUTE, NULL);
INSERT INTO TB_CT_ALERTA (id_alerta, id_leitura, tp_alerta, ds_titulo, ds_descricao, st_alerta, dt_criacao, dt_encerramento)
VALUES (190, 7, 'CLIMA', 'Chuva intensa no entorno do Terminal Central',
        'Leitura SIMULADA de 41 mm/h ultrapassou o limite de demonstração (30 mm/h). Não é um alerta oficial da Defesa Civil.',
        'ENCERRADO', CURRENT_TIMESTAMP - INTERVAL '1' DAY, CURRENT_TIMESTAMP - INTERVAL '20' HOUR);

COMMIT;
