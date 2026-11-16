-- =====================================================================
-- CitizenTech - Consultas de uso (rodar depois de citizentech.sql)
-- Cada consulta simula uma pergunta real feita pelo app, pela API
-- ou pelo painel administrativo.
-- =====================================================================

-- 1) Tela "Explorar": locais com quantidade de recursos, obstáculos e
--    relatos em aberto (JOIN + agregação condicional).
SELECT l.id_local,
       l.nm_local,
       l.st_acessibilidade,
       COUNT(DISTINCT CASE WHEN i.tp_item = 'RECURSO'   THEN i.id_item END) AS qt_recursos,
       COUNT(DISTINCT CASE WHEN i.tp_item = 'OBSTACULO' THEN i.id_item END) AS qt_obstaculos,
       COUNT(DISTINCT CASE WHEN o.st_ocorrencia <> 'RESOLVIDA' THEN o.id_ocorrencia END) AS qt_relatos_abertos
  FROM TB_CT_LOCAL l
  LEFT JOIN TB_CT_ITEM_ACESSIBILIDADE i ON i.id_local = l.id_local
  LEFT JOIN TB_CT_OCORRENCIA o          ON o.id_local = l.id_local
 GROUP BY l.id_local, l.nm_local, l.st_acessibilidade
 ORDER BY qt_relatos_abertos DESC, l.nm_local;

-- 2) Tela "Meus relatos": relatos da cidadã Ana com nome do local,
--    do mais recente para o mais antigo.
SELECT o.id_ocorrencia,
       l.nm_local,
       o.ds_categoria,
       o.st_ocorrencia,
       o.dt_criacao
  FROM TB_CT_OCORRENCIA o
  JOIN TB_CT_USUARIO u ON u.id_usuario = o.id_usuario
  JOIN TB_CT_LOCAL   l ON l.id_local   = o.id_local
 WHERE u.ds_email = 'ana@citizentech.com'
 ORDER BY o.dt_criacao DESC;

-- 3) Painel do administrador: quantidade de relatos por status e por
--    categoria, para priorizar o atendimento.
SELECT o.st_ocorrencia,
       o.ds_categoria,
       COUNT(*) AS qt_relatos
  FROM TB_CT_OCORRENCIA o
 GROUP BY o.st_ocorrencia, o.ds_categoria
 ORDER BY o.st_ocorrencia, qt_relatos DESC;

-- 4) IoT: estatísticas de cada sensor (média, máxima, total de leituras e
--    quantas passaram do limite configurado).
SELECT s.cd_sensor,
       s.tp_sensor,
       l.nm_local,
       s.vl_limite_alerta,
       COUNT(r.id_leitura)            AS qt_leituras,
       ROUND(AVG(r.vl_leitura), 2)    AS vl_media,
       MAX(r.vl_leitura)              AS vl_maxima,
       SUM(CASE WHEN r.vl_leitura >= s.vl_limite_alerta THEN 1 ELSE 0 END) AS qt_acima_limite
  FROM TB_CT_SENSOR s
  JOIN TB_CT_LOCAL l               ON l.id_local  = s.id_local
  LEFT JOIN TB_CT_LEITURA_SENSOR r ON r.id_sensor = s.id_sensor
 GROUP BY s.cd_sensor, s.tp_sensor, l.nm_local, s.vl_limite_alerta
 ORDER BY qt_acima_limite DESC, s.cd_sensor;

-- 5) Tela "Alertas": alertas ativos com o valor da leitura, a unidade e o
--    local que dispararam o alerta (JOIN em 4 tabelas).
SELECT a.id_alerta,
       a.tp_alerta,
       a.ds_titulo,
       l.nm_local,
       r.vl_leitura,
       s.ds_unidade,
       s.vl_limite_alerta,
       a.dt_criacao
  FROM TB_CT_ALERTA a
  JOIN TB_CT_LEITURA_SENSOR r ON r.id_leitura = a.id_leitura
  JOIN TB_CT_SENSOR         s ON s.id_sensor  = r.id_sensor
  JOIN TB_CT_LOCAL          l ON l.id_local   = s.id_local
 WHERE a.st_alerta = 'ATIVO'
 ORDER BY a.dt_criacao DESC;

-- 6) Engajamento: cidadãos que mais relatam barreiras e quantos relatos
--    deles já foram resolvidos (HAVING filtra quem tem ao menos 1 relato).
SELECT u.nm_usuario,
       COUNT(o.id_ocorrencia) AS qt_relatos,
       SUM(CASE WHEN o.st_ocorrencia = 'RESOLVIDA' THEN 1 ELSE 0 END) AS qt_resolvidos
  FROM TB_CT_USUARIO u
  JOIN TB_CT_OCORRENCIA o ON o.id_usuario = u.id_usuario
 WHERE u.tp_perfil = 'CIDADAO'
 GROUP BY u.nm_usuario
HAVING COUNT(o.id_ocorrencia) >= 1
 ORDER BY qt_relatos DESC;

-- 7) Detecção de leitura desatualizada: última leitura de cada sensor e
--    se ela tem mais de 30 minutos (subconsulta + agregação).
SELECT s.cd_sensor,
       l.nm_local,
       ult.dt_ultima,
       CASE WHEN ult.dt_ultima < CURRENT_TIMESTAMP - INTERVAL '30' MINUTE
            THEN 'DESATUALIZADA' ELSE 'OK' END AS situacao
  FROM TB_CT_SENSOR s
  JOIN TB_CT_LOCAL l ON l.id_local = s.id_local
  JOIN (SELECT id_sensor, MAX(dt_leitura) AS dt_ultima
          FROM TB_CT_LEITURA_SENSOR
         GROUP BY id_sensor) ult ON ult.id_sensor = s.id_sensor
 ORDER BY ult.dt_ultima;

-- 8) Usuários e suas preferências de acessibilidade (JOIN 1:1).
SELECT u.nm_usuario,
       u.tp_perfil,
       p.fl_evitar_escadas,
       p.fl_priorizar_rampas,
       p.fl_alto_contraste
  FROM TB_CT_USUARIO u
  LEFT JOIN TB_CT_PREFERENCIA p ON p.id_usuario = u.id_usuario
 ORDER BY u.nm_usuario;
