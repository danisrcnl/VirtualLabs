import { Component, Input, OnInit } from '@angular/core';
import {Assignment} from '../../model/assignment.model';
import {Paper} from '../../model/paper.model';
import {PaperStatus} from '../../model/paperStatus.model';
import {PaperStatusTime} from '../../model/paperStatusTime.model';

interface PaperWithHistory {
  paper: Paper;
  history: PaperStatusTime[];
}

interface AssignmentWithPapers {
  assignment: Assignment;
  papers: Paper[];
}


@Component({
  selector: 'app-elaboratiteacher',
  templateUrl: './elaboratiteacher.component.html',
  styleUrls: ['./elaboratiteacher.component.css']
})
export class ElaboratiteacherComponent implements OnInit {

  assignments: Assignment[];

  @Input('assignments')
  set _assignments (assignments: Assignment[]) {
    this.assignments = assignments;
  }

  



  constructor() { }

  ngOnInit(): void {
  }

}
