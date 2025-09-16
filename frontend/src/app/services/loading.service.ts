import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LoadingService {
  private _isLoading = signal(false);
  
  get isLoading() {
    return this._isLoading.asReadonly();
  }
  
  show() {
    this._isLoading.set(true);
  }
  
  hide() {
    this._isLoading.set(false);
  }
}