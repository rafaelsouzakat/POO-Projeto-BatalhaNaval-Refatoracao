-- =============================================================
-- schema.sql
-- Batalha Naval – Esquema SQLite
-- Grupo G01 | project.version 1.0.0
--
-- Entidades principais conforme o Plano de Projeto:
--   Partida  (agregado raiz)
--   Tabuleiro (um por jogador por partida)
--   Navio    (frota de cada tabuleiro)
--   Tiro     (histórico de disparos)
-- =============================================================

PRAGMA journal_mode = WAL;
PRAGMA foreign_keys = ON;

-- -------------------------------------------------------------
-- 1. PARTIDA
--    Agregado raiz. Guarda metadados da sessão e o vencedor.
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS partida (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    seed          TEXT             NOT NULL DEFAULT '',   -- seed usada (vazia = aleatório)
    board_size    INTEGER          NOT NULL DEFAULT 10,
    status        TEXT             NOT NULL DEFAULT 'EM_ANDAMENTO'
                                   CHECK (status IN ('EM_ANDAMENTO', 'VITORIA_JOGADOR', 'VITORIA_CPU')),
    turno_atual   INTEGER          NOT NULL DEFAULT 1,    -- número do turno corrente
    criado_em     TEXT             NOT NULL               -- ISO-8601: YYYY-MM-DD HH:MM:SS
                                   DEFAULT (strftime('%Y-%m-%d %H:%M:%S', 'now')),
    atualizado_em TEXT             NOT NULL
                                   DEFAULT (strftime('%Y-%m-%d %H:%M:%S', 'now'))
);

-- -------------------------------------------------------------
-- 2. TABULEIRO
--    Um tabuleiro por jogador por partida (JOGADOR | CPU).
--    Armazena snapshot serializado (opcional) para replay.
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tabuleiro (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    partida_id   INTEGER NOT NULL REFERENCES partida(id) ON DELETE CASCADE,
    dono         TEXT    NOT NULL CHECK (dono IN ('JOGADOR', 'CPU')),
    -- Snapshot JSON da matriz de células para suporte a replay
    -- Ex.: {"cells":[[".",".","S",...],...],"shots":[...]}
    snapshot_json TEXT,
    UNIQUE (partida_id, dono)
);

-- -------------------------------------------------------------
-- 3. NAVIO
--    Frota de cada tabuleiro. Reflete ArrayList<Navio> em Java.
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS navio (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    tabuleiro_id   INTEGER NOT NULL REFERENCES tabuleiro(id) ON DELETE CASCADE,
    nome           TEXT    NOT NULL,          -- ex: "Porta-avioes"
    tamanho        INTEGER NOT NULL,          -- ex: 5
    coord_inicio_x INTEGER NOT NULL,          -- coluna  0..N-1
    coord_inicio_y INTEGER NOT NULL,          -- linha   0..N-1
    horizontal     INTEGER NOT NULL           -- 1 = horizontal, 0 = vertical
                   CHECK (horizontal IN (0, 1)),
    hp_atual       INTEGER NOT NULL,          -- vida restante (começa = tamanho)
    afundado       INTEGER NOT NULL DEFAULT 0 -- 0 = flutuando, 1 = afundado
                   CHECK (afundado IN (0, 1))
);

-- -------------------------------------------------------------
-- 4. TIRO
--    Histórico completo de disparos (ambos os jogadores).
--    Usado para log, replay e QA.
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tiro (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    partida_id   INTEGER NOT NULL REFERENCES partida(id) ON DELETE CASCADE,
    turno        INTEGER NOT NULL,
    atirador     TEXT    NOT NULL CHECK (atirador IN ('JOGADOR', 'CPU')),
    coord_x      INTEGER NOT NULL,  -- coluna alvo 0..N-1
    coord_y      INTEGER NOT NULL,  -- linha  alvo 0..N-1
    resultado    TEXT    NOT NULL
                 CHECK (resultado IN ('AGUA', 'ACERTO', 'AFUNDOU')),
    navio_id     INTEGER REFERENCES navio(id) ON DELETE SET NULL,  -- NULL se AGUA
    disparado_em TEXT    NOT NULL
                 DEFAULT (strftime('%Y-%m-%d %H:%M:%S', 'now'))
);

-- -------------------------------------------------------------
-- 5. RANKING
--    Placar acumulado de jogadores (para CRUD de ranking).
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ranking (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    nome_jogador TEXT   NOT NULL UNIQUE,
    vitorias    INTEGER NOT NULL DEFAULT 0,
    derrotas    INTEGER NOT NULL DEFAULT 0,
    tiros_dados INTEGER NOT NULL DEFAULT 0,
    acertos     INTEGER NOT NULL DEFAULT 0,
    atualizado_em TEXT  NOT NULL
                  DEFAULT (strftime('%Y-%m-%d %H:%M:%S', 'now'))
);

-- -------------------------------------------------------------
-- Índices para consultas frequentes
-- -------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_tiro_partida   ON tiro(partida_id);
CREATE INDEX IF NOT EXISTS idx_tiro_turno     ON tiro(partida_id, turno);
CREATE INDEX IF NOT EXISTS idx_navio_tabuleiro ON navio(tabuleiro_id);
CREATE INDEX IF NOT EXISTS idx_tabuleiro_partida ON tabuleiro(partida_id);
