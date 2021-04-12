import { NgModule, Component } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {AppComponent} from './app.component'
import { StudentsComponent } from './student/students.component';
import {TeacherContComponent} from './teacher/teacher-cont.component';
import {StudentsContComponent} from './student/students-cont.component';
import { VmsContcomponentComponent } from './teacher/vms-contcomponent.component';
import { PageNotFoundComponentComponent } from './page-not-found-component/page-not-found-component.component';
import {MatTabsModule} from '@angular/material/tabs';
import { RegisterComponent } from './auth/register/register.component';
import { LoginDialogComponent } from './auth/login-dialog.component';
//import { AuthGuard } from './_helpers/auth.guard'
import { HomeComponentComponent } from './home-component/home-component.component';
import {TeacherComponent} from './teacher/teacher.component';
import { AppComponentStudent } from './student/app.component';
import { AppComponentTeacher } from './teacher/app.component';
import { VmsContcomponentComponent2 } from './student/vms-contcomponent.component';
export const routes: Routes = [

  

{ path :"",
component : HomeComponentComponent

},


{
  path: "auth/register",
  component : RegisterComponent


},

{
  path: "auth/login",
  component : LoginDialogComponent,

},



{ path:"teacher", component : AppComponentTeacher, children: [
  
  { path: "course",  children: [
    
     
    
      { path: "vms", component: VmsContcomponentComponent } ,
      { path: "students", component: TeacherContComponent}
    
      
  ]}]}
,

{
  path: "student",
  component: AppComponentStudent, children: [
    
    {path: "course",  children: [
    
      { path: "vms", component: VmsContcomponentComponent2 } ,
      { path: "students", component: StudentsContComponent}
    
    ]}],
     //runGuardsAndResolvers: 'always'
   

},

{
  path : "**",
  component : PageNotFoundComponentComponent

}
 
 
];

@NgModule({


  imports: [RouterModule.forRoot(routes, {onSameUrlNavigation: 'reload', enableTracing: false }),MatTabsModule,],
  exports: [RouterModule]
})
export class AppRoutingModule { }
