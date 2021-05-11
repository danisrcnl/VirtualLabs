import { THIS_EXPR } from '@angular/compiler/src/output/output_ast';
import { parseI18nMeta } from '@angular/compiler/src/render3/view/i18n/meta';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatChip, MatChipList } from '@angular/material/chips';
import { AssignmentService } from 'app/services/assignment.service';
import { StudentService } from 'app/services/student.service';
import { Observable, of } from 'rxjs';
import {Assignment} from '../../model/assignment.model';
import {Paper} from '../../model/paper.model';
import {PaperStatus} from '../../model/paperStatus.model';
import {PaperStatusTime} from '../../model/paperStatusTime.model';
import {StudentDTO} from '../../model/studentDTO.model';
import {AssignmentWithPapers, PaperWithHistory} from '../../model/assignmentsupport.model';

import { environment } from 'environments/environment';
import {CreatePaperComponent} from 'app/create-paper/create-paper.component';
import { ViewPaperComponent } from 'app/view-paper/view-paper.component';

import { ViewAssignmentComponent } from 'app/view-assignment/view-assignment.component';

@Component({
  selector: 'app-elaboratistudent',
  templateUrl: './elaboratistudent.component.html',
  styleUrls: ['./elaboratistudent.component.css']
})
export class ElaboratistudentComponent implements OnInit {

  assignmentWithPapers: AssignmentWithPapers[] = [];
  viewingPapers: PaperWithHistory[] = [];
  filteredViewingPapers: PaperWithHistory[] = [];
  status: String[] = ["null", "letti", "consegnati", "rivisti", "valutati"];
  selection: String[] = [];
  baseimage = environment.baseimage;

  @Output() setcontentpaper = new EventEmitter<any>();

  @Output() lettaconsegna = new EventEmitter<any>();



  @Input('assignmentWithPapers')
  set _assignmentWithPapers (assignmentWithPapers: AssignmentWithPapers[]) {
    this.assignmentWithPapers = assignmentWithPapers;
  }

  


  constructor(private assignmentService: AssignmentService, private studentService: StudentService, public dialog: MatDialog) { }

  ngOnInit(): void {
    
  }

  ordered (awp: AssignmentWithPapers[]) {
    var newArr: AssignmentWithPapers[] = [];
    newArr = awp.sort((a1, a2) => {
      if(a1.assignment.creationDate > a2.assignment.creationDate)
        return 1;
      if(a1.assignment.creationDate < a2.assignment.creationDate)
        return -1;
      else
        return 0;
    });
    return newArr;
  }

  setView (papersWithHistory: PaperWithHistory[]) {
    this.viewingPapers = papersWithHistory;
    var rv: PaperWithHistory[] = [];
    this.viewingPapers.forEach(p => {
      if(p.paper.currentStatus.toString() == "RIVISTO" ||
      p.paper.currentStatus.toString() == "CONSEGNATO" ||
      p.paper.currentStatus.toString() == "VALUTATO")
        rv.push(p);
    });
    this.filteredViewingPapers = rv;
  }

  getValids (papersWithHistory: PaperWithHistory[]) {
    var rv: PaperWithHistory[] = [];
    
    papersWithHistory.forEach(p => {
      if(p.paper.currentStatus.toString() == "RIVISTO" ||
      p.paper.currentStatus.toString() == "CONSEGNATO" ||
      p.paper.currentStatus.toString() == "VALUTATO")
        rv.push(p);
    });
    
    return rv;
  }



  hasSameStatus (status: PaperStatus, value: String) {


    if (status.toString() == "CONSEGNATO" && value == " consegnati ")
      return true;

    if (status.toString() == "LETTO" && value == " letti ")
      return true;

    if (status.toString() == "RIVISTO" && value == " rivisti ")
      return true;

    if (status.toString() == "VALUTATO" && value == " valutati ")
      return true;

    if (status.toString() == "NULL" && value == " null ")
      return true;

    return false;

  }

  viewPaper (id: number, history: PaperStatusTime[], currentStatus: String, editable: Boolean, assignmentId: number, assignment: Assignment, mark: number) {
    const dialogRef = this.dialog.open(ViewPaperComponent, {
      width: '800px',
      data: {
        id: id,
        history: history,
        currentStatus: currentStatus,
        editable: editable,
        teacher: false,
        student: true,
        assignment: assignment,
        mark: mark
      }
    });
  
    dialogRef.afterClosed().subscribe(edit => {
      if(edit == true)
        this.createPaper(assignmentId);
    });
  }

  createPaper (id: number) {
    const dialogRef = this.dialog.open(CreatePaperComponent, {
      width: '600px',
      data: {
        id: id
      }
    });

    dialogRef.afterClosed().subscribe( data => {

     this.setcontentpaper.emit({content: data, id: id});

    })


  }

  isNull (papersWithHistory: PaperWithHistory[]) {
    if(papersWithHistory[0] == undefined)
      return false;
    return this.hasSameStatus(papersWithHistory[0].paper.currentStatus, " null ");
  }

  isRead (papersWithHistory: PaperWithHistory[]) {
    if(papersWithHistory[0] == undefined)
      return false;
    return this.hasSameStatus(papersWithHistory[0].paper.currentStatus, " letti ");
  }

  viewAssignment (assignment: Assignment) {
    const dialogRef = this.dialog.open(ViewAssignmentComponent, {
      width: '600px',
      data: {
        assignment: assignment
      }
    });

     dialogRef.afterClosed().subscribe(data => {

  this.lettaconsegna.emit(assignment);


 })
    

  }

  displayDate(date: Date) {
    var newDate: Date = new Date(date);
    var dd = String(newDate.getDate()).padStart(2, '0');
    var mm = String(newDate.getMonth() + 1).padStart(2, '0'); //January is 0!
    var yyyy = newDate.getFullYear();
    return dd + "/" + mm + "/" + yyyy;
  }

  isExpired(date: Date) {
    var expiry = new Date(date);
    var now = new Date();
    if(now > expiry)
      return true;
    else
      return false;
  }

  classOf(date: Date){
    if(this.isExpired(date))
      return "expired";
    else return "notExpired";
  }
    
}


