import { Injectable } from '@angular/core';
import { Customer } from './model/customer';
import { Reading } from './model/reading';

@Injectable({
  providedIn: 'root'
})
export class FileConverterService {

  constructor() { }

  /**
   * Converts a CSV string containing customer data into an array of Customer objects.
   * The CSV must include headers: UUID, Anrede, Vorname, Nachname, Geburtsdatum.
   * Anrede is mapped to a gender value ('M', 'W', 'U', 'D'), Geburtsdatum is parsed into a Date object.
   * Rows with missing fields or invalid data are skipped.
   *
   * @param {string} csvContent - The CSV-formatted string containing customer data.
   * @return {Customer[]} An array of Customer objects parsed from the CSV content. Returns an empty array if the CSV is invalid or empty.
   */
  csvToCustomer(csvContent: string): Customer[] {
    if (!csvContent) {
      console.error('CSV content is empty');
      return [];
    }

    const lines = csvContent.split('\n').map(line => line.trim());
    if (lines.length < 2) {
      console.error('CSV file must contain at least a header row and one data row');
      return [];
    }

    const headers = lines[0].split(',').map(header => header.trim());
    const requiredHeaders = ['UUID', 'Anrede', 'Vorname', 'Nachname', 'Geburtsdatum'];
    const missingHeaders = requiredHeaders.filter(header => !headers.includes(header));

    if (missingHeaders.length > 0) {
      console.error('Missing required headers:', missingHeaders);
      return [];
    }

    const headerIndices = {
      id: headers.indexOf('UUID'),
      salutation: headers.indexOf('Anrede'),
      firstName: headers.indexOf('Vorname'),
      lastName: headers.indexOf('Nachname'),
      birthDate: headers.indexOf('Geburtsdatum'),
    };

    const customers: Customer[] = [];
    for (let i = 1; i < lines.length; i++) {
      const line = lines[i];
      if (!line) continue;

      const values = line.split(',').map(value => value.trim());
      if (values.length < requiredHeaders.length) {
        console.warn(`Row ${i} has missing fields; skipping row:`, line);
        continue;
      }

      let gender: 'D' | 'M' | 'U' | 'W';
      switch (values[headerIndices.salutation]) {
        case 'Herr':
          gender = 'M';
          break;
        case 'Frau':
          gender = 'W';
          break;
        case 'k.A.':
          gender = 'U';
          break;
        default:
          gender = 'U';
          break;
      }

      const birthdateValue = values[headerIndices.birthDate];
      const birthDate = birthdateValue ? new Date(birthdateValue.split('.').reverse().join('-')) : undefined;

      const customer: Customer = {
        id: values[headerIndices.id],
        gender: gender,
        firstName: values[headerIndices.firstName],
        lastName: values[headerIndices.lastName],
        birthDate: birthDate,
      };

      customers.push(customer);
    }

    return customers;
  }

  /**
   * Converts a list of customers into a CSV formatted string.
   *
   * @param {Customer[]} customers - The array of customer objects to be converted. Each customer object should contain properties such as id, gender, firstName, lastName, and optionally a birthDate.
   * @return {string} A CSV formatted string containing the customer data. Returns an empty string if the input list is empty or undefined.
   */
  customersToCsv(customers: Customer[]): string {
    if (!customers || customers.length === 0) {
      console.error('Customer list is empty');
      return '';
    }

    const headers = ['UUID', 'Anrede', 'Vorname', 'Nachname', 'Geburtsdatum'];

    const genderToSalutation = (gender: 'D' | 'M' | 'U' | 'W'): string => {
      switch (gender) {
        case 'M':
          return 'Herr';
        case 'W':
          return 'Frau';
        case 'U':
        case 'D':
          return 'k.A.';
        default:
          return 'k.A.';
      }
    };

    const csvRows = [headers.join(',')];
    const formatBirthdate = (date?: Date): string => {
      return date ? date.toISOString().split('T')[0].split('-').reverse().join('.') : '';
    };


    for (const customer of customers) {
      const gender = customer.gender || 'U';
      const birthDate = formatBirthdate(customer.birthDate);

      const row = [
        customer.id,
        genderToSalutation(gender),
        customer.firstName,
        customer.lastName,
        birthDate
      ];

      csvRows.push(row.join(','));
    }


    return csvRows.join('\n');
  }

  /**
   * Converts a JSON string to a Customer object. The JSON string must adhere to the required structure.
   *
   * @param {string} json - The JSON string representing a customer.
   * @return {Customer} The Customer object created from the JSON string.
   * @throws {Error} If the input JSON string is empty or invalid.
   */
  jsonToCustomer(json: string): Customer {
    if (!json) {
      throw new Error('Input JSON is empty');
    }

    let parsedData;
    try {
      parsedData = JSON.parse(json);
    } catch (error) {
      throw new Error('Invalid JSON format');
    }

    // Map parsed fields to the Customer interface
    const customer: Customer = {
      id: parsedData.id || '',
      firstName: parsedData.firstName || '',
      lastName: parsedData.lastName || '',
      gender: parsedData.gender || 'U', // Default to 'U' if gender is not specified
      birthDate: Array.isArray(parsedData.birthDate) // Convert the year, month, day array to a Date object
        ? new Date(parsedData.birthDate[0], parsedData.birthDate[1] - 1, parsedData.birthDate[2])
        : undefined
    };

    return customer;
  }

  /**
   * Converts a Customer object to a JSON string representation.
   *
   * @param {Customer} customer - The customer object to be converted to JSON. Must not be null or undefined.
   * @return {string} A formatted JSON string representation of the customer object.
   * @throws {Error} If the customer object is null or undefined.
   */
  customerToJson(customer: Customer): string {
    if (!customer) {
      throw new Error('Customer object is null or undefined');
    }

    const json = {
      id: customer.id,
      firstName: customer.firstName,
      lastName: customer.lastName,
      gender: customer.gender,
      birthDate: customer.birthDate
        ? [
          customer.birthDate.getFullYear(),
          customer.birthDate.getMonth() + 1, // Month is 0-based in JavaScript
          customer.birthDate.getDate()
        ]
        : null // Include null if birthDate is undefined
    };

    return JSON.stringify(json, null, 2); // Pretty-print with 2 spaces
  }

  /**
   * Converts a JSON string representing an array of customer data into a list of Customer objects.
   *
   * @param {string} json - The JSON string to be converted, which must represent an array of customer objects.
   * @return {Customer[]} An array of Customer objects created from the JSON input.
   * @throws Will throw an error if the input JSON is empty, not in valid JSON format, or does not represent an array.
   */
  jsonToCustomerList(json: string): Customer[] {
    if (!json) {
      throw new Error('Input JSON is empty');
    }

    let parsedData;
    try {
      parsedData = JSON.parse(json);
    } catch (error) {
      throw new Error('Invalid JSON format');
    }

    if (!Array.isArray(parsedData)) {
      throw new Error('JSON must represent an array');
    }

    return parsedData.map((data) => ({
      id: data.id || '',
      firstName: data.firstName || '',
      lastName: data.lastName || '',
      gender: data.gender || 'U',
      birthDate: Array.isArray(data.birthDate)
        ? new Date(data.birthDate[0], data.birthDate[1] - 1, data.birthDate[2])
        : undefined,
    }));
  }

  /**
   * Converts a list of Customer objects into a JSON string.
   *
   * @param {Customer[]} customers - An array of Customer objects to be converted.
   * @return {string} A JSON string representing the list of customers.
   * @throws {Error} If the customers array is null, undefined, or empty.
   */
  customerListToJson(customers: Customer[]): string {
    if (!customers || customers.length === 0) {
      throw new Error('Customer list is empty');
    }

    const jsonList = customers.map((customer) => ({
      id: customer.id,
      firstName: customer.firstName,
      lastName: customer.lastName,
      gender: customer.gender,
      birthDate: customer.birthDate
        ? [
          customer.birthDate.getFullYear(),
          customer.birthDate.getMonth() + 1,
          customer.birthDate.getDate(),
        ]
        : null,
    }));

    return JSON.stringify(jsonList, null, 2);
  }

  //
  // READINGS
  //

  /**
   * Converts a CSV string containing meter reading data into an array of Reading objects.
   *
   * @param {string} csvContent The CSV content as a string. Each line should contain fields related to meter readings.
   * @param {'WATER' | 'POWER' | 'HEAT' | 'UNKNOWN'} kindOfMeter The type of meter measured in the readings (e.g., WATER, POWER, HEAT, or UNKNOWN).
   * @return {Reading[]} An array of Reading objects constructed from the provided CSV content. Returns an empty array if the CSV is invalid or missing required data.
   */
  csvToReadings(csvContent: string, kindOfMeter: 'WATER' | 'POWER' | 'HEAT' | 'UNKNOWN'): Reading[] {
    if (!csvContent) {
      console.error('CSV content is empty');
      return [];
    }

    const lines = csvContent.split('\n').map(line => line.trim());
    if (lines.length < 4) {
      console.error('Insufficient data in the CSV');
      return [];
    }

    const customerIdLine = lines.find(line => line.startsWith('"Kunde"'));
    if (!customerIdLine) {
      console.error('Customer ID is missing in the CSV');
      return [];
    }
    const customerId = customerIdLine.split(';')[1]?.replace(/"/g, '').trim();

    const meterIdLine = lines.find(line => line.startsWith('"Zählernummer"'));
    if (!meterIdLine) {
      console.error('Meter ID is missing in the CSV');
      return [];
    }
    const meterId = meterIdLine.split(';')[1]?.replace(/"/g, '').trim();

    const readingsStartIndex = lines.findIndex(line => line.startsWith('"Datum"'));
    if (readingsStartIndex === -1) {
      console.error('No data section found in the CSV');
      return [];
    }
    const readingsLines = lines.slice(readingsStartIndex + 1);

    const readings: Reading[] = [];
    for (const line of readingsLines) {
      if (!line || line === ';;') continue;

      const [dateStr, meterCountStr, comment = ''] = line.split(';').map(value => value.replace(/"/g, '').trim());

      const reading: Reading = {
        customer: {
          id: customerId || 'UNKNOWN',
          gender: 'U',
          lastName: undefined,
          firstName: undefined
        },
        kindOfMeter: kindOfMeter,
        meterId: meterId || 'UNKNOWN',
        meterCount: parseInt(meterCountStr || '0', 10),
        substitute: false,
        dateOfReading: dateStr ? new Date(dateStr.split('.').reverse().join('-')) : new Date(),
        comment: comment || '',
      };


      readings.push(reading);
    }

    return readings;
  }

  /**
   * Converts an array of readings into a CSV string format suitable for export.
   *
   * @param {Reading[]} readings - The list of readings containing details such as date of reading, meter count, and comments.
   * Each reading object should include customer information and other metadata like meter ID and kind of meter.
   * If the readings array is empty or undefined, an error message is logged, and an empty string is returned.
   *
   * @return {string} The generated CSV string, including metadata headers and readings information.
   * Returns an empty string if the readings array is missing or empty.
   */
  readingsToCsv(readings: Reading[]): string {
    if (!readings || readings.length === 0) {
      console.error('Reading list is empty');
      return '';
    }

    const customerId = readings[0]?.customer.id || 'UNKNOWN';
    const meterId = readings[0]?.meterId || 'UNKNOWN';
    const kindOfMeter = readings[0]?.kindOfMeter || 'UNKNOWN';

    const metadata = [
      `"Kunde";"${customerId}"`,
      `"Zählernummer";"${meterId}"`,
      ';;',
      ';;',
      '"Datum";"Zählerstand in kWh";"Kommentar"',
    ];

    const dataLines = readings.map(reading => {
      return [
        `"${reading.dateOfReading.toISOString().split('T')[0].split('-').reverse().join('.')}"`,
        `"${reading.meterCount}"`,
        `"${reading.comment}"`,
      ].join(';');
    });

    return metadata.concat(dataLines).join('\n');
  }

  /**
   * Converts a JSON string into a Reading object.
   *
   * @param {string} json - The JSON string representing the reading data.
   * @return {Reading} - The parsed Reading object constructed from the JSON string.
   * @throws {Error} - If the input JSON is empty or has an invalid format.
   */
  jsonToReading(json: string): Reading {
    if (!json) {
      throw new Error('Input JSON is empty');
    }

    let parsedData;
    try {
      parsedData = JSON.parse(json);
    } catch (error) {
      throw new Error('Invalid JSON format');
    }


    const reading: Reading = {
      customer: {
        id: parsedData.customer?.id || '',
        gender: 'U',
        lastName: undefined,
        firstName: undefined
      },
      kindOfMeter: parsedData.kindOfMeter || 'UNKNOWN',
      meterId: parsedData.meterId || 'UNKNOWN',
      meterCount: parsedData.meterCount || 0,
      substitute: Boolean(parsedData.substitute),
      dateOfReading: Array.isArray(parsedData.dateOfReading)
        ? new Date(parsedData.dateOfReading[0], parsedData.dateOfReading[1] - 1, parsedData.dateOfReading[2])
        : new Date(),
      comment: parsedData.comment || '',
    };

    return reading;
  }

  /**
   * Converts a Reading object into a JSON string representation.
   *
   * @param {Reading} reading - The Reading object to be converted to JSON.
   *                             This must contain details like customer information,
   *                             date of reading, meter details, and other related data.
   * @return {string} The JSON string representation of the provided Reading object.
   *                  Throws an error if the Reading object is null or undefined.
   */
  readingToJson(reading: Reading): string {
    if (!reading) {
      throw new Error('Reading object is null or undefined');
    }

    const json = {
      id: null,
      customer: {
        id: reading.customer.id,
      },
      dateOfReading: [
        reading.dateOfReading.getFullYear(),
        reading.dateOfReading.getMonth() + 1,
        reading.dateOfReading.getDate(),
      ],
      comment: reading.comment,
      meterId: reading.meterId,
      substitute: reading.substitute,
      meterCount: reading.meterCount,
      kindOfMeter: reading.kindOfMeter,
    };

    return JSON.stringify(json, null, 2);
  }

  /**
   * Converts a JSON string representing an array of readings into a list of Reading objects.
   *
   * @param {string} json - The JSON string representing an array of readings. It must be valid JSON and represent an array of objects.
   * @return {Reading[]} - An array of Reading objects parsed from the JSON string.
   * @throws {Error} - Throws if the input JSON is empty, invalid, or does not represent an array.
   */
  jsonToReadingList(json: string): Reading[] {
    if (!json) {
      throw new Error('Input JSON is empty');
    }

    let parsedData;
    try {
      parsedData = JSON.parse(json);
    } catch (error) {
      throw new Error('Invalid JSON format');
    }

    if (!Array.isArray(parsedData)) {
      throw new Error('JSON must represent an array');
    }

    const readings: Reading[] = parsedData.map((data) => ({
      customer: {
        id: data.customer?.id || '',
        gender: 'U',
        lastName: undefined,
        firstName: undefined
      },
      kindOfMeter: data.kindOfMeter || 'UNKNOWN',
      meterId: data.meterId || 'UNKNOWN',
      meterCount: data.meterCount || 0,
      substitute: Boolean(data.substitute),
      dateOfReading: Array.isArray(data.dateOfReading)
        ? new Date(data.dateOfReading[0], data.dateOfReading[1] - 1, data.dateOfReading[2])
        : new Date(),
      comment: data.comment || '',
    }));

    return readings;
  }

  /**
   * Converts a list of readings into a JSON string.
   *
   * @param {Reading[]} readings - An array of Reading objects containing details of the readings.
   * @returns {string} A formatted JSON string representation of the reading list.
   * @throws {Error} If the readings list is empty or null.
   */
  readingListToJson(readings: Reading[]): string {
    if (!readings || readings.length === 0) {
      throw new Error('Reading list is empty');
    }

    const jsonList = readings.map((reading) => ({
      id: null,
      customer: {
        id: reading.customer.id,
      },
      dateOfReading: [
        reading.dateOfReading.getFullYear(),
        reading.dateOfReading.getMonth() + 1,
        reading.dateOfReading.getDate(),
      ],
      comment: reading.comment,
      meterId: reading.meterId,
      substitute: reading.substitute,
      meterCount: reading.meterCount,
      kindOfMeter: reading.kindOfMeter,
    }));

    return JSON.stringify(jsonList, null, 2);
  }


  // XML

  /**
   * Converts a list of Customer objects into an XML formatted string.
   *
   * @param {Customer[]} customers - The array of customer objects to be converted.
   * @return {string} An XML formatted string representing the customer data.
   */
  customersToXml(customers: Customer[]): string {
    if (!customers || customers.length === 0) {
      console.error('Customer list is empty');
      return '';
    }

    const root = document.implementation.createDocument(null, 'Customers', null);
    const rootElement = root.documentElement;

    customers.forEach(customer => {
      const customerElement = root.createElement('Customer');

      const idElement = root.createElement('UUID');
      idElement.textContent = customer.id.toString();
      customerElement.appendChild(idElement);

      const salutationElement = root.createElement('Anrede');
      salutationElement.textContent = customer.gender === 'M' ? 'Herr' : customer.gender === 'W' ? 'Frau' : 'k.A.';
      customerElement.appendChild(salutationElement);

      const firstNameElement = root.createElement('Vorname');
      firstNameElement.textContent = customer.firstName?.toString() ?? '';
      customerElement.appendChild(firstNameElement);

      const lastNameElement = root.createElement('Nachname');
      lastNameElement.textContent = customer.lastName?.toString() ?? '';
      customerElement.appendChild(lastNameElement);

      const birthdateElement = root.createElement('Geburtsdatum');
      birthdateElement.textContent = customer.birthDate
        ? customer.birthDate.toISOString().split('T')[0]
        : '';
      customerElement.appendChild(birthdateElement);

      rootElement.appendChild(customerElement);
    });

    const serializer = new XMLSerializer();
    return serializer.serializeToString(root);
  }

  /**
   * Converts an XML string into an array of Customer objects.
   *
   * @param {string} xml - The XML string representing customers.
   * @return {Customer[]} An array of Customer objects parsed from the XML string.
   */
  xmlToCustomer(xml: string): Customer[] {
    if (!xml) {
      console.error('Input XML is empty');
      return [];
    }

    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(xml, 'application/xml');
    const customers: Customer[] = [];

    const customerNodes = xmlDoc.getElementsByTagName('Customer');
    for (let i = 0; i < customerNodes.length; i++) {
      const customerNode = customerNodes[i];

      const id = customerNode.getElementsByTagName('UUID')[0]?.textContent || '';
      const salutation = customerNode.getElementsByTagName('Anrede')[0]?.textContent || '';
      const firstName = customerNode.getElementsByTagName('Vorname')[0]?.textContent || '';
      const lastName = customerNode.getElementsByTagName('Nachname')[0]?.textContent || '';
      const birthdateText = customerNode.getElementsByTagName('Geburtsdatum')[0]?.textContent || '';
      const birthDate = birthdateText ? new Date(birthdateText) : undefined;

      const gender: 'M' | 'W' | 'U' | 'D' =
        salutation === 'Herr'
          ? 'M'
          : salutation === 'Frau'
            ? 'W'
            : 'U';

      customers.push({
        id,
        firstName,
        lastName,
        gender,
        birthDate
      });
    }

    return customers;
  }

  /**
   * Converts a list of Reading objects into an XML formatted string.
   *
   * @param {Reading[]} readings - The array of reading objects to be converted.
   * @return {string} An XML formatted string representing the reading data.
   */
  readingsToXml(readings: Reading[]): string {
    if (!readings || readings.length === 0) {
      console.error('Reading list is empty');
      return '';
    }

    const root = document.implementation.createDocument(null, 'Readings', null);
    const rootElement = root.documentElement;

    readings.forEach(reading => {
      const readingElement = root.createElement('Reading');

      const customerIdElement = root.createElement('Kunde');
      customerIdElement.textContent = reading.customer.id.toString();
      readingElement.appendChild(customerIdElement);

      const meterIdElement = root.createElement('Zählernummer');
      meterIdElement.textContent = reading.meterId;
      readingElement.appendChild(meterIdElement);

      const dateElement = root.createElement('Datum');
      dateElement.textContent = reading.dateOfReading.toISOString().split('T')[0];
      readingElement.appendChild(dateElement);

      const meterCountElement = root.createElement('Zählerstand');
      meterCountElement.textContent = reading.meterCount.toString();
      readingElement.appendChild(meterCountElement);

      const kindOfMeterElement = root.createElement('ArtDesZählers');
      kindOfMeterElement.textContent = reading.kindOfMeter;
      readingElement.appendChild(kindOfMeterElement);

      const commentElement = root.createElement('Kommentar');
      commentElement.textContent = reading.comment || '';
      readingElement.appendChild(commentElement);

      rootElement.appendChild(readingElement);
    });

    const serializer = new XMLSerializer();
    return serializer.serializeToString(root);
  }

  /**
   * Converts an XML string into an array of Reading objects.
   *
   * @param {string} xml - The XML string representing readings.
   * @return {Reading[]} An array of Reading objects parsed from the XML string.
   */
  xmlToReading(xml: string): Reading[] {
    if (!xml) {
      console.error('Input XML is empty');
      return [];
    }

    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(xml, 'application/xml');
    const readings: Reading[] = [];

    const readingNodes = xmlDoc.getElementsByTagName('Reading');
    for (let i = 0; i < readingNodes.length; i++) {
      const readingNode = readingNodes[i];

      const customerId = readingNode.getElementsByTagName('Kunde')[0]?.textContent || '';
      const meterId = readingNode.getElementsByTagName('Zählernummer')[0]?.textContent || '';
      const dateOfReadingText = readingNode.getElementsByTagName('Datum')[0]?.textContent || '';
      const meterCount = parseInt(readingNode.getElementsByTagName('Zählerstand')[0]?.textContent || '0', 10);
      const kindOfMeter = readingNode.getElementsByTagName('ArtDesZählers')[0]?.textContent || 'UNKNOWN';
      const comment = readingNode.getElementsByTagName('Kommentar')[0]?.textContent || '';

      readings.push({
        customer: {
          id: customerId,
          gender: 'U',
          firstName: undefined,
          lastName: undefined,
        },
        meterId,
        kindOfMeter: kindOfMeter as 'WATER' | 'POWER' | 'HEAT' | 'UNKNOWN',
        dateOfReading: new Date(dateOfReadingText),
        meterCount,
        substitute: false,
        comment,
      });
    }

    return readings;
  }




}
