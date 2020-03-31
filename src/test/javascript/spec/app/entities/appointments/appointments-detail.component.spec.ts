import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MeetmonkTestModule } from '../../../test.module';
import { AppointmentsDetailComponent } from 'app/entities/appointments/appointments-detail.component';
import { Appointments } from 'app/shared/model/appointments.model';

describe('Component Tests', () => {
  describe('Appointments Management Detail Component', () => {
    let comp: AppointmentsDetailComponent;
    let fixture: ComponentFixture<AppointmentsDetailComponent>;
    const route = ({ data: of({ appointments: new Appointments(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MeetmonkTestModule],
        declarations: [AppointmentsDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(AppointmentsDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(AppointmentsDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load appointments on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.appointments).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
