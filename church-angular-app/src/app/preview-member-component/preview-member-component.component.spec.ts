import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreviewMemberComponent } from './preview-member-component.component';

describe('PreviewMemberComponent', () => {
  let component: PreviewMemberComponent;
  let fixture: ComponentFixture<PreviewMemberComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PreviewMemberComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PreviewMemberComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
