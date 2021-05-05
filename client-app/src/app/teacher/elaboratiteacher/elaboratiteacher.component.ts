import { THIS_EXPR } from '@angular/compiler/src/output/output_ast';
import { parseI18nMeta } from '@angular/compiler/src/render3/view/i18n/meta';
import { Component, Input, OnInit, Output, EventEmitter  } from '@angular/core';
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
import { ViewPaperComponent } from 'app/view-paper/view-paper.component';
import { ConsegnadialogComponent } from '../consegnadialog/consegnadialog.component';
import { A11yModule } from '@angular/cdk/a11y';


export interface DialogConsegna {

giorno : number;
mese : String;
anno : number;
content : String;

}

@Component({
  selector: 'app-elaboratiteacher',
  templateUrl: './elaboratiteacher.component.html',
  styleUrls: ['./elaboratiteacher.component.css']
})
export class ElaboratiteacherComponent implements OnInit {

  assignmentWithPapers: AssignmentWithPapers[] = [];
  viewingPapers: PaperWithHistory[] = [];
  filteredViewingPapers: PaperWithHistory[] = [];
  status: String[] = ["null", "letti", "consegnati", "rivisti", "valutati"];
  selection: String[] = [];
  consegnadata = {} as DialogConsegna;


  @Output() addconsegna = new EventEmitter<DialogConsegna>();

  @Input('assignmentWithPapers')
  set _assignmentWithPapers (assignmentWithPapers: AssignmentWithPapers[]) {
    this.assignmentWithPapers = assignmentWithPapers;
    console.log(this.assignmentWithPapers);
  }

  constructor (private assignmentService: AssignmentService, private studentService: StudentService, public dialog: MatDialog) { }

  ngOnInit (): void {   
    
  }

  addAssignment () {

  }

  setView (papersWithHistory: PaperWithHistory[]) {
    this.viewingPapers = papersWithHistory;
    this.filteredViewingPapers = this.viewingPapers;
    console.log(this.viewingPapers);
  }

  toggle(chip: MatChip) {
    
    var newSelection: String[] = [];

    if(chip.selected){

      chip.deselect();
      this.selection.forEach(status => {
        if(status != chip.value)
          newSelection.push(status);
      })
      this.selection = newSelection;
      
    } 

    else {
      this.selection = [];
      chip.toggleSelected();
      this.selection.push(chip.value);
    }

    console.log(this.selection);
    this.filter(this.selection);
    console.log(this.filteredViewingPapers);

    
  }

  clearChips () {
    this.selection = [];
  }

  filter (selection: String[]) {
    var keep: Boolean = false;
    var newArray: PaperWithHistory[] = [];

    if(selection.length == 0) {
      this.filteredViewingPapers = this.viewingPapers;
      return;
    }


 
    this.viewingPapers.forEach(p => {
      selection.forEach(s => {
        if(this.hasSameStatus(p.paper.currentStatus, s))
          keep = true;
      });

      if(keep) {
        newArray.push(p);
        keep = false;
      }

    });
    this.filteredViewingPapers = newArray;
  }


  createconsegna() 
  {
    const dialogRef = this.dialog.open(ConsegnadialogComponent, {
      width: '400px',
      data: {
        
      }
    });
  
    dialogRef.afterClosed().subscribe(data => {

      console.log(data);

      this.consegnadata.anno = data.anno;
      this.consegnadata.giorno = data.giorno;
      this.consegnadata.mese = data.mese;
      this.consegnadata.content = data.content;
      
      this.addconsegna.emit(this.consegnadata);
      

    });

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

  viewPaper (id: number, history: PaperStatusTime[], currentStatus: String, editable: Boolean) {
    const dialogRef = this.dialog.open(ViewPaperComponent, {
      width: '600px',
      data: {
        id: id,
        history: history,
        currentStatus: currentStatus,
        editable: editable,
        teacher: true,
        student: false
      }
    });
  
    dialogRef.afterClosed().subscribe(result => {
    });
  }
    
}
