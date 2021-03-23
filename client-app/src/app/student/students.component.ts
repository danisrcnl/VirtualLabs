import { Component, OnInit, Input, Output, EventEmitter, SimpleChange, SimpleChanges } from '@angular/core';
import { Student } from './student.model';
import {MatTableDataSource} from '@angular/material/table';
import {MatTableModule} from '@angular/material/table';
import {SelectionModel} from '@angular/cdk/collections';
import {FormBuilder, FormControl,FormGroup,Validators } from '@angular/forms';
import {Observable, zip } from 'rxjs';
import {map, startWith} from 'rxjs/operators';
import {MatFormFieldModule} from '@angular/material/form-field';
import {FormsModule,ReactiveFormsModule} from '@angular/forms';
import {MatAutocompleteModule} from '@angular/material/autocomplete'
import {MatInputModule} from '@angular/material/input';
import { SummaryResolver } from '@angular/compiler';
import {MatTabsModule} from '@angular/material/tabs';
import { Routes, RouterModule, Router, ActivatedRoute } from '@angular/router';
import { StudentsContComponent } from './students-cont.component';
import { PageNotFoundComponentComponent } from '../page-not-found-component/page-not-found-component.component';
import { Proposal } from '../model/proposal.model';
import { Team } from '../model/team.model';
import { MemberStatus } from '../model/memberstatus.model';
import { StudentDTO } from '../model/studentDTO.model';
import { Console } from 'console';


  @Component({
    selector: 'app-students',
    templateUrl: './students.component.html',
    styleUrls: ['./students.component.css'],
    inputs :['tablevalue']
  })
export class StudentsComponent implements OnInit {
     
    
    @Output() addstudentEvent = new EventEmitter<Student>();
    
    @Output() removestudentsEvent = new EventEmitter<Student[]>();

    @Output() invitestudentEvent = new EventEmitter<String[]>();

    @Output() groupName = new EventEmitter <String>();

    @Output() timeoutValue = new EventEmitter <Number>();


    enrolledstudents : Student[];
    compagni : Student[];
    groupid : number;
    teams : Team[];
    displayedColumns: string[] = ['select','name','firstname','serial'];
    headers = ['id','name','firstName'];
    headers1 = ['Serial','Name','Firstname'];
        
    selection = new SelectionModel<Student>(true, []);
    students : Student [];
    studentsIds : String[] = []; 
    members : MemberStatus[] = new Array<MemberStatus>();
  
    public dataSource2 = new MatTableDataSource<Student>(this.Compagni);
   
    mycontrol = new FormControl();
    filteredOptions = new Observable<Student[]>();
    myForm : FormGroup;
    timeout : number;
    public groupname : string = "";
    public href :string ="";
    public href2 : string ="";
    public hreff : string ="";
    public subject : string ="";
    public value : boolean;
    public value1 : boolean;
    public tableclasses = { "hide" : false, "show" : false  }
    public compagniclass = { "hide" : false, "show2" : false }
    public groupclasses = { "hide" :true }

    private tablevalue : boolean;

    public get _enrolledstudents(): Student[] {
      return this.enrolledstudents;
    }

    public get _compagni(): Student[] {
      return this.compagni;
    }
    @Input('enrolledstudents')
    set enrolledStudents (students : Student[])
    {
        this.enrolledstudents = students;        
 
    }

    @Input('tablevalue')
    set tableValue (val : boolean)
    {
      this.tablevalue = val;
    }

    set Groupclasses (val : boolean)
    {
      this.groupclasses.hide = val;
    }

    @Input ('compagni')
    set Compagni (students : Student[])
    {
      this.compagni = students;
    }

    @Input ('groupid')
    set Groupid (val : number)
    {
      this.groupid = val;
    }
    
    @Input ('teams')
    set Teams (val : Team[])
    {
      this.teams = val;
    }

   ngOnChanges (changes: SimpleChanges) {


    this.tableclasses.hide = changes.tableValue.currentValue;
    this.tableclasses.show = !changes.tableValue.currentValue;;
    if (changes.tableValue.currentValue)
    {
      this.compagniclass.hide = false;
      this.compagniclass.show2 = true;
    }
    else{
      this.compagniclass.hide = true;
      this.compagniclass.show2 = false;
    }

  
   }

    public get _tablevalue(): boolean {
      return this.tablevalue;
    }

    
    @Input ('dataSource')
    dataSource = new MatTableDataSource<Student>(this.enrolledStudents);

    
    @Input('studenti') 
    studenti : Student [];
    
  
    
    
constructor (private router : Router, private activeRoute: ActivatedRoute, private fb: FormBuilder) {

}




    ngOnInit() {

      this.myForm = this.fb.group({
        timeout: '',
        groupname: ''
      })
      

const queryParams = this.activeRoute.snapshot.queryParams
const routeParams = this.activeRoute.snapshot.params;

this.activeRoute.queryParams.subscribe (queryParams => {

});

this.activeRoute.params.subscribe (routeParams => {
this.hreff = this.router.url;
  this.subject = this.hreff.substring(0,this.hreff.lastIndexOf('/'));
 
  this.href = this.hreff; console.log(this.href);
   this.href2 = this.subject + '/vms';
   console.log(this.href2);
;
});
  
     this.enrolledstudents = Object.assign(this.enrolledstudents);
     this.dataSource = new MatTableDataSource<Student> (this.enrolledstudents);
     this.compagni = Object.assign (this.compagni);
     console.log(this.compagni);
     this.dataSource2 = new MatTableDataSource<Student> (this.compagni);
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

  sendinvite() {
      
   
   this.students = this.selection.selected; 
   this.selection.clear();
   this.timeoutValue.emit(this.timeout);
   this.groupName.emit(this.groupname);
   
   this.students.forEach(x => {

   this.studentsIds.push(x.id.toString());
   })
   console.log(this.students);
   console.log(this.studentsIds);
   this.invitestudentEvent.emit(this.studentsIds);

  }

  accept()
  {

  }

  reject ()
  {
    
  }

  
}
