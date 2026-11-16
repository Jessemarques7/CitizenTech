"""
Executa os casos manuais do Plano de Testes contra a API em execução e grava um log de evidências
(requisição, status esperado, status obtido e corpo da resposta).

Uso (com a API rodando):
    python docs/evidencias/coletar_evidencias.py --api http://localhost:8080/api
Saída: docs/evidencias/execucao_api.txt
"""
import argparse
import json
import urllib.error
import urllib.request
from datetime import datetime
from pathlib import Path

DEVICE_KEY = "sensor-demo-key-2026"


def chamar(api, metodo, rota, corpo=None, token=None, headers=None):
    h = {"Content-Type": "application/json"}
    if token:
        h["Authorization"] = "Bearer " + token
    h.update(headers or {})
    dados = json.dumps(corpo).encode() if corpo is not None else None
    req = urllib.request.Request(api.rstrip("/") + rota, data=dados, method=metodo, headers=h)
    try:
        with urllib.request.urlopen(req, timeout=10) as r:
            texto = r.read().decode()
            return r.status, json.loads(texto) if texto else None
    except urllib.error.HTTPError as e:
        texto = e.read().decode()
        return e.code, json.loads(texto) if texto else None


def main():
    p = argparse.ArgumentParser()
    p.add_argument("--api", default="http://localhost:8080/api")
    args = p.parse_args()
    api = args.api
    saida = []
    resultados = []

    def caso(cid, titulo, esperado, metodo, rota, corpo=None, token=None, headers=None, resumo=None):
        status, resp = chamar(api, metodo, rota, corpo, token, headers)
        ok = status == esperado
        resultados.append((cid, titulo, esperado, status, ok))
        corpo_txt = json.dumps(resumo(resp) if (resumo and resp is not None) else resp, ensure_ascii=False, indent=2)
        if len(corpo_txt) > 1500:
            corpo_txt = corpo_txt[:1500] + "\n... (truncado)"
        saida.append(
            f"### {cid} - {titulo}\n"
            f"{metodo} {rota}\n"
            + (f"Corpo: {json.dumps(corpo, ensure_ascii=False)}\n" if corpo is not None else "")
            + f"Esperado: HTTP {esperado} | Obtido: HTTP {status} | {'PASSOU' if ok else 'FALHOU'}\n"
            f"Resposta:\n{corpo_txt}\n"
        )
        return resp

    login = lambda e, s: chamar(api, "POST", "/auth/login", {"email": e, "senha": s})[1]["token"]

    caso("CT-01", "Login válido (cidadã)", 200, "POST", "/auth/login",
         {"email": "ana@citizentech.com", "senha": "Cidadao@2026"},
         resumo=lambda r: {**r, "token": r["token"][:25] + "..."})
    caso("CT-02", "Login com senha errada", 401, "POST", "/auth/login",
         {"email": "ana@citizentech.com", "senha": "senhaErrada1"})
    ana = login("ana@citizentech.com", "Cidadao@2026")
    admin = login("admin@citizentech.com", "Admin@2026")
    caso("CT-05", "Rota protegida sem token", 401, "GET", "/locais")
    caso("CT-07", "Listar locais (cidadã logada)", 200, "GET", "/locais", token=ana,
         resumo=lambda r: [{k: l[k] for k in ("id", "nome", "statusAcessibilidade", "ocupacao", "nivelOcupacao")} for l in r])
    caso("CT-08", "Local inexistente", 404, "GET", "/locais/nao-existe", token=ana)
    nova = caso("CT-09", "Registrar barreira", 201, "POST", "/ocorrencias",
                {"localId": "terminal", "categoria": "Rampa bloqueada", "descricao": "Carro estacionado em cima da rampa."},
                token=ana)
    caso("CT-10", "Registrar barreira inválida (categoria vazia + HTML)", 400, "POST", "/ocorrencias",
         {"localId": "terminal", "categoria": "", "descricao": "<script>alert(1)</script>"}, token=ana)
    caso("CT-11", "Cidadã pede relatos de outro usuário", 403, "GET", "/ocorrencias?usuarioId=3", token=ana)
    caso("CT-12a", "Cidadã tenta mudar status (só admin)", 403, "PUT", f"/ocorrencias/{nova['id']}/status",
         {"status": "Em análise"}, token=ana)
    caso("CT-12b", "Admin muda status", 200, "PUT", f"/ocorrencias/{nova['id']}/status",
         {"status": "Em análise"}, token=admin)
    caso("CT-13", "Mudar status de relato já resolvido", 409, "PUT", "/ocorrencias/CT-1018/status",
         {"status": "Recebida"}, token=admin)
    caso("CT-14", "Admin exclui relato", 204, "DELETE", f"/ocorrencias/{nova['id']}", token=admin)
    caso("CT-15", "Sensor acima do limite gera alerta", 201, "POST", "/sensores/leituras",
         [{"tipo": "chuva", "localId": "centro-cultural", "valor": 42, "unidade": "mm/h"}],
         headers={"X-Device-Key": DEVICE_KEY})
    caso("CT-15b", "Alerta aparece para o cidadão", 200, "GET", "/alertas", token=ana,
         resumo=lambda r: [{k: a[k] for k in ("id", "tipo", "titulo", "valor", "unidade", "status")} for a in r])
    caso("CT-16", "Leitura de sensor sem chave do dispositivo", 401, "POST", "/sensores/leituras",
         [{"tipo": "chuva", "localId": "centro-cultural", "valor": 50}])
    caso("CT-17", "Leitura com valor negativo", 400, "POST", "/sensores/leituras",
         [{"tipo": "chuva", "localId": "centro-cultural", "valor": -5}], headers={"X-Device-Key": DEVICE_KEY})
    caso("CT-18", "Preferências de outra pessoa", 403, "GET", "/preferencias/3", token=ana)
    caso("CT-19", "Cidadã tenta cadastrar local (só admin)", 403, "POST", "/locais",
         {"id": "x", "nome": "X", "categoria": "X", "endereco": "X", "statusAcessibilidade": "Parcial",
          "recursos": [], "obstaculos": []}, token=ana)
    caso("CT-21", "Busca com tentativa de SQL injection", 200, "GET", "/locais?busca=%27%20OR%20%271%27%3D%271",
         token=ana)

    passou = sum(1 for r in resultados if r[4])
    cabecalho = [
        f"CitizenTech - Evidências de execução da API",
        f"Data/hora: {datetime.now():%d/%m/%Y %H:%M:%S} | API: {api}",
        f"Resultado: {passou}/{len(resultados)} casos com o status esperado",
        "",
        f"{'Caso':<7} {'Esperado':>8} {'Obtido':>7}  Resultado  Descrição",
    ]
    for cid, titulo, esp, obt, ok in resultados:
        cabecalho.append(f"{cid:<7} {esp:>8} {obt:>7}  {'PASSOU ' if ok else 'FALHOU '}   {titulo}")
    texto = "\n".join(cabecalho) + "\n\n" + "\n".join(saida)
    destino = Path(__file__).with_name("execucao_api.txt")
    destino.write_text(texto, encoding="utf-8")
    print("\n".join(cabecalho))
    print(f"\nLog completo: {destino}")


if __name__ == "__main__":
    main()
