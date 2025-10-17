import { HttpInterceptorFn } from '@angular/common/http';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const token = sessionStorage.getItem('sdr_jwt');
  return token ? next(req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })) : next(req);
};
