import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { MeetmonkSharedModule } from 'app/shared/shared.module';
import { MeetmonkCoreModule } from 'app/core/core.module';
import { MeetmonkAppRoutingModule } from './app-routing.module';
import { MeetmonkHomeModule } from './home/home.module';
import { MeetmonkEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ErrorComponent } from './layouts/error/error.component';

@NgModule({
  imports: [
    BrowserModule,
    MeetmonkSharedModule,
    MeetmonkCoreModule,
    MeetmonkHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    MeetmonkEntityModule,
    MeetmonkAppRoutingModule
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent],
  bootstrap: [MainComponent]
})
export class MeetmonkAppModule {}
