import {Component} from '@angular/core';
import {MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle} from '@angular/material/dialog';
import {MatButton} from '@angular/material/button';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgForm} from '@angular/forms';

@Component({
  selector: 'app-delete-popup',
  standalone: true,
  template: `

    <h2 mat-dialog-title xmlns="http://www.w3.org/1999/html">
      insert title name
    </h2>

    <mat-dialog-content>

    </mat-dialog-content>


    <form #f="ngForm" (ngSubmit)="onSubmit(f)">

      <div>
        <mat-dialog-content>
            Are you sure you want to delete this customer?
        </mat-dialog-content>
      </div>

      <mat-dialog-actions>
        <button mat-flat-button mat-dialog-close>Cancel</button>
        <button mat-flat-button>Confirm</button>
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
    FormsModule,
    ReactiveFormsModule
  ]
})
export class DeletePopupComponent {
  onSubmit(f: NgForm) {
    console.log(f.value);
  }
}
