"""Gera os scripts usados pela API no perfil H2 (modo Oracle) a partir de citizentech.sql.

O script Oracle é a fonte única do modelo. O H2 não executa o bloco PL/SQL de limpeza,
então ele é trocado por DROP TABLE IF EXISTS. Rode sempre que alterar citizentech.sql:

    python database/gerar_h2.py
"""
import re
from pathlib import Path

raiz = Path(__file__).resolve().parent
origem = (raiz / "citizentech.sql").read_text(encoding="utf-8")
destino = raiz.parent / "api" / "src" / "main" / "resources" / "db" / "h2"
destino.mkdir(parents=True, exist_ok=True)

ddl, carga = origem.split("-- 2) Carga inicial", 1)
ddl = re.sub(r"BEGIN.*?END;\s*/\s*", "", ddl, flags=re.S)
tabelas = re.findall(r"CREATE TABLE (\w+)", ddl)
drops = "".join(f"DROP TABLE IF EXISTS {t} CASCADE;\n" for t in reversed(tabelas))
cabecalho = "-- ARQUIVO GERADO por database/gerar_h2.py a partir de database/citizentech.sql. Não edite.\n"

(destino / "schema.sql").write_text(cabecalho + drops + ddl, encoding="utf-8")
(destino / "data.sql").write_text(cabecalho + "-- 2) Carga inicial" + carga, encoding="utf-8")
print(f"Gerados schema.sql e data.sql em {destino} ({len(tabelas)} tabelas)")
