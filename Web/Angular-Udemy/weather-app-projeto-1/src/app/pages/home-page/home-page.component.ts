import { Component, OnDestroy, OnInit } from '@angular/core';
import { WeatherService } from '../../services/weather.service';
import { Data } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [],
  templateUrl: './home-page.component.html'
})
export class HomePageComponent implements OnInit, OnDestroy{
  private readonly destroy$: Subject<void> = new Subject();

  weatherData!: Data;

  constructor(private weatherService: WeatherService) { }

  ngOnInit(): void {
      this.getWeatherData('Go');
  }

  getWeatherData(cityName: string): void {
    this.weatherService.getWeatherData(cityName)
    .pipe(
      takeUntil(this.destroy$)
    )
    .subscribe(data => {
      this.weatherData = data;
      console.log(this.weatherData);
    });
  }

  ngOnDestroy(): void {
    throw new Error('Method not implemented.');
    this.destroy$.next();
    this.destroy$.complete();
  }
}
