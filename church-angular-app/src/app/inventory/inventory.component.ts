import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

interface Inventory {
  itemType: string;
  quantity: number;
  price: number;
}

@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './inventory.component.html',
  styleUrl: './inventory.component.css'
})

export class InventoryComponent {

  inventories: any;

  constructor(private http:HttpClient) { }
  
  ngOnInit(){
    let response = this.http.get("http://localhost:8080/all-inventory");
    response.subscribe((data)=> this.inventories = data);
  }

  newInventory = {
    itemType: '',
    quantity: 0,
    price: 0
  };

  // Adds the new row to the table
  addInventory() {
    
    let newItem = JSON.stringify(this.newInventory);
    console.log(newItem);
    let response = this.http.post("http://localhost:8080/inventory", newItem, {headers : new HttpHeaders({ 'Content-Type': 'application/json', 'Accept': 'application/json' })});
    response.subscribe((data)=>data);
  }
}
