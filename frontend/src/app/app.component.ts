import {Component} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {RestComponent} from './rest/rest.component';
import {NgOptimizedImage} from '@angular/common';
import { HeaderComponent } from './header/header.component';
import {MatTooltip} from '@angular/material/tooltip';
import {MatDialog} from '@angular/material/dialog';
import {CreateCustomerPopupComponent} from './popup/createCustomer.popup.component';


@Component({
  selector: 'app-root',
  template: `
  <main>
    <app-header></app-header>
    <app-rest></app-rest>
    <!--
    <button class="details-button"
            matTooltip="Show details"
            matTooltipShowDelay="300"
            matTooltipPosition="above"
            matTooltipHideDelay="100"
            (click)="openDetailsDialog()">
      <img class="details-data" ngSrc="detail.png" aria-hidden="true" height="45" width="45" alt="logo">
    </button>

    <button class="create-button"
            matTooltip="Create a new customer"
            matTooltipShowDelay="300"
            matTooltipPosition="above"
            matTooltipHideDelay="100"
            (click)="openCreateDialog()">
      <img class="edit-data" ngSrc="plus.png" aria-hidden="true" height="45" width="45" alt="logo">
    </button>

    <button class="edit-button"
            matTooltip="Edit an existing customer"
            matTooltipShowDelay="300"
            matTooltipPosition="above"
            matTooltipHideDelay="100"
            (click)="openCreateDialog()">
      <img class="edit-data" ngSrc="pencil.png" aria-hidden="true" height="45" width="45" alt="logo">
    </button>

    <button class="delete-button"
            matTooltip="Delete a customer"
            matTooltipShowDelay="300"
            matTooltipPosition="above"
            matTooltipHideDelay="100"
            (click)="openDeleteDialog()">
      <img class="delete-data" ngSrc="delete.png" aria-hidden="true" height="45" width="45" alt="logo">
    </button>

    <button class="ok-button"
            matTooltip="Ok"
            matTooltipShowDelay="300"
            matTooltipPosition="above"
            matTooltipHideDelay="100">
      <img class="delete-data" ngSrc="check.png" aria-hidden="true" height="45" width="45" alt="logo">
    </button>

    <button class="cancel-button"
            matTooltip="Cancel"
            matTooltipShowDelay="300"
            matTooltipPosition="above"
            matTooltipHideDelay="100">
      <img class="delete-data" ngSrc="cross.png" aria-hidden="true" height="45" width="45" alt="logo">
    </button>
    -->
  </main>
  `
  ,
  styleUrl: './app.component.scss',
  imports: [RestComponent, NgOptimizedImage, HeaderComponent, CreateCustomerPopupComponent, MatTooltip],
})
export class AppComponent {
  static title = 'Hausverwaltung - Dashboard';

  /*
  constructor(public dialog: MatDialog) {}

  openDetailsDialog() {
    this.dialog.open(DetailsPopupComponent);
  }

  openCreateDialog() {
    this.dialog.open(CreatePopupComponent, {width: '600px'});
  }

  openDeleteDialog() {
    this.dialog.open(DeletePopupComponent, {width: '600px'});
  }
   */
}
