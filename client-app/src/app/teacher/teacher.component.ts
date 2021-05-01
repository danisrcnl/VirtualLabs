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
    studenti$ : Observable<StudentDTO[]>;

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
    studenti : StudentDTO[] = new Array <StudentDTO>();

     
    @Input('studenti$')
    set Studenti (students : Observable<StudentDTO[]>)
    {
       this.studenti$ = students;
 
    }
    
    
    displayedColumns: string[] = ['select','id','name','firstname'];
    selection = new SelectionModel<StudentDTO>(true, []);
    mycontrol = new FormControl();
    filteredOptions : Observable<StudentDTO[]>;
    studentiss : StudentDTO[] = new Array<StudentDTO>();

    public href :string ="";
    public href2 : string ="";
    public href3 : string ="";
    public hreff : string ="";
    public subject : string ="";
    public firstParam : string ="";
    
    constructor (private router : Router, private activeRoute: ActivatedRoute) {

   
}

    ngOnInit() {

this.activeRoute.queryParams.subscribe(data => {

console.log(this.studenti);
      this.firstParam = this.activeRoute.snapshot.queryParamMap.get('name');

this.activeRoute.params.subscribe (routeParams => {
this.hreff = this.router.url;
  this.subject = this.hreff.substring(0,this.hreff.lastIndexOf('?'));
  this.hreff = this.hreff.substring(0,this.hreff.lastIndexOf('/'));
  this.href = this.subject; console.log(this.href);
   this.href2 = this.hreff + '/vms';
   this.href3= this.hreff + '/elaborati';
   console.log(this.href2);
;

     this.enrolledstudents = Object.assign(this.enrolledstudents);
     this.studenti = Object.assign(this.studenti);
     this.dataSource = new MatTableDataSource<StudentDTO> (this.enrolledstudents);


    this.studenti$.subscribe(data => {data.forEach(s => { this.studentiss.push(s)})
  
   this.filteredOptions = this.mycontrol.valueChanges.pipe(
        startWith(''),
        map (studentiss => this._filter(studentiss,studentiss)));    
  
  
  });

    

     
})

    })
}  ;
      

     
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
        map (studentiss => this._filter(studentiss,studentiss)));    

      
    }
  
  
  
  
  
  private _filter(studente: string,studenti: StudentDTO[]){
 
    const filterValue = studente.toLowerCase();
     
  
  return studenti.filter(student => this.displayFn(student).toLowerCase().includes(filterValue));
  
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
    this.studenti$.subscribe(data1 => {

      
      

this.filteredOptions = this.mycontrol.valueChanges.pipe(
        startWith(''),
        map(value => typeof value === 'string' ? value : this.displayFn(value)),
        map (data => this._filter(data,data1)));    


    });

   



   /* this.filteredOptions = this.mycontrol.valueChanges.pipe(
      startWith(''),
      map(value => typeof value === 'string' ? value : this.displayFn(value)),
      map(displayedValue => this._filter(displayedValue))
    );*/
  }

  
}
