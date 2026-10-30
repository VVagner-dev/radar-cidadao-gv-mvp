import { Component, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import * as L from 'leaflet';
import { ApiService, Categoria, Denuncia } from './api.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
<main class="report-page" *ngIf="!success; else confirmation">
  <section class="hero"><p class="eyebrow">Cidadania digital</p><h1>Viu um problema na cidade?</h1><p>Registre sua denúncia e ajude a melhorar a Grande Vitória.</p></section>
  <section class="report-layout">
    <section class="map-workspace">
      <div class="map-intro"><span class="step-number">1</span><div><h2>Localize o problema</h2><p>Digite um endereço, escolha uma sugestão ou clique no mapa para posicionar o marcador.</p></div></div>
      <div class="search"><input [(ngModel)]="consulta" (ngModelChange)="aoDigitarEndereco()" placeholder="Rua, número, bairro ou ponto de referência" autocomplete="off"><button type="button" (click)="buscarEndereco()">Buscar</button></div>
      <ul class="suggestions" *ngIf="sugestoes.length"><li *ngFor="let item of sugestoes" (click)="selecionar(item)"><b>{{item.displayPlace}}</b><small>{{item.displayName}}</small></li></ul>
      <div id="report-map" class="map"></div><p class="location-text">Pino selecionado: {{latitude.toFixed(5)}}, {{longitude.toFixed(5)}}</p>
    </section>
    <aside class="report-form">
      <div class="form-heading"><span class="step-number">2</span><div><h2>Descreva a denúncia</h2><p>Não inclua dados pessoais de outras pessoas.</p></div></div>
      <div class="type-grid"><button *ngFor="let type of types" type="button" [class.selected]="categoria===type.value" (click)="categoria=type.value">{{type.label}}</button></div>
      <label>Descreva o problema<textarea [(ngModel)]="descricao" maxlength="2000" placeholder="Ex.: há um buraco grande na via, em frente ao ponto de ônibus."></textarea></label>
      <div class="upload-heading"><span class="step-number small">3</span><div><strong>Adicione fotos <small>opcional</small></strong><p>Até 3 imagens JPG, PNG ou WebP de até 5 MB.</p></div></div>
      <label class="upload">Selecionar fotos<input type="file" accept="image/jpeg,image/png,image/webp" multiple (change)="onFiles($event)"></label>
      <div class="files" *ngIf="fotos.length"><span *ngFor="let photo of fotos; let i=index">{{photo.name}} <button type="button" (click)="remove(i)">×</button></span></div>
      <p class="error" *ngIf="error">{{error}}</p><button class="submit" type="button" (click)="enviar()" [disabled]="sending">{{sending?'Enviando...':'Enviar denúncia'}}</button>
    </aside>
  </section>
</main>
<ng-template #confirmation><main class="confirmation"><div class="check">✓</div><p class="eyebrow">Denúncia recebida</p><h1>Obrigado por colaborar.</h1><p>Guarde este protocolo para referência.</p><strong>{{success?.protocolo}}</strong><div class="classification"><span>Categoria sugerida: <b>{{label(success?.categoriaSugerida)}}</b></span><span>Prioridade: <b>{{label(success?.prioridadeSugerida)}}</b></span><p>{{success?.justificativaIa}}</p></div><a class="submit" href="/">Fazer outra denúncia</a></main></ng-template>`
})
export class DenunciarComponent implements AfterViewInit {
  types:{value:Categoria,label:string}[]=[{value:'BURACO_VIA',label:'Buraco ou via'},{value:'ACESSIBILIDADE',label:'Acessibilidade'},{value:'ILUMINACAO',label:'Iluminação'},{value:'LIMPEZA_URBANA',label:'Limpeza'},{value:'SEGURANCA',label:'Segurança'},{value:'OUTROS',label:'Outro'}];
  categoria:Categoria='BURACO_VIA'; descricao=''; consulta=''; latitude=-20.3155; longitude=-40.3128; fotos:File[]=[]; sugestoes:any[]=[]; error=''; sending=false; success?:Denuncia; private map?:L.Map; private marker?:L.Marker; private buscaTimer?:ReturnType<typeof setTimeout>;
  constructor(private api:ApiService) {}
  ngAfterViewInit(){this.map=L.map('report-map').setView([this.latitude,this.longitude],12);L.tileLayer('http://localhost:8080/api/localizacao/mapa/{z}/{x}/{y}.png',{attribution:'© LocationIQ © OpenStreetMap'}).addTo(this.map);this.setPin(this.latitude,this.longitude);this.map.on('click',e=>this.setPin(e.latlng.lat,e.latlng.lng));}
  setPin(lat:number,lng:number){this.latitude=lat;this.longitude=lng;if(!this.map)return;if(!this.marker){const icon=L.divIcon({className:'report-pin',html:'<span>●</span>',iconSize:[34,34],iconAnchor:[17,34]});this.marker=L.marker([lat,lng],{draggable:true,icon}).addTo(this.map);this.marker.on('dragend',()=>{const p=this.marker!.getLatLng();this.latitude=p.lat;this.longitude=p.lng;});}else this.marker.setLatLng([lat,lng]);this.map.setView([lat,lng],16);}
  aoDigitarEndereco(){clearTimeout(this.buscaTimer);if(this.consulta.trim().length<3){this.sugestoes=[];return;}this.buscaTimer=setTimeout(()=>this.buscarEndereco(),350);}
  buscarEndereco(){if(this.consulta.trim().length<3)return;fetch('http://localhost:8080/api/localizacao/sugestoes?q='+encodeURIComponent(this.consulta)).then(r=>r.ok?r.json():Promise.reject()).then(items=>this.sugestoes=items).catch(()=>this.error='Não foi possível buscar o endereço. Marque o pino diretamente no mapa.');}
  selecionar(item:any){this.consulta=item.displayName;this.sugestoes=[];this.setPin(Number(item.latitude),Number(item.longitude));}
  onFiles(event:Event){const files=Array.from((event.target as HTMLInputElement).files||[]);const selected=[...this.fotos,...files];if(selected.length>3||selected.some(f=>f.size>5*1024*1024||!['image/jpeg','image/png','image/webp'].includes(f.type))){this.error='Use no máximo 3 fotos JPG, PNG ou WebP de até 5 MB.';return;}this.fotos=selected;this.error='';}
  remove(i:number){this.fotos.splice(i,1);}
  enviar(){if(!this.descricao.trim()){this.error='Descreva o problema antes de enviar.';return;}this.sending=true;this.error='';this.api.create({descricao:this.descricao,categoria:this.categoria,endereco:this.consulta,latitude:this.latitude,longitude:this.longitude},this.fotos).subscribe({next:r=>{this.success=r;this.sending=false;},error:e=>{this.error=e.error?.mensagem||'Não foi possível enviar agora.';this.sending=false;}});}
  label(value?:string){return (value||'').replaceAll('_',' ').toLowerCase().replace(/\b\w/g,l=>l.toUpperCase());}
}
