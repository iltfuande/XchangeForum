import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent {

  year: number = 2023;
  authorFullName: string = 'Михайло Терьохін';
  telegram: string = 'https://t.me/iltfuande';
  mail: string = 'iltfuande@gmail.com';
  github: string = 'https://github.com/iltfuande/XchangeForum';
  linkedIn: string = 'https://www.linkedin.com/in/iltfuande/';
}
