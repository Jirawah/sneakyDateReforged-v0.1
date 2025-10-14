import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) =>
  next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      // backend renvoie { timestamp, status, error, message } via GlobalExceptionHandler
      console.error('[HTTP Error]', err.error || err.message);
      return throwError(() => err);
    })
  );
