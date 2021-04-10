import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Student } from './student.model';
import {MatTableDataSource} from '@angular/material/table';
import {MatTableModule} from '@angular/material/table';
import {SelectionModel} from '@angular/cdk/collections';
import {FormControl,Validators } from '@angular/forms';
import {Observable } from 'rxjs';
import {map, startWith} from 'rxjs/operators';
import { Routes, RouterModule, Router } from '@angular/router';
import { PageNotFoundComponentComponent } from '../page-not-found-component/page-not-found-component.component';


  @Component({
    selector: 'app-teacher',
    templateUrl: './teacher.component.html',
    styleUrls: ['./teacher.component.css']
  })
export class TeacherComponent implements OnInit {

  

     
    @Output() addstudentEvent = new EventEmitter<Student>();
    
    @Output() removestudentsEvent = new EventEmitter<Student[]>();

 


    enrolledstudents : Student[];

    public get _enrolledstudents(): Student[] {
      return this.enrolledstudents;
    }

    @Input('enrolledstudents')
    set enrolledStudents (students : Student[])
    {
        this.enrolledstudents = students;        
 
    }
    
    @Input ('dataSource')
    dataSource = new MatTableDataSource<Student>(this.enrolledStudents);

    
    
    
    @Input('studenti') 
    studenti : Student [];
    
    displayedColumns: string[] = ['select','id','name','firstname'];
    selection = new SelectionModel<Student>(true, []);
    mycontrol = new FormControl();
    filteredOptions : Observable<Student[]>;

    public href :string ="";
    public href2 : string ="";
    public hreff : string ="";
    public subject : string ="";
    
    constructor (private router : Router) {

    this.hreff = router.url;
    this.subject = this.hreff.substring(this.hreff.lastIndexOf('/')+1 );
    console.log(this.subject);
    this.href = '/teacher/course/'+ this.subject;
    this.href2 = this.href + '/vms';
}

    ngOnInit() {


     this.enrolledstudents = Object.assign(this.enrolledstudents);
     this.dataSource = new MatTableDataSource<Student> (this.enrolledstudents);
      this.filteredOptions = this.mycontrol.valueChanges.pipe(
        startWith(''),
        map (studenti => this._filter(studenti)));    


        console.log(this.studenti);
        console.log(this.enrolledstudents);
        
      }

     
    isAllSelected() {
      
      const numSelected = this.selection.selected.length;
      const numRows = this.dataSource.data.length;
      return numSelected === numRows;
    }
  
    /** Selects all rows if they are not all selected; otherwise clear selection. */
    masterToggle() {
      this.isAllSelected() ?
          this.selection.clear() :
          this.dataSource.data.forEach(row => this.selection.select(row));
    }
    removeSelectedRows() {
      
      this.removestudentsEvent.emit(this.selection.selected);
      this.selection = new SelectionModel<Student>(true, []);
      
      this.filteredOptions = this.mycontrol.valueChanges.pipe(
        startWith(''),
        map (studenti => this._filter(studenti)));    

      
    }
  
  
  
  
  
  private _filter(studente: string): Student [] {
  const filterValue = studente.toLowerCase();
  
  return this.studenti.filter(option => this.displayFn(option).toLowerCase().includes(filterValue));
  
  }
  
  
    displayFn(studente: Student): string {
      return studente ? studente.name + ' ' + studente.firstname + ' '+ '(' + studente.serial + ')': undefined;
    }
  
  studenteselezionato : Student;
  
  saveobject(event) {
    const selectedValue = event.option.value;
    console.log(selectedValue);
    
    this.studenteselezionato = Object.assign(selectedValue);
    
  console.log(this.enrolledstudents);
  
  }

  addstudent() {
  
    this.addstudentEvent.emit(this.studenteselezionato);
    console.log(this.studenti);

  }

  updateFilteredOptions() {
    this.filteredOptions = this.mycontrol.valueChanges.pipe(
      startWith(''),
      map(value => typeof value === 'string' ? value : this.displayFn(value)),
      map(displayedValue => this._filter(displayedValue))
    );
  }

  
}
