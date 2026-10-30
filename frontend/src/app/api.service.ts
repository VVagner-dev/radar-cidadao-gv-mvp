import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

export type Categoria = 'ACESSIBILIDADE'|'BURACO_VIA'|'ILUMINACAO'|'LIMPEZA_URBANA'|'SEGURANCA'|'OUTROS';
export interface Denuncia { protocolo:string; descricao:string; endereco:string|null; latitude:number; longitude:number; categoriaInformada:Categoria; categoriaSugerida:Categoria; prioridadeSugerida:string; situacao:string; justificativaIa:string; metodoClassificacao:string; criadaEm:string; }
@Injectable({providedIn:'root'}) export class ApiService {
  private base='http://localhost:8080/api/denuncias';
  constructor(private http:HttpClient) {}
  create(payload: Record<string,unknown>, fotos:File[]) { const form=new FormData(); form.append('denuncia',new Blob([JSON.stringify(payload)],{type:'application/json'})); fotos.forEach(f=>form.append('fotos',f)); return this.http.post<Denuncia>(this.base,form); }
  list(){return this.http.get<{itens:Denuncia[]}>(this.base);}
  summary(){return this.http.get<{total:number;recebidas:number;emAnalise:number;resolvidas:number}>(this.base+'/resumo');}
}
