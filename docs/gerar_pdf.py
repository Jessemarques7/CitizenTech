"""
Gera docs/CitizenTech_Entregaveis.pdf a partir de docs/entregaveis.html:
desenha o Diagrama ER (SVG), insere os logs reais de docs/evidencias/ e imprime em PDF
com o Microsoft Edge ou Google Chrome em modo headless.

    python docs/gerar_pdf.py

Antes, preencha os integrantes e o link do pitch em docs/entregaveis.html.
"""
import html
import shutil
import subprocess
import sys
import time
from pathlib import Path

DOCS = Path(__file__).resolve().parent
EVID = DOCS / "evidencias"

# (nome, x, y, largura, atributos)  -  "PK"/"FK"/"UK" no início marca a chave
ENTIDADES = [
    ("TB_CT_USUARIO", 20, 20, 220, ["PK id_usuario", "nm_usuario", "UK ds_email", "ds_senha_hash", "tp_perfil", "dt_cadastro"]),
    ("TB_CT_OCORRENCIA", 290, 20, 230, ["PK id_ocorrencia", "FK id_usuario", "FK id_local", "ds_categoria", "ds_descricao",
                                        "st_ocorrencia", "dt_criacao", "dt_atualizacao"]),
    ("TB_CT_LOCAL", 580, 20, 220, ["PK id_local", "nm_local", "ds_categoria", "ds_endereco", "st_acessibilidade",
                                   "ds_resumo", "ds_confianca", "nr_ocupacao", "dt_ultima_leitura"]),
    ("TB_CT_ITEM_ACESSIBILIDADE", 860, 20, 220, ["PK id_item", "FK id_local", "tp_item", "ds_item"]),
    ("TB_CT_PREFERENCIA", 20, 300, 220, ["PK FK id_usuario", "fl_evitar_escadas", "fl_priorizar_rampas",
                                         "fl_alertas_visuais", "fl_alto_contraste"]),
    ("TB_CT_SENSOR", 580, 300, 220, ["PK id_sensor", "UK cd_sensor", "tp_sensor", "FK id_local", "ds_unidade",
                                     "vl_limite_alerta", "fl_ativo"]),
    ("TB_CT_LEITURA_SENSOR", 860, 300, 220, ["PK id_leitura", "FK id_sensor", "vl_leitura", "dt_leitura", "fl_simulada"]),
    ("TB_CT_ALERTA", 860, 500, 220, ["PK id_alerta", "FK UK id_leitura", "tp_alerta", "ds_titulo", "ds_descricao",
                                     "st_alerta", "dt_criacao", "dt_encerramento"]),
]
# (x1, y1, x2, y2, rótulo lado 1, rótulo lado 2)
RELACOES = [
    (240, 80, 290, 80, "1", "N"),       # usuario - ocorrencia
    (580, 110, 520, 110, "1", "N"),     # local - ocorrencia
    (800, 70, 860, 70, "1", "N"),       # local - item
    (130, 176, 130, 300, "1", "1"),     # usuario - preferencia
    (690, 212, 690, 300, "1", "N"),     # local - sensor
    (800, 370, 860, 370, "1", "N"),     # sensor - leitura
    (970, 430, 970, 500, "1", "0..1"),  # leitura - alerta
]
LINHA = 18


def der_svg():
    partes = ['<svg viewBox="0 0 1100 700" width="100%" xmlns="http://www.w3.org/2000/svg" '
              'role="img" aria-label="Diagrama entidade-relacionamento da CitizenTech">']
    for x1, y1, x2, y2, a, b in RELACOES:
        partes.append(f'<line x1="{x1}" y1="{y1}" x2="{x2}" y2="{y2}" stroke="#087F78" stroke-width="2"/>')
        vertical = x1 == x2
        dx, dy = (8, 0) if vertical else (0, -6)
        s1 = 1 if (y2 > y1 if vertical else x2 > x1) else -1
        partes.append(f'<text x="{x1 + dx + (0 if vertical else 6 * s1 - (6 if s1 < 0 else 0))}" '
                      f'y="{y1 + dy + (14 * s1 if vertical else 0)}" font-size="12" font-weight="700" fill="#A45D08">{a}</text>')
        partes.append(f'<text x="{x2 + dx - (0 if vertical else 14 * s1 + (8 if s1 < 0 else 0))}" '
                      f'y="{y2 + dy - (6 * s1 if vertical else 0)}" font-size="12" font-weight="700" fill="#A45D08">{b}</text>')
    for nome, x, y, w, attrs in ENTIDADES:
        h = 30 + len(attrs) * LINHA + 8
        partes.append(f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="6" fill="#ffffff" stroke="#12313D" stroke-width="1.5"/>')
        partes.append(f'<rect x="{x}" y="{y}" width="{w}" height="26" rx="6" fill="#12313D"/>')
        partes.append(f'<rect x="{x}" y="{y + 18}" width="{w}" height="8" fill="#12313D"/>')
        partes.append(f'<text x="{x + w / 2}" y="{y + 18}" text-anchor="middle" font-size="12" font-weight="700" fill="#ffffff">{nome}</text>')
        for i, a in enumerate(attrs):
            chaves = [p for p in a.split()[:-1]]
            campo = a.split()[-1]
            ty = y + 30 + (i + 1) * LINHA - 4
            cor = "#087F78" if "PK" in chaves else ("#A45D08" if "FK" in chaves else "#18333B")
            peso = "700" if chaves else "400"
            partes.append(f'<text x="{x + 10}" y="{ty}" font-size="11" fill="#5d7176" font-weight="700">{" ".join(chaves)}</text>')
            partes.append(f'<text x="{x + 62}" y="{ty}" font-size="11.5" fill="{cor}" font-weight="{peso}">{campo}</text>')
    partes.append('<text x="20" y="690" font-size="11" fill="#5d7176">PK = chave primária · FK = chave estrangeira · '
                  'UK = única · cardinalidades nas linhas (1, N, 0..1)</text>')
    partes.append("</svg>")
    return "\n".join(partes)


def ler(caminho, linhas=None):
    if not caminho.exists():
        return f"(arquivo {caminho.name} não encontrado - rode a geração de evidências)"
    texto = caminho.read_text(encoding="utf-8")
    if linhas:
        texto = "\n".join(texto.splitlines()[:linhas])
    return html.escape(texto.strip())


def navegador():
    candidatos = [
        r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe",
        r"C:\Program Files\Microsoft\Edge\Application\msedge.exe",
        r"C:\Program Files\Google\Chrome\Application\chrome.exe",
        shutil.which("google-chrome") or "", shutil.which("chromium") or "", shutil.which("msedge") or "",
    ]
    return next((c for c in candidatos if c and Path(c).exists()), None)


def main():
    testes = "\n".join(ler(p) for p in sorted((EVID / "testes-automatizados").glob("*.txt")))
    pagina = (DOCS / "entregaveis.html").read_text(encoding="utf-8")
    pagina = (pagina.replace("{{DER}}", der_svg())
              .replace("{{LOG_TESTES}}", testes)
              .replace("{{LOG_API}}", ler(EVID / "execucao_api.txt", linhas=24))
              .replace("{{LOG_IOT}}", ler(EVID / "execucao_iot.txt")))
    build = DOCS / "build"
    build.mkdir(exist_ok=True)
    final = build / "entregaveis_final.html"
    final.write_text(pagina, encoding="utf-8")

    exe = navegador()
    if not exe:
        print(f"Navegador não encontrado. Abra {final} e use Imprimir > Salvar como PDF.")
        return 1
    pdf = DOCS / "CitizenTech_Entregaveis.pdf"
    pdf.unlink(missing_ok=True)
    # Perfil separado: sem ele o navegador pode repassar o pedido a uma janela já aberta e não gerar o PDF.
    subprocess.run([exe, "--headless", "--disable-gpu", "--no-pdf-header-footer",
                    f"--user-data-dir={build / 'perfil-navegador'}",
                    f"--print-to-pdf={pdf}", final.as_uri()], check=True, timeout=120,
                   stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    # O Edge pode terminar o comando antes de gravar o arquivo: espera o PDF aparecer e parar de crescer.
    tamanho = -1
    for _ in range(120):
        if pdf.exists() and pdf.stat().st_size > 0 and pdf.stat().st_size == tamanho:
            print(f"PDF gerado: {pdf}")
            return 0
        tamanho = pdf.stat().st_size if pdf.exists() else -1
        time.sleep(0.5)
    print("O navegador não gerou o PDF a tempo. Abra docs/build/entregaveis_final.html e use Imprimir > Salvar como PDF.")
    return 1


if __name__ == "__main__":
    sys.exit(main())
