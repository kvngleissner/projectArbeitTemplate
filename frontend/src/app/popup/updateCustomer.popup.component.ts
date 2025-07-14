import {Component, Inject, inject, Injectable} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogTitle
} from '@angular/material/dialog';
import {MatButton} from '@angular/material/button';
import {MatFormField, MatLabel} from '@angular/material/form-field';
import {MatInput} from '@angular/material/input';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgForm} from '@angular/forms';
import {MatOption, MatSelect} from '@angular/material/select';
import {RestComponent} from '../rest/rest.component';
import {Customer} from '../model/customer';

@Component({
  selector: 'app-update-customer-popup',
  standalone: true,
  template: `

    <h2 mat-dialog-title xmlns="http://www.w3.org/1999/html">
      Update Customer Information
    </h2>

    <mat-dialog-content>

    </mat-dialog-content>


    <form #f="ngForm" (ngSubmit)="onSubmit(f)">
      <div>
        <mat-form-field class="birthDate-input">
          <mat-label>Birthday</mat-label>
          <input matInput type="date" name="birthDate" ngModel required #birthDate="ngModel">
        </mat-form-field>
      </div>

      <div>
        <mat-form-field class="first-name-input">
          <mat-label>First Name</mat-label>
          <input matInput type="text" pattern="[ a-zA-Z]*" placeholder='e.g. Max' name="firstName" ngModel required
                 #firstName="ngModel">
        </mat-form-field>
      </div>

      <div>
        <mat-form-field class="last-name-input">
          <mat-label>Last Name</mat-label>
          <input matInput type="text" pattern="[ a-zA-Z]*" placeholder='e.g. Mustermann' name="lastName" ngModel
                 required #lastName="ngModel">
        </mat-form-field>
      </div>

      <div>
        <mat-form-field class="gender-option">
          <mat-label>Gender</mat-label>
          <mat-select name="gender" ngModel required #gender="ngModel">
            <mat-option value="M">Male</mat-option>
            <mat-option value="W">Female</mat-option>
            <mat-option value="D">Divers</mat-option>
            <mat-option value="U">Indefinable</mat-option>
          </mat-select>
        </mat-form-field>
      </div>

      <mat-dialog-actions>
        <button mat-flat-button mat-dialog-close>Cancel</button>
        <button mat-flat-button>Update Customer</button>
      </mat-dialog-actions>

    </form>
  `,
  styles: [
    ``,
  ],
  imports: [
    MatDialogActions,
    MatButton,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle,
    MatFormField,
    MatInput,
    MatLabel,
    FormsModule,
    MatOption,
    MatSelect,
    ReactiveFormsModule
  ]
})
export class UpdateCustomerPopupComponent {
  restComponent = inject(RestComponent);
  data = inject(MAT_DIALOG_DATA);
  private currentData: any;
  private newData: any;


  onSubmit(f: NgForm) {
    this.newData = f.value;
    this.currentData = this.data.customer;
    this.newData.id = this.currentData.id;
    console.log('current: ', this.currentData);
    console.log('new: ', this.newData);
    this.restComponent.getUpdateCustomers(this.newData);
  }
}
