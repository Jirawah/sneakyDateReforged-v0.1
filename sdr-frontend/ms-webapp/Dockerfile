## Étape 1 : build Angular
#FROM node:20 AS build
#WORKDIR /app
#COPY package*.json ./
#RUN npm install
#COPY . .
#RUN npm run build -- --configuration=production
#
## Étape 2 : servir avec nginx
#FROM nginx:1.25
#COPY --from=build /app/dist/ms-webapp /usr/share/nginx/html
#COPY nginx.conf /etc/nginx/conf.d/default.conf
#EXPOSE 80
#CMD ["nginx", "-g", "daemon off;"]
####################################################################
# # Étape 1 : build Angular
# FROM node:20 AS build
# WORKDIR /app
# # 1) Installer à partir du lockfile pour des builds reproductibles
# COPY package*.json ./
# RUN npm ci
# # 2) Copier le code et builder
# COPY . .
# # Permet de surcharger la conf si besoin: --build-arg CONFIG=development
# ARG CONFIG=production
# RUN npm run build -- --configuration=$CONFIG
# # Étape 2 : servir avec nginx
# FROM nginx:1.25
# # Copier les assets Angular
# COPY --from=build /app/dist/ms-webapp /usr/share/nginx/html
# # Fallback SPA pour le routing Angular
# COPY nginx.conf /etc/nginx/conf.d/default.conf
# # (Optionnel) éviter les 413 si tu as de gros payloads d'upload (à mettre dans nginx.conf)
# # client_max_body_size 10m;
# EXPOSE 80
# CMD ["nginx", "-g", "daemon off;"]
##########################################################################
# Étape 1 : build Angular (inchangé)
FROM node:20 AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
ARG CONFIG=production
RUN npm run build -- --configuration=$CONFIG

# Étape 2 : Nginx
FROM nginx:1.25
# important: nettoyer la page par défaut
RUN rm -rf /usr/share/nginx/html/*

# 1) Si ton build sort dans dist/ms-webapp (cas le plus courant)
COPY --from=build /app/dist/ms-webapp/ /usr/share/nginx/html/

# 2) Si jamais tu vois un sous-dossier browser (SSR ou config différente), utilise ceci à la place :
# COPY --from=build /app/dist/ms-webapp/browser/ /usr/share/nginx/html/

COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

