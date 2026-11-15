"""
CitizenTech - Simulador de sensores IoT (Parte 6 da Global Solution).

Simula sensores de OCUPACAO (pessoas) e de CHUVA (mm/h) descritos em sensores.json e envia
as leituras para a API (POST /api/sensores/leituras) autenticando como dispositivo
(cabecalho X-Device-Key). A API grava cada leitura, atualiza a ocupacao do local e gera ou
encerra alertas conforme o limite de cada sensor; o app mostra os alertas na aba "Alertas".

Usa apenas a biblioteca padrao do Python 3 (nada para instalar).

Exemplos:
    python iot/simulador_sensores.py                      # 10 ciclos, 1 a cada 3 s
    python iot/simulador_sensores.py --cenario tempestade # chuva acima do limite -> alerta
    python iot/simulador_sensores.py --cenario lotacao --ciclos 3
    python iot/simulador_sensores.py --api http://localhost:8081/api --ciclos 0   # infinito
"""
import argparse
import json
import os
import random
import sys
import time
import urllib.error
import urllib.request
from datetime import datetime
from pathlib import Path

CENARIOS = {
    "normal": "valores oscilam perto da base (raramente passam do limite)",
    "tempestade": "a chuva sobe ate o pico nos ultimos ciclos (gera alerta de Clima)",
    "lotacao": "a ocupacao sobe ate o pico nos ultimos ciclos (gera alerta de Ocupacao)",
    "pico": "todos os sensores vao ao pico no ultimo ciclo e depois normalizam",
}


def carregar_sensores(caminho):
    with open(caminho, encoding="utf-8") as f:
        return json.load(f)["sensores"]


def proximo_valor(sensor, anterior, cenario, ciclo, total):
    """Passeio aleatorio em torno da base; nos cenarios de evento, sobe em direcao ao pico."""
    alvo = sensor["base"]
    progresso = (ciclo + 1) / total if total else 0
    sobe = (
        (cenario == "tempestade" and sensor["tipo"] == "chuva")
        or (cenario == "lotacao" and sensor["tipo"] == "ocupacao")
        or (cenario == "pico" and ciclo == total - 1)
    )
    suavizacao = 0.6
    if sobe:
        alvo = sensor["base"] + (sensor["pico"] - sensor["base"]) * progresso
        suavizacao = 1.0  # durante o evento o valor acompanha o alvo, sem atraso
    valor = anterior + (alvo - anterior) * suavizacao + random.uniform(-sensor["variacao"], sensor["variacao"])
    valor = max(sensor["minimo"], min(sensor["maximo"], valor))
    return round(valor) if sensor["tipo"] == "ocupacao" else round(valor, 1)


def enviar(api, chave, leituras):
    corpo = json.dumps(leituras).encode("utf-8")
    req = urllib.request.Request(
        api.rstrip("/") + "/sensores/leituras",
        data=corpo,
        method="POST",
        headers={"Content-Type": "application/json", "X-Device-Key": chave},
    )
    with urllib.request.urlopen(req, timeout=10) as resp:
        return resp.status, json.loads(resp.read().decode("utf-8"))


def main():
    parser = argparse.ArgumentParser(description="Simulador de sensores IoT da CitizenTech")
    parser.add_argument("--api", default=os.getenv("CITIZENTECH_API", "http://localhost:8080/api"))
    parser.add_argument("--chave", default=os.getenv("CITIZENTECH_DEVICE_KEY", "sensor-demo-key-2026"),
                        help="chave do dispositivo (X-Device-Key)")
    parser.add_argument("--ciclos", type=int, default=10, help="quantidade de envios (0 = infinito)")
    parser.add_argument("--intervalo", type=float, default=3.0, help="segundos entre envios")
    parser.add_argument("--cenario", choices=CENARIOS.keys(), default="normal")
    parser.add_argument("--config", default=str(Path(__file__).with_name("sensores.json")))
    parser.add_argument("--seed", type=int, help="semente aleatoria (resultado reproduzivel)")
    args = parser.parse_args()

    if args.seed is not None:
        random.seed(args.seed)
    sensores = carregar_sensores(args.config)
    valores = {s["codigo"]: s["base"] for s in sensores}
    total = args.ciclos if args.ciclos > 0 else 20

    print(f"CitizenTech IoT | API {args.api} | cenario '{args.cenario}': {CENARIOS[args.cenario]}")
    for s in sensores:
        print(f"  - {s['codigo']:<13} {s['tipo']:<9} @ {s['localId']:<16} {s['o_que_mede']}")
    print("Leituras SIMULADAS - nao sao dados oficiais.\n")

    ciclo = 0
    try:
        while args.ciclos == 0 or ciclo < args.ciclos:
            leituras = []
            for s in sensores:
                valores[s["codigo"]] = proximo_valor(s, valores[s["codigo"]], args.cenario, ciclo % total, total)
                leituras.append({
                    "sensorCodigo": s["codigo"],
                    "tipo": s["tipo"],
                    "localId": s["localId"],
                    "valor": valores[s["codigo"]],
                    "unidade": s["unidade"],
                })
            hora = datetime.now().strftime("%H:%M:%S")
            try:
                status, resposta = enviar(args.api, args.chave, leituras)
            except urllib.error.HTTPError as e:
                detalhe = e.read().decode("utf-8", errors="replace")
                print(f"[{hora}] ERRO HTTP {e.code}: {detalhe}")
                if e.code in (401, 403):
                    print("Verifique a chave do dispositivo (--chave / CITIZENTECH_DEVICE_KEY).")
                    return 1
            except urllib.error.URLError as e:
                print(f"[{hora}] API indisponivel em {args.api} ({e.reason}). Nova tentativa no proximo ciclo.")
            else:
                print(f"[{hora}] ciclo {ciclo + 1} -> HTTP {status}")
                for r in resposta["resultados"]:
                    marca = "ACIMA DO LIMITE" if r["acimaDoLimite"] else "ok"
                    extra = ""
                    if r.get("alertaGerado"):
                        extra += f"  >> ALERTA {r['alertaGerado']} gerado"
                    if r.get("alertasEncerrados"):
                        extra += f"  >> encerrou {', '.join(r['alertasEncerrados'])}"
                    print(f"    {r['sensor']:<13} {r['valor']:>7} (limite {r['limite']}) {marca}{extra}")
            ciclo += 1
            if args.ciclos == 0 or ciclo < args.ciclos:
                time.sleep(args.intervalo)
    except KeyboardInterrupt:
        print("\nSimulacao interrompida.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
