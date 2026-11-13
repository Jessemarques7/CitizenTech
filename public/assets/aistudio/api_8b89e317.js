/*
 * CitizenTech API adapter
 * DEMO: deixe API_BASE_URL vazio para navegar com dados locais (localStorage).
 * INTEGRAÇÃO: informe a URL-base da API REST do grupo, por exemplo:
 * window.CITIZENTECH_API_BASE = "http://localhost:8080/api";
 * O contrato esperado está em API_CONTRATO.md.
 */
const API_BASE_URL = window.CITIZENTECH_API_BASE || "";
const DEMO_KEY = "citizentech_demo_v1";
const DEMO_USER = "cidadao-demo-01";

const seedData = {
  locais: [
    {id:"biblioteca",nome:"Biblioteca Central",categoria:"Serviço público",endereco:"Rua das Palmeiras, 120 · Centro",statusAcessibilidade:"Parcial",resumo:"Entrada principal nivelada. Há um trecho de calçada irregular na lateral.",recursos:["Entrada principal com rampa","Banheiro acessível","Balcão de atendimento rebaixado","Piso tátil na entrada"],obstaculos:["Calçada lateral irregular"],ocupacao:42,nivelOcupacao:"Alta",ultimaLeitura:"14:32",atualizadoEm:"Hoje, 14:32",confianca:"Cadastro + relato comunitário"},
    {id:"terminal",nome:"Terminal Central",categoria:"Transporte",endereco:"Av. Integração, 450 · Centro",statusAcessibilidade:"Acessível",resumo:"Acesso principal com piso tátil e elevador sinalizado.",recursos:["Rampas de acesso","Piso tátil direcional","Elevador sinalizado","Balcão preferencial"],obstaculos:[],ocupacao:18,nivelOcupacao:"Moderada",ultimaLeitura:"14:28",atualizadoEm:"Hoje, 14:28",confianca:"Cadastro local"},
    {id:"centro-cultural",nome:"Centro Cultural",categoria:"Cultura e lazer",endereco:"Praça das Artes, 35 · Centro",statusAcessibilidade:"Acessível",resumo:"Entrada sem degraus e espaços de circulação amplos.",recursos:["Acesso sem degraus","Banheiro acessível","Assentos reservados","Sinalização visual"],obstaculos:[],ocupacao:9,nivelOcupacao:"Baixa",ultimaLeitura:"14:25",atualizadoEm:"Hoje, 14:25",confianca:"Cadastro local"}
  ],
  ocorrencias: [
    {id:"CT-1024",localId:"biblioteca",localNome:"Biblioteca Central",categoria:"Calçada irregular",descricao:"Piso desnivelado na lateral da entrada, próximo à faixa de pedestres.",status:"Em análise",criadoEm:"Hoje, 10:42",progresso:45},
    {id:"CT-1018",localId:"terminal",localNome:"Terminal Central",categoria:"Elevador",descricao:"Elevador voltou a operar após manutenção.",status:"Resolvida",criadoEm:"Ontem, 16:10",progresso:100}
  ],
  alertas: [
    {id:"AL-205",tipo:"Clima",titulo:"Chuva intensa no entorno da Praça Central",local:"Praça Central",valor:35,unidade:"mm/h",atualizadoEm:"Hoje, 14:35",descricao:"A leitura simulada ultrapassou o limite de demonstração configurado pela equipe. Considere verificar as condições do trajeto antes de sair."}
  ],
  preferencias: {evitarEscadas:true,priorizarRampas:true,alertasVisuais:true,altoContraste:false,usuarioId:DEMO_USER}
};

function readDemo(){
  try { const saved=localStorage.getItem(DEMO_KEY); return saved?JSON.parse(saved):JSON.parse(JSON.stringify(seedData)); }
  catch { return JSON.parse(JSON.stringify(seedData)); }
}
function writeDemo(data){ try{localStorage.setItem(DEMO_KEY,JSON.stringify(data));}catch(e){console.warn("Não foi possível persistir a demonstração",e);} }
function getToken(){ try{return localStorage.getItem("citizentech_token")||"";}catch{return "";} }
async function http(method,path,body){
  const headers={"Accept":"application/json"};
  if(body!==undefined) headers["Content-Type"]="application/json";
  const token=getToken(); if(token) headers.Authorization=`Bearer ${token}`;
  let response;
  try{
    response=await fetch(`${API_BASE_URL}${path}`,{method,headers,body:body===undefined?undefined:JSON.stringify(body)});
  }catch(error){throw new Error("Não foi possível conectar à API. Verifique se o backend está ativo e se o CORS permite esta origem.");}
  if(!response.ok){let message=`A API respondeu com status ${response.status}.`;try{const payload=await response.json();message=payload.message||payload.detail||message;}catch{}throw new Error(message);}
  if(response.status===204)return null;
  return response.json();
}
function demoRequest(method,path,body){
  const data=readDemo(); const clean=path.split("?")[0];
  if(method==="GET"&&clean==="/locais")return Promise.resolve(data.locais);
  if(method==="GET"&&clean.startsWith("/locais/")){const found=data.locais.find(x=>x.id===clean.split("/").pop());return found?Promise.resolve(found):Promise.reject(new Error("Local não encontrado."));}
  if(method==="GET"&&clean==="/ocorrencias")return Promise.resolve(data.ocorrencias);
  if(method==="POST"&&clean==="/ocorrencias"){
    const local=data.locais.find(x=>x.id===body.localId);
    if(!local)return Promise.reject(new Error("Selecione um local válido."));
    const novo={id:`CT-${Math.floor(1000+Math.random()*8999)}`,localId:body.localId,localNome:local.nome,categoria:body.categoria,descricao:body.descricao,status:"Recebida",criadoEm:"Agora",progresso:15};
    data.ocorrencias.unshift(novo);writeDemo(data);return Promise.resolve(novo);
  }
  if(method==="GET"&&clean==="/alertas")return Promise.resolve(data.alertas);
  if(method==="GET"&&clean.startsWith("/preferencias/"))return Promise.resolve(data.preferencias);
  if(method==="PUT"&&clean.startsWith("/preferencias/")){data.preferencias={...data.preferencias,...body};writeDemo(data);return Promise.resolve(data.preferencias);}
  if(method==="POST"&&clean==="/sensores/leituras"){
    const readings=Array.isArray(body)?body:[body];
    readings.forEach(r=>{
      if(r.tipo==="ocupacao"){
        const local=data.locais.find(x=>x.id===r.localId);if(local){local.ocupacao=Number(r.valor);local.nivelOcupacao=Number(r.valor)>=35?"Alta":Number(r.valor)>=16?"Moderada":"Baixa";local.ultimaLeitura="Agora";local.atualizadoEm="Agora";}
      }
      if(r.tipo==="chuva"){
        const value=Number(r.valor);if(value>=30){data.alertas.unshift({id:`AL-${Date.now()}`,tipo:"Clima",titulo:"Chuva intensa identificada na área monitorada",local:r.localNome||"Ponto monitorado",valor:value,unidade:r.unidade||"mm/h",atualizadoEm:"Agora",descricao:"A leitura do sensor simulado ultrapassou o limite de demonstração configurado pela equipe."});}
      }
    });writeDemo(data);return Promise.resolve({message:"Leituras registradas na demonstração.",leituras:readings.length});
  }
  return Promise.reject(new Error(`Rota de demonstração não configurada: ${method} ${path}`));
}
const api={
  modo:API_BASE_URL?"api":"demo",
  request(method,path,body){return API_BASE_URL?http(method,path,body):demoRequest(method,path,body);},
  locais(){return this.request("GET","/locais");},
  local(id){return this.request("GET",`/locais/${encodeURIComponent(id)}`);},
  ocorrencias(){return this.request("GET",`/ocorrencias?usuarioId=${encodeURIComponent(DEMO_USER)}`);},
  criarOcorrencia(payload){return this.request("POST","/ocorrencias",{...payload,usuarioId:DEMO_USER});},
  alertas(){return this.request("GET","/alertas");},
  preferencias(){return this.request("GET",`/preferencias/${DEMO_USER}`);},
  salvarPreferencias(payload){return this.request("PUT",`/preferencias/${DEMO_USER}`,payload);},
  registrarLeituras(payload){return this.request("POST","/sensores/leituras",payload);}
};
