import { THIS_EXPR } from '@angular/compiler/src/output/output_ast';
import { parseI18nMeta } from '@angular/compiler/src/render3/view/i18n/meta';
import { Component, Input, OnInit } from '@angular/core';
<<<<<<< HEAD
import { MatDialog } from '@angular/material/dialog';
=======
import { MatChip, MatChipList } from '@angular/material/chips';
import { Observable } from 'rxjs';
>>>>>>> 43f67575226a03087f883ad7d6f324fd419ca48f
import {Assignment} from '../../model/assignment.model';
import {Paper} from '../../model/paper.model';
import {PaperStatus} from '../../model/paperStatus.model';
import {PaperStatusTime} from '../../model/paperStatusTime.model';
<<<<<<< HEAD
import {StudentDTO} from '../../model/studentDTO.model'
import { ConsegnadialogComponent } from '../consegnadialog/consegnadialog.component';
=======
import {StudentDTO} from '../../model/studentDTO.model';
>>>>>>> 43f67575226a03087f883ad7d6f324fd419ca48f

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
  viewingPapers: PaperWithHistory[] = [];
  filteredViewingPapers: PaperWithHistory[] = [];
  status: String[] = ["letti", "consegnati", "rivisti", "valutati"];
  selection: String[] = [];

  @Input('assignmentWithPapers')
  set _assignmentWithPapers (assignmentWithPapers: AssignmentWithPapers[]) {
    this.assignmentWithPapers = assignmentWithPapers;
    console.log(this.assignmentWithPapers);
  }

  constructor () { }

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


<<<<<<< HEAD
  constructor(public matDialog : MatDialog) { }
=======
    this.viewingPapers.forEach(p => {
      selection.forEach(s => {
        if(this.hasSameStatus(p.paper.currentStatus, s))
          keep = true;
      });
>>>>>>> 43f67575226a03087f883ad7d6f324fd419ca48f

      if(keep) {
        newArray.push(p);
        keep = false;
      }

    });
    this.filteredViewingPapers = newArray;
  }

<<<<<<< HEAD

  createconsegna() 
  {
    this.matDialog.open(ConsegnadialogComponent);

  }

=======
  hasSameStatus (status: PaperStatus, value: String) {


    if (status.toString() == "CONSEGNATO" && value == " consegnati ")
      return true;

    if (status.toString() == "LETTO" && value == " letti ")
      return true;

    if (status.toString() == "RIVISTO" && value == " rivisti ")
      return true;

    if (status.toString() == "VALUTATO" && value == " valutati ")
      return true;

    return false;

  }
    
>>>>>>> 43f67575226a03087f883ad7d6f324fd419ca48f
}
