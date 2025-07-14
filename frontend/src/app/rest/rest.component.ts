import {AfterViewInit, ChangeDetectorRef, Component, Injectable, OnInit, ViewChild} from '@angular/core'
import { RestService } from '../rest.service'
import {NgForOf, NgOptimizedImage} from "@angular/common";
import {CommonModule} from '@angular/common';
import {Customer} from '../model/customer';
import {Reading} from '../model/reading';
import {
  MatCell, MatCellDef,
  MatColumnDef, MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow, MatHeaderRowDef,
  MatRow, MatRowDef,
  MatTable,
  MatTableDataSource,
  MatTableModule,
} from '@angular/material/table';
import {MatSort, MatSortModule} from '@angular/material/sort';
import {MatTooltip} from '@angular/material/tooltip';
import {MatDialog} from '@angular/material/dialog';
import {MatFormField, MatFormFieldModule, MatLabel} from '@angular/material/form-field';
import {CreateCustomerPopupComponent} from '../popup/createCustomer.popup.component';
import {NgForm} from '@angular/forms';
import {MatPaginator} from '@angular/material/paginator';
import {UpdateCustomerPopupComponent} from '../popup/updateCustomer.popup.component';
import {CreateReadingPopupComponent} from '../popup/createReading.popup.component';
import {UpdateReadingPopupComponent} from '../popup/updateReading.popup.component';
import {DeleteCustomerPopupComponent} from '../popup/deleteCustomer.popup.component';
import {DeleteReadingPopupComponent} from "../popup/deleteReading.popup.component";
import {MatInputModule} from '@angular/material/input';
import {DeletePopupComponent} from "../header/delete.popup.component";
@Injectable({ providedIn: 'root' })

@Component({
  selector: 'app-rest',
  templateUrl: './rest.component.html',
  styleUrls: ['./rest.component.css'],
  imports: [ CommonModule, MatLabel, MatPaginator, MatFormField, MatTable, MatInputModule, MatFormFieldModule, MatHeaderRow, MatRow, MatColumnDef, MatHeaderCell, MatCell, MatSort, MatHeaderRowDef, MatRowDef, MatHeaderCellDef, MatCellDef
  ]
})
export class RestComponent implements OnInit {
  customers:any = [];
  readings:any = [];
  token:string = '';
  tempCustomer:any=[];

  ////
  displayedColumnsCustomer: string[] = ['id', 'lastName', 'firstName', 'gender', 'birthDate', 'actions'];
  displayedColumnsReading: string[] = ['id', 'customerId', 'kindOfMeter', 'meterId', 'meterCount', 'substitute', 'dateOfReading', 'comment', 'actions'];
  dataSourceCustomer!: MatTableDataSource<Customer>;
  dataSourceReading!: MatTableDataSource<Reading>;
  @ViewChild(MatSort, { static: false }) sort!: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator!: MatPaginator;

  applyFilter(event: Event, option:any) {
    const filterValue = (event.target as HTMLInputElement).value;

    switch (option) {
      case "customers":
        this.dataSourceCustomer.filter = filterValue.trim().toLowerCase();
        break;
      case "readings":
        this.dataSourceReading.filter = filterValue.trim().toLowerCase();
        break;
      default:
        console.log("error");
        break;
    }

    if (this.dataSourceCustomer.paginator) {
      this.dataSourceCustomer.paginator.firstPage();
    }
    if (this.dataSourceReading.paginator) {
      this.dataSourceReading.paginator.firstPage();
    }
  }
  ////

  constructor(private restService: RestService, private dialog: MatDialog, private cdr: ChangeDetectorRef) {}

  openCreateDialog(option:string, data:any) {
    switch (option) {
      case "createCustomer":
        this.dialog.open(CreateCustomerPopupComponent, {width: '600px'});
        console.log(option);
        break;
      case "createReading":
        this.dialog.open(CreateReadingPopupComponent, {
          width: '600px',
          data: {customer: data}
        });
        console.log(option);
        console.log(data);
        break;
      default:
        console.log("error");
        break;
    }
  }

  openUpdateDialog(option:string, data:any) {
    switch (option) {
      case "updateCustomer":
        this.dialog.open(UpdateCustomerPopupComponent, {
          width: '600px',
          data: {customer: data}
        });
        console.log(option);
        console.log(data);
        break;
      case "updateReading":
        this.dialog.open(UpdateReadingPopupComponent, {
          width: '600px',
          data: {reading: data}
        });
        console.log(option);
        console.log(data);
        break;
      default:
        console.log("error");
        break;
    }
  }

  openDeleteDialog(option:string, data:any) {
    switch (option) {
      case "deleteCustomer":
        this.dialog.open(DeleteCustomerPopupComponent, {
          width: '600px',
          data: {customer: data}
        });
        console.log(option);
        console.log(data);
        break;
      case "deleteReading":
        this.dialog.open(DeleteReadingPopupComponent, {
          width: '600px',
          data: {reading: data}
        });
        console.log(option);
        console.log(data);
        break;
      default:
        console.log("error");
        break;
    }
  }

  closeDialog() {
    this.dialog.closeAll();
  }

  ngOnInit(): void {
    this.getPrintCustomers()
  }

  reloadPage() {
    window.location.reload();
    //this.getPrintCustomers()
  }

  ////// Calls Customer

  getPrintCustomers() {
    this.restService.getAuthToken().subscribe(result => {
      this.token = result;
    });

    this.restService.getCustomer().subscribe((result:Customer) => {
      this.customers = result;
      console.log(this.customers);

      this.dataSourceCustomer = new MatTableDataSource<Customer>(Object.values(this.customers));
      this.dataSourceCustomer.paginator = this.paginator;
      this.dataSourceCustomer.sort = this.sort;
    });
  }

  getCreateCustomer(customer:any) {
    this.restService.getAuthToken().subscribe(result => {
      this.token = result;
    });

    const newCustomer: Customer = {
      id: '',
      gender: 'U',
      lastName: '',
      firstName: '',
      birthDate: new Date()
    };

    newCustomer.gender = customer.gender;
    newCustomer.firstName = customer.firstName;
    newCustomer.lastName = customer.lastName;
    newCustomer.birthDate = new Date(customer.birthday);

    this.restService.createCustomer(newCustomer).subscribe();
    console.log("REQUEST SEND: CreateCustomer");
    this.closeDialog();
  }

  getUpdateCustomers(customer:any) {
    this.restService.getAuthToken().subscribe(result => {
      this.token = result;
    });

    const newData: Customer = {
      id: '',
      gender: 'U',
      lastName: '',
      firstName: '',
      birthDate: new Date()
    };

    newData.id = customer.id;
    newData.gender = customer.gender;
    newData.firstName = customer.firstName;
    newData.lastName = customer.lastName;
    newData.birthDate = new Date(customer.birthDate);

    this.restService.updateCustomer(newData).subscribe();
    console.log("REQUEST SEND: UpdateCustomer");
    this.closeDialog();
  }

  getDeleteCustomers(id:string) {
    this.restService.getAuthToken().subscribe(result => {
      this.token = result;
    });

    this.restService.deleteCustomer(id).subscribe();
    this.getPrintCustomers();
  }

  ////// Calls Reading

  getCreateReading(reading:any) {
    this.restService.getAuthToken().subscribe(result => {
      this.token = result;
    });

    const customer: Customer = {
      id: '',
      gender: 'U',
      lastName: '',
      firstName: '',
      birthDate: new Date()
    };

    const newReading: Reading = {
      id: '',
      customer: customer,
      kindOfMeter: 'UNKNOWN',
      //@To-Do Fehler in Ausgabe meterId prüfen
      meterId: '',
      meterCount: 0,
      substitute: false,
      dateOfReading: new Date(),
      comment: ''
    };

    newReading.id = '';
    newReading.customer = reading.customer;
    newReading.kindOfMeter = reading.kindOfMeter;
    newReading.meterId = reading.meterId;
    newReading.meterCount = reading.meterCount;
    newReading.substitute = reading.substitute;
    newReading.dateOfReading = new Date(reading.dateOfReading);
    if (reading.comment === undefined) {
      reading.comment = '';
    }
    newReading.comment = reading.comment;

    console.log(newReading);

    this.restService.createReading(newReading).subscribe();
    console.log("REQUEST SEND");
    this.closeDialog();
    //this.getPrintReadings(newReading.customer);
  }

  getUpdateReading(reading:any) {
    this.restService.getAuthToken().subscribe(result => {
      this.token = result;
    });

    console.log("Reading in method: ", reading);

    this.restService.updateReading(reading).subscribe();
    //this.closeDialog();
  }

  getDeleteReading(reading:any) {
    this.restService.getAuthToken().subscribe(result => {
      this.token = result;
    });

    this.restService.deleteReading(reading.id).subscribe();
    //this.getPrintReadings(reading.customer);
  }

  getPrintReadings(customer:any) {
    this.restService.getAuthToken().subscribe(result => {
      this.token = result;
    });

    this.tempCustomer = customer;

    console.log("customer in component: ", customer);
    this.restService.getReading(customer.id).subscribe((result:Reading) => {
      this.readings = result;
      //console.log('results of customer:', this.readings);

      this.dataSourceReading = new MatTableDataSource<Reading>(Object.values(this.readings));
      this.dataSourceReading.paginator = this.paginator;
      this.dataSourceReading.sort = this.sort;
    });

    // remove message for empty readings table
    (<HTMLInputElement> document.getElementById("table-notification-empty")).setAttribute('style', 'display:none');

    // When Readings of a specific Customer are selected, enable New Readings button so Customer-ID is not empty
    (<HTMLInputElement> document.getElementById("btn-createReading")).disabled = false;
  }
}
