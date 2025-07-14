import { TestBed } from '@angular/core/testing';

import { FileConverterService } from './file-converter.service';

describe('FileConverterService', () => {
  let service: FileConverterService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FileConverterService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
