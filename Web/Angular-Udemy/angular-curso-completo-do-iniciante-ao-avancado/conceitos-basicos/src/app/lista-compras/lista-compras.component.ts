import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ItemLista } from './itemlista';
@Component({
  selector: 'app-lista-compras',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './lista-compras.component.html',
  styleUrl: './lista-compras.component.scss'
})

export class ListaComprasComponent {
  item: string = '';
  lista: ItemLista[] = [];
   
  adicionarItem(){
    let itemLista = new ItemLista();
    itemLista.nome = this.item;
    itemLista.id = this.lista.length + 1;

    this.lista.push(itemLista);

    console.table(this.lista);

    this.item = ''
  }

  riscarItem(itemLista: ItemLista){
    itemLista.marcado = !itemLista.marcado;
  }

  limparLista(){
    this.lista = [];
  }
}
