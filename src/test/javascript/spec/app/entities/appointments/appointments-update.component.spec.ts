import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { MeetmonkTestModule } from '../../../test.module';
import { AppointmentsUpdateComponent } from 'app/entities/appointments/appointments-update.component';
import { AppointmentsService } from 'app/entities/appointments/appointments.service';
import { Appointments } from 'app/shared/model/appointments.model';

describe('Component Tests', () => {
  describe('Appointments Management Update Component', () => {
    let comp: AppointmentsUpdateComponent;
    let fixture: ComponentFixture<AppointmentsUpdateComponent>;
    let service: AppointmentsService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MeetmonkTestModule],
        declarations: [AppointmentsUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(AppointmentsUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(AppointmentsUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(AppointmentsService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Appointments(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new Appointments();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
