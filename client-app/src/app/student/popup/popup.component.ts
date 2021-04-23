import {Component, Inject} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import { DialogPopup } from '../students-cont.component';

@Component({
  selector: 'your-dialog',
  template: '{{ data.name }}',
})
export class Popup {
  constructor(public dialogRef: MatDialogRef<Popup>,
    @Inject(MAT_DIALOG_DATA) public data: DialogPopup){ }
}