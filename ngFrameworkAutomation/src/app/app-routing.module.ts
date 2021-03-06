import { UpdateUserComponent } from './util/update-user/update-user.component';
import { RegisterComponent } from './util/SignUp/register/register.component';
import { CreateTemplateComponent } from './util/generator/create-template/create-template.component';
import { GenerateComponent } from './util/generator/generate/generate.component';
import { ResultsComponent } from './util/search/results/results.component';
import { LandingPageComponent } from './util/home/landing-page/landing-page.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { UserProfileComponent } from './util/user/user-profile/user-profile.component';


const routes: Routes = [
  { path: 'results', component: ResultsComponent },
  { path: 'templates/:id', component: GenerateComponent },
  { path: 'template/create', component: CreateTemplateComponent },
  { path: 'template/edit/:id', component: CreateTemplateComponent },
  { path: 'search', component: ResultsComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'userProfile', component: UserProfileComponent },
  { path: 'updateUser', component: UpdateUserComponent },
  { path: '**', component: LandingPageComponent }

];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
