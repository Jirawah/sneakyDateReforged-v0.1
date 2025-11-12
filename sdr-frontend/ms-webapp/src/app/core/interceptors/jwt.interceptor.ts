// import { HttpInterceptorFn } from '@angular/common/http';

// export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
//   const token = sessionStorage.getItem('sdr_jwt');
//   return token
//     ? next(req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }))
//     : next(req);
// };
import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const token = sessionStorage.getItem('sdr_jwt');
  if (!token) return next(req);

  // Préfixes d’API vers lesquels on veut ajouter le Bearer
  const API_PREFIXES = [
    environment.apiBaseUrl,
    environment.profileApiUrl,
  ].filter((p): p is string => !!p);

  const isApiCall = API_PREFIXES.some(prefix => req.url.startsWith(prefix));
  if (!isApiCall) return next(req);

  const authReq = req.clone({
    setHeaders: { Authorization: `Bearer ${token}` },
  });
  return next(authReq);
};
