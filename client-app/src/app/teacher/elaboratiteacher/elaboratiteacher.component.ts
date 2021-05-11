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
content : FormData

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

  @Output() addvalutazione = new EventEmitter<any>();

  @Output() addsoluzione = new EventEmitter<any>();

  @Output() addconsegna = new EventEmitter<DialogConsegna>();

  @Input('assignmentWithPapers')
  set _assignmentWithPapers (assignmentWithPapers: AssignmentWithPapers[]) {
    this.assignmentWithPapers = assignmentWithPapers;
   
  }

  constructor (private assignmentService: AssignmentService, private studentService: StudentService, public dialog: MatDialog) { }

  ngOnInit (): void {   
    
  }

  addAssignment () {

  }

  setView (papersWithHistory: PaperWithHistory[]) {
    this.viewingPapers = papersWithHistory;
    this.filteredViewingPapers = this.viewingPapers;
   
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

    this.filter(this.selection);
   

    
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

    
      var file = data.formData;
      var formValue = data.consegnaForm;
      this.consegnadata.anno = formValue.get("anno").value;
      this.consegnadata.giorno = formValue.get("giorno").value;
      this.consegnadata.mese = formValue.get("mese").value;
      this.consegnadata.content = file;
      this.addconsegna.emit(this.consegnadata);
      

    });

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

  viewPaper (id: number, history: PaperStatusTime[], currentStatus: String, editable: Boolean, assignmentId : number, assignment: Assignment) {
    const dialogRef = this.dialog.open(ViewPaperComponent, {
      width: '800px',
      data: {
        id: id,
        history: history,
        currentStatus: currentStatus,
        editable: editable,
        teacher: true,
        student: false,
        assignmentId: assignmentId,
        assignment: assignment
      }
    });
  
    dialogRef.afterClosed().subscribe(result => {

        if(result.voto!= undefined)
        this.addvalutazione.emit({voto : result.voto, paperid : id});
        if(result.soluzione!=undefined)
        this.addsoluzione.emit({res: result, paperid : id, assid : assignmentId});
        
    });
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
