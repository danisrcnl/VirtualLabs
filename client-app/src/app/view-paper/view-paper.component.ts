import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { PaperStatusTime } from 'app/model/paperStatusTime.model';

@Component({
  selector: 'app-view-paper',
  templateUrl: './view-paper.component.html',
  styleUrls: ['./view-paper.component.css']
})
export class ViewPaperComponent implements OnInit {

  constructor(@Inject(MAT_DIALOG_DATA) public data: any) { }

  history: PaperStatusTime[] = [];
  id: number;

  ngOnInit(): void {
    this.id = this.data.id;
    this.history = this.data.history;
  }

  displayDate(date: Date) {
    var newDate: Date = new Date(date);
    return newDate.getDay() + "/" + newDate.getMonth() + "/" + newDate.getFullYear();
  }

  displayTime(date: Date) {
    var newDate: Date = new Date(date);
    return newDate.getHours() + ":" + newDate.getMinutes();
  }
}
