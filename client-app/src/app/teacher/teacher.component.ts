import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Student } from './student.model';
import {MatTableDataSource} from '@angular/material/table';
import {MatTableModule} from '@angular/material/table';
import {SelectionModel} from '@angular/cdk/collections';
import {FormControl,Validators } from '@angular/forms';
import {Observable } from 'rxjs';
import {map, startWith} from 'rxjs/operators';
import { Routes, RouterModule, Router, ActivatedRoute } from '@angular/router';
import { PageNotFoundComponentComponent } from '../page-not-found-component/page-not-found-component.component';
import { StudentDTO } from 'app/model/studentDTO.model';


  @Component({
    selector: 'app-teacher',
    templateUrl: './teacher.component.html',
    styleUrls: ['./teacher.component.css']
  })
export class TeacherComponent implements OnInit {

  

     
    @Output() addstudentEvent = new EventEmitter<StudentDTO>();
    
    @Output() removestudentsEvent = new EventEmitter<StudentDTO[]>();

 


    enrolledstudents : StudentDTO[];

    public get _enrolledstudents(): StudentDTO[] {
      return this.enrolledstudents;
    }

    @Input('enrolledstudents')
    set enrolledStudents (students : StudentDTO[])
    {
        this.enrolledstudents = students;        
 
    }
    
    @Input ('dataSource')
    dataSource = new MatTableDataSource<StudentDTO>(this.enrolledStudents);

    
    
    
    @Input('studenti') 
    studenti : StudentDTO [];
    
    displayedColumns: string[] = ['select','id','name','firstname'];
    selection = new SelectionModel<StudentDTO>(true, []);
    mycontrol = new FormControl();
    filteredOptions : Observable<StudentDTO[]>;

    public href :string ="";
    public href2 : string ="";
    public hreff : string ="";
    public subject : string ="";
    public firstParam : string ="";
    
    constructor (private router : Router, private activeRoute: ActivatedRoute) {

    this.hreff = router.url;
    this.subject = this.hreff.substring(this.hreff.lastIndexOf('/')+1 );
    console.log(this.subject);
    this.href = '/teacher/course/'+ this.subject;
    this.href2 = this.href + '/vms';
}

    ngOnInit() {


      this.firstParam = this.activeRoute.snapshot.queryParamMap.get('name');

this.activeRoute.params.subscribe (routeParams => {
this.hreff = this.router.url;
  this.subject = this.hreff.substring(0,this.hreff.lastIndexOf('?'));
  this.hreff = this.hreff.substring(0,this.hreff.lastIndexOf('/'));
  this.href = this.subject; console.log(this.href);
   this.href2 = this.hreff + '/vms';
   console.log(this.href2);
;
});

     this.enrolledstudents = Object.assign(this.enrolledstudents);
     this.dataSource = new MatTableDataSource<StudentDTO> (this.enrolledstudents);
      this.filteredOptions = this.mycontrol.valueChanges.pipe(
        startWith(''),
        map (studenti => this._filter(studenti)));    

        
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
      this.selection = new SelectionModel<StudentDTO>(true, []);
      
      this.filteredOptions = this.mycontrol.valueChanges.pipe(
        startWith(''),
        map (studenti => this._filter(studenti)));    

      
    }
  
  
  
  
  
  private _filter(studente: string): StudentDTO [] {
  const filterValue = studente.toLowerCase();
  
  return this.studenti.filter(option => this.displayFn(option).toLowerCase().includes(filterValue));
  
  }
  
  
    displayFn(studente: StudentDTO): string {
      return studente ? studente.name + ' ' + studente.firstName + ' '+ '(' + studente.id + ')': undefined;
    }
  
  studenteselezionato : StudentDTO;
  
  saveobject(event) {
    const selectedValue = event.option.value;
    
    
    this.studenteselezionato = Object.assign(selectedValue);
    

  
  }

  addstudent() {
  
    this.addstudentEvent.emit(this.studenteselezionato);
    this.studenteselezionato = null;

  }

  updateFilteredOptions() {
    this.filteredOptions = this.mycontrol.valueChanges.pipe(
      startWith(''),
      map(value => typeof value === 'string' ? value : this.displayFn(value)),
      map(displayedValue => this._filter(displayedValue))
    );
  }

  
}
