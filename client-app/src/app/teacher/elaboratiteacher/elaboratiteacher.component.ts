import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import {Assignment} from '../../model/assignment.model';
import {Paper} from '../../model/paper.model';
import {PaperStatus} from '../../model/paperStatus.model';
import {PaperStatusTime} from '../../model/paperStatusTime.model';
import {StudentDTO} from '../../model/studentDTO.model'
import { ConsegnadialogComponent } from '../consegnadialog/consegnadialog.component';

class PaperWithHistory {
  paper: Paper;
  creator: StudentDTO;
  history: PaperStatusTime[];
}

class AssignmentWithPapers {
  assignment: Assignment;
  papersWithHistory: PaperWithHistory[];
}

@Component({
  selector: 'app-elaboratiteacher',
  templateUrl: './elaboratiteacher.component.html',
  styleUrls: ['./elaboratiteacher.component.css']
})
export class ElaboratiteacherComponent implements OnInit {

  assignmentWithPapers: AssignmentWithPapers[] = [];

  @Input('assignmentWithPapers')
  set _assignmentWithPapers (assignmentWithPapers: AssignmentWithPapers[]) {
    this.assignmentWithPapers = assignmentWithPapers;
  }

  



  constructor(public matDialog : MatDialog) { }

  ngOnInit(): void {
  }


  createconsegna() 
  {
    this.matDialog.open(ConsegnadialogComponent);

  }

}
