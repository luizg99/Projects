import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WeatherService {

  apiKey = '7e85f54a0f00e8ae17dc1617fa17a6fc';

  constructor(private http: HttpClient) { }

  getWeatherData(cityName: string): Observable<any> {
    return this.http.get(
      `https://api.openweathermap.org/data/2.5/weather?lat=${'-16.6869'}&lon=${'-49.2648'}&appid=${this.apiKey}`
    );
  }
}
