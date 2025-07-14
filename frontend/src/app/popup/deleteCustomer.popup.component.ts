import {Component, inject} from '@angular/core';
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
import {RestService} from '../rest.service';

@Component({
  selector: 'app-delete-customer-popup',
  standalone: true,
  template: `

    <h2 mat-dialog-title xmlns="http://www.w3.org/1999/html">
      Delete Customer
    </h2>

    <form #f="ngForm" (ngSubmit)="onSubmit(f)">

      <div>
        <mat-dialog-content>
          Are you sure you want to delete this customer?
        </mat-dialog-content>
      </div>

      <mat-dialog-actions>
        <button mat-flat-button mat-dialog-close>Cancel</button>
        <button mat-flat-button>Delete</button>
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
export class DeleteCustomerPopupComponent {
  restComponent = inject(RestComponent);
  data = inject(MAT_DIALOG_DATA);

  onSubmit(f: NgForm) {
    console.log(this.data);
    this.restComponent.getDeleteCustomers(this.data);
  }
}
