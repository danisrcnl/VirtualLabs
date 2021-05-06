import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Assignment } from 'app/model/assignment.model';
import { environment } from 'environments/environment';

@Component({
  selector: 'app-view-assignment',
  templateUrl: './view-assignment.component.html',
  styleUrls: ['./view-assignment.component.css']
})
export class ViewAssignmentComponent implements OnInit {

  constructor(@Inject(MAT_DIALOG_DATA) public data: any) { }

  currentAssignment: Assignment;
  assPath: String;


  ngOnInit(): void {
    this.currentAssignment = this.data.assignment;
    this.assPath = environment.baseimage + "/" + this.currentAssignment.content;
  }

}
