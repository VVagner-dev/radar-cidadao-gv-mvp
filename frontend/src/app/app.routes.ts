import { Routes } from '@angular/router';
import { DenunciarComponent } from './denunciar.component';
import { PainelComponent } from './painel.component';
export const routes: Routes = [{ path: '', component: DenunciarComponent, title: 'Denunciar | Radar Cidadão GV' }, { path: 'painel', component: PainelComponent, title: 'Painel público | Radar Cidadão GV' }, { path: '**', redirectTo: '' }];
