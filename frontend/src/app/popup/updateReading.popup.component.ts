import {Component, Inject, inject} from '@angular/core';
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
import {MatRadioButton, MatRadioGroup} from '@angular/material/radio';

@Component({
  selector: 'app-update-customer-popup',
  standalone: true,
  template: `

    <h2 mat-dialog-title xmlns="http://www.w3.org/1999/html">
      Update Reading Information
    </h2>

    <mat-dialog-content>

    </mat-dialog-content>



    <form #f="ngForm" (ngSubmit)="onSubmit(f)">
      <div>
        <mat-form-field class="dateOfReading-input">
          <mat-label>Date of Reading</mat-label>
          <input matInput type="date" name="dateOfReading" ngModel required #dateOfReading="ngModel">
        </mat-form-field>
      </div>

      <div>
        <mat-form-field class="meter-id-input">
          <mat-label>Meter ID</mat-label>
          <input matInput type="text" placeholder='MST-az12345' name="meterId" ngModel required
                 #meterId="ngModel">
        </mat-form-field>
      </div>

      <div>
        <mat-form-field class="meter-count-input">
          <mat-label>Meter Count</mat-label>
          <input matInput type="text" pattern="[0-9]*" placeholder='12345' name="meterCount" ngModel
                 required #meterCount="ngModel">
        </mat-form-field>
      </div>

      <div>
        <mat-form-field class="kindOfMeter-option">
          <mat-label>Kind of Meter</mat-label>
          <mat-select name="kindOfMeter" ngModel required #kindOfMeter="ngModel">
            <mat-option value="WATER">WATER</mat-option>
            <mat-option value="POWER">POWER</mat-option>
            <mat-option value="HEAT">HEAT</mat-option>
            <mat-option value="UNKNOWN">UNKNOWN</mat-option>
          </mat-select>
        </mat-form-field>
      </div>

      <div>
        <mat-form-field class="substitute-option">
          <mat-label>Substitute</mat-label>
          <mat-select name="substitute" [(ngModel)]="f.value.substitute" ngModel required #substitute="ngModel">
            <mat-option value=false>No</mat-option>
            <mat-option value=true>Yes</mat-option>
          </mat-select>
        </mat-form-field>
      </div>

      <div>
        <mat-form-field class="comment-input">
          <mat-label>Comment</mat-label>
          <input matInput type="text" placeholder='Comment' name="comment" ngModel #comment="ngModel">
        </mat-form-field>
      </div>

      <mat-dialog-actions>
        <button mat-flat-button mat-dialog-close>Cancel</button>
        <button mat-flat-button>Update Reading</button>
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
    ReactiveFormsModule,
    MatRadioButton,
    MatRadioGroup
  ]
})
export class UpdateReadingPopupComponent {
  restComponent = inject(RestComponent);
  data = inject(MAT_DIALOG_DATA);
  private currentData: any;
  private newData: any;

  onSubmit(f: NgForm) {
    this.restComponent.getUpdateReading(f.value);

    this.newData = f.value;
    this.currentData = this.data.reading;
    this.newData.id = this.currentData.id;
    console.log('current: ', this.currentData);
    console.log('new: ', this.newData);
    this.restComponent.getUpdateReading(this.newData);
  }
}
