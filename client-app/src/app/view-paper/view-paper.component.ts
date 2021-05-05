import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { PaperStatusTime } from 'app/model/paperStatusTime.model';
import { environment } from 'environments/environment';

@Component({
  selector: 'app-view-paper',
  templateUrl: './view-paper.component.html',
  styleUrls: ['./view-paper.component.css']
})
export class ViewPaperComponent implements OnInit {

  constructor(@Inject(MAT_DIALOG_DATA) public data: any) { }

  history: PaperStatusTime[] = [];
  id: number;
  currentStatus: String;
  editable: Boolean;
  ratable: Boolean;
  step1: Boolean = true;
  goRate: Boolean = false;
  goReview: Boolean = false;
  teacher: Boolean = false;
  student: Boolean = false;
  rates: number[] = [];
  revisioni: Boolean = false;
  currentReview: String;
  currentSol: String = "";
  imageViewer: Boolean = false;

  ngOnInit(): void {
    this.id = this.data.id;
    this.history = this.data.history;
    this.currentStatus = this.data.currentStatus;
    this.editable = this.data.editable;
    this.ratable = (this.currentStatus == "RIVISTO" && this.editable == false);
    for(let i = 1; i<=30; i++) {
      this.rates.push(i);
    }
    this.teacher = this.data.teacher;
    this.student = this.data.student;
  }

  displayDate(date: Date) {
    var newDate: Date = new Date(date);
    return newDate.getDate() + "/" + (newDate.getMonth()+1) + "/" + newDate.getFullYear();
  }

  displayTime(date: Date) {
    var newDate: Date = new Date(date);
    return newDate.getHours() + ":" + newDate.getMinutes();
  }

  clickrate(){
    this.step1 = false;
    this.goRate = true;
  }

  clickreview(){
    this.step1 = false;
    this.goReview = true;
  }

  viewReview(content: String) {
    this.currentReview = content;
    this.step1 = false;
    this.revisioni = true;
  }

  exitReview() {
    this.step1 = true;
    this.revisioni = false;
  }

  goViewSol (relativePath: String) {
    var newImg: String = environment.baseimage + "/" + relativePath;
    this.currentSol = newImg;
    this.imageViewer = true;
  }

  exitImageViewer() {
    this.imageViewer = false;
  }
}
