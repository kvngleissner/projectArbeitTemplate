import {Component} from '@angular/core';
import {NgOptimizedImage} from '@angular/common';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatMenuModule} from '@angular/material/menu';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {Customer} from "../model/customer";
import {FileConverterService} from "../file-converter.service";
import {Reading} from "../model/reading";


//additional <mat-menu>
@Component({
  selector: 'app-header',
  template: `
    <mat-toolbar color="primary" class="header-bar">
      <div class="header-content">
        <img class="brand-logo" ngSrc="icon-circle.png" alt="logo" height="60" width="60">
        <span class="title">Dashboard</span>
      </div>

      <span class="spacer"></span>

      <button mat-icon-button [matMenuTriggerFor]="importMenu" matTooltip="Import Data" class="import-button">
        <mat-icon>file_upload</mat-icon>
      </button>
      <mat-menu #importMenu="matMenu">
        <button mat-menu-item (click)="importData('customers')">
          <mat-icon>group</mat-icon>
          Customers
        </button>
        <button mat-menu-item (click)="importData('readings')">
          <mat-icon>post_add</mat-icon>
          Readings
        </button>
      </mat-menu>

      <button mat-icon-button [matMenuTriggerFor]="exportMenu" matTooltip="Export Data" class="export-button">
        <mat-icon>file_download</mat-icon>
      </button>
      <mat-menu #exportMenu="matMenu">
        <button mat-menu-item [matMenuTriggerFor]="customerMenu">
          Customers
          <mat-menu #customerMenu>
            <button mat-menu-item (click)="exportData('csv', 'customers')">
              <mat-icon>description</mat-icon>
              CSV
            </button>
            <button mat-menu-item (click)="exportData('xml', 'customers')">
              <mat-icon>code</mat-icon>
              XML
            </button>
            <button mat-menu-item (click)="exportData('json', 'customers')">
              <mat-icon>data_object</mat-icon>
              JSON
            </button>
          </mat-menu>
        </button>
        <button mat-menu-item [matMenuTriggerFor]="readingMenu">
          Readings
          <mat-menu #readingMenu>
            <button mat-menu-item (click)="exportData('csv', 'readings')">
              <mat-icon>description</mat-icon>
              CSV
            </button>
            <button mat-menu-item (click)="exportData('xml', 'readings')">
              <mat-icon>code</mat-icon>
              XML
            </button>
            <button mat-menu-item (click)="exportData('json', 'readings')">
              <mat-icon>data_object</mat-icon>
              JSON
            </button>
          </mat-menu>
        </button>
      </mat-menu>

      <button mat-icon-button matTooltip="Log Out" class="logout-button">
        <mat-icon>logout</mat-icon>
      </button>
    </mat-toolbar>
  `,
  styleUrls: ['./header.component.scss'],
  imports: [
    NgOptimizedImage,
    MatTooltipModule,
    MatMenuModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule
  ]
})

export class HeaderComponent {
  customers: Customer[] = [
    {id: '1', gender: 'M', lastName: 'Doe', firstName: 'John', birthDate: new Date('1990-01-01')},
    {id: '2', gender: 'W', lastName: 'Smith', firstName: 'Jane', birthDate: new Date('1985-05-15')},
  ];

  customerOne: Customer = {id: '1', gender: 'M', lastName: 'Doe', firstName: 'John', birthDate: new Date('1990-01-01')};
  customerTwo: Customer = {
    id: '2',
    gender: 'W',
    lastName: 'Smith',
    firstName: 'Jane',
    birthDate: new Date('1985-05-15')
  }
  readings: Reading[] = [
    {
      id: '1',
      customer: this.customerOne,
      kindOfMeter: 'HEAT',
      meterId: '1',
      meterCount: 30,
      substitute: false,
      dateOfReading: new Date('2025-04-24'),
      comment: "no comment"
    },
    {
      id: '2',
      customer: this.customerTwo,
      kindOfMeter: 'WATER',
      meterId: '2',
      meterCount: 500,
      substitute: false,
      dateOfReading: new Date('2025-04-24'),
      comment: "no comment"
    }
  ]

  constructor(private fileConverterService: FileConverterService) {
  }

  onFileSelect(fileInput: HTMLInputElement): void {
    if (fileInput.files && fileInput.files.length > 0) {
      const file = fileInput.files[0];
      console.log("File selected:", file.name);

      const reader = new FileReader();

      reader.onload = () => {
        const content = reader.result as string;

        const readings: Reading[] = this.fileConverterService.jsonToReadingList(content);
        console.log(readings);
      };

      reader.onerror = (error) => {
        console.error("Error reading file:", error);
      };

      reader.readAsText(file);
    } else {
      console.log("No file selected");
    }
  }

  exportData(filetype: string, tableName: string): void {
    console.log(`Selected format: ` + filetype);
    switch (tableName) {
      case 'customers':
        switch (filetype) {
          case 'csv':
            this.exportCustomersAsCsv();
            break;
          case 'xml':
            this.exportCustomersAsXml();
            break;
          case 'json':
            this.exportCustomersAsJson();
            break;
          default:
            console.error('Unsupported export format selected');
        }
        break;

      case 'readings':
        switch (filetype) {
          case 'csv':
            this.exportReadingsAsCsv();
            break;
          case 'xml':
            this.exportReadingsAsXml();
            break;
          case 'json':
            this.exportReadingsAsJson();
            break;
          default:
            console.error('Unsupported export format selected');
        }
        break;
    }
  }

//renamed by me

  exportCustomersAsCsv(): void {
    const csvCustomerData = this.fileConverterService.customersToCsv(this.customers);
    this.downloadFile(csvCustomerData, 'customers.csv', 'text/csv');
  }

  exportCustomersAsXml(): void {
    const xmlCustomerData = this.fileConverterService.customersToXml(this.customers);
    this.downloadFile(xmlCustomerData, 'customers.xml', 'application/xml');
  }

  exportCustomersAsJson(): void {
    const jsonCustomerData = this.fileConverterService.customerListToJson(this.customers);
    this.downloadFile(jsonCustomerData, 'customers.json', 'application/json');
  }

  exportReadingsAsCsv(): void {
    const csvReadingData = this.fileConverterService.readingsToCsv(this.readings);
    this.downloadFile(csvReadingData, 'readings.csv', 'text/csv');
  }

  exportReadingsAsXml(): void {
    const xmlReadingData = this.fileConverterService.readingsToXml(this.readings);
    this.downloadFile(xmlReadingData, 'readings.xml', 'application/xml');
  }

  exportReadingsAsJson(): void {
    const jsonReadingData = this.fileConverterService.readingListToJson(this.readings);
    this.downloadFile(jsonReadingData, 'readings.json', 'application/json');
  }

  private downloadFile(data: string, filename: string, contentType: string): void {
    const blob = new Blob([data], {type: contentType});
    const link = document.createElement('a');

    link.href = URL.createObjectURL(blob);
    link.download = filename;
    link.click();

    URL.revokeObjectURL(link.href);
  }

  importData(dataType: string): void {
    console.log('Import Data Type:', dataType);

    const fileInput = document.createElement('input');
    fileInput.type = 'file';
    fileInput.accept = '.csv,.json,.xml';

    fileInput.addEventListener('change', (event: any) => {
      const file = event.target.files[0];

      if (file) {
        const reader = new FileReader();

        reader.onload = () => {
          const fileContent = reader.result;
          const fileExtension = this.getFileExtension(file.name);

          if (fileContent) {
            if (dataType === 'customers') {
              this.handleCustomerImport(fileContent, fileExtension);
            } else if (dataType === 'readings') {
              this.handleReadingImport(fileContent, fileExtension);
            } else {
              console.error('Unknown data type:', dataType);
            }
          } else {
            console.error('File content is empty!');
          }
        };

        reader.readAsText(file);
      }
    });

    fileInput.click();
  }

  private getFileExtension(fileName: string): string {
    return fileName.split('.').pop()?.toLowerCase() || '';
  }

  private handleCustomerImport(fileContent: string | ArrayBuffer, fileExtension: string): void {
    console.log('Handling customer import for file type:', fileExtension);
    let importedCustomers: Customer[] = [];
    if (typeof fileContent === 'string') {
      switch (fileExtension) {
        case 'csv':
          console.log('Parsing customer CSV file...');
          importedCustomers = this.fileConverterService.csvToCustomer(fileContent);
          break;

        case 'json':
          console.log('Parsing customer JSON file...');
          importedCustomers = this.fileConverterService.jsonToCustomerList(fileContent);
          break;

        case 'xml':
          console.log('Parsing customer XML file...');
          importedCustomers = this.fileConverterService.xmlToCustomer(fileContent);
          break;

        default:
          console.error('Unsupported file type for customers:', fileExtension);
      }
    } else {
      console.error('Invalid file content for customers!');
    }
    this.customers = importedCustomers;
    console.log(importedCustomers);
  }

  private handleReadingImport(fileContent: string | ArrayBuffer, fileExtension: string): void {
    console.log('Handling reading import for file type:', fileExtension);
    let importedReadings: Reading[] = [];
    if (typeof fileContent === 'string') {
      switch (fileExtension) {
        case 'csv':
          console.log('Parsing readings CSV file...');
          importedReadings = this.fileConverterService.csvToReadings(fileContent, 'POWER');
          console.log(
            'Parsing readings CSV file...'
          )
          break;

        case 'json':
          console.log('Parsing readings JSON file...');
          importedReadings = this.fileConverterService.jsonToReadingList(fileContent);
          break;

        case 'xml':
          console.log('Parsing readings XML file...');
          importedReadings = this.fileConverterService.xmlToReading(fileContent);
          console.log(
            'Parsing readings XML file...'
          )
          break;

        default:
          console.error('Unsupported file type for readings:', fileExtension);
      }
    } else {
      console.error('Invalid file content for readings!');
    }

    console.log(importedReadings);
  }

}
